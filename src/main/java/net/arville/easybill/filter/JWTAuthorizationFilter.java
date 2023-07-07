package net.arville.easybill.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.arville.easybill.exception.UnauthorizedRequestException;
import net.arville.easybill.helper.AuthenticatedUser;
import net.arville.easybill.helper.JwtUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

import static net.arville.easybill.constant.EasybillConstants.UNAUTHENTICATED_ROUTES_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class JWTAuthorizationFilter extends OncePerRequestFilter {
    private final AuthenticatedUser authenticatedUser;
    private final JwtUtils jwtUtils;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().toLowerCase();

        return UNAUTHENTICATED_ROUTES_PREFIX.stream()
                .map(prefix -> prefix.replaceAll("/\\*+", ""))
                .anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
            throw new UnauthorizedRequestException("No Authorization header present", true);

        String accessToken = authorizationHeader.substring("Bearer ".length());
        DecodedJWT decodedJWT = jwtUtils.verifyToken(accessToken);

        Long userId = decodedJWT.getClaim("user_id").asLong();

        var authToken = new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());

        authenticatedUser.setUserId(userId);

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
