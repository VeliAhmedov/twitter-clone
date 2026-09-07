package com.twittvl.backend.follow;

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
            @RequestHeader("X-User-Id") Long followerId,
            @PathVariable Long followedId) {
        return followService.followUser(followerId, followedId);
    }

    @DeleteMapping
    public Long unfollowUser(
            @RequestHeader("X-User-Id") Long followerId,
            @PathVariable Long followedId) {
        return followService.unfollowUser(followerId, followedId);
    }
}
