package com.anup.bgu.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record NotificationRequest(
        @NotBlank(message = "Title cannot be blank.")
        @Size(max = 100, message = "Title cannot exceed 100 characters.")
        String title,

        @NotBlank(message = "Message cannot be blank.")
        @Size(max = 500, message = "Message cannot exceed 500 characters.")
        String message
) implements Serializable {
}
