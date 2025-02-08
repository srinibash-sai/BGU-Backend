package com.anup.bgu.noticeboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NoticeRequest(
        @NotBlank(message = "Notice is a required field.")
        @Size(max = 500, message = "Maximum Notice length is 500 characters.")
        String notice,

        @NotBlank(message = "Expiration is a required field.")
        @Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-\\d{4} ([01]\\d|2[0-3]):[0-5]\\d$",
                message = "Date time must be in this format 'dd-MM-yyyy HH:mm'.")
        String expire
) {
}
