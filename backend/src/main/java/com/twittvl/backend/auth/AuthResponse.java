package com.twittvl.backend.auth;

public record AuthResponse(String accessToken, String refreshToken, String tokenType, String role) {
    public AuthResponse(String accessToken, String refreshToken, String role) {
        this(accessToken, refreshToken, "Bearer", role);
    }
}
