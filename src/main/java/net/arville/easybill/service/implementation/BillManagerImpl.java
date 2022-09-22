package net.arville.easybill.service.implementation;

import lombok.AllArgsConstructor;
import net.arville.easybill.dto.response.BillResponse;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.exception.UserNotFoundException;
import net.arville.easybill.model.User;
import net.arville.easybill.repository.BillRepository;
import net.arville.easybill.repository.UserRepository;
import net.arville.easybill.service.manager.BillManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BillManagerImpl implements BillManager {

    private final BillRepository billRepository;
    private final UserRepository userRepository;

    public UserResponse getAllBills(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        List<BillResponse> usersBill = billRepository.findAllUserBills(userId).stream()
                .map(BillResponse::map)
                .collect(Collectors.toList());

        UserResponse userResponse = UserResponse.mapWithoutDate(user);
        userResponse.setBillResponseList(usersBill);

        return userResponse;
    }
}
