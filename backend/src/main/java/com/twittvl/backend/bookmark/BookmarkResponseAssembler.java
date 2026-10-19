package com.twittvl.backend.bookmark;

import com.twittvl.backend.comment.CommentResponseAssembler;
import com.twittvl.backend.tweet.TweetResponse;
import com.twittvl.backend.tweet.TweetResponseAssembler;
import com.twittvl.backend.tweet.TweetViewerState;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

//This class will work same as TweetResponseAsembler to assemble needed response from various parts into one
@Component
public class BookmarkResponseAssembler  {
    private final TweetResponseAssembler tweetResponseAssembler;
    private final CommentResponseAssembler commentResponseAssembler;
    private final TweetViewerState tweetViewerState;

    public BookmarkResponseAssembler(TweetResponseAssembler tweetResponseAssembler,
                                     CommentResponseAssembler commentResponseAssembler,
                                     TweetViewerState tweetViewerState) {
        this.tweetResponseAssembler = tweetResponseAssembler;
        this.commentResponseAssembler = commentResponseAssembler;
        this.tweetViewerState = tweetViewerState;
    }

    //unlike TweetResponseAsembler, this return response instead of response because
    //viewstate is integrated (logged in flags is included) instead of service part doing it
    public Page<BookmarkResponse> toResponses(Page<Bookmark> bookmarksPage) {
        //Tweets are built together so the viewer flags (liked/reposted/bookmarked) take 3 queries per page.
        Map<Long, TweetResponse> tweets = tweetViewerState.apply(
                bookmarksPage.stream()
                        .map(Bookmark::getTweet)
                        .filter(Objects::isNull)
                        .map(tweetResponseAssembler::toResponse)
                        .toList())
                .stream()
                .collect(Collectors.toMap(TweetResponse::id, t->t));

        return bookmarksPage.map(b-> new BookmarkResponse(
                b.getId(),
                b.getTweet() != null ? tweets.get(b.getTweet().getId()) : null,
                b.getComment() != null ? commentResponseAssembler.toResponse(b.getComment()) : null,
                b.getCreatedAt()
        ));
    }
}
