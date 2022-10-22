package net.arville.easybill.service.implementation;

import lombok.RequiredArgsConstructor;
import net.arville.easybill.dto.response.BillResponse;
import net.arville.easybill.dto.response.OrderHeaderResponse;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.model.Bill;
import net.arville.easybill.model.OrderDetail;
import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.model.User;
import net.arville.easybill.model.helper.BillStatus;
import net.arville.easybill.repository.BillRepository;
import net.arville.easybill.service.manager.BillManager;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillManagerImpl implements BillManager {
    private final BillRepository billRepository;

    @Override
    public Set<Bill> generateCorrespondingBills(OrderHeader orderHeader) {
        Double discount = orderHeader.getDiscount();
        BigDecimal upto = orderHeader.getUpto();
        BigDecimal totalPayment = orderHeader.getTotalPayment();
        Set<OrderDetail> orderList = orderHeader.getOrderDetailList();

        BigDecimal totalOrderAmount = orderList.stream()
                .map(order -> order.getPrice().multiply(BigDecimal.valueOf(order.getQty())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

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
                .map(user -> Bill.builder()
                        .orderHeader(orderHeader)
                        .status(Objects.equals(user.getId(), orderHeader.getBuyer().getId()) ? BillStatus.PAID : BillStatus.UNPAID)
                        .user(user)
                        .build()
                )
                .collect(Collectors.toSet());
    }

    @Override
    public UserResponse getAllUsersBill(User user) {
        var aggregated = billRepository.findAllUsersBills(user.getId())
                .stream()
                .collect(Collectors.groupingBy(
                        Bill::getOrderHeaderBuyer,
                        Collectors.collectingAndThen(Collectors.toList(), this::calculateAggregatedValue))
                );

        return UserResponse.template(user)
                .billResponseList(aggregated.entrySet()
                        .stream()
                        .map(userMapEntry -> {
                            User buyer = userMapEntry.getKey();
                            var totalOwe = (BigDecimal) userMapEntry.getValue().getTotalOweAmount();
                            var orderHeaderList = (List<OrderHeader>) userMapEntry.getValue().getRelatedOrderHeader();
                            return BillResponse.builder()
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
        var aggregated = billRepository.findAllBillToUser(user.getId())
                .stream()
                .collect(Collectors.groupingBy(
                        Bill::getUser,
                        Collectors.collectingAndThen(Collectors.toList(), this::calculateAggregatedValue))
                );

        return UserResponse.template(user)
                .billResponseList(aggregated.entrySet()
                        .stream()
                        .map(userMapEntry -> {
                            User relatedUser = userMapEntry.getKey();
                            var totalOwe = (BigDecimal) userMapEntry.getValue().getTotalOweAmount();
                            var orderHeaderList = (List<OrderHeader>) userMapEntry.getValue().getRelatedOrderHeader();
                            return BillResponse.builder()
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

    private BigDecimal calculateDiscount(
            OrderDetail order,
            BigDecimal totalDiscountAmount,
            BigDecimal totalOrderAmount
    ) {
        var totalOrderDetail = BigDecimal.valueOf(order.getQty()).multiply(order.getPrice());
        return totalOrderDetail.multiply(totalDiscountAmount).divide(totalOrderAmount, 0, RoundingMode.HALF_UP);
    }

    private BillResponse.AggregatedRelatedOrderWithTotalOwe calculateAggregatedValue(List<Bill> billList) {
        var totalOweAmount = billList.stream()
                .map(Bill::getOweAmountWithBillTransaction)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var orderHeaderList = billList.stream()
                .map(Bill::getOrderHeader)
                .collect(Collectors.toList());

        return BillResponse.AggregatedRelatedOrderWithTotalOwe
                .builder()
                .totalOweAmount(totalOweAmount)
                .relatedOrderHeader(orderHeaderList)
                .build();
    }

}
