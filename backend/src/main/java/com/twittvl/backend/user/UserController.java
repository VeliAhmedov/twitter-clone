package com.twittvl.backend.user;

import com.twittvl.backend.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final AvatarStorageService avatarStorageService;

    public UserController(UserService userService,
                          AvatarStorageService avatarStorageService) {
        this.userService = userService;
        this.avatarStorageService = avatarStorageService;
    }

    @GetMapping
    public Page<UserResponse> getAllUsers( @PageableDefault(size = 20) Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @GetMapping("/{username}")
    public UserResponse getUserByUsername(@PathVariable String username) {
        return userService.findUserByUsername(username);
    }

    @PutMapping(value = {"/{id}"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserResponse updateProfile(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart("profile") @Valid UserUpdateRequest request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) throws IOException {
        return  userService.updateProfile(id, request,userDetails.getId(), avatar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        userService.deleteUser(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
