package com.twittvl.backend.tweet;

import java.time.Instant;

public record TweetResponse(
        Long id,
        String content,
        String imageUrl,
        QuotedTweetResponse quotedTweet,
        QuotedCommentResponse quotedComment,
        Long userId,
        String username,
        String userDisplayName,
        String userAvatarUrl,
        long likeCount,
        long commentCount,
        boolean edited,
        boolean quoteUnavailable,
        Instant createdAt
) {
    public TweetResponse withCounts(long likeCount, long commentCount) {
        return new TweetResponse(id, content, imageUrl, quotedTweet, quotedComment, userId, username,
                userDisplayName, userAvatarUrl, likeCount, commentCount, edited, quoteUnavailable, createdAt);
    } // copy existing fields on new record to keep count updated simpler without too much boilerplate in service
}
