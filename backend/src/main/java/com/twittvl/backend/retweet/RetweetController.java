package com.twittvl.backend.retweet;

import com.twittvl.backend.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class RetweetController {
    private final RetweetService retweetService;
    public RetweetController(RetweetService retweetService) {
        this.retweetService = retweetService;
    }

    @PostMapping("/api/tweets/{tweetId}/retweet")
    @ResponseStatus(HttpStatus.CREATED)
    public long retweetTweet(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long tweetId) {
        return retweetService.retweetTweet(userDetails.getId(), tweetId);
    }

    @PostMapping("/api/comments/{commentId}/retweet")
    @ResponseStatus(HttpStatus.CREATED)
    public long retweetComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long commentId) {
        return retweetService.retweetComment(userDetails.getId(), commentId);
    }

    @DeleteMapping("/api/tweets/{tweetId}/retweet")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public long unRetweetTweet(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long tweetId) {
        return retweetService.unRetweetTweet(userDetails.getId(), tweetId);
    }

    @DeleteMapping("/api/comments/{commentId}/retweet")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public long unRetweetComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long commentId) {
        return retweetService.unRetweetComment(userDetails.getId(), commentId);
    }

    @GetMapping("/api/users/{userId}/retweets")
    @ResponseStatus(HttpStatus.OK)
    public Page<RetweetResponse> getRetweets(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return retweetService.getRetweetsByUserId(userId, pageable);
    }
}
