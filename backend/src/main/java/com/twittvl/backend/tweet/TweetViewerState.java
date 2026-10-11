package com.twittvl.backend.tweet;

import com.twittvl.backend.bookmark.BookmarkRepository;
import com.twittvl.backend.retweet.RetweetRepository;
import com.twittvl.backend.tweetLike.TweetLikeRepository;
import org.springframework.stereotype.Component;

@Component
public class TweetViewerState {
    //Main purpose: "did the logged-in user like/repost/bookmark this" to responses, using 3 queries per page
    private final BookmarkRepository bookmarkRepository;
    private final TweetLikeRepository tweetLikeRepository;
    private final RetweetRepository retweetRepository;

    public TweetViewerState(BookmarkRepository bookmarkRepository, TweetLikeRepository tweetLikeRepository,
                            RetweetRepository retweetRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.tweetLikeRepository = tweetLikeRepository;
        this.retweetRepository = retweetRepository;
    }
}
