package com.twittvl.backend.tweetLike;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TweetLikeRepository extends JpaRepository<TweetLike, Long> {
    Optional<TweetLike> findByTweetIdAndUserId(Long tweetId, Long userId);
    boolean existsByTweetIdAndUserId(Long tweetId, Long userId);
    long countByTweetId(Long tweetId);
}
