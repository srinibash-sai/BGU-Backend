package com.anup.bgu.registration.dto;

import com.anup.bgu.registration.entities.TeamMember;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RegistrationRequest(
        @Size(max = 200, message = "Student name cannot exceed 200 characters.")
        @NotBlank(message = "Student Name is a required field.")
        String name,

        @Size(max = 200, message = "Team name cannot exceed 200 characters.")
        String teamName,

        @Email(message = "Email should be valid.")
        @NotBlank(message = "Email is a required field.")
        String email,

        @Size(max = 10, min = 10, message = "Phone must be 10 characters long.")
        @NotBlank(message = "Phone is a required field.")
        String phone,

        @Pattern(regexp = "^(MALE|FEMALE|OTHERS)$", message = "Gender must be one of the following values: MALE, FEMALE, OTHERS.")
        String gender,

        List<TeamMember> teamMembers
) {
}
