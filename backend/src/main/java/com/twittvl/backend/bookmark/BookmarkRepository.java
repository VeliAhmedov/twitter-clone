package com.twittvl.backend.bookmark;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    //fix N+1 query problem
    @EntityGraph(attributePaths = {"tweet", "tweet.user", "comment", "comment.user"})
    Page<Bookmark> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    boolean existsByUserIdAndTweetId(Long userId, Long tweetId);

    boolean existsByUserIdAndCommentId(Long userId, Long commentId);

    Optional<Bookmark> findByUserIdAndTweetId(Long userId, Long tweetId);

    Optional<Bookmark> findByUserIdAndCommentId(Long userId, Long commentId);
}