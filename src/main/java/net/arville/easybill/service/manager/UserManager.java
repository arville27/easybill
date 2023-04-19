package net.arville.easybill.service.manager;

import net.arville.easybill.dto.request.UserChangePasswordRequest;
import net.arville.easybill.dto.request.UserChangeUsernameRequest;
import net.arville.easybill.dto.request.UserRegistrationRequest;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.model.User;

import java.util.List;

public interface UserManager {

    UserResponse addNewUser(UserRegistrationRequest request);

    List<UserResponse> getAllUser();

    User getUserByUsername(String username);

    User getUserByUserId(Long userId);

    void changeUserPassword(UserChangePasswordRequest request, User authenticatedUser);

    void changeUserUsername(UserChangeUsernameRequest request, User authenticatedUser);
}
