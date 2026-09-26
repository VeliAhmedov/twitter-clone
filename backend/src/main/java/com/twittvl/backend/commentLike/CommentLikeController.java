package com.twittvl.backend.commentLike;

import com.twittvl.backend.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return commentLikeService.likeComment(userDetails.getId(), commentId);
    }

    @DeleteMapping("/api/comments/{commentId}/likes")
    public long unlikeComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return commentLikeService.unlikeComment(userDetails.getId(), commentId);
    }

    @GetMapping("/api/comments/{commentId}/likes")
    public Page<CommentLikeUserResponse> getCommentLikers(
            @PathVariable Long commentId,
            @PageableDefault(size = 20) Pageable pageable) {
        return commentLikeService.getCommentLikers(commentId, pageable);
    }
}
