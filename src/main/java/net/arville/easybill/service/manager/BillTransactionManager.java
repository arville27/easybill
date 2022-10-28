package net.arville.easybill.service.manager;

import net.arville.easybill.dto.request.PayBillRequest;
import net.arville.easybill.dto.response.BillTransactionResponse;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.model.Bill;
import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface BillTransactionManager {

    BillTransactionResponse payUnpaidBills(User user, PayBillRequest payBillRequest);

    UserResponse getRelevantUsersBillTransaction(User user);

    BigDecimal getTotalOweAmountFromBills(List<Bill> unpaidBills);

    void automaticCalculateRelaterUserBills(OrderHeader savedOrderHeader);
}
