package net.arville.easybill.configuration;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.AllArgsConstructor;
import net.arville.easybill.filter.ExceptionHandlerFilter;
import net.arville.easybill.filter.JWTAuthenticationFilter;
import net.arville.easybill.filter.JWTAuthorizationFilter;
import net.arville.easybill.service.implementation.UserManagerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static net.arville.easybill.constant.EasybillConstants.AUTH_PATH;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class RestSecurityConfig {
    private final Jackson2ObjectMapperBuilder mapperBuilder;
    private final Algorithm algorithm;
    private final UserManagerImpl userDetailsService;
    private final PasswordEncoder encoder;
    private final JWTAuthorizationFilter authorizationFilter;
    private final ExceptionHandlerFilter exceptionHandlerFilter;

    @Bean(name = "corsFilter")
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);

        corsConfiguration.setAllowedOrigins(
                List.of(
                        "http://10.20.158.12:4200",
                        "http://192.168.100.96:4200",
                        "http://localhost:4200"
                )
        );

        corsConfiguration.setAllowedHeaders(
                List.of(
                        HttpHeaders.ORIGIN,
                        HttpHeaders.ACCEPT,
                        HttpHeaders.CONTENT_TYPE,
                        HttpHeaders.AUTHORIZATION,
                        HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                        HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD,
                        HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS
                )
        );

        corsConfiguration.setExposedHeaders(
                List.of(
                        HttpHeaders.ORIGIN,
                        HttpHeaders.ACCEPT,
                        HttpHeaders.CONTENT_TYPE,
                        HttpHeaders.AUTHORIZATION,
                        HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                        HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD,
                        HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS
                )
        );

        corsConfiguration.setAllowedMethods(
                List.of(
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.DELETE.name(),
                        HttpMethod.OPTIONS.name()
                )
        );

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationManager authManager,
            CorsConfigurationSource corsFilter
    ) throws Exception {
        var authFilter = new JWTAuthenticationFilter(authManager, mapperBuilder, algorithm, userDetailsService);
        authFilter.setFilterProcessesUrl(AUTH_PATH);

        http
                .cors(httpSecurityCorsConfigurer ->
                        httpSecurityCorsConfigurer.configurationSource(corsFilter)
                )
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) -> requests.anyRequest().authenticated())
                .addFilterBefore(exceptionHandlerFilter, LogoutFilter.class)
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilter(authFilter);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(encoder);
        return authenticationManagerBuilder.build();
    }
}
