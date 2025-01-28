package com.anup.bgu.payments.service.impl;

import com.anup.bgu.event.entities.Event;
import com.anup.bgu.event.repo.EventRepository;
import com.anup.bgu.event.service.EventService;
import com.anup.bgu.exceptions.models.PaymentConflictException;
import com.anup.bgu.image.service.ImageService;
import com.anup.bgu.mail.dto.MailData;
import com.anup.bgu.payments.entities.Payment;
import com.anup.bgu.payments.repo.PaymentRepository;
import com.anup.bgu.payments.service.PaymentService;
import com.anup.bgu.registration.entities.SoloRegistration;
import com.anup.bgu.registration.entities.TeamMember;
import com.anup.bgu.registration.entities.TeamRegistration;
import com.anup.bgu.registration.repo.RegistrationCacheRepo;
import com.anup.bgu.registration.repo.SoloRegistrationRepository;
import com.anup.bgu.registration.repo.TeamRegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final SoloRegistrationRepository soloRepository;
    private final TeamRegistrationRepository teamRepository;
    private final RegistrationCacheRepo registrationCacheRepo;
    private final PaymentRepository paymentRepository;
    private final ImageService imageService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final EventService eventService;

    @Override
    @Transactional
    public Payment addPayment(MultipartFile file, String transactionId, Integer amount, String registrationId) {

        validateIfTransactionIdExists(transactionId);

        Optional<SoloRegistration> soloRegistrationOptional = registrationCacheRepo.findSoloRegistrationById(registrationId);
        Optional<TeamRegistration> teamRegistrationOptional = registrationCacheRepo.findTeamRegistrationById(registrationId);

        if (soloRegistrationOptional.isPresent()) {
            //do solo
            SoloRegistration soloRegistration = soloRegistrationOptional.get();
            if (soloRegistration.getEvent().getAmount() != amount) {
                throw new PaymentConflictException("Payment amount for " + soloRegistration.getEvent().getTitle() + " is " + soloRegistration.getEvent().getAmount());
            }
            Payment payment=Payment.builder()
                    .id(UUID.randomUUID().toString())
                    .transactionId(transactionId)
                    .amount(amount)
                    .build();

            String pathToImage = imageService.savePaymentImage(file, soloRegistration.getEvent().getId(),payment.getId());
            payment.setPathToScreenshot(pathToImage);

            soloRegistration.setPayment(payment);

            soloRepository.save(soloRegistration);
            eventService.increaseRegistrationCount(soloRegistration.getEvent().getId());

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

        }
        else if(teamRegistrationOptional.isPresent()) {
            //do team
            TeamRegistration teamRegistration = teamRegistrationOptional.get();

            if (teamRegistration.getEvent().getAmount() != amount) {
                throw new PaymentConflictException("Payment amount for " + teamRegistration.getEvent().getTitle() + " is " + teamRegistration.getEvent().getAmount());
            }
            Payment payment=Payment.builder()
                    .id(UUID.randomUUID().toString())
                    .transactionId(transactionId)
                    .amount(amount)
                    .build();

            String pathToImage = imageService.savePaymentImage(file, teamRegistration.getEvent().getId(),payment.getId());
            payment.setPathToScreenshot(pathToImage);

            teamRegistration.setPayment(payment);

            teamRepository.save(teamRegistration);
            eventService.increaseRegistrationCount(teamRegistration.getEvent().getId());

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
        }

        return null;
    }

    @Override
    public byte[] getPaymentImage(String id) {
        Optional<Payment> optionalPayment = paymentRepository.findById(id);
        if (optionalPayment.isEmpty()) {
            throw new PaymentConflictException("Screenshot Not exist!");
        }
        Payment payment=optionalPayment.get();

        return imageService.getImage(payment.getPathToScreenshot());
    }

    private void validateIfTransactionIdExists(String transactionId) {
        Optional<Payment> optionalPayment = paymentRepository.findByTransactionId(transactionId);
        if (optionalPayment.isPresent()) {
            throw new PaymentConflictException("This transaction id " + transactionId + " already exists.");
        }
    }
}