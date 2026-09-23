package com.twittvl.backend.notification;


import com.twittvl.backend.common.exception.ResourceNotFoundException;
import com.twittvl.backend.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findAllByRecipientIdOrderByCreatedAtDesc(userId,pageable)
                .map(this::toNotificationResponse);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(userId);
    }

    @Transactional
    public NotificationResponse markAsRead(Long userId, Long notificationId) {
        Notification notification =notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification with id: " + notificationId + " not found"));
        if (notification.getRecipient().getId().equals(userId)) {
            throw new IllegalArgumentException("you can only mark your own notification as read");
        }
        notification.setRead(true);
        return toNotificationResponse(notification);
    }

    //created converter here instead of MapStruct
    // as so many error occurred and code got complicated
    private NotificationResponse toNotificationResponse(Notification notification) {
        User sender = notification.getSender();
        String message = buildTypeMessage(notification.getType());
        return new NotificationResponse(
                notification.getId(),
                sender.getId(),
                sender.getDisplayName(),
                notification.getType(),
                message,
                notification.getTweetId(),
                notification.getCommentId(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
    private String buildTypeMessage(NotificationType type) {
        return switch (type){
            case FOLLOW -> "followed you";
            case COMMENT -> "commented on your tweet";
            case REPLY -> "replied on your comment";
            case TWEET_LIKE ->  "liked your tweet";
            case COMMENT_LIKE ->  "liked your comment";
        };
    }
}
