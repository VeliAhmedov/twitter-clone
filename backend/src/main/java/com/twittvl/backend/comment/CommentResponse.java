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
        long retweetCount,
        boolean likedByMe,
        boolean retweetedByMe,
        boolean bookmarkedByMe,
        boolean edited,
        Instant createdAt
) {
    public CommentResponse withCounts(long likeCount, long replyCount, long retweetCount) {
        return new CommentResponse(id, tweetId, userId, parentCommentId, username, displayName,
                userAvatarUrl, content, imageUrl, likeCount, replyCount, retweetCount, likedByMe,
                retweetedByMe, bookmarkedByMe, edited, createdAt);
    } // copy existing fields on new record to keep count updated simpler without too much boilerplate in service

    public CommentResponse withLikedByMe(boolean likedByMe, boolean retweetedByMe, boolean bookmarkedByMe) {
        return new CommentResponse(id, tweetId, userId, parentCommentId, username, displayName,
                userAvatarUrl, content, imageUrl, likeCount, replyCount, retweetCount, likedByMe,
                retweetedByMe, bookmarkedByMe, edited, createdAt);
    }
}
