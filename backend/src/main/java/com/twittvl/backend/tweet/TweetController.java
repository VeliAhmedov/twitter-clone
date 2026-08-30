package com.twittvl.backend.tweet;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/tweets")
public class TweetController {
    private final TweetService tweetService;

    public  TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @PostMapping
    public ResponseEntity<TweetResponse> postTweet (
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody
            @Valid TweetRequest tweetRequest) {
        TweetResponse tweetResponse = tweetService.postTweet(userId, tweetRequest);
        return  ResponseEntity.status(HttpStatus.CREATED).body(tweetResponse);
    }

    @GetMapping("/{id}")
    public TweetResponse getById(@PathVariable Long id) {
        return tweetService.getById(id);
    }

    @GetMapping("/feed")
    public Page<TweetResponse> getFeed(@PageableDefault(size = 20) Pageable pageable) {
        return tweetService.getFeed(pageable);
    }

}
