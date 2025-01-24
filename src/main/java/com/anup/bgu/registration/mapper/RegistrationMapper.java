package com.anup.bgu.registration.mapper;

import com.anup.bgu.payments.entities.Payment;
import com.anup.bgu.registration.dto.RegistrationRequest;
import com.anup.bgu.registration.dto.RegistrationResponse;
import com.anup.bgu.registration.entities.SoloRegistration;
import com.anup.bgu.registration.entities.StudentType;
import com.anup.bgu.registration.entities.TeamMember;
import com.anup.bgu.registration.entities.TeamRegistration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RegistrationMapper {

    @Value("${secret.api-base-url}")
    private String API_BASE_URL;

    public RegistrationResponse toSoloRegistrationResponse(SoloRegistration soloRegistration) {
        RegistrationResponse registrationResponse = new RegistrationResponse();
        registrationResponse.setId(soloRegistration.getId());
        registrationResponse.setName(soloRegistration.getName());
        registrationResponse.setEmail(soloRegistration.getEmail());
        registrationResponse.setPhone(soloRegistration.getPhone());
        registrationResponse.setStudentType(soloRegistration.getStudentType().toString());
        registrationResponse.setGender(soloRegistration.getGender().toString());
        registrationResponse.setRegistrationDate(toLocalDateTimeFromInstant(soloRegistration.getRegistrationDate()).toString());
        registrationResponse.setCollegeName(soloRegistration.getCollegeName());

        if (soloRegistration.getStudentType().equals(StudentType.NON_BGU)) {
            registrationResponse.setTransactionId(soloRegistration.getPayment().getTransactionId());
            registrationResponse.setScreenshot(getScreenshotURL(soloRegistration.getPayment()));
        }

        return registrationResponse;
    }

    public RegistrationResponse toTeamRegistrationResponse(TeamRegistration teamRegistration) {
        RegistrationResponse registrationResponse = new RegistrationResponse();
        registrationResponse.setId(teamRegistration.getId());
        registrationResponse.setLeaderName(teamRegistration.getLeaderName());
        registrationResponse.setTeamName(teamRegistration.getTeamName());
        registrationResponse.setEmail(teamRegistration.getEmail());
        registrationResponse.setPhone(teamRegistration.getPhone());
        registrationResponse.setStudentType(teamRegistration.getStudentType().toString());
        registrationResponse.setGender(teamRegistration.getGender().toString());
        registrationResponse.setRegistrationDate(toLocalDateTimeFromInstant(teamRegistration.getRegistrationDate()).toString());
        registrationResponse.setCollegeName(teamRegistration.getCollegeName());

        if (teamRegistration.getStudentType().equals(StudentType.NON_BGU)) {
            registrationResponse.setTransactionId(teamRegistration.getPayment().getTransactionId());
            registrationResponse.setScreenshot(getScreenshotURL(teamRegistration.getPayment()));
        }

        List<RegistrationResponse.Member> teamMembers = new ArrayList<>();
        for (TeamMember teamMember : teamRegistration.getTeamMembers()) {
            RegistrationResponse.Member member = new RegistrationResponse.Member();
            member.setEmail(teamMember.getEmail());
            member.setName(teamMember.getName());
            teamMembers.add(member);
        }
        registrationResponse.setTeamMembers(teamMembers);

        return registrationResponse;
    }

    public List<RegistrationResponse> toSoloListRegistrationResponse(List<SoloRegistration> soloRegistrations) {
        log.info("toSoloListRegistrationResponse() -> {}",soloRegistrations.toString());
        List<RegistrationResponse> registrationResponses = new ArrayList<>();
        for (SoloRegistration soloRegistration : soloRegistrations) {
            log.info("toSoloListRegistrationResponse() -> for loop -> {}",soloRegistration.toString());
            registrationResponses.add(toSoloRegistrationResponse(soloRegistration));
        }
        log.info("toSoloListRegistrationResponse() -> {}",registrationResponses.toString());
        return registrationResponses;
    }

    public List<RegistrationResponse> toTeamListRegistrationResponse(List<TeamRegistration> teamRegistrations) {
        List<RegistrationResponse> registrationResponses = new ArrayList<>();
        for (TeamRegistration teamRegistration : teamRegistrations) {
            registrationResponses.add(toTeamRegistrationResponse(teamRegistration));
        }
        return registrationResponses;
    }

    private LocalDateTime toLocalDateTimeFromInstant(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneOffset.systemDefault());
    }

    private String getScreenshotURL(Payment payment) {
        return API_BASE_URL + "/payments/image/" + payment.getId();
    }
}
