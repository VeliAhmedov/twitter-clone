package com.twittvl.backend.tweet;

import com.twittvl.backend.bookmark.BookmarkRepository;
import com.twittvl.backend.retweet.RetweetRepository;
import com.twittvl.backend.security.CurrentUser;
import com.twittvl.backend.tweetLike.TweetLikeRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public List<TweetResponse> apply (List<TweetResponse> tweets) {
        Long currentUserId = CurrentUser.idOrNull();
        if (currentUserId == null || tweets.isEmpty()) return tweets;

        List<Long> ids = tweets.stream().map(TweetResponse::id).toList();
        Set<Long> liked = new HashSet<>(tweetLikeRepository.f(currentUserId, ids));
        Set<Long> retweeted = new HashSet<>(retweetRepository.findRetweetedTweetIds(currentUserId, ids));
        Set<Long> bookmarked = new HashSet<>(bookmarkRepository.findBookmarkedTweetIds(currentUserId, ids));
    }
}
