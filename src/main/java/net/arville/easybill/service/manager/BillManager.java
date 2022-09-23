package net.arville.easybill.service.manager;

import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.model.Bill;
import net.arville.easybill.model.OrderHeader;

import java.util.List;

public interface BillManager {

    UserResponse getAllBills(Long userId);

    List<Bill> generateBillsFromOrderHeader(OrderHeader orderHeader);
}
