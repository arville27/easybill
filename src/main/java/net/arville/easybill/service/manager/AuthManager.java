package net.arville.easybill.service.manager;

import net.arville.easybill.dto.request.UserLoginRequest;
import net.arville.easybill.service.helper.AuthenticatedUserResult;

public interface AuthManager {
    AuthenticatedUserResult authenticateUser(UserLoginRequest authRequest);

}
