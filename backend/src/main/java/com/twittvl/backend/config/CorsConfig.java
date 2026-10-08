package com.twittvl.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins}") List<String> allowedOrigins //read allowed frontend origins from app.yaml
    ) {
        CorsConfiguration config = new CorsConfiguration(); //object to define cors rules
        config.setAllowedOrigins(allowedOrigins); //which front origins allowed
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")); //allowed HTTP methods
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept")); //allowed headers to send
        config.setAllowCredentials(true); // needed only if the refresh token travels in a cookie (which will be added)
        config.setMaxAge(3600L); //how long to cache results of CORS preflight requests

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config); //apply CORS rules ones starting with /api which is all of them
        return source;
    }
}
/*
                 1. Frontend
                localhost:3000
                      ↓
            2. Browser CORS check
       Origin, method, headers, credentials
                      ↓
         3. Spring Boot + Spring Security
    JWT authentication and authorization still apply
                      ↓
           4. Controller and service
              Process the request
 */