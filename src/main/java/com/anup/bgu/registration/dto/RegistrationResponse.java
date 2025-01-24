package com.anup.bgu.registration.dto;

import com.anup.bgu.registration.entities.TeamMember;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegistrationResponse implements Serializable {
    private String id;
    private String name;
    private String leaderName;  //For team
    private String teamName;  //For team
    private String email;
    private String phone;
    private String studentType;
    private String gender;
    private String registrationDate;
    private String collegeName;
    private String transactionId;
    private String screenshot;

    private List<Member> teamMembers;

    @NoArgsConstructor
    @Setter
    public static class Member implements Serializable {
        private String name;
        private String email;
    }
}
