package com.twittvl.backend.retweet;

import com.twittvl.backend.comment.Comment;
import com.twittvl.backend.notification.Notification;
import com.twittvl.backend.tweet.Tweet;
import com.twittvl.backend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Instant;

@Entity
@Setter
@Getter
@Table(name = "retweets", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "tweet_id"}),
        @UniqueConstraint(columnNames = {"user_id", "comment_id"})
})
public class Retweet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tweet_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Tweet tweet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comment comment;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Retweet retweet)) return false;
        return id != null && id.equals(retweet.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
