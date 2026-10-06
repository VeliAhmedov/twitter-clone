package com.twittvl.backend.feed;

import com.twittvl.backend.tweet.TweetResponse;

import java.util.List;

public record FeedCache(
        List<TweetResponse> content,
        long totalElements
) {
}
