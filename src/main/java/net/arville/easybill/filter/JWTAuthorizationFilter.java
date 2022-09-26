package net.arville.easybill.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import net.arville.easybill.payload.ResponseStructure;
import net.arville.easybill.payload.helper.ResponseStatus;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@AllArgsConstructor
@Configuration
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final Jackson2ObjectMapperBuilder mapper;
    private final Algorithm algorithm;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String accessToken = authorizationHeader.substring("Bearer ".length());
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(accessToken);

                String username = decodedJWT.getSubject();
                List<GrantedAuthority> authorities = decodedJWT.getClaim("roles")
                        .asList(String.class).stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                var authToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (JWTVerificationException e) {
                var body = createResponseBody(
                        response,
                        ResponseStatus.UNAUTHORIZED,
                        HttpStatus.UNAUTHORIZED,
                        null,
                        e.getMessage()
                );
                mapper.build().writeValue(response.getWriter(), body);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                var body = createResponseBody(
                        response,
                        ResponseStatus.UNKNOWN_ERROR,
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        null
                );
                mapper.build().writeValue(response.getWriter(), body);
                return;
            }
        } else {
            var body = createResponseBody(
                    response,
                    ResponseStatus.UNAUTHORIZED,
                    HttpStatus.UNAUTHORIZED,
                    null
            );
            mapper.build().writeValue(response.getWriter(), body);
            return;
        }

        filterChain.doFilter(request, response);
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
