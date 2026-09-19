package com.twittvl.backend.commentLike;

import org.springframework.web.bind.annotation.*;

@RestController
public class CommentLikeController {

    private final CommentLikeService commentLikeService;
    public CommentLikeController(CommentLikeService commentLikeService) {
        this.commentLikeService = commentLikeService;
    }

    @PostMapping("/api/comments/{commentId}/likes")
    public long likeComment(
            @PathVariable Long commentId,
            @RequestHeader("X-User-Id") Long userId){
        return commentLikeService.likeComment(userId, commentId);
    }

    @DeleteMapping("/api/comments/{commentId}/likes")
    public long unlikeComment(
            @PathVariable Long commentId,
            @RequestHeader ("X-User-Id") Long userId){
        return commentLikeService.unlikeComment(userId, commentId);
    }
}
