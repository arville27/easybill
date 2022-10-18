package net.arville.easybill.service.manager;

import net.arville.easybill.dto.request.PayBillRequest;
import net.arville.easybill.dto.response.BillTransactionResponse;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.model.Status;
import net.arville.easybill.model.User;

import java.util.List;

public interface StatusManager {
    List<Status> createCorrespondingStatusFromOrderHeader(OrderHeader orderHeader);

    UserResponse getAllUsersBill(User user);

    UserResponse getAllUsersBillToUser(User user);

    BillTransactionResponse payUnpaidStatus(User user, PayBillRequest payBillRequest);
}
