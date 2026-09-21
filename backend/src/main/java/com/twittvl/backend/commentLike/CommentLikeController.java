package com.twittvl.backend.commentLike;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/api/comments/{commentId}/likes")
    public Page<CommentLikeUserResponse> getCommentLikers(
            @PathVariable Long commentId,
            @PageableDefault(size = 20) Pageable pageable){
        return commentLikeService.getCommentLikers(commentId, pageable);
    }
}
