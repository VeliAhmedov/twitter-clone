package com.twittvl.backend.security;

import com.twittvl.backend.common.exception.ApiErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.json.JsonMapper;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(LoginRateLimitFilter.class);

    //increment count of failure attempt with expire
    private static final RedisScript<Long> INCR_WITH_TTL = RedisScript.of("""
            local c = redis.call('INCR', KEYS[1])
            if c == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]) end
            return c
            """, Long.class); //Lua script used for atomicity as INCR and EXPIRE can cause race condition

    private final StringRedisTemplate redis;
    private final JsonMapper jsonMapper; //used for converting apierror to json
    private final LoginRateLimitProperties loginRateLimitProperties;

    public LoginRateLimitFilter(StringRedisTemplate redis, JsonMapper jsonMapper, LoginRateLimitProperties loginRateLimitProperties) {
        this.redis = redis;
        this.jsonMapper = jsonMapper;
        this.loginRateLimitProperties = loginRateLimitProperties;
    }

    protected boolean shouldNotFilter(HttpServletRequest request) {
        //filter is only used for this operation, others are free of it
        return !("POST".equalsIgnoreCase(request.getMethod()) && request.getRequestURI().endsWith("/login"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String key = "ratelimit:login:" + request.getRemoteAddr();

        if (isBlocked(key)) {
            filterChain.doFilter(request, response);
        }

        filterChain.doFilter(request, response);

        if (response.getStatus() == HttpStatus.UNAUTHORIZED.value()) {
            recordFailure(key);
        }
    }

    private boolean isBlocked(String key) {
        try {
            String value = redis.opsForValue().get(key);
            return value != null && Long.parseLong(value) >= loginRateLimitProperties.maxFailures();
        } catch (Exception e) {
            log.warn("Rate limit check has failed, allwoing request: {}", e.getMessage());
            return false; //fail open
        } //this can be improved
    }

    private void recordFailure(String key) {
        try {
            redis.execute(INCR_WITH_TTL, List.of(key), String.valueOf(loginRateLimitProperties.windowSeconds()));
        } catch (Exception e) {
            log.warn("Rate limit check has failed : {}", e.getMessage());
        }
    }

    private void reject(HttpServletRequest request, HttpServletResponse response, String key) throws IOException {
        Long retryAfter = redis.getExpire(key);
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Retry-After", String.valueOf(retryAfter != null && retryAfter > 0 ? retryAfter : loginRateLimitProperties.windowSeconds()));
        ApiErrorResponse limitError = new ApiErrorResponse(HttpStatus.TOO_MANY_REQUESTS.value(), HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(),
                "too many failed login attempts, try again later", request.getRequestURI(), Instant.now());
        response.getWriter().write(jsonMapper.writeValueAsString(limitError));
    }
}
/*
          POST /api/auth/login
                  ↓
           LoginRateLimitFilter
                  ↓
         Is this IP already blocked?
                   │
               ┌───┴────┐
               │        │
              YES       NO
               │        │
              429       ↓
                      Login
                        ↓
                   Was login 401?
                     │       │
                    YES      NO
                     │       │
                 increment  nothing
 */