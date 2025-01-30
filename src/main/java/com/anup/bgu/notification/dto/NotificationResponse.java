package com.anup.bgu.notification.dto;

public record NotificationResponse(
        String title,
        String message,
        String timestamp
) {
}
