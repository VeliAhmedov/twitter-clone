package com.twittvl.backend.tweet;

import java.time.Instant;

public record QuotedTweetResponse(
        Long id,
        String content,
        String imageUrl,
        Long userId,
        String username,
        String userDisplayName,
        String userAvatarUrl,
        Instant createdAt
) {}