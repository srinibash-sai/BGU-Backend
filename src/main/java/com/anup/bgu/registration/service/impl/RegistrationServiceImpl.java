package com.anup.bgu.registration.service.impl;

import com.anup.bgu.event.entities.Event;
import com.anup.bgu.event.entities.EventTeamType;
import com.anup.bgu.event.entities.Status;
import com.anup.bgu.event.service.EventService;
import com.anup.bgu.exceptions.models.RegistrationProcessingException;
import com.anup.bgu.registration.dto.RegSuccess;
import com.anup.bgu.registration.dto.RegistrationRequest;
import com.anup.bgu.registration.entities.*;
import com.anup.bgu.registration.repo.NonBguRegistrationCacheRepo;
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
    private final NonBguRegistrationCacheRepo nonBguCacheRepo;

    @Override
    public RegSuccess register(String eventId, RegistrationRequest request) {
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

    private RegSuccess doTeamRegistration(Event event, RegistrationRequest request) {

        if(request.email().endsWith("bgu.ac.in"))
        {
            TeamRegistration teamRegistration=TeamRegistration.builder()
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

            teamRegistration = teamRepository.save(teamRegistration);
            return new RegSuccess(teamRegistration.getId(),false,0);
        }
        else {
            TeamRegistration teamRegistration=TeamRegistration.builder()
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

            teamRegistration = nonBguCacheRepo.save(teamRegistration);
            return new RegSuccess(teamRegistration.getId(),true,event.getAmount());
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

    private RegSuccess doSoloRegistration(Event event, RegistrationRequest request) {
        if(request.email().endsWith("bgu.ac.in"))
        {
            SoloRegistration soloRegistration=SoloRegistration.builder()
                    .id(UUID.randomUUID().toString())
                    .name(request.name())
                    .email(request.email())
                    .phone(request.phone())
                    .studentType(StudentType.BGU)
                    .gender(Gender.valueOf(request.gender()))
                    .event(event)
                    .collegeName("Birla Global University")
                    .build();
            soloRegistration = soloRepository.save(soloRegistration);
            return new RegSuccess(soloRegistration.getId(),false,0);
        }
        else {
            SoloRegistration soloRegistration=SoloRegistration.builder()
                    .id(UUID.randomUUID().toString())
                    .name(request.name())
                    .email(request.email())
                    .phone(request.phone())
                    .studentType(StudentType.NON_BGU)
                    .gender(Gender.valueOf(request.gender()))
                    .event(event)
                    .collegeName(request.collegeName())
                    .build();
            soloRegistration = nonBguCacheRepo.save(soloRegistration);
            return new RegSuccess(soloRegistration.getId(),true,event.getAmount());
        }
    }

    private Optional<TeamRegistration> getByTeamEventAndLeader(Event event, String email) {
        return teamRepository.findByEmailAndEvent(email, event);
    }

    private Optional<SoloRegistration> getBySoloEventAndUser(Event event, String email) {
        return soloRepository.findByEmailAndEvent(email, event);
    }
}
