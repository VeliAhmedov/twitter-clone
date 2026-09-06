package com.twittvl.backend.follow;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Integer> {
    Optional<Follow> findByTweetIdAndUserId(Long tweetId, Long userId);
    boolean existsByTweetIdAndUserId(Long tweetId, Long userId);
    long countByTweetIdAndUserId(Long tweetId, Long userId);
}
