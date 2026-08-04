package com.twittvl.backend.user;

import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record UserUpdateRequest(
        @Size(max = 160)
        String bio,
        @Size(max = 30)
        String displayName,
        @URL
        String avatarURL) {
}
