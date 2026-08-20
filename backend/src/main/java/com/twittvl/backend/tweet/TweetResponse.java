package com.twittvl.backend.tweet;

import java.time.Instant;

public record TweetResponse(
        Long id,
        String content,
        String imageUrl,
        Long userId,
        String username,
        String userAvatarUrl,
        boolean edited,
        Instant createdAt
) {
}
