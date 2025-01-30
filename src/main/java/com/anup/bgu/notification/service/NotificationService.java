package com.anup.bgu.notification.service;


import com.anup.bgu.notification.dto.NotificationRequest;
import jakarta.validation.Valid;

public interface NotificationService{
    void pushNotification(NotificationRequest request);

}
