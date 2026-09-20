package com.twittvl.backend.tweet;

import com.twittvl.backend.comment.CommentResponse;

import java.time.Instant;

public record TweetResponse(
        Long id,
        String content,
        String imageUrl,
        Long userId,
        String username,
        String userAvatarUrl,
        long likeCount,
        boolean edited,
        Instant createdAt
) {
    public TweetResponse withLikeCount(long likeCount) {
        return new TweetResponse(id, content, imageUrl, userId, username,
                userAvatarUrl, likeCount, edited, createdAt);
    } // copy existing fields on new record to keep count updated simpler without too much boilerplate in service
}
