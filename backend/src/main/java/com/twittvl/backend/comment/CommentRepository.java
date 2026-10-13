package com.twittvl.backend.comment;

import com.twittvl.backend.common.projection.IdCount;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    //exclude replies to comment, only direct comment shown
    Page<Comment> findAllByTweetIdAndParentCommentIsNullOrderByCreatedAtDesc(Long tweetId, Pageable pageable);
    Page<Comment> findAllByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<Comment> findAllByParentCommentIdOrderByCreatedAtDesc(Long parentCommentId, Pageable pageable);
    long countByTweetIdAndParentCommentIsNull(Long tweetId);
    long countByParentCommentId(Long parentCommentId);

    //comment that is retweeted by user and comment
    @Query("select count(r) > 0 from Retweet r where r.userId = :userId and r.commentId = :commentId")
    boolean isCommentRetweetedBy(@Param("userId") Long userId, @Param("commentId") Long commentId);
}
