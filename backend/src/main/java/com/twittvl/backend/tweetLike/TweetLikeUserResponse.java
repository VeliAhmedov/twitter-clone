package com.twittvl.backend.tweetLike;

public record TweetLikeUserResponse(
        Long id,
        String username,
        String displayName,
        String avatarURL
) {
}
