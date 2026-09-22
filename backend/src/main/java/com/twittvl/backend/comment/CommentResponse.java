package com.twittvl.backend.comment;

import java.time.Instant;

public record CommentResponse(
        Long id,
        Long tweetId,
        Long userId,
        Long parentCommentId,
        String username,
        String displayName,
        String userAvatarUrl,
        String content,
        String imageUrl,
        long likeCount,
        long replyCount,
        boolean edited,
        Instant createdAt
) {
    public CommentResponse withLikeCount(long likeCount, long replyCount) {
        return new CommentResponse(id, tweetId, userId, parentCommentId, username, displayName,
                userAvatarUrl, content, imageUrl, likeCount, replyCount, edited, createdAt);
    } // copy existing fields on new record to keep count updated simpler without too much boilerplate in service
}
