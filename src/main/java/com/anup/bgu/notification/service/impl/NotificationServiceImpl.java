package com.anup.bgu.notification.service.impl;

import com.anup.bgu.notification.dto.NotificationRequest;
import com.anup.bgu.notification.dto.TokenRequest;
import com.anup.bgu.notification.entities.NotificationHistory;
import com.anup.bgu.notification.entities.SubscribedToken;
import com.anup.bgu.notification.repo.NotificationRepository;
import com.anup.bgu.notification.repo.SubscribedTokenRepository;
import com.anup.bgu.notification.service.NotificationService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final FirebaseMessaging firebaseMessaging;
    private final NotificationRepository notificationRepository;
    private final SubscribedTokenRepository tokenRepository;
    private final String TOPIC = "broadcast";

    @Override
    public void pushNotification(NotificationRequest request) {

        NotificationHistory notificationHistory = NotificationHistory.builder()
                .title(request.title())
                .message(request.message())
                .build();

        notificationRepository.save(notificationHistory);

        Notification notification = Notification
                .builder()
                .setTitle(request.title())
                .setBody(request.message())
                .build();

        Message message = Message.builder()
                .setNotification(notification)
                .setTopic(TOPIC)
                .build();

        try {
            String response = firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribe(TokenRequest token) {
        if (!tokenRepository.existsByToken(token.token())) {
            try {
                firebaseMessaging.subscribeToTopic(List.of(token.token()), TOPIC);

            } catch (FirebaseMessagingException e) {
                throw new RuntimeException(e);
            }
            log.debug("subscribe()-> Subscribed to token:{}",token.token());
            tokenRepository.save(
              SubscribedToken.builder().token(token.token()).build()
            );
        }
    }

    @Override
    public void onMessage(org.springframework.data.redis.connection.Message message, byte[] pattern) {
        JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer();

        NotificationRequest notificationRequest = (NotificationRequest) serializer.deserialize(message.getBody());

        pushNotification(notificationRequest);
    }
}
