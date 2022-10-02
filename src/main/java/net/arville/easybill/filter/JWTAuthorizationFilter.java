package net.arville.easybill.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import net.arville.easybill.exception.UnauthorizedRequestException;
import net.arville.easybill.helper.AuthenticatedUser;
import net.arville.easybill.helper.JwtUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static net.arville.easybill.constant.EasybillConstants.AUTH_PATH;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@AllArgsConstructor
public class JWTAuthorizationFilter extends OncePerRequestFilter {
    private final AuthenticatedUser authenticatedUser;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals(AUTH_PATH)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
            throw new UnauthorizedRequestException();

        String accessToken = authorizationHeader.substring("Bearer ".length());
        DecodedJWT decodedJWT = jwtUtils.verifyToken(accessToken);

        String username = decodedJWT.getSubject();

        var authToken = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());

        authenticatedUser.setUserId(decodedJWT.getClaim("user_id").asLong());
        authenticatedUser.setUsername(decodedJWT.getSubject());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
