package com.twittvl.backend.notification;

import com.twittvl.backend.common.exception.ResourceNotFoundException;
import com.twittvl.backend.config.RabbitMQConfig;
import com.twittvl.backend.user.User;
import com.twittvl.backend.user.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    public NotificationListener(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE_NAME)
    public void handleNotification(NotificationMessage message) {
        User sender = userRepository.findById(message.senderId())
                .orElseThrow(() -> new ResourceNotFoundException("User with " + message.senderId() + " not found"));
        User recipient = userRepository.findById(message.recipientId())
                .orElseThrow(() -> new ResourceNotFoundException("User with " + message.recipientId() + " not found"));

        Notification notification = new Notification();
        notification.setSender(sender);
        notification.setRecipient(recipient);
        notification.setType(message.type());
        notification.setTweetId(message.tweetId());
        notification.setCommentId(message.commentId());
        notificationRepository.save(notification);
    }
}
