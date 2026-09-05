package com.twittvl.backend.like;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/likes")
public class LikeController {
    private final LikeService likeService;
    public LikeController(LikeService likeService) {this.likeService = likeService;}

    @PostMapping
    public long likeTweet(
            @PathVariable long id,
            @RequestHeader ("X-User-Id") Long userId){
        return likeService.likeTweet(id, userId);
    }

    @DeleteMapping
    public long unlikeTweet(
            @PathVariable long id,
            @RequestHeader ("X-User-Id") Long userId){
        return likeService.unlikeTweet(id, userId);
    }


}
