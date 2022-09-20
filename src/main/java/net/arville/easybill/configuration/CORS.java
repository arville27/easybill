package net.arville.easybill.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CORS {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);

        corsConfiguration.setAllowedOrigins(
                List.of(
                    "http://10.20.158.12:4200",
                    "http://localhost:4200"
                )
        );

        corsConfiguration.setAllowedHeaders(
                List.of(
                    "Origin",
                    "Access-Control-Allow-Origin",
                    "Content-Type",
                    "Accept",
                    "Authorization",
                    "Origin, Accept",
                    "X-Requested-With",
                    "Access-Control-Request-Method",
                    "Access-Control-Request-Headers"
                )
        );

        corsConfiguration.setExposedHeaders(
                List.of(
                    "Origin",
                    "Content-Type",
                    "Accept",
                    "Authorization",
                    "Access-Control-Allow-Origin",
                    "Access-Control-Allow-Origin",
                    "Access-Control-Allow-Credentials"
                )
        );

        corsConfiguration.setAllowedMethods(
                List.of(
                    "GET",
                    "POST",
                    "PUT",
                    "DELETE",
                    "OPTIONS"
                )
        );

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}