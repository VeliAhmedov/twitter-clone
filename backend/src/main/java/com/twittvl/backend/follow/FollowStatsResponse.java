package com.twittvl.backend.follow;

public record FollowStatsResponse(
        long followerCount,
        long followingCount
) {
}
