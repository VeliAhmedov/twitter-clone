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
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;
    private final JwtUtil jwtUtil;
    private final SecureRandom secureRandom = new SecureRandom();
    public AuthService(UserRepository userRepository, UserMapper userMapper,
                       PasswordEncoder passwordEncoder, RefreshTokenRepository refreshTokenRepository,
                       JwtProperties jwtProperties, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
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

    @Transactional(readOnly = true)
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

    @Transactional
    public AuthResponse refresh (RefreshRequest refreshRequest) {
        String hash = hashToken(refreshRequest.refreshToken()); //rehash raw token
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid refresh token"));
        if (stored.isRevoked()) {
            //if reuse detected, kill everything about that user, forced log out until credentials given
            //revoke so that someone can't use it
            refreshTokenRepository.revokeAllByUserId(stored.getUser().getId());
            throw new InvalidCredentialsException("Refresh token reuse detected, please log in again");
        }
        if (stored.getExpiredAt().isBefore(Instant.now())) {
            throw new InvalidCredentialsException("Refresh token expired");
        }
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        User user = stored.getUser();
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole().name());
        String newRefreshToken = createRefreshToken(user);
        return new AuthResponse(accessToken, newRefreshToken, user.getRole().name());
    }

    public void logout(RefreshRequest refreshRequest) {
        refreshTokenRepository.findByTokenHash(hashToken(refreshRequest.refreshToken()))
                .ifPresent(r -> {r.setRevoked(true); refreshTokenRepository.save(r);});
    }


    public String createRefreshToken (User user) {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String rawToken = Base64.getEncoder().withoutPadding().encodeToString(randomBytes);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hashToken(rawToken));
        refreshToken.setExpiredAt(Instant.now().plusMillis(jwtProperties.refreshTokenExpirationMs()));
        refreshTokenRepository.save(refreshToken);

        return rawToken; //raw value go to client, hashed version is stored in DB
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
