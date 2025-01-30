package com.anup.bgu.feedback.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FeedbackRequest(
        @Email(message = "Email should be valid.")
        @NotBlank(message = "Email is a required field.")
        String email,

        @NotBlank(message = "Message cannot be blank.")
        @Size(max = 500, message = "Message cannot exceed 500 characters.")
        String message
) {
}
