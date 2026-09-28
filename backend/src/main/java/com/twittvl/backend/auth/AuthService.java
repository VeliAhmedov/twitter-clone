package com.twittvl.backend.auth;

import com.twittvl.backend.common.exception.InvalidCredentialsException;
import com.twittvl.backend.security.JwtProperties;
import com.twittvl.backend.security.JwtUtil;
import com.twittvl.backend.user.User;
import com.twittvl.backend.user.UserMapper;
import com.twittvl.backend.user.UserRepository;
import com.twittvl.backend.user.UserResponse;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final RefreshTokenRedisService refreshTokenRedisService;
    private final JwtProperties jwtProperties;
    private final JwtUtil jwtUtil;
    private final SecureRandom secureRandom = new SecureRandom();
    public AuthService(UserRepository userRepository, UserMapper userMapper,
                       PasswordEncoder passwordEncoder, RefreshTokenRedisService refreshTokenRedisService,
                       JwtProperties jwtProperties, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRedisService = refreshTokenRedisService;
        this.jwtProperties = jwtProperties;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
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

    @Transactional
    public AuthResponse login (LoginRequest loginRequest) {
        User user = userRepository.findByUsername((loginRequest.username()))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
        //generate 15 minute access token
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole().name());
        //generate 15 days refresh token
        String refreshToken = createRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken, user.getRole().name());
    }

    @Transactional(readOnly = true)
    public AuthResponse refresh (RefreshRequest refreshRequest) {
        String oldHash = hashToken(refreshRequest.refreshToken());
        String newRawToken = generateRawToken();
        String newHash = hashToken(refreshRequest.refreshToken());

        //Redis validate old token, detect reuse then store new one for user
        Long userId = refreshTokenRedisService.rotate(oldHash, newHash, jwtProperties.refreshTokenExpirationMs());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid refresh token"));
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole().name());
        return new AuthResponse(accessToken, newRawToken, user.getRole().name());

    }

    public void logout(RefreshRequest refreshRequest) {
        refreshTokenRedisService.revokeToken(refreshRequest.refreshToken());
    }


    public String createRefreshToken (User user) {
        String rawToken = generateRawToken();
        Instant expiresAt = Instant.now().plusSeconds(jwtProperties.refreshTokenExpirationMs());
        refreshTokenRedisService.save(hashToken(rawToken), user.getId(), expiresAt);
        return rawToken;
    }

    private String generateRawToken(){
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getEncoder().withoutPadding().encodeToString(randomBytes);
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
