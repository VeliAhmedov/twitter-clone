package com.twittvl.backend.retweet;

import com.twittvl.backend.comment.Comment;
import com.twittvl.backend.comment.CommentResponse;
import com.twittvl.backend.tweet.Tweet;
import com.twittvl.backend.tweet.TweetResponse;

import java.time.Instant;

public record RetweetResponse(
        Long id,
        Instant repostedAt,
        TweetResponse tweet,                //have value -> retweet of tweet
        CommentResponse comment             //have value -> retweet of comment/reply
) {
}
//neither tweetId nor commentId can be both null or have value. only one can have value and other null