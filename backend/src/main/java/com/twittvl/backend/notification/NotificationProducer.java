package com.twittvl.backend.notification;

import com.twittvl.backend.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

//if I want being swappable transports,
//I can change to interface +RabbitMQNotificationProducer implementation
//Purpose: sending notification
@Component
public class NotificationProducer {
    private final RabbitTemplate rabbitTemplate;
    public NotificationProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    public void sendNotification(Long senderId, Long recipientId, NotificationType type, Long tweetId, Long commentId) {
        if (senderId.equals(recipientId)) return;
        NotificationMessage notificationMessage = new NotificationMessage(senderId, recipientId, type, tweetId, commentId);
        rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_EXCHANGE_NAME, RabbitMQConfig.NOTIFICATION_ROUTING_KEY, notificationMessage);
    }
}
