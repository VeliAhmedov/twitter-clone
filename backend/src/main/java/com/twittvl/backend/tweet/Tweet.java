package com.twittvl.backend.tweet;

import com.twittvl.backend.comment.Comment;
import com.twittvl.backend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Table(name = "tweets")
@Entity
@Getter
@Setter
public class Tweet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    //this is tweet we quoted, if person created that tweet we quoted, it is set to null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quoted_tweet_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Tweet quotedTweet;

    //this is comment/reply we quoted, if person created that comment/reply we quoted, it is set to null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quoted_comment_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Comment quotedComment;

    @Column(length = 280)
    private String content;

    private String imageUrl;

    private boolean edited = false;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tweet tweets)) return false;
        return id != null && id.equals(tweets.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

//there are different types of tweet:
//  1.normal tweet
//    quotedTweet = null
//    quotedComment = null
//
//  2.quote tweet
//    quotedTweet = Tweet
//    quotedComment = null
//
//  3.quote comment
//    quotedTweet = null
//    quotedComment = Comment

//condition inside db for condition that either one of them or both must be null, both can't have values
//ALTER TABLE tweets
//ADD CONSTRAINT tweet_single_quote_check
//CHECK (quoted_tweet_id IS NULL OR quoted_comment_id IS NULL);