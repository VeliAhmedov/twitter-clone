package com.twittvl.backend.comment;

import java.time.Instant;

public record CommentResponse(
        Long id,
        Long tweetId,
        Long userId,
        String username,
        String userAvatarUrl,
        String content,
        String imageUrl,
        boolean edited,
        Instant createdAt
) {
}
