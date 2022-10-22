package net.arville.easybill.service.manager;

import net.arville.easybill.dto.request.PayBillRequest;
import net.arville.easybill.dto.response.BillTransactionResponse;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.model.User;

public interface BillTransactionManager {

    BillTransactionResponse payUnpaidBills(User user, PayBillRequest payBillRequest);

    UserResponse getRelevantUsersBillTransaction(User user);
}
