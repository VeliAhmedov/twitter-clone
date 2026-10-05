package com.twittvl.backend.retweet;

import com.twittvl.backend.tweet.Tweet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RetweetRepository extends JpaRepository<Retweet, Long> {
    //get all retweets of user
    @EntityGraph(attributePaths = {"tweet", "tweet.user",
            "tweet.quotedTweet", "tweet.quotedTweet.user",
            "tweet.quotedComment", "tweet.quotedComment.user", "tweet.quotedComment.tweet",
            "comment", "comment.user", "comment.tweet", "comment.parentComment"})
    Page<Retweet> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    boolean existsByUserIdAndTweetId(Long userId, Long tweetId);
    boolean existsByUserIdAndCommentId(Long userId, Long commentId);
    Optional<Retweet> findByUserIdAndTweetId(Long userId, Long tweetId);
    Optional<Retweet> findByUserIdAndCommentId(Long userId, Long commentId);
}
