package com.twittvl.backend.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByTweetIdOrderByCreatedAtDesc(Long tweetId, Pageable pageable);
    Page<Comment> findAllByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
