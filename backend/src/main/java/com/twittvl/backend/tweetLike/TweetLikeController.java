package com.twittvl.backend.tweetLike;

import com.twittvl.backend.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class TweetLikeController {
    private final TweetLikeService tweetLikeService;
    public TweetLikeController(TweetLikeService tweetLikeService) {this.tweetLikeService = tweetLikeService;}

    @PostMapping("/api/tweets/{tweetId}/likes")
    public long likeTweet(
            @PathVariable Long tweetId,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        return tweetLikeService.likeTweet(userDetails.getId(), tweetId);
    }

    @DeleteMapping("/api/tweets/{tweetId}/likes")
    public long unlikeTweet(
            @PathVariable Long tweetId,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        return tweetLikeService.unlikeTweet(userDetails.getId(), tweetId);
    }

    @GetMapping("/api/tweets/{tweetId}/likes")
    public Page<TweetLikeUserResponse> getTweetLikers(
            @PathVariable Long tweetId,
            @PageableDefault(size = 20) Pageable pageable){
        return tweetLikeService.getTweetLikers(tweetId, pageable);
    }
}
