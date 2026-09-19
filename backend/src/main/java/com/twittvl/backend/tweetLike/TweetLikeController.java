package com.twittvl.backend.tweetLike;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/tweetLikes")
public class TweetLikeController {
    private final TweetLikeService tweetLikeService;
    public TweetLikeController(TweetLikeService tweetLikeService) {this.tweetLikeService = tweetLikeService;}

    @PostMapping("/{tweetId}")
    public long likeTweet(
            @PathVariable long tweetId,
            @RequestHeader ("X-User-Id") Long userId){
        return tweetLikeService.likeTweet(userId, tweetId);
    }

    @DeleteMapping("/{tweetId}")
    public long unlikeTweet(
            @PathVariable long tweetId,
            @RequestHeader ("X-User-Id") Long userId){
        return tweetLikeService.unlikeTweet(userId, tweetId);
    }


}
