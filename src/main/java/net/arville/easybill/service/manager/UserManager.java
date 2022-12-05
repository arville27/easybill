package net.arville.easybill.service.manager;

import net.arville.easybill.dto.request.UserChangeAccountNumberRequest;
import net.arville.easybill.dto.request.UserChangePasswordRequest;
import net.arville.easybill.dto.request.UserRegistrationRequest;
import net.arville.easybill.dto.response.PaginationResponse;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.model.User;

import java.util.List;

public interface UserManager {
    PaginationResponse<UserResponse> getUserRelevantOrder(User user, int pageNumber, int pageSize, String keyword, String orderStatus);

    UserResponse addNewUser(UserRegistrationRequest request);

    PaginationResponse<UserResponse> getUsersOrder(User user, int pageNumber, int pageSize, String keyword, String orderStatus);

    List<UserResponse> getAllUser();

    User getUserByUsername(String username);

    User getUserByUserId(Long userId);

    void changeUserPassword(UserChangePasswordRequest request, User authenticatedUser);

    void changeUserAccountNumber(UserChangeAccountNumberRequest request, User authenticatedUser);

}
