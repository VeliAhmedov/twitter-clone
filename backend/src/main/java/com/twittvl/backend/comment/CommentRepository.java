package com.twittvl.backend.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    //exclude replies to comment, only direct comment shown
    Page<Comment> findAllByTweetIdAndParentCommentIsNullOrderByCreatedAtDesc(Long tweetId, Pageable pageable);
    Page<Comment> findAllByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<Comment> findAllByParentCommentIdOrderByCreatedAtDesc(Long parentCommentId, Pageable pageable);
    long countByTweetIdAndParentCommentIsNull(Long tweetId);
}
