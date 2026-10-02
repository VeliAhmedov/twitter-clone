package com.twittvl.backend.bookmark;

import com.twittvl.backend.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public long bookmarkTweet(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long tweetId) {
        return bookmarkService.bookmarkTweet(userDetails.getId(), tweetId);
    }

    @PostMapping("/comment/{commentId}")
    public long bookmarkComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long commentId) {
        return bookmarkService.bookmarkComment(userDetails.getId(), commentId);
    }

    @DeleteMapping("/tweet/{tweetId}")
    public long unbookmarkTweet(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long tweetId) {
        return bookmarkService.unbookmarkTweet(userDetails.getId(), tweetId);
    }

    @DeleteMapping("/comment/{commentId}")
    public long unbookmarkComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long commentId) {
        return bookmarkService.unbookmarkComment(userDetails.getId(), commentId);
    }

    @GetMapping
    public Page<BookmarkResponse> getBookmarks(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        return bookmarkService.getBookmarksByUserId(userDetails.getId(), pageable);
    }
}
