package com.twittvl.backend.bookmark;

import com.twittvl.backend.comment.CommentResponse;
import com.twittvl.backend.tweet.TweetRepository;
import com.twittvl.backend.tweet.TweetResponse;

import java.time.Instant;

public record BookmarkResponse(
        Long id,
        TweetResponse tweet,
        CommentResponse comment,
        Instant createdAt
) {
}
