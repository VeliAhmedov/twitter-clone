package com.twittvl.backend.notification;

import com.twittvl.backend.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

//if I want being swappable transports,
//I can change to interface +RabbitMQNotificationProducer implementation
//Purpose: sending notification
@Component
public class NotificationProducer {

}
