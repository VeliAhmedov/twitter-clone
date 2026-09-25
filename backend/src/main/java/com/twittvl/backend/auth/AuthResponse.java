package com.twittvl.backend.auth;

public record AuthResponse(String accessToken, String refreshToken, String tokenType) {
    public AuthResponse(String accessToken, String refreshToken) {
        this(accessToken, refreshToken, "tokenBearer");
    }
}
