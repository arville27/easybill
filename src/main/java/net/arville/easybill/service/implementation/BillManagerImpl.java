package net.arville.easybill.service.implementation;

import lombok.AllArgsConstructor;
import net.arville.easybill.dto.response.BillResponse;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.model.Bill;
import net.arville.easybill.model.OrderDetail;
import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.model.User;
import net.arville.easybill.repository.BillRepository;
import net.arville.easybill.service.manager.BillManager;
import net.arville.easybill.service.manager.UserManager;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BillManagerImpl implements BillManager {

    private final BillRepository billRepository;
    private final UserManager userManager;

    public UserResponse getAllBills(Long userId) {

        User user = userManager.getUserByUserId(userId);

        List<BillResponse> usersBill = billRepository.findAllUserBills(userId).stream()
                .map(BillResponse::map)
                .collect(Collectors.toList());

        UserResponse userResponse = UserResponse.mapWithoutDate(user);
        userResponse.setBillResponseList(usersBill);

        return userResponse;
    }

    public List<Bill> generateBillsFromOrderHeader(OrderHeader orderHeader) {

        User buyer = orderHeader.getUser();
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

        Map<Long, Bill> billMap = new HashMap<>();

        AtomicBoolean isBuyerIncluded = new AtomicBoolean(false);

        orderList.forEach(order -> {
            User currentUser = order.getUser();

            if (currentUser.getId().equals(buyer.getId())) {
                isBuyerIncluded.set(true);
                return;
            }

            var currentUserBill = billMap.getOrDefault(
                    currentUser.getId(),
                    Bill.builder().user(currentUser).owe(buyer).oweTotal(BigDecimal.valueOf(0)).build()
            );

            // TODO: Consider existing bill from buyer side to the current user
            BigDecimal orderDiscount = order.getPrice().multiply(discountAmount).divide(totalOrderAmount, 3, RoundingMode.HALF_UP);

            currentUserBill.addOweTotal(order.getPrice().subtract(orderDiscount));

            billMap.putIfAbsent(currentUser.getId(), currentUserBill);

        });

        int participantCount = billMap.size() + (isBuyerIncluded.get() ? 1 : 0);

        BigDecimal perUserFee = othersFee.divide(BigDecimal.valueOf(participantCount), 3, RoundingMode.HALF_UP);

        var bills = billMap.values().stream()
                .map(bill -> bill.addOweTotal(perUserFee))
                .map(bill -> this.getExistingBill(bill.getUser(), bill.getOwe()).addOweTotal(bill.getOweTotal()))
                .collect(Collectors.toList());

        return billRepository.saveAll(bills);
    }

    private Bill getExistingBill(User user, User oweTo) {
        return billRepository
                .findAllUserBills(user.getId())
                .stream()
                .filter(bill -> bill.getOwe().getId().equals(oweTo.getId()))
                .findAny()
                .orElse(Bill.builder()
                        .user(user)
                        .owe(oweTo)
                        .oweTotal(BigDecimal.valueOf(0))
                        .build()
                );
    }
}
