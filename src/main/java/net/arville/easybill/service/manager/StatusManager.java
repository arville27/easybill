package net.arville.easybill.service.manager;

import net.arville.easybill.dto.response.StatusResponse;
import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.model.Status;

import java.util.List;

public interface StatusManager {

    List<Status> createCorrespondingStatusFromOrderHeader(OrderHeader orderHeader);

    List<StatusResponse> getAllStatus();
}
