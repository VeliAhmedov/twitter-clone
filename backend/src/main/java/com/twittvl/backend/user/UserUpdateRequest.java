package com.twittvl.backend.user;

import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record UserUpdateRequest(
        @Size(max = 150)
        String bio,
        @Size(max = 30)
        String displayName,
        @URL
        String avatarURL) {
}
