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

    @PostMapping("/api/users/{followedUserId}/follow")
    public Long followUser(
            @RequestHeader("X-User-Id") Long followerId,
            @PathVariable Long followedUserId) {
        return followService.followUser(followerId, followedUserId);
    }

    @DeleteMapping("/api/users/{followedUserId}/follow")
    public Long unfollowUser(
            @RequestHeader("X-User-Id") Long followerId,
            @PathVariable Long followedUserId) {
        return followService.unfollowUser(followerId, followedUserId);
    }

    @GetMapping("/api/users/{userId}/followers")
    public Page<FollowUserResponse> getFollowers (
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return followService.getFollowers(userId, pageable);
    }

    @GetMapping("/api/users/{userId}/follwing")
    public Page<FollowUserResponse> getFollowings (
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return followService.getFollowers(userId, pageable);
    }

    @GetMapping("/api/users/{userId}/follow-stats")
    public FollowStatsResponse getFollowerStats(
            @RequestHeader("X-User-Id") Long userId) {
        return followService.getFollowStats(userId);
    }
}
