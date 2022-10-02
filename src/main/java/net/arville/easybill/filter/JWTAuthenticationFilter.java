package net.arville.easybill.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.arville.easybill.dto.request.UserLoginRequest;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.exception.MissingRequiredPropertiesException;
import net.arville.easybill.exception.UserNotFoundException;
import net.arville.easybill.payload.ResponseStructure;
import net.arville.easybill.payload.helper.ResponseStatus;
import net.arville.easybill.service.manager.UserManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@AllArgsConstructor
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final Jackson2ObjectMapperBuilder mapperBuilder;
    private final Algorithm algorithm;
    private final UserManager userManager;

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        User userPrincipal = (User) authResult.getPrincipal();

        var user = userManager.getUserByUser(userPrincipal.getUsername());

        String accessToken = JWT.create()
                .withClaim("user_id", user.getId())
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim(
                        "roles",
                        userPrincipal
                                .getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList())
                )
                .sign(algorithm);

        var tokenResponse = UserResponse.mapWithoutDate(user);
        tokenResponse.setAccessToken(accessToken);
        ResponseStructure body = createResponseBody(
                response,
                ResponseStatus.SUCCESS,
                HttpStatus.OK,
                tokenResponse
        );
        mapperBuilder.build().writeValue(response.getWriter(), body);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        ResponseStructure body;
        if (failed.getCause() instanceof UserNotFoundException) {
            body = createResponseBody(response, ResponseStatus.USER_NOT_FOUND, HttpStatus.NOT_FOUND, null, failed.getMessage());
        } else {
            body = createResponseBody(response, ResponseStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED, null, failed.getMessage());
        }
        mapperBuilder.build().writeValue(response.getWriter(), body);
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UserLoginRequest authRequest = mapperBuilder.build().readValue(request.getInputStream(), UserLoginRequest.class);

        var missingProperties = authRequest.getMissingProperties();

        if (missingProperties.size() > 0) {
            throw new MissingRequiredPropertiesException(missingProperties);
        }

        var authToken = new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());

        Authentication auth = null;

        // UserNotFoundException can only be caught in the attempt authentication method, otherwise it will output the stacktrace to stdout
        try {
            auth = authenticationManager.authenticate(authToken);
        } catch (AuthenticationException e) {
            if (e.getCause() instanceof UserNotFoundException) {
                var body = createResponseBody(
                        response,
                        ResponseStatus.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        null,
                        e.getMessage()
                );
                mapperBuilder.build().writeValue(response.getWriter(), body);
            } else {
                throw e;
            }
        }

        return auth;
    }

    private ResponseStructure createResponseBody(HttpServletResponse response, ResponseStatus status, HttpStatus statusCode, Object data) {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(statusCode.value());
        return status.GenerateGeneralBody(data);
    }

    private ResponseStructure createResponseBody(HttpServletResponse response, ResponseStatus status, HttpStatus statusCode, Object data, String extraMessage) {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(statusCode.value());
        return status.GenerateGeneralBody(data, extraMessage);
    }
}