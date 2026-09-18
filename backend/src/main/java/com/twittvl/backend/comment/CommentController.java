package com.twittvl.backend.comment;


import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/api/tweets/{tweetId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long tweetId,
            @RequestBody @Valid CommentRequest commentRequest) {
        CommentResponse commentResponse = commentService.createComment(commentRequest, userId, tweetId);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponse);
    }

    @GetMapping("/api/tweets/{tweetId}/comments/{id}")
    public CommentResponse getById(@PathVariable Long id) {
        return commentService.getById(id);
    }

    @GetMapping("/api/tweets/{tweetId}/comments")
    public Page<CommentResponse> getCommentsByTweetId(
            @PathVariable Long tweetId,
            @PageableDefault(size = 20) Pageable pageable) {
        return commentService.getCommentsByTweedId(pageable, tweetId);
    }

    @PatchMapping("/api/tweets/{tweetId}/comments/{id}")
    public CommentResponse editComment(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CommentRequest commentRequest){
        return commentService.editComment(id, userId, commentRequest);
    }

    @DeleteMapping("/api/tweets/{tweetId}/comments/{id}")
    public ResponseEntity<Void> deleteComments(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        commentService.deleteComment(userId, id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/api/users/{userId}/comments")
    public Page<CommentResponse> getCommentsByUserId(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return commentService.getCommentsByUserId(pageable, userId);
    }
}
