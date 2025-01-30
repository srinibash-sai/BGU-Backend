package com.anup.bgu.feedback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FeedbackResponse(
        String email,
        String message,
        String timestamp
) {
}
