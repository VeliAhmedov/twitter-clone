package com.twittvl.backend.bookmark;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Page<Bookmark> findByUserId(Long userId, Pageable pageable);
    boolean existByUserIdAndTweetId(Long userId, Long tweetId);
    boolean existByUserIdAndCommentId(Long userId, Long commentId);
    Optional<Bookmark> findByUserIdAndTweetId(Long userId, Long tweetId);
    Optional<Bookmark> findByUserIdAndCommentId(Long userId, Long commentId);
    void deleteByUserIdAndTweetId(Long userId, Long tweetId);
    void deleteByUserIdAndCommentId(Long userId, Long commentId);
}