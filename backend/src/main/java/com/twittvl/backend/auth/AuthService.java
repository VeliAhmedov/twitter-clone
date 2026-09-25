package com.twittvl.backend.auth;

import com.twittvl.backend.security.JwtProperties;
import com.twittvl.backend.user.User;
import com.twittvl.backend.user.UserMapper;
import com.twittvl.backend.user.UserRepository;
import com.twittvl.backend.user.UserResponse;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;
    private final SecureRandom secureRandom = new SecureRandom();
    public AuthService(UserRepository userRepository, UserMapper userMapper,
                       PasswordEncoder passwordEncoder, RefreshTokenRepository refreshTokenRepository,
                       JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
    }

    public UserResponse register (RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.username())){
            throw new IllegalArgumentException("Username is already in use");
        }
        if (userRepository.existsByEmail(registerRequest.email())){
            throw new IllegalArgumentException("Email is already in use");
        }
        User user = new User();
        user.setUsername(registerRequest.username());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setDisplayName(registerRequest.displayName());
        user.setEmail(registerRequest.email());
        user.setBio(registerRequest.bio());

        return userMapper.userToUserResponse(userRepository.save(user));
    }

    public String createRefreshToken (User user) {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String rawToken = Base64.getEncoder().withoutPadding().encodeToString(randomBytes);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hashToken(rawToken));
        refreshToken.setExpiredAt(Instant.now().plusMillis(jwtProperties.refreshTokenExpirationMs()));

        return rawToken;
    }

    String hashToken (String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(rawToken.getBytes(StandardCharsets.UTF_8)));
        }catch (NoSuchAlgorithmException ex){
            throw new IllegalStateException("SHA-256 algorithm not found");
        }
    }
}
