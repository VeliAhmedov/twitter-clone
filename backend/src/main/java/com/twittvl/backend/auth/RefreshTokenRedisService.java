package com.twittvl.backend.auth;

import com.twittvl.backend.common.exception.InvalidCredentialsException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
public class RefreshTokenRedisService {

    private final StringRedisTemplate redis;
    public RefreshTokenRedisService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    //store new refresh token in Redis, tokenHash = hashed refresh token, userId, owner of token
    public void save(String tokenHash, Long userId, Instant expiresAt) {
        String key = tokenKey(tokenHash); //redis syntax take if it is tokenHash = abc123 -> key = refresh:token:abc123
        redis.opsForHash().put(key, "userId", userId.toString()); // refresh:token:abc123 -> userId = 12 via Redis Hash
        redis.expireAt(key, expiresAt); //set so when reached it automatically cleans up

        String setKey = userSetKey(userId); //create user's token set -> refresh:user:12
        redis.opsForSet().add(setKey, tokenHash); //refresh:user:12 -> abc232, ggd5, swe423 | refresh tokens of user
        redis.expireAt(setKey, expiresAt);
        // save(tokenHash, userId, expiration) -> 1. refresh:token:<hash> -> userId = 14 and TTL = expiration
        // 2. refresh:user:14 -> <hash> and TTL = expiration
    }

    //rotate old refresh token with new one and returns userID, alongside detects reuse or expired
    //Refresh token A -> used once -> Refresh token B created -> A becomes invalid
    //it will not let refresh token be used again due security purposes
    public  Long rotate (String oldHash, String newHash, Long newExpiredMs) {
        String oldKey = tokenKey(oldHash); //find old token
        Object userIdRaw = redis.opsForHash().get(oldKey, "userId"); //find owner

        if (userIdRaw == null) {
            throw new InvalidCredentialsException("Refresh token is invalid or expired");
        }
        Long userId = Long.valueOf(userIdRaw.toString()); //convert redis value to Long

        //Atomic operation, only first caller can set replaced,second caller means reuse
        Boolean firstUse = redis.opsForHash().putIfAbsent(oldKey, "firstUse", 1); //if not used, it marks as rotated if used return false
        if (!Boolean.TRUE.equals(firstUse)) {
            //Token A -> legitimate refresh -> Token B -> Attacker tries Token A -> reuse detected -> revoke ALL user's refresh
            revokeAllByUserId(userId);
            throw new InvalidCredentialsException("Refresh token reuse detected, all sessions are revoked");
        }
        redis.opsForSet().remove(userSetKey(userId), oldHash); //remove old refresh token
        save(newHash, userId, Instant.now().plusMillis(newExpiredMs)); //new replacement
        return userId;
    }

    //revoke (delete) one specific refreshToken
    public void revokeToken (String tokenHash){
        Object userIdRaw = redis.opsForHash().get(tokenKey(tokenHash), "userId"); //refresh:user:<userId>
        redis.delete(tokenKey(tokenHash)); //delete refresh:token:fgd123 from refresh:user:<userId>
        if (userIdRaw != null) {
            redis.opsForSet().remove(userSetKey(Long.valueOf(userIdRaw.toString())), tokenHash);
        } //remove from user's set
    }

    //revoke/log out every refreshToken belonging to this user
    public void revokeAllByUserId (Long userId) {
        String setKey = userSetKey(userId);//refresh:user:12 has hashA, hashB
        Set<String> hashes = redis.opsForSet().members(setKey); //hashA, hashB
        if (hashes != null && !hashes.isEmpty()) {
            redis.delete(hashes.stream().map(this::tokenKey).toList()); //convert refresh:token:hashA (B) and delete
        }
        redis.delete(setKey); // refresh:user:12 is removed -> user 12 has no refresh token
    }

    //build redis key for one token
    private String tokenKey(String tokenHash) { return "refresh:token:" + tokenHash; }
    //build redis key containing user's token
    private String userSetKey(Long userId)    { return "refresh:user:" + userId; }

    //tokens are stored in hash || Actual refresh token -> SHA/hash -> Redis
}
/*  Refresh Token A
          ↓
         used
          ↓
      Refresh Token B
           ↓
     marked "rotated"
           ↓
        used again
           ↓
      REUSE DETECTED
            ↓
     Revoke all user's sessions
 */
