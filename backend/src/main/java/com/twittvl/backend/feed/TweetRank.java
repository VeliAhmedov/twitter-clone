package com.twittvl.backend.feed;

import com.twittvl.backend.tweet.Tweet;

public record TweetRank(
        Tweet tweet,
        double affinity,
        double engagement,
        double recency,
        double totalScore
) {
}
//this record class give fields to rank tweets to best suited to user