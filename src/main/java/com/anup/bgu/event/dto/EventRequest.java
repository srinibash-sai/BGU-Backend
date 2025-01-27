package com.anup.bgu.event.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
public record EventRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title cannot exceed 200 characters")
        String title,

        @NotBlank(message = "Description is required")
        @Size(max = 65000, message = "Description cannot exceed 65,000 characters.")
        String description,

        @NotBlank(message = "Status is required")
        @Pattern(regexp = "^(ONGOING|CLOSED|UPCOMING)$", message = "Please provide valid status.")
        String status,

        @NotBlank(message = "Rules is required")
        @Size(max = 65000, message = "Rules cannot exceed 65,000 characters.")
        String rules,

        @NotBlank(message = "Date time is a required field.")
        @Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-\\d{4} ([01]\\d|2[0-3]):[0-5]\\d$", message = "Date time must be in this format 'dd-MM-yyyy HH:mm'.")
        String dateTime,

        @NotBlank(message = "Coordinator name is required")
        @Size(max = 100, message = "Coordinator name cannot exceed 100 characters.")
        String coordinatorName,

        @NotBlank(message = "Coordinator number is required")
        @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Coordinator number must be a valid phone number.")
        String coordinatorNumber,

        @Min(value = 0, message = "Minimum amount should be 0")
        Integer amount,

        @NotBlank(message = "Team Type is required")
        @Pattern(regexp = "^(SOLO|TEAM)$", message = "Please provide valid teamType")
        String teamType,
        @NotBlank(message = "Event Type is required")
        @Pattern(regexp = "^(SPECTRA|ATOS|YOLO)$", message = "Please provide valid event type")
        String eventType,

        @Min(value = 2, message = "Minimum Team size should be 2")
        Integer maxTeamSize,

        @Min(value = 2, message = "Minimum Team size should be 2")
        Integer minTeamSize
) {
}
