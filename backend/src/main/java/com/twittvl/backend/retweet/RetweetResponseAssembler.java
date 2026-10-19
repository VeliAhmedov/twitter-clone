package com.twittvl.backend.retweet;

import com.twittvl.backend.comment.CommentResponseAssembler;
import com.twittvl.backend.tweet.TweetResponse;
import com.twittvl.backend.tweet.TweetResponseAssembler;
import com.twittvl.backend.tweet.TweetViewerState;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class RetweetResponseAssembler {
    private final TweetResponseAssembler tweetResponseAssembler;
    private final CommentResponseAssembler commentResponseAssembler;
    private final TweetViewerState tweetViewerState;

    public RetweetResponseAssembler(TweetResponseAssembler tweetResponseAssembler, CommentResponseAssembler commentResponseAssembler, TweetViewerState tweetViewerState) {
        this.tweetResponseAssembler = tweetResponseAssembler;
        this.commentResponseAssembler = commentResponseAssembler;
        this.tweetViewerState = tweetViewerState;
    }

    public Page<RetweetResponse> toResponses(Page<Retweet> retweetPage) {
        Map<Long, TweetResponse> tweets = tweetViewerState.apply(
                retweetPage.stream()
                        .map(Retweet::getTweet)
                        .filter(Objects::nonNull)
                        .map(tweetResponseAssembler::toResponse)
                        .toList())
                .stream()
                .collect(Collectors.toMap(TweetResponse::id, t->t));
        return retweetPage.map(r -> new RetweetResponse(
                r.getId(),
                r.getCreatedAt(),
                r.getTweet() != null ? tweets.get(r.getTweet().getId()) : null,
                r.getComment() != null ? commentResponseAssembler.toResponse(r.getComment()) : null
        ));
    }
}
