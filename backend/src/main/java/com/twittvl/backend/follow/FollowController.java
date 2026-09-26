package com.twittvl.backend.follow;

import com.twittvl.backend.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class FollowController {
    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/api/users/{followedId}/follow")
    public long followUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long followedId) {
        return followService.followUser(userDetails.getId(), followedId);
    }

    @DeleteMapping("/api/users/{followedId}/follow")
    public long unfollowUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long followedId) {
        return followService.unfollowUser(userDetails.getId(), followedId);
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
    //what to do for these 2
}
