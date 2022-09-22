package net.arville.easybill.service.manager;

import net.arville.easybill.dto.response.UserResponse;

public interface BillManager {

    UserResponse getAllBills(Long userId);
}
