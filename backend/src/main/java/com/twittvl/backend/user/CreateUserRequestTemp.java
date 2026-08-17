package com.twittvl.backend.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequestTemp(
        @NotBlank
        @Size(min = 3, max = 30)
        String username,
        @NotBlank
        @Size(min = 8, max = 50)
        String password,
        @NotBlank
        @Size(min = 1, max = 30)
        String displayName,
        @NotBlank
        String email
) {
}
