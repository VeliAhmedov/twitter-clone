package com.twittvl.backend.bookmark;

import java.time.Instant;

public record BookmarkResponse(
        Long id,
        Long twitterId,
        Long commentId,
        Instant createdAt
) {
}
