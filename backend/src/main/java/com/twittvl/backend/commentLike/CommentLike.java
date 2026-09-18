package com.twittvl.backend.commentLike;

import com.twittvl.backend.comment.Comment;
import com.twittvl.backend.tweet.Tweet;
import com.twittvl.backend.tweetLike.TweetLike;
import com.twittvl.backend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "comment_likes", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "comment_id"}))
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  //this is person who liked

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment; //this is comment that is liked

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommentLike commentLike)) return false;
        return id != null && id.equals(commentLike.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
