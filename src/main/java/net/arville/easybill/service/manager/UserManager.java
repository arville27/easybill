package net.arville.easybill.service.manager;

import net.arville.easybill.dto.request.UserRegistrationRequest;
import net.arville.easybill.dto.response.UserResponse;

import java.util.List;

public interface UserManager {
    UserResponse getUserRelevantOrder(Long userId);

    UserResponse addNewUser(UserRegistrationRequest request);

    List<UserResponse> getAllUser();
}
