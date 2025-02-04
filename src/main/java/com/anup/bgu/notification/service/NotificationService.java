package com.anup.bgu.notification.service;


import com.anup.bgu.notification.dto.NotificationRequest;
import com.anup.bgu.notification.dto.TokenRequest;
import org.springframework.data.redis.connection.MessageListener;

public interface NotificationService extends MessageListener {
    void pushNotification(NotificationRequest request);
    void subscribe(TokenRequest token);
}
