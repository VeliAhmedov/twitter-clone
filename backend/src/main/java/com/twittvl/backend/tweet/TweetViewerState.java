package com.twittvl.backend.tweet;

import com.twittvl.backend.bookmark.BookmarkRepository;
import com.twittvl.backend.retweet.RetweetRepository;
import com.twittvl.backend.security.CurrentUser;
import com.twittvl.backend.tweetLike.TweetLikeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

    //get full list of tweet IDs of liked, bookmarked, retweeted
    public List<TweetResponse> apply (List<TweetResponse> tweets) {
        Long currentUserId = CurrentUser.idOrNull();
        if (currentUserId == null || tweets.isEmpty()) return tweets;

        List<Long> ids = tweets.stream().map(TweetResponse::id).toList();
        Set<Long> liked = new HashSet<>(tweetLikeRepository.findLikedTweetIds(currentUserId, ids));
        Set<Long> retweeted = new HashSet<>(retweetRepository.findRetweetedTweetIds(currentUserId, ids));
        Set<Long> bookmarked = new HashSet<>(bookmarkRepository.findBookmarkedTweetIds(currentUserId, ids));

        return tweets.stream()
                .map(t -> t.withViewerState(liked.contains(t.id()), retweeted.contains(t.id()), bookmarked.contains(t.id())))
                .toList();
    }

    public TweetResponse apply(TweetResponse tweet) {
        return apply(List.of(tweet)).get(0);
    }

    //turn into pageable for future use of tweet service and pagination
    public Page<TweetResponse> apply(Page<TweetResponse> page) {
        return new PageImpl<>(apply(page.getContent()), page.getPageable(), page.getTotalElements());
    }
}
/*
when we like, bookmark or retweet, icons highlighted but then we refresh it will disappear

When a user likes, reposts, or bookmarks a tweet, the UI must still show the correct (filled/active)
state after a page refresh. The frontend must never be the source of truth.

1. Frontend --> (React)Renders buttons using flags returned by the API (likedByMe, retweetedByMe, bookmarkedByMe).
2. Backend --> Serves feed data + per-user flags. Handles like/repost/bookmark actions.
3. Database --> Stores the actual relationships (which user performed which action on which tweet).
4. Redis --> Caches the shared feed and counts only. Never stores user-specific flags.

 flow is like React request feed, backend first cache if not load shared tweets.
 backend then return with 3 bulk queries that has flag for like, bookmark and retweet
 which frontend checks and according to that React renders them for user

 Redis isn't option as it is used for sharing across so personal flags used

 These are all batch queries so no N+1 problem and no query per look up
 */
