package com.twittvl.backend.config;

import com.twittvl.backend.security.JwtAccessDeniedHandler;
import com.twittvl.backend.security.JwtAuthEntryPoint;
import com.twittvl.backend.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password4j.Argon2Password4jPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    //argon2id is used for hashing of password
    @Bean
    public PasswordEncoder passwordEncoder() {
        String encodingId = "argon2id";
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("argon2id", new Argon2Password4jPasswordEncoder());
        encoders.put("bcrypt", new BCryptPasswordEncoder(12));
        return new DelegatingPasswordEncoder(encodingId, encoders);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter,
                                                   JwtAuthEntryPoint jwtAuthEntryPoint,
                                                   JwtAccessDeniedHandler jwtAccessDeniedHandler) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) //for now my project is stateless REST working with header that is why csrf is disabled, also make app session stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth //auth rules to whom allow to where
                        .requestMatchers("/api/auth/**").permitAll() //accessible to everyone
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/webjars/**").permitAll() //to test via swagger without restrictions
                        .anyRequest().authenticated()) //other those above, everything required to be authenticated user
                .exceptionHandling(ex -> ex // we put 401 and 403 we created to use
                        .authenticationEntryPoint(jwtAuthEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); //put JwtFilter to spring security before anything else

        return http.build();
    }
}
//                HTTP REQUEST
//                      │
//                      ▼
//                jwtAuthFilter
//                      │
//              ┌───────┴───────┐
//              │               │
//          Valid JWT       No/invalid JWT
//              │               │
//              ▼               ▼
//        Authentication      401 if protected
//              │
//              ▼
//     Authorization rules
//              │
//       ┌──────┼───────────┐
//       │      │           │
//    /auth   Swagger    Other APIs
//       │      │           │
//     PUBLIC  PUBLIC    authenticated
//                          │
//                          ▼
//                       Controller
