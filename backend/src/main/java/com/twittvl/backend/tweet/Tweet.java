package com.twittvl.backend.tweet;

import com.twittvl.backend.like.Like;
import com.twittvl.backend.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comments;
import org.hibernate.validator.constraints.URL;

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
    private User user;

    @Column(length = 200)
    private String content;

    @URL
    private String imageUrl;

    private Boolean edited = false;

    @Column(nullable = false)
    private boolean active = true;

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
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tweet)) {
            return false;
        }
        Tweet tweets = (Tweet) o;
        return id != null && id.equals(tweets.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
