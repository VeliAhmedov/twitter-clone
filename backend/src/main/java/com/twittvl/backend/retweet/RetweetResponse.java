package com.twittvl.backend.retweet;

import java.time.Instant;

public record RetweetResponse(
        Long id,
        Instant repostedAt,
        Long tweetId,
        Long comment
) {
}
