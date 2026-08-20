package com.twittvl.backend.tweet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TweetRepository extends JpaRepository<Tweet, Long> {

    //via page, not all tweets will load as user scrolls down will see tweets page by page for better
    Page<Tweet> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<Tweet> findAllByOrderByCreatedAtDesc(Pageable pageable);
}