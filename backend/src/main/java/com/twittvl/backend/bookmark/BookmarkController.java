package com.twittvl.backend.bookmark;

import com.twittvl.backend.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @PostMapping("/tweet/{tweetId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void bookmarkTweet(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long tweetId) {
        bookmarkService.bookmarkTweet(userDetails.getId(), tweetId);
    }

    @PostMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void bookmarkComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long commentId) {
        bookmarkService.bookmarkComment(userDetails.getId(), commentId);
    }

    @DeleteMapping("/tweet/{tweetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unbookmarkTweet(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long tweetId) {
        bookmarkService.unbookmarkTweet(userDetails.getId(), tweetId);
    }

    @DeleteMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unbookmarkComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long commentId) {
        bookmarkService.unbookmarkComment(userDetails.getId(), commentId);
    }

    @GetMapping
    public Page<BookmarkResponse> getBookmarks(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        return bookmarkService.getBookmarksByUserId(userDetails.getId(), pageable);
    }
}
