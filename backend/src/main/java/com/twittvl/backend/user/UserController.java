package com.twittvl.backend.user;

import com.twittvl.backend.comment.CommentResponse;
import com.twittvl.backend.comment.CommentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final CommentService commentService;

    public UserController(UserService userService, CommentService commentService) {
        this.userService = userService;
        this.commentService =  commentService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequestTemp request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{userId}/comments")
    public Page<CommentResponse> getCommentsByUserId(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return commentService.getCommentsByUserId(pageable, userId);
    }
}
