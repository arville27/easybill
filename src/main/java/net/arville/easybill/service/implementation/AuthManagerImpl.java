package net.arville.easybill.service.implementation;

import lombok.RequiredArgsConstructor;
import net.arville.easybill.dto.request.UserLoginRequest;
import net.arville.easybill.exception.InvalidCredentialsException;
import net.arville.easybill.exception.MissingRequiredPropertiesException;
import net.arville.easybill.helper.JwtUtils;
import net.arville.easybill.service.helper.AuthenticatedUserResult;
import net.arville.easybill.service.manager.AuthManager;
import net.arville.easybill.service.manager.UserManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthManagerImpl implements AuthManager {
    private final UserManager userManager;
    private final org.springframework.security.authentication.AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthenticatedUserResult authenticateUser(UserLoginRequest authRequest) throws AuthenticationException {
        var missingProperties = authRequest.getMissingProperties();

        if (missingProperties.size() > 0) {
            throw new MissingRequiredPropertiesException(missingProperties);
        }

        // TODO: Optimize login query, with current setup, this service and custom user details service will perform same query.
        var user = userManager.getUserByUsername(authRequest.getUsername());

        var authToken = new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());

        try {
            authenticationManager.authenticate(authToken);
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtUtils.createToken(user);

        return AuthenticatedUserResult.builder()
                .user(user)
                .accessToken(accessToken)
                .build();
    }
}

