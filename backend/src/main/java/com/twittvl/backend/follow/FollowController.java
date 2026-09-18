package com.twittvl.backend.follow;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
public class FollowController {
    private final FollowService followService;
    public  FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/api/users/{followedId}/follow")
    public long followUser(
            @RequestHeader("X-User-Id") Long followerId,
            @PathVariable Long followedId) {
        return followService.followUser(followerId, followedId);
    }

    @DeleteMapping("/api/users/{followedId}/follow")
    public long unfollowUser(
            @RequestHeader("X-User-Id") Long followerId,
            @PathVariable Long followedId) {
        return followService.unfollowUser(followerId, followedId);
    }


    @GetMapping("/api/users/{userId}/followers")
    public Page<FollowUserResponse> getFollowers(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return followService.getFollowers(userId, pageable);
    }

    @GetMapping("/api/users/{userId}/following")
    public Page<FollowUserResponse> getFollowing(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return followService.getFollowing(userId, pageable);
    }

    @GetMapping("/api/users/{userId}/follow-stats")
    public FollowStatsResponse getFollowStats(@PathVariable Long userId) {
        return followService.getFollowStats(userId);
    }
}
