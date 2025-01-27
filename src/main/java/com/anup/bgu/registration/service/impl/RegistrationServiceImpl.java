package com.anup.bgu.registration.service.impl;

import com.anup.bgu.event.entities.Event;
import com.anup.bgu.event.entities.EventTeamType;
import com.anup.bgu.event.entities.Status;
import com.anup.bgu.event.service.EventService;
import com.anup.bgu.exceptions.models.RegistrationProcessingException;
import com.anup.bgu.mail.dto.MailData;
import com.anup.bgu.otp.dto.OtpRequest;
import com.anup.bgu.otp.dto.OtpResponse;
import com.anup.bgu.otp.service.OtpService;
import com.anup.bgu.registration.dto.RegSuccess;
import com.anup.bgu.registration.dto.RegistrationRequest;
import com.anup.bgu.registration.dto.RegistrationResponse;
import com.anup.bgu.registration.entities.*;
import com.anup.bgu.registration.mapper.RegistrationMapper;
import com.anup.bgu.registration.repo.RegistrationCacheRepo;
import com.anup.bgu.registration.repo.SoloRegistrationRepository;
import com.anup.bgu.registration.repo.TeamRegistrationRepository;
import com.anup.bgu.registration.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {

    public static final String BGU_MAIL_DOMAIN = "bgu.ac.in";
    private final EventService eventService;
    private final SoloRegistrationRepository soloRepository;
    private final TeamRegistrationRepository teamRepository;
    private final RegistrationCacheRepo registrationCacheRepo;
    private final OtpService otpService;
    private final RegistrationMapper registrationMapper;
    private final RedisTemplate<String, Object> redisTemplate;


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
    public RegSuccess verifyOtp(OtpRequest otpRequest) {
        final String registrationId = otpRequest.registrationId();
        final String otp = otpRequest.otp();

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

    @Override
    public List<RegistrationResponse> getAllRegistration(String eventId) {
        Event event = eventService.getEventById(eventId);
        log.info("getAllRegistration() -> {}",event);

        if (event.getTeamType().equals(EventTeamType.SOLO)) {
            List<SoloRegistration> soloRegistrations = soloRepository.findAllByEvent(event);
            log.info("getAllRegistration() -> {}",soloRegistrations);
            return registrationMapper.toSoloListRegistrationResponse(soloRegistrations);
        } else {
            List<TeamRegistration> teamRegistrations = teamRepository.findAllByEvent(event);
            log.info("getAllRegistration() -> {}",teamRegistrations);
            return registrationMapper.toTeamListRegistrationResponse(teamRegistrations);
        }
    }

    private RegSuccess proceedTeam(String registrationId, TeamRegistration teamRegistration) {
        if (teamRegistration.getStudentType().equals(StudentType.BGU)) {
            //BGU team
            teamRepository.save(teamRegistration);

            //email notification
            Map<String, Object> variables = new HashMap<>();
            List<String> teamMembers = teamRegistration.getTeamMembers().stream()
                    .map(TeamMember::getName)
                    .collect(Collectors.toList());

            variables.put("teamName", teamRegistration.getTeamName());
            variables.put("eventTitle", teamRegistration.getEvent().getTitle());
            variables.put("registrationId", registrationId);
            variables.put("eventDateTime", teamRegistration.getEvent().getDateTime());
            variables.put("coordinatorName", teamRegistration.getEvent().getCoordinatorName());
            variables.put("coordinatorNumber", teamRegistration.getEvent().getCoordinatorNumber());
            variables.put("teamMembers", teamMembers);

            String subject = "Team " + teamRegistration.getTeamName() + " - Registration Confirmation for " + teamRegistration.getEvent().getTitle();

            MailData mailData=new MailData(
                    teamRegistration.getEmail(),
                    subject,
                    "team-registration",
                    variables
            );

            redisTemplate.convertAndSend("mail",mailData);

            return new RegSuccess(registrationId, false, 0);

        } else {
            //Non BGU team
            return new RegSuccess(registrationId, true, teamRegistration.getEvent().getAmount());
        }
    }

    private RegSuccess proceedSolo(String registrationId, SoloRegistration soloRegistration) {
        if (soloRegistration.getStudentType().equals(StudentType.BGU)) {
            //BGU solo
            soloRepository.save(soloRegistration);

            //email notification
            Map<String, Object> variables = new HashMap<>();
            variables.put("studentName", soloRegistration.getName());
            variables.put("eventTitle", soloRegistration.getEvent().getTitle());
            variables.put("registrationId", registrationId);
            variables.put("eventDateTime", soloRegistration.getEvent().getDateTime());
            variables.put("coordinatorName", soloRegistration.getEvent().getCoordinatorName());
            variables.put("coordinatorNumber", soloRegistration.getEvent().getCoordinatorNumber());

            MailData mailData=new MailData(
                    soloRegistration.getEmail(),
                    "Registration Complete",
                    "solo-registration",
                    variables
            );
            redisTemplate.convertAndSend("mail",mailData);

            return new RegSuccess(registrationId, false, 0);
        } else {
            //Non BGU solo
            return new RegSuccess(registrationId, true, soloRegistration.getEvent().getAmount());
        }
    }

    private OtpResponse doTeamRegistration(Event event, RegistrationRequest request) {

        if (request.email().endsWith(BGU_MAIL_DOMAIN)) {
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
            if(request.collegeName()==null) throw new RegistrationProcessingException("Please Provide College name");

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
        List<String> requestMemberMail = request.teamMembers().stream()
                .map(TeamMember::getEmail)
                .toList();

        for (TeamRegistration teamRegistration : teamRegistrations) {
            //Check Team name
            if (teamRegistration.getTeamName().equals(request.teamName())) {
                throw new RegistrationProcessingException("Team name '" + request.teamName() + "' already exists.");
            }

            for (TeamMember member : teamRegistration.getTeamMembers()) {
                //Check request mail in other team member mail
                if (requestMemberMail.contains(member.getEmail())) {
                    throw new RegistrationProcessingException("Email " + member.getEmail() + " is already registered.");
                }

                //Check if leader email exist in other team or not
                if(member.getEmail().equals(request.email())) {
                    throw new RegistrationProcessingException("Email " + member.getEmail() + " is already registered.");
                }
            }
        }
    }

    private OtpResponse doSoloRegistration(Event event, RegistrationRequest request) {
        if (request.email().endsWith(BGU_MAIL_DOMAIN)) {
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
            if(request.collegeName()==null) throw new RegistrationProcessingException("Please Provide College name");

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
