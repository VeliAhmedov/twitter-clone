package com.twittvl.backend.notification;

public record NotificationMessage(
        Long senderId,
        Long recipientId,
        NotificationType type,
        Long tweetId,
        Long commentId
) {
}
