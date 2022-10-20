package net.arville.easybill.service.implementation;

import lombok.AllArgsConstructor;
import net.arville.easybill.dto.request.PayBillRequest;
import net.arville.easybill.dto.response.BillTransactionResponse;
import net.arville.easybill.dto.response.OrderHeaderResponse;
import net.arville.easybill.dto.response.StatusResponse;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.exception.InvalidPropertiesValue;
import net.arville.easybill.exception.MissingRequiredPropertiesException;
import net.arville.easybill.model.*;
import net.arville.easybill.model.helper.BillStatus;
import net.arville.easybill.repository.BillTransactionRepository;
import net.arville.easybill.repository.StatusRepository;
import net.arville.easybill.service.manager.StatusManager;
import net.arville.easybill.service.manager.UserManager;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StatusManagerImpl implements StatusManager {
    private final StatusRepository statusRepository;
    private final UserManager userManager;
    private final BillTransactionRepository billTransactionRepository;

    @Override
    public Set<Status> createCorrespondingStatusFromOrderHeader(OrderHeader orderHeader) {
        Double discount = orderHeader.getDiscount();
        BigDecimal upto = orderHeader.getUpto();
        BigDecimal totalPayment = orderHeader.getTotalPayment();
        Set<OrderDetail> orderList = orderHeader.getOrderDetailList();

        BigDecimal totalOrderAmount = orderList.stream()
                .map(order -> order.getPrice().multiply(BigDecimal.valueOf(order.getQty())))
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);

        BigDecimal discountAmountBeforeUpto = totalOrderAmount.multiply(BigDecimal.valueOf(discount)).setScale(0, RoundingMode.HALF_UP);
        BigDecimal discountAmount = discountAmountBeforeUpto.compareTo(upto) > 0 ? upto : discountAmountBeforeUpto;

        BigDecimal othersFee = totalPayment.add(discountAmount).subtract(totalOrderAmount);

        // Fill missing attribute in order header model
        orderHeader.setTotalOrderAmount(totalOrderAmount);
        orderHeader.setDiscountAmount(discountAmount);
        orderHeader.setOtherFee(othersFee);

        // Fill missing attribute in order detail model
        Set<User> participatedUser = new HashSet<>();
        orderList.forEach(order -> {
            participatedUser.add(order.getUser());
            order.setItemDiscount(this.calculateDiscount(order, discountAmount, totalOrderAmount));
        });

        orderHeader.setParticipatingUserCount(participatedUser.size());

        return participatedUser.stream()
                .map(user -> Status.builder()
                        .orderHeader(orderHeader)
                        .status(Objects.equals(user.getId(), orderHeader.getBuyer().getId()) ? BillStatus.PAID : BillStatus.UNPAID)
                        .user(user)
                        .build()
                )
                .collect(Collectors.toSet());
    }

    @Override
    public UserResponse getAllUsersBill(User user) {
        var aggregated = statusRepository.findAllUsersStatus(user.getId())
                .stream()
                .collect(Collectors.groupingBy(
                                s -> s.getOrderHeader().getBuyer(),
                                Collectors.collectingAndThen(Collectors.toList(), statusList -> {
                                    var totalOweAmount = statusList.stream()
                                            .map(Status::getOweAmountWithBillTransaction)
                                            .reduce(BigDecimal.valueOf(0), BigDecimal::add);

                                    var orderHeaderList = statusList.stream()
                                            .map(Status::getOrderHeader)
                                            .collect(Collectors.toList());
                                    return StatusResponse.AggregatedRelatedOrderWithTotalOwe
                                            .builder()
                                            .totalOweAmount(totalOweAmount)
                                            .relatedOrderHeader(orderHeaderList)
                                            .build();
                                })
                        )
                );

        return UserResponse.template(user)
                .statusResponseList(aggregated.entrySet()
                        .stream()
                        .map(userMapEntry -> {
                            User buyer = userMapEntry.getKey();
                            var totalOwe = (BigDecimal) userMapEntry.getValue().getTotalOweAmount();
                            var orderHeaderList = (List<OrderHeader>) userMapEntry.getValue().getRelatedOrderHeader();
                            return StatusResponse.builder()
                                    .oweResponse(UserResponse.mapWithoutDate(buyer))
                                    .oweAmount(totalOwe)
                                    .relatedOrderHeader(orderHeaderList
                                            .stream()
                                            .map(orderHeader -> OrderHeaderResponse.template(orderHeader).build())
                                            .collect(Collectors.toList())
                                    )
                                    .status(BillStatus.UNPAID)
                                    .build();
                        })
                        .collect(Collectors.toList())
                )
                .build();
    }

    @Override
    public UserResponse getAllUsersBillToUser(User user) {
        var aggregated = statusRepository.findAllStatusToUser(user.getId())
                .stream()
                .collect(Collectors.groupingBy(
                                Status::getUser,
                                Collectors.collectingAndThen(Collectors.toList(), statusList -> {
                                    var totalOweAmount = statusList.stream()
                                            .map(Status::getOweAmountWithBillTransaction)
                                            .reduce(BigDecimal.valueOf(0), BigDecimal::add);

                                    var orderHeaderList = statusList.stream()
                                            .map(Status::getOrderHeader)
                                            .collect(Collectors.toList());

                                    return StatusResponse.AggregatedRelatedOrderWithTotalOwe
                                            .builder()
                                            .totalOweAmount(totalOweAmount)
                                            .relatedOrderHeader(orderHeaderList)
                                            .build();
                                })
                        )
                );

        return UserResponse.template(user)
                .statusResponseList(aggregated.entrySet()
                        .stream()
                        .map(userMapEntry -> {
                            User relatedUser = userMapEntry.getKey();
                            var totalOwe = (BigDecimal) userMapEntry.getValue().getTotalOweAmount();
                            var orderHeaderList = (List<OrderHeader>) userMapEntry.getValue().getRelatedOrderHeader();
                            return StatusResponse.builder()
                                    .userResponse(UserResponse.mapWithoutDate(relatedUser))
                                    .oweAmount(totalOwe)
                                    .relatedOrderHeader(orderHeaderList
                                            .stream()
                                            .map(orderHeader -> OrderHeaderResponse.template(orderHeader).build())
                                            .collect(Collectors.toList())
                                    )
                                    .status(BillStatus.UNPAID)
                                    .build();
                        })
                        .collect(Collectors.toList())
                )
                .build();
    }

    @Transactional
    public BillTransactionResponse payUnpaidStatus(User user, PayBillRequest payBillRequest) {
        var missingProperties = payBillRequest.getMissingProperties();
        if (missingProperties.size() > 0) {
            throw new MissingRequiredPropertiesException(missingProperties);
        }

        if (payBillRequest.getAmount().compareTo(BigDecimal.valueOf(0)) <= 0) {
            throw new InvalidPropertiesValue(
                    Map.of("amount", "should be more than 0")
            );
        }

        BigDecimal payAmount = payBillRequest.getAmount();
        User targetUser = userManager.getUserByUserId(payBillRequest.getUserId());

        var unpaidStatus = statusRepository.findAllUsersBillsToSpecificUser(user.getId(), targetUser.getId());

        BigDecimal totalBillToTargetUser = unpaidStatus
                .stream()
                .map(Status::getOweAmountWithBillTransaction)
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);

        if (totalBillToTargetUser.compareTo(BigDecimal.valueOf(0)) <= 0)
            throw new InvalidPropertiesValue(
                    Map.of("user_id", "you do not have any unpaid bills to this user")
            );
        else if (payBillRequest.getAmount().compareTo(totalBillToTargetUser) > 0)
            throw new InvalidPropertiesValue(
                    Map.of("amount", "should be less or equals to " + totalBillToTargetUser)
            );

        BillTransaction billTransaction = BillTransaction.builder()
                .payer(user)
                .receiver(targetUser)
                .amount(payAmount)
                .build();

        // Pay amount equals to total bill to target user
        if (totalBillToTargetUser.compareTo(payAmount) == 0) {
            var allPaidBills = unpaidStatus
                    .stream()
                    .peek(status -> status.setStatus(BillStatus.PAID))
                    .map(status -> {
                        var billTransactionHeader = BillTransactionHeader.builder()
                                .status(status)
                                .billTransaction(billTransaction)
                                .paidAmount(status.getOweAmount())
                                .build();
                        status.addBillTransactionHeader(billTransactionHeader);
                        return billTransactionHeader;
                    })
                    .collect(Collectors.toList());

            billTransaction.setBillTransactionHeaderList(allPaidBills);
        }
        // Pay amount not equals to total bill to target user, cascade to all possible bills
        else {
            final BigDecimal[] tempMaxPayableAmount = {BigDecimal.valueOf(0)};
            var billTransactionHeaderList = unpaidStatus
                    .stream()
                    .takeWhile(status -> tempMaxPayableAmount[0].compareTo(payAmount) < 0)
                    .map(status -> {
                        BigDecimal temp = payAmount.subtract(tempMaxPayableAmount[0]);

                        BigDecimal paidForThisStatus = status.getOweAmountWithBillTransaction();

                        if (paidForThisStatus.compareTo(temp) <= 0) {
                            status.setStatus(BillStatus.PAID);
                        } else {
                            paidForThisStatus = temp;
                        }

                        tempMaxPayableAmount[0] = tempMaxPayableAmount[0].add(paidForThisStatus);

                        var billTransactionHeader = BillTransactionHeader.builder()
                                .status(status)
                                .billTransaction(billTransaction)
                                .paidAmount(paidForThisStatus)
                                .build();

                        status.addBillTransactionHeader(billTransactionHeader);

                        return billTransactionHeader;
                    })
                    .collect(Collectors.toList());

            billTransaction.setBillTransactionHeaderList(billTransactionHeaderList);
        }

        var billTransactionEntity = billTransactionRepository.save(billTransaction);

        return BillTransactionResponse.map(billTransactionEntity);
    }

    private BigDecimal calculateDiscount(
            OrderDetail order,
            BigDecimal totalDiscountAmount,
            BigDecimal totalOrderAmount
    ) {
        var totalOrderDetail = BigDecimal.valueOf(order.getQty()).multiply(order.getPrice());
        return totalOrderDetail.multiply(totalDiscountAmount).divide(totalOrderAmount, 0, RoundingMode.HALF_UP);
    }

}
