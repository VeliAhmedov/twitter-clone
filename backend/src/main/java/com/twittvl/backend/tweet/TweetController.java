package com.twittvl.backend.tweet;

import com.twittvl.backend.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tweets")
public class TweetController {
    private final TweetService tweetService;

    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @PostMapping
    public ResponseEntity<TweetResponse> postTweet(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody
            @Valid TweetRequest tweetRequest) {
        TweetResponse tweetResponse = tweetService.postTweet(userDetails.getId(), tweetRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(tweetResponse);
    }

    @GetMapping("/{id}")
    public TweetResponse getById(@PathVariable Long id) {
        return tweetService.getById(id);
    }

    @GetMapping
    public Page<TweetResponse> getByUserId(
            @RequestParam Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return tweetService.getByUserId(userId, pageable);
    }

    @GetMapping("/feed")
    public Page<TweetResponse> getFeed(
            @PageableDefault(size = 20) Pageable pageable) {
        return tweetService.getFeed(pageable);
    }

    @PatchMapping("/{id}")
    public TweetResponse editTweet(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody TweetRequest tweetRequest) {
        return tweetService.editTweet(id, userDetails.getId(), tweetRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTweet(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        tweetService.deleteTweet(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
