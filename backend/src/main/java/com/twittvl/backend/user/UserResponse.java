package com.twittvl.backend.user;
import java.time.Instant;

public record UserResponse(
        Long id,
        String email,
        String username,
        String displayName,
        String avatarURL,
        Instant createdAdd) {
}
