package com.twittvl.backend.notification;

import com.twittvl.backend.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public Page<NotificationResponse> getNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        return notificationService.getNotifications(userDetails.getId(), pageable);
    }

    @GetMapping("/unread-count")
    public long getUnreadNotificationCount(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return notificationService.getUnreadCount(userDetails.getId());
    }

    @PatchMapping("/{notificationId}/read")
    public NotificationResponse markAsRead(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long notificationId) {
        return notificationService.markAsRead(userDetails.getId(), notificationId);
    }

    @PatchMapping("/read-all")
    public int markAllAsRead(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return notificationService.markAllAsRead(userDetails.getId());
    }
}
