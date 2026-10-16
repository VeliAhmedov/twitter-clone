package com.twittvl.backend.comment;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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

    //number of likes plus number of replies = relevancy
    @Query(value = """
        select c from Comment c
        where c.tweet.id = :tweetId and c.parentComment is null
        order by (select count(l) from CommentLike l where l.comment.id = c.id)
               + (select count(r) from Comment r where r.parentComment.id = c.id) desc,
               c.createdAt desc
        """,
            countQuery = "select count(c) from Comment c where c.tweet.id = :tweetId and c.parentComment is null")
    Page<Comment> findTopLevelByRelevance(@Param("tweetId") Long tweetId, Pageable pageable);

    @Query(value = """
        select c from Comment c
        where c.parentComment.id = :parentId
        order by (select count(l) from CommentLike l where l.comment.id = c.id)
               + (select count(r) from Comment r where r.parentComment.id = c.id) desc,
               c.createdAt desc
        """,
            countQuery = "select count(c) from Comment c where c.parentComment.id = :parentId")
    Page<Comment> findRepliesByRelevance(@Param("parentId") Long parentId, Pageable pageable);

}