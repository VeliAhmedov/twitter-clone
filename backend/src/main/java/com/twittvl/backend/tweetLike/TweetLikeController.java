package com.twittvl.backend.tweetLike;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
public class TweetLikeController {
    private final TweetLikeService tweetLikeService;
    public TweetLikeController(TweetLikeService tweetLikeService) {this.tweetLikeService = tweetLikeService;}

    @PostMapping("/api/tweets/{tweetId}/likes")
    public long likeTweet(
            @PathVariable Long tweetId,
            @RequestHeader ("X-User-Id") Long userId){
        return tweetLikeService.likeTweet(userId, tweetId);
    }

    @DeleteMapping("/api/tweets/{tweetId}/likes")
    public long unlikeTweet(
            @PathVariable Long tweetId,
            @RequestHeader ("X-User-Id") Long userId){
        return tweetLikeService.unlikeTweet(userId, tweetId);
    }

    @GetMapping("/api/tweets/{tweetId}/likes")
    public Page<TweetLikeUserResponse> getTweetLikers(Long tweetId, Pageable pageable){
        return tweetLikeService.getTweetLikers(tweetId, pageable);
    }
}
