package com.twittvl.backend.comment;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    //exclude replies to comment, only direct comment shown
    Page<Comment> findAllByTweetIdAndParentCommentIsNullOrderByCreatedAtDesc(Long tweetId, Pageable pageable);
    Page<Comment> findAllByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<Comment> findAllByParentCommentIdOrderByCreatedAtDesc(Long parentCommentId, Pageable pageable);
    long countByTweetIdAndParentCommentIsNull(Long tweetId);
    long countByParentCommentId(Long parentCommentId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.recipient.id = :userId AND n.isRead = false")
    int markAllAsReadByRecipientId(@Param("userId") Long userId);
}
