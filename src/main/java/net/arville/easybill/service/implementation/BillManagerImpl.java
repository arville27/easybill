package net.arville.easybill.service.implementation;

import lombok.RequiredArgsConstructor;
import net.arville.easybill.dto.response.BillResponse;
import net.arville.easybill.dto.response.OrderHeaderResponse;
import net.arville.easybill.dto.response.PaymentAccountResponse;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.model.Bill;
import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.model.User;
import net.arville.easybill.model.helper.BillStatus;
import net.arville.easybill.repository.BillRepository;
import net.arville.easybill.service.manager.BillManager;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillManagerImpl implements BillManager {
    private final BillRepository billRepository;

    @Override
    public Set<Bill> generateCorrespondingBills(OrderHeader orderHeader) {
        var billList = orderHeader.getParticipatingUsers().stream()
                .map(user -> Bill.builder()
                        .user(user)
                        .orderHeader(orderHeader)
                        .status(Objects.equals(user.getId(), orderHeader.getBuyer().getId()) ? BillStatus.PAID : BillStatus.UNPAID)
                        .build()
                )
                .collect(Collectors.toSet());

        orderHeader.setBillList(billList);

        return billList;
    }

    @Override
    public UserResponse getAllUsersBill(User user) {
        var aggregated = billRepository.findAllUsersBills(user.getId())
                .stream()
                .collect(Collectors.groupingBy(
                        Bill::getOrderHeaderBuyer,
                        Collectors.collectingAndThen(Collectors.toList(), this::calculateAggregatedValue))
                );

        return generateBillResponse(user, aggregated, false);
    }

    private UserResponse generateBillResponse(User user, Map<User, BillResponse.AggregatedRelatedOrderWithTotalOwe> aggregated, boolean isReceivables) {
        return UserResponse.template(user)
                .paymentAccountList(user.getPaymentAccountList().stream().map(PaymentAccountResponse::mapWithoutDate).toList())
                .billResponseList(aggregated.entrySet()
                        .stream()
                        .map(userMapEntry -> {
                            User buyer = userMapEntry.getKey();
                            var totalOwe = (BigDecimal) userMapEntry.getValue().getTotalOweAmount();
                            var orderHeaderList = (List<OrderHeader>) userMapEntry.getValue().getRelatedOrderHeader();
                            return BillResponse.builder()
                                    .userResponse(UserResponse.mapWithoutDate(buyer))
                                    .oweAmount(totalOwe)
                                    .relatedOrderHeader(orderHeaderList
                                            .stream()
                                            .map(orderHeader -> OrderHeaderResponse
                                                    .template(orderHeader)
                                                    .buyerResponse(UserResponse.mapWithoutDate(orderHeader.getBuyer()))
                                                    .totalBill(orderHeader.getRelevantBill(isReceivables ? buyer : user).getOweAmountWithBillTransaction())
                                                    .build()
                                            )
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

        return generateBillResponse(user, aggregated, true);
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
