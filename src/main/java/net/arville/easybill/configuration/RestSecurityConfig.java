package net.arville.easybill.configuration;

import net.arville.easybill.filter.ExceptionHandlerFilter;
import net.arville.easybill.filter.JWTAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static net.arville.easybill.constant.EasybillConstants.ALLOWED_ORIGINS;
import static net.arville.easybill.constant.EasybillConstants.UNAUTHENTICATED_ROUTES_PREFIX;

@Configuration
@EnableWebSecurity
public class RestSecurityConfig {
    @Bean(name = "corsFilter")
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);

        corsConfiguration.setAllowedOrigins(ALLOWED_ORIGINS);

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
            CorsConfigurationSource corsFilter,
            JWTAuthorizationFilter authorizationFilter,
            ExceptionHandlerFilter exceptionHandlerFilter
    ) throws Exception {
        http
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsFilter))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionConfigurer -> sessionConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(exceptionHandlerFilter, LogoutFilter.class)
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> requests.antMatchers(UNAUTHENTICATED_ROUTES_PREFIX.toArray(String[]::new)).permitAll())
                .authorizeHttpRequests((requests) -> requests.anyRequest().authenticated());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http, PasswordEncoder encoder, UserDetailsService userDetailsService) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(encoder);
        return authenticationManagerBuilder.build();
    }

}
