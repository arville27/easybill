package net.arville.easybill.service.manager;

import net.arville.easybill.dto.response.BillResponse;
import net.arville.easybill.model.Bill;
import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.model.User;

import java.util.Map;
import java.util.Set;

public interface BillManager {
    Set<Bill> generateCorrespondingBills(OrderHeader orderHeader);

    Map<User, BillResponse.AggregatedRelatedOrderWithTotalOwe> getUserPayables(User user);

    Map<User, BillResponse.AggregatedRelatedOrderWithTotalOwe> getUserReceivables(User user);
}
