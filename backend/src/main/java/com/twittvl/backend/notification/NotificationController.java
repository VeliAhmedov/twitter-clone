package com.twittvl.backend.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
            @RequestHeader("X-User-Id") Long userId,
            @PageableDefault(size = 20)  Pageable pageable) {
        return notificationService.getNotifications(userId, pageable);
    }

    @GetMapping("/unread-count")
    public long getUnreadNotificationCount(@RequestHeader("X-User-Id") Long userId) {
        return notificationService.getUnreadCount(userId);
    }

    @PatchMapping("/{notificationId}/read")
    public NotificationResponse markAsRead(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long notificationId) {
        return notificationService.markAsRead(userId, notificationId);
    }

    @PatchMapping("/read-all")
    public int markAllAsRead(@RequestHeader("X-User-Id") Long userId) {
        return notificationService.markAllAsRead(userId);
    }
}
