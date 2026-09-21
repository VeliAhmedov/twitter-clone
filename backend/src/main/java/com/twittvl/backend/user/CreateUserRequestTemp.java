package com.twittvl.backend.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequestTemp(
        @NotBlank
        @Size(min = 3, max = 30)
        String username,
        @NotBlank
        @Size(min = 8, max = 50)
        String password,
        @Size(max = 150)
        String bio,
        @NotBlank
        @Size(min = 1, max = 30)
        String displayName,
        @NotBlank
        @Email
        String email
) {
}
