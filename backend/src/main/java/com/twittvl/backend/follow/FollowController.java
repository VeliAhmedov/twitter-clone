package com.twittvl.backend.follow;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{followedId}/follow")
public class FollowController {
    private final FollowService followService;
    public  FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping
    public Long followUser(
            @PathVariable Long followedId,
            @RequestHeader("X-User-Id") Long followerId) {
        return followService.followUser(followerId, followedId);
    }

    @DeleteMapping
    public Long unfollowUser(
            @RequestHeader("X-User-Id") Long followerId,
            @PathVariable Long followedId) {
        return followService.unfollowUser(followerId, followedId);
    }

    @GetMapping("/api/users/{userId}/followers")
    public Page<FollowUserResponse> getFollowers (
            @PathVariable Long followedId,
            @PageableDefault(size = 20) Pageable pageable) {
        return followService.getFollowers(followedId, pageable);
    }

    @GetMapping("/api/users/{userId}/follwing")
    public Page<FollowUserResponse> getFollowings (
            @PathVariable Long followerId,
            @PageableDefault(size = 20) Pageable pageable) {
        return followService.getFollowers(followerId, pageable);
    }
}
