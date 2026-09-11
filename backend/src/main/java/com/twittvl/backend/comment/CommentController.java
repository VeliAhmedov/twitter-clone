package com.twittvl.backend.comment;


import com.twittvl.backend.tweet.TweetRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tweets/{tweetId}/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long tweetId,
            @RequestBody @Valid CommentRequest commentRequest) {
        CommentResponse commentResponse = commentService.createComment(commentRequest, userId, tweetId);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponse);
    }

    @GetMapping("/{id}")
    public CommentResponse getById(@PathVariable Long id) {
        return commentService.getById(id);
    }

    @GetMapping
    public Page<CommentResponse> getCommentsByUserId(@RequestParam Long userId, Pageable pageable) {
        return commentService.getCommentsByUserId(pageable, userId);
    }

    @GetMapping
    public Page<CommentResponse> getCommentsByTweetId(@RequestParam Long tweetId, Pageable pageable) {
        return commentService.getCommentsByTweedId(pageable, tweetId);
    }

    @PatchMapping("{id}")
    public CommentResponse updateComment(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CommentRequest commentRequest){
        return commentService.editComment(id, userId, commentRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComments(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        commentService.deleteComment(userId, id);
        return ResponseEntity.noContent().build();
    }
}
