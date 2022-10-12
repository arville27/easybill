package net.arville.easybill.service.implementation;

import lombok.AllArgsConstructor;
import net.arville.easybill.dto.response.StatusResponse;
import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.model.Status;
import net.arville.easybill.model.User;
import net.arville.easybill.model.helper.BillStatus;
import net.arville.easybill.repository.StatusRepository;
import net.arville.easybill.service.manager.StatusManager;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StatusManagerImpl implements StatusManager {
    private final StatusRepository statusRepository;

    public List<Status> createCorrespondingStatusFromOrderHeader(OrderHeader orderHeader) {
        Set<User> participatedUser = new HashSet<>();
        orderHeader.getOrderDetailList().forEach(order -> participatedUser.add(order.getUser()));

        var status = participatedUser.stream()
                .map(user -> Status.builder()
                        .orderHeader(orderHeader)
                        .status(BillStatus.UNPAID)
                        .user(user)
                        .build()
                )
                .collect(Collectors.toList());

        return statusRepository.saveAll(status);
    }

    public List<StatusResponse> getAllStatus() {
        return statusRepository.findAll().stream()
                .map(StatusResponse::map)
                .collect(Collectors.toList());
    }

}
