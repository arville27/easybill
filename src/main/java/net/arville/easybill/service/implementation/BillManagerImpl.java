package net.arville.easybill.service.implementation;

import lombok.RequiredArgsConstructor;
import net.arville.easybill.dto.response.BillResponse;
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
    public Map<User, BillResponse.AggregatedRelatedOrderWithTotalOwe> getUserPayables(User user) {
        return billRepository.findAllUserPayables(user.getId())
                .stream()
                .collect(Collectors.groupingBy(
                        Bill::getOrderHeaderBuyer,
                        Collectors.collectingAndThen(Collectors.toList(), this::calculateAggregatedValue))
                );
    }

    @Override
    public Map<User, BillResponse.AggregatedRelatedOrderWithTotalOwe> getUserReceivables(User user) {
        return billRepository.findAllUserReceivables(user.getId())
                .stream()
                .collect(Collectors.groupingBy(
                        Bill::getUser,
                        Collectors.collectingAndThen(Collectors.toList(), this::calculateAggregatedValue))
                );
    }

    private BillResponse.AggregatedRelatedOrderWithTotalOwe calculateAggregatedValue(List<Bill> billList) {
        var totalOweAmount = billList.stream()
                .map(Bill::getOweAmountWithBillTransaction)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var orderHeaderList = billList.stream()
                .map(Bill::getOrderHeader)
                .toList();

        return BillResponse.AggregatedRelatedOrderWithTotalOwe
                .builder()
                .totalOweAmount(totalOweAmount)
                .relatedOrderHeader(orderHeaderList)
                .build();
    }

}
