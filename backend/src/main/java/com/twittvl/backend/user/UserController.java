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

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequestTemp request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("{username}")
    public UserResponse getUserById(@PathVariable String username) {
        return userService.findUserByUsername(username);
    }

    @PutMapping({"id"})
    public UserResponse updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest userUpdateRequest){
        return  userService.updateProfile(id, userUpdateRequest);
    }
}
