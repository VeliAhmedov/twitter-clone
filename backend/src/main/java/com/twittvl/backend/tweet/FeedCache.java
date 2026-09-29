package com.twittvl.backend.tweet;

import java.util.List;

public record FeedCache(
        List<TweetResponse> content,
        long totalElements
) {
}
//in normal circumstances cache can't seem to serialize page and my validator in Redis config only permits ones under backend
//
