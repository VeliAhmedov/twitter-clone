package com.twittvl.backend.follow;

public record FollowUserResponse(
        Long id,
        String username,
        String displayName,
        String avatarURL
) {
}
