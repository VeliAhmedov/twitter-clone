package com.twittvl.backend.tweet.cache;

import com.twittvl.backend.tweet.TweetResponse;

import java.util.List;

public record FeedCache(
        List<TweetResponse> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {
}
