package net.arville.easybill.service.manager;

import net.arville.easybill.dto.request.UserRegistrationRequest;
import net.arville.easybill.dto.response.PaginationResponse;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.model.User;

import java.util.List;

public interface UserManager {
    PaginationResponse<UserResponse> getUserRelevantOrder(User user, int pageNumber, int pageSize);

    UserResponse addNewUser(UserRegistrationRequest request);

    PaginationResponse<UserResponse> getUsersOrder(User user, int pageNumber, int pageSize);

    List<UserResponse> getAllUser();

    User getUserByUsername(String username);

    User getUserByUserId(Long userId);
}
