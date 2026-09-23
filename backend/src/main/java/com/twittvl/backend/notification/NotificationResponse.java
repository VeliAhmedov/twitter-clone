package com.twittvl.backend.notification;

import java.time.Instant;

//purpose: displaying notification as response
public record NotificationResponse(
        Long id,
        Long senderId,
        String senderDisplayName,
        NotificationType notificationType,
        String message,
        Long tweetId,
        Long commentId,
        boolean read,
        Instant createdAt
) {
}
