package com.twittvl.backend.tweet;

import java.time.Instant;

public record TweetResponse(
        Long id,
        String content,
        String imageUrl,
        Long userId,
        String username,
        String userAvatarUrl,
        long likeCount,
        long commentCount,
        boolean edited,
        Instant createdAt
) {
    public TweetResponse withCounts(long likeCount, long commentCount) {
        return new TweetResponse(id, content, imageUrl, userId, username,
                userAvatarUrl, likeCount, commentCount, edited, createdAt);
    } // copy existing fields on new record to keep count updated simpler without too much boilerplate in service
}
