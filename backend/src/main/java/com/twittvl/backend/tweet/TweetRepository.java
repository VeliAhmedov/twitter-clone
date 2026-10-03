package com.twittvl.backend.tweet;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {

    //via page, not all tweets will load as user scrolls down will see tweets page by page for better
    @EntityGraph(attributePaths = {"user", "quotedTweet", "quotedTweet.user"})
    Page<Tweet> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "quotedTweet", "quotedTweet.user"})
    Page<Tweet> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"user", "quotedTweet", "quotedTweet.user"})
    Page<Tweet> findByQuotedTweetIdOrderByCreatedAtDesc(Long quotedTweetId, Pageable pageable);
}