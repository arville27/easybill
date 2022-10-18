package net.arville.easybill.service.manager;

import net.arville.easybill.dto.request.UserRegistrationRequest;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.model.User;

import java.util.List;

public interface UserManager {
    UserResponse getUserRelevantOrder(User user);

    UserResponse addNewUser(UserRegistrationRequest request);

    UserResponse getUsersOrder(User user);

    List<UserResponse> getAllUser();

    User getUserByUsername(String username);

    User getUserByUserId(Long userId);
}
