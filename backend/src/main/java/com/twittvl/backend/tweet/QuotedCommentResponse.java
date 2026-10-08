package com.twittvl.backend.tweet;

import java.time.Instant;

public record QuotedCommentResponse(
        Long id,
        Long tweetId,
        String content,
        String imageUrl,
        Long userId,
        String username,
        String userDisplayName,
        String userAvatarUrl,
        Instant createdAt
) {
}
