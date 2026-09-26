package com.twittvl.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twittvl.backend.common.exception.ApiErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import java.io.IOException;
import java.time.Instant;

//Verifies that the authenticated user possesses the required role for this route;
// otherwise, access is forbidden.
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ApiErrorResponse forbiddenError = new ApiErrorResponse(
                HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                "You don't have permission to access to this source", request.getRequestURI(), Instant.now());
        response.getWriter().write(mapper.writeValueAsString(forbiddenError));
    }
}
//
