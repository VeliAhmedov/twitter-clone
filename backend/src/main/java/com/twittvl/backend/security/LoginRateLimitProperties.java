package com.twittvl.backend.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rate-limit.login")
public record LoginRateLimitProperties(
        int maxFailures,
        long windowSeconds
) {
}
