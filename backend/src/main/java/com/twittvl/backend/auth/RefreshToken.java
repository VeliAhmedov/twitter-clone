package com.twittvl.backend.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 64)
    private String tokenHash;

    @Column(nullable = false)
    private Instant expiredAt;

    @Column(nullable = false)
    private boolean revoked =false;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RefreshToken tokens)) return false;
        return id != null && id.equals(tokens.id);
    }
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
