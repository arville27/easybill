package net.arville.easybill.service.implementation;

import lombok.AllArgsConstructor;
import net.arville.easybill.dto.request.PayBillRequest;
import net.arville.easybill.dto.response.BillTransactionResponse;
import net.arville.easybill.dto.response.OrderHeaderResponse;
import net.arville.easybill.dto.response.StatusResponse;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.exception.InvalidPropertiesValue;
import net.arville.easybill.exception.MissingRequiredPropertiesException;
import net.arville.easybill.model.OrderDetail;
import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.model.Status;
import net.arville.easybill.model.User;
import net.arville.easybill.model.helper.BillStatus;
import net.arville.easybill.repository.StatusRepository;
import net.arville.easybill.service.manager.StatusManager;
import net.arville.easybill.service.manager.UserManager;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StatusManagerImpl implements StatusManager {
    private final StatusRepository statusRepository;
    private final UserManager userManager;

    @Override
    public List<Status> createCorrespondingStatusFromOrderHeader(OrderHeader orderHeader) {
        Double discount = orderHeader.getDiscount();
        BigDecimal upto = orderHeader.getUpto();
        BigDecimal totalPayment = orderHeader.getTotalPayment();
        List<OrderDetail> orderList = orderHeader.getOrderDetailList();

        BigDecimal totalOrderAmount = orderList.stream()
                .map(order -> order.getPrice().multiply(BigDecimal.valueOf(order.getQty())))
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);

        BigDecimal discountAmountBeforeUpto = totalOrderAmount.multiply(BigDecimal.valueOf(discount));
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
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getAllUsersBill(User user) {
        var aggregated = statusRepository.findAllUsersStatus(user.getId())
                .stream()
                .collect(Collectors.groupingBy(
                                s -> s.getOrderHeader().getBuyer(),
                                Collectors.collectingAndThen(Collectors.toList(), statusList -> {
                                    var totalOweAmount = statusList.stream()
                                            .map(Status::getOweAmount)
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
                                            .map(Status::getOweAmount)
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

        User targetUser = userManager.getUserByUserId(payBillRequest.getUserId());

        return null;
    }

    private BigDecimal calculateDiscount(
            OrderDetail order,
            BigDecimal totalDiscountAmount,
            BigDecimal totalOrderAmount
    ) {
        var totalOrderDetail = BigDecimal.valueOf(order.getQty()).multiply(order.getPrice());
        return totalOrderDetail.multiply(totalDiscountAmount).divide(totalOrderAmount, RoundingMode.HALF_UP);
    }

}
