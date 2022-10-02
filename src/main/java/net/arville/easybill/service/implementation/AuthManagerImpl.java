package net.arville.easybill.service.implementation;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.arville.easybill.dto.request.UserLoginRequest;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.exception.InvalidCredentialsException;
import net.arville.easybill.exception.MissingRequiredPropertiesException;
import net.arville.easybill.exception.UserNotFoundException;
import net.arville.easybill.helper.JwtUtils;
import net.arville.easybill.repository.UserRepository;
import net.arville.easybill.service.manager.AuthManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Data
@Service
public class AuthManagerImpl implements AuthManager {
    private final UserRepository userRepository;
    private final org.springframework.security.authentication.AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public UserResponse authenticateUser(UserLoginRequest authRequest) throws AuthenticationException {
        var missingProperties = authRequest.getMissingProperties();

        if (missingProperties.size() > 0) {
            throw new MissingRequiredPropertiesException(missingProperties);
        }

        // TODO: Optimize login query, with current setup, this service and custom user details service will perform same query.
        var user = userRepository
                .findUserByUsername(authRequest.getUsername())
                .orElseThrow(() -> new UserNotFoundException(authRequest.getUsername()));

        var authToken = new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());

        try {
            authenticationManager.authenticate(authToken);
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtUtils.createToken(user);

        UserResponse authResponse = UserResponse.mapWithoutDate(user);
        authResponse.setAccessToken(accessToken);

        return authResponse;
    }
}

