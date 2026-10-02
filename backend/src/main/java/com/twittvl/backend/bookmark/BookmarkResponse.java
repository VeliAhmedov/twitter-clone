package com.twittvl.backend.bookmark;

import java.time.Instant;

public record BookmarkResponse(
        Long id,
        Long tweetId,
        Long commentId,
        String username,
        String displayName,
        String content,
        String imageUrl,
        Instant createdAt
) {
}
