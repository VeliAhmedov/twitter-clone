package com.twittvl.backend.bookmark;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
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

    //This repo will be used to get ID of bookmarked tweets for viewer state
    @Query("select b.tweet.id from Bookmark b where b.user.id = :userId and b.tweet.id in :tweetIds")
    List<Long> findBookmarkedTweetIds(@Param("userId") Long userId, @Param("tweetIds") Collection<Long> tweetIds);
}