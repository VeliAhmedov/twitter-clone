package com.twittvl.backend.tweetLike;

import com.twittvl.backend.common.projection.IdCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TweetLikeRepository extends JpaRepository<TweetLike, Long> {
    Optional<TweetLike> findByTweetIdAndUserId(Long tweetId, Long userId);

    boolean existsByTweetIdAndUserId(Long tweetId, Long userId);

    long countByTweetId(Long tweetId);

    Page<TweetLike> findAllByTweetIdOrderByCreatedAtDesc(Long tweetId, Pageable pageable);

    //This repo will be used to get ID of liked tweets for viewer state
    @Query("select l.tweet.id from TweetLike l where l.user.id = :userId and l.tweet.id in :tweetIds")
    List<Long> findLikedTweetIds(@Param("userId") Long userId, @Param("tweetIds") Collection<Long> tweetIds);

}