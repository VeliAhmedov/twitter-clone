package com.twittvl.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey secretKey;
    private final long accessTokenExpirationMs;
    public JwtUtil(JwtProperties jwtProperties) {
        byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.secret());
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpirationMs = jwtProperties.accessTokenExpirationMs();
    }

    public String generateAccessToken(Long userId, String userName, String role) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + accessTokenExpirationMs);
        return  Jwts.builder()
                .subject(userName)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }
    public Claims parseAndValidateAccessToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public boolean isTokenValid(String token) {
        try {
            parseAndValidateAccessToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }


}
