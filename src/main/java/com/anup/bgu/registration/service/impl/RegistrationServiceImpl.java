package com.anup.bgu.registration.service.impl;

import com.anup.bgu.event.entities.Event;
import com.anup.bgu.event.entities.EventTeamType;
import com.anup.bgu.event.entities.Status;
import com.anup.bgu.event.service.EventService;
import com.anup.bgu.exceptions.models.RegistrationProcessingException;
import com.anup.bgu.otp.dto.OtpResponse;
import com.anup.bgu.otp.service.OtpService;
import com.anup.bgu.registration.dto.RegSuccess;
import com.anup.bgu.registration.dto.RegistrationRequest;
import com.anup.bgu.registration.entities.*;
import com.anup.bgu.registration.repo.RegistrationCacheRepo;
import com.anup.bgu.registration.repo.SoloRegistrationRepository;
import com.anup.bgu.registration.repo.TeamRegistrationRepository;
import com.anup.bgu.registration.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {

    private final EventService eventService;
    private final SoloRegistrationRepository soloRepository;
    private final TeamRegistrationRepository teamRepository;
    private final RegistrationCacheRepo registrationCacheRepo;
    private final OtpService otpService;

    @Override
    public OtpResponse register(String eventId, RegistrationRequest request) {
        Event event = eventService.getEventById(eventId);

        if (!event.getStatus().equals(Status.ONGOING)) {
            throw new RegistrationProcessingException("Event is " + event.getStatus().toString().toLowerCase() + ". Can not register now.");
        }

        if (event.getTeamType().equals(EventTeamType.SOLO) && getBySoloEventAndUser(event, request.email()).isPresent()) {
            throw new RegistrationProcessingException("Registration already exists by email:" + request.email() + "!");
        } else if (event.getTeamType().equals(EventTeamType.TEAM) && getByTeamEventAndLeader(event, request.email()).isPresent()) {
            throw new RegistrationProcessingException("Registration already exists by email:" + request.email() + "!");
        }

        if (event.getTeamType().equals(EventTeamType.SOLO)) {
            return doSoloRegistration(event, request);
        } else {
            validateTeam(event, request);
            return doTeamRegistration(event, request);
        }
    }

    @Override
    public RegSuccess verifyOtp(String registrationId, String otp) {
        otpService.verifyOtp(registrationId, otp);

        Optional<SoloRegistration> soloRegistrationOptional = registrationCacheRepo.findSoloRegistrationById(registrationId);
        Optional<TeamRegistration> teamRegistrationOptional = registrationCacheRepo.findTeamRegistrationById(registrationId);

        if (soloRegistrationOptional.isPresent()) {
            SoloRegistration soloRegistration = soloRegistrationOptional.get();
            return proceedSolo(registrationId, soloRegistration);
        } else {
            TeamRegistration teamRegistration = teamRegistrationOptional.get();
            return proceedTeam(registrationId, teamRegistration);
        }
    }

    private RegSuccess proceedTeam(String registrationId, TeamRegistration teamRegistration) {
        if (teamRegistration.getStudentType().equals(StudentType.BGU)) {
            teamRepository.save(teamRegistration);
            //send notification
            return new RegSuccess(registrationId, false, 0);

        } else {
            return new RegSuccess(registrationId, true, teamRegistration.getEvent().getAmount());
        }
    }

    private RegSuccess proceedSolo(String registrationId, SoloRegistration soloRegistration) {
        if (soloRegistration.getStudentType().equals(StudentType.BGU)) {
            soloRepository.save(soloRegistration);
            //send notification
            return new RegSuccess(registrationId, false, 0);
        } else {
            return new RegSuccess(registrationId, true, soloRegistration.getEvent().getAmount());
        }
    }

    private OtpResponse doTeamRegistration(Event event, RegistrationRequest request) {

        if (request.email().endsWith("bgu.ac.in")) {
            TeamRegistration teamRegistration = TeamRegistration.builder()
                    .id(UUID.randomUUID().toString())
                    .leaderName(request.name())
                    .teamName(request.teamName())
                    .email(request.email())
                    .phone(request.phone())
                    .studentType(StudentType.BGU)
                    .gender(Gender.valueOf(request.gender()))
                    .event(event)
                    .collegeName("Birla Global University")
                    .build();

            if (request.teamMembers() != null) {
                for (TeamMember member : request.teamMembers()) {
                    member.setId(UUID.randomUUID().toString());
                    member.setTeamRegistration(teamRegistration);
                }
            }
            teamRegistration.setTeamMembers(request.teamMembers());

            teamRegistration = registrationCacheRepo.save(teamRegistration);

            return otpService.sendOtp(teamRegistration.getId(), teamRegistration.getEmail());
        } else {
            TeamRegistration teamRegistration = TeamRegistration.builder()
                    .id(UUID.randomUUID().toString())
                    .leaderName(request.name())
                    .teamName(request.teamName())
                    .email(request.email())
                    .phone(request.phone())
                    .studentType(StudentType.NON_BGU)
                    .gender(Gender.valueOf(request.gender()))
                    .event(event)
                    .collegeName(request.collegeName())
                    .build();

            if (request.teamMembers() != null) {
                for (TeamMember member : request.teamMembers()) {
                    member.setId(UUID.randomUUID().toString());
                    member.setTeamRegistration(teamRegistration);
                }
            }
            teamRegistration.setTeamMembers(request.teamMembers());

            teamRegistration = registrationCacheRepo.save(teamRegistration);

            return otpService.sendOtp(teamRegistration.getId(), teamRegistration.getEmail());

        }
    }

    private void validateTeam(Event event, RegistrationRequest request) {
        if (request.teamMembers() == null || request.teamMembers().size() < event.getMinMember() - 1 || request.teamMembers().size() > event.getMaxMember() - 1) {
            throw new RegistrationProcessingException("Team size should be between " + event.getMinMember() + " and " + event.getMaxMember() + " members.");
        }

        List<TeamRegistration> teamRegistrations = teamRepository.findAllByEvent(event);
        for (TeamRegistration teamRegistration : teamRegistrations) {
            if (teamRegistration.getTeamName().equals(request.teamName())) {
                throw new RegistrationProcessingException("Team name " + request.teamName() + " already exists.");
            }

            for (TeamMember member : teamRegistration.getTeamMembers()) {
                if (request.teamMembers().contains(member.getEmail())) {
                    throw new RegistrationProcessingException("Email " + member.getEmail() + " is already registered.");
                }
            }
        }
    }

    private OtpResponse doSoloRegistration(Event event, RegistrationRequest request) {
        if (request.email().endsWith("bgu.ac.in")) {
            SoloRegistration soloRegistration = SoloRegistration.builder()
                    .id(UUID.randomUUID().toString())
                    .name(request.name())
                    .email(request.email())
                    .phone(request.phone())
                    .studentType(StudentType.BGU)
                    .gender(Gender.valueOf(request.gender()))
                    .event(event)
                    .collegeName("Birla Global University")
                    .build();
            soloRegistration = registrationCacheRepo.save(soloRegistration);

            return otpService.sendOtp(soloRegistration.getId(), soloRegistration.getEmail());
        } else {
            SoloRegistration soloRegistration = SoloRegistration.builder()
                    .id(UUID.randomUUID().toString())
                    .name(request.name())
                    .email(request.email())
                    .phone(request.phone())
                    .studentType(StudentType.NON_BGU)
                    .gender(Gender.valueOf(request.gender()))
                    .event(event)
                    .collegeName(request.collegeName())
                    .build();
            soloRegistration = registrationCacheRepo.save(soloRegistration);

            return otpService.sendOtp(soloRegistration.getId(), soloRegistration.getEmail());
        }
    }

    private Optional<TeamRegistration> getByTeamEventAndLeader(Event event, String email) {
        return teamRepository.findByEmailAndEvent(email, event);
    }

    private Optional<SoloRegistration> getBySoloEventAndUser(Event event, String email) {
        return soloRepository.findByEmailAndEvent(email, event);
    }
}
