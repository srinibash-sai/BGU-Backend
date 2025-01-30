package com.anup.bgu.notification.service.impl;

import com.anup.bgu.notification.dto.NotificationRequest;
import com.anup.bgu.notification.repo.NotificationRepository;
import com.anup.bgu.notification.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public void pushNotification(NotificationRequest request) {

    }
}
