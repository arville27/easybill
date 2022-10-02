package net.arville.easybill.service.manager;

import net.arville.easybill.dto.request.UserLoginRequest;
import net.arville.easybill.dto.response.UserResponse;

public interface AuthManager {
    UserResponse authenticateUser(UserLoginRequest authRequest);

}
