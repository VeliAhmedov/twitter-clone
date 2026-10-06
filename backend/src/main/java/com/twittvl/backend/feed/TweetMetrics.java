package com.twittvl.backend.feed;

public record TweetMetrics(
        long likes,
        long comments,
        long retweets
) {
}

//this record give field needed for algorithm for metric for  tweets
