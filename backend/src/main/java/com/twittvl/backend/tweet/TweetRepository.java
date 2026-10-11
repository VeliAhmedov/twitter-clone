package com.twittvl.backend.tweet;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {

    //via page, not all tweets will load as user scrolls down will see tweets page by page for better
    @EntityGraph(attributePaths = {"user", "quotedTweet", "quotedTweet.user","quotedComment", "quotedComment.user", "quotedComment.tweet"})
    Page<Tweet> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "quotedTweet", "quotedTweet.user","quotedComment", "quotedComment.user", "quotedComment.tweet"})
    Page<Tweet> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"user", "quotedTweet", "quotedTweet.user"})
    Page<Tweet> findByQuotedTweetIdOrderByCreatedAtDesc(Long quotedTweetId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "quotedComment", "quotedComment.user", "quotedComment.tweet"})
    Page<Tweet> findByQuotedCommentIdOrderByCreatedAtDesc(Long quotedCommentId, Pageable pageable);

    //Those 3 repo will be used to get ID of liked, bookmarked and retweeted tweets for viewer state
    //(:tweetIds is param we will give)
    @Query("select l.tweet.id from TweetLike l where l.user.id = :userId and l.tweet.id in :tweetIds")
    List<Long> findLikedTweetIds(@Param("userId") Long userId, @Param("tweetIds") Collection<Long> tweetIds);

    @Query("select r.tweet.id from Retweet r where r.user.id = :userId and r.tweet.id in :tweetIds")
    List<Long> findRetweetedTweetIds(@Param("userId") Long userId, @Param("tweetIds") Collection<Long> tweetIds);

    @Query("select b.tweet.id from Bookmark b where b.user.id = :userId and b.tweet.id in :tweetIds")
    List<Long> findBookmarkedTweetIds(@Param("userId") Long userId, @Param("tweetIds") Collection<Long> tweetIds);

}