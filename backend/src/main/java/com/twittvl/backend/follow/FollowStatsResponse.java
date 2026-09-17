package com.twittvl.backend.follow;

public record FollowStatsResponse(
        Long followerCount,
        Long followingCount
) {
}
