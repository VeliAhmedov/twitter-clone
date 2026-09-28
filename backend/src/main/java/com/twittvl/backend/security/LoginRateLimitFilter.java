package com.twittvl.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(LoginRateLimitFilter.class);

    private static final RedisScript<Long> INCR_WITH_TTL = RedisScript.of("""
            local c = redis.call('INCR', KEYS[1])
            if c == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]) end
            return c
            """, Long.class);

    private final StringRedisTemplate redis;
    private final JsonMapper jsonMapper;
    private final int maxFailures;
    private final long windowSeconds; //you can change name afterward

    public LoginRateLimitFilter(StringRedisTemplate redis, JsonMapper jsonMapper,
                                @Value("${app.rate-limit.login.max-failures:10}") int maxFailures,
                                @Value("${app.rate-limit.login.window-seconds:900}") long windowSeconds) {
        this.redis = redis;
        this.jsonMapper = jsonMapper;
        this.maxFailures = maxFailures;
        this.windowSeconds = windowSeconds;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    }
}