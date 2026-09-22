package com.twittvl.backend.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.twittvl.backend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User recipient;  //user who get notification

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User sender; //user who do action that cause notification

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(name = "tweet_id")
    private Long tweetId;

    @Column(name = "comment_id")
    private Long commentId;

    @JsonProperty("isRead")
    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification notification)) return false;
        return id != null && id.equals(notification.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
