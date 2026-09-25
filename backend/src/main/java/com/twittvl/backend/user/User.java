package com.twittvl.backend.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.validator.constraints.URL;
import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    // Twitter can only have 1 account per user, so I didn't add account
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Column(nullable = false, length = 600)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(length = 150)
    private String bio;

    @Column(nullable = false, unique = true)
    @Email
    private String email;

    @URL
    private String avatarURL;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User users)) return false;
        return id != null && id.equals(users.id);
    }
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
