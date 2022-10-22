package net.arville.easybill.service.manager;

import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.model.Bill;
import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.model.User;

import java.util.Set;

public interface BillManager {
    Set<Bill> generateCorrespondingBills(OrderHeader orderHeader);

    UserResponse getAllUsersBill(User user);

    UserResponse getAllUsersBillToUser(User user);
}
