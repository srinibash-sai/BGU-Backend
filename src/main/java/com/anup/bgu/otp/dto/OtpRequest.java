package com.anup.bgu.otp.dto;


import jakarta.validation.constraints.NotBlank;

public record OtpRequest(
        @NotBlank(message = "registrationId is required")
        String registrationId,

        @NotBlank(message = "otp is required")
        String otp
) {
}
