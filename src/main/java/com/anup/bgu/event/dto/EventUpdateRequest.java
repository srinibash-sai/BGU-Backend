package com.anup.bgu.event.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EventUpdateRequest(
        @Size(max = 200, message = "Title cannot exceed 200 characters")
        String title,

        @Size(max = 65000, message = "Description cannot exceed 65,000 characters.")
        String description,

        @Pattern(regexp = "^(ONGOING|CLOSED|UPCOMING)$", message = "Please provide valid status.")
        String status,

        @Size(max = 65000, message = "Rules cannot exceed 65,000 characters.")
        String rules,

        @Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-\\d{4} ([01]\\d|2[0-3]):[0-5]\\d$", message = "Date time must be in this format 'dd-MM-yyyy HH:mm'.")
        String dateTime,

        @Size(max = 100, message = "Coordinator name cannot exceed 100 characters.")
        String coordinatorName,

        @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Coordinator number must be a valid phone number.")
        String coordinatorNumber,

        @Min(value = 0, message = "Minimum amount should be 0")
        int amount,

        @Pattern(regexp = "^(SOLO|TEAM)$", message = "Please provide valid teamType")
        String teamType,
        @Pattern(regexp = "^(SPECTRA|ATOS|YOLO)$", message = "Please provide valid event type")
        String eventType,

        @Min(value = 2, message = "Minimum Team size should be 2")
        int maxTeamSize,

        @Min(value = 2, message = "Minimum Team size should be 2")
        int minTeamSize
) {
}
