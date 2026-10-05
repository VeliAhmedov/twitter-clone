package com.twittvl.backend.retweet;

import java.time.Instant;

public record RetweetResponse(
        Long id,
        Instant repostedAt,
        Long tweetId,                //have value -> retweet of tweet
        Long commentId                 //have value -> retweet of comment/reply
) {
}
//neither tweetId nor commentId can be both null or have value. only one can have value and other null