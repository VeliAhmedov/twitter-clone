package com.twittvl.backend.commentLike;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/commentLikes")
public class CommentLikeController {

    private CommentLikeService commentLikeService;
    public CommentLikeController(CommentLikeService commentLikeService) {
        this.commentLikeService = commentLikeService;
    }

    @PostMapping
    public long likeComment(
            @PathVariable long commentId,
            @RequestHeader("X-User-Id") Long userId){
        return commentLikeService.likeComment(userId, commentId);
    }

    @DeleteMapping
    public long unlikeComment(
            @PathVariable long commentId,
            @RequestHeader ("X-User-Id") Long userId){
        return commentLikeService.unlikeComment(userId, commentId);
    }
}
