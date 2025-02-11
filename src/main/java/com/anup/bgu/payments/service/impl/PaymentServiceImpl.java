package com.anup.bgu.payments.service.impl;

import com.anup.bgu.event.service.EventService;
import com.anup.bgu.exceptions.models.EmailNotVerifiedException;
import com.anup.bgu.exceptions.models.PaymentConflictException;
import com.anup.bgu.exceptions.models.RegistrationNotFoundException;
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
    public void addPayment(MultipartFile file, String transactionId, String amountStr, String registrationId) {

        Optional<SoloRegistration> soloRegistrationOptional = registrationCacheRepo.findSoloRegistrationById(registrationId);
        Optional<TeamRegistration> teamRegistrationOptional = registrationCacheRepo.findTeamRegistrationById(registrationId);
        int amount = (int) Math.round(Double.parseDouble(amountStr));

        if (soloRegistrationOptional.isPresent()) {
            //do solo
            SoloRegistration soloRegistration = soloRegistrationOptional.get();
            validateIfTransactionIdExists(transactionId);

            if (!soloRegistration.getEmailVerified()) {
                log.debug("addPayment()-> Email Unverified!... {}", soloRegistration.toString());
                throw new EmailNotVerifiedException("Email not verified! Please verify using OTP.");
            }

            if (soloRegistration.getEvent().getAmount() != amount) {
                log.debug("addPayment()-> Not equal payment amount!... {}", soloRegistration.toString());
                throw new PaymentConflictException("Payment amount for " + soloRegistration.getEvent().getTitle() + " is " + soloRegistration.getEvent().getAmount());
            }

            Payment payment = Payment.builder()
                    .id(UUID.randomUUID().toString())
                    .transactionId(transactionId)
                    .amount(amount)
                    .build();

            String pathToImage = imageService.savePaymentImage(file, soloRegistration.getEvent().getId(), payment.getId());
            log.debug("addPayment()-> Path to payment:{}", pathToImage);
            payment.setPathToScreenshot(pathToImage);

            log.debug("addPayment()-> Payment: {}", payment.toString());

            soloRegistration.setPayment(payment);

            soloRepository.save(soloRegistration);
            log.debug("addPayment()->Solo Registration saved : {}", soloRegistration);
            eventService.increaseRegistrationCount(soloRegistration.getEvent().getId());
            registrationCacheRepo.delete(soloRegistration);

            //email notification
            Map<String, Object> variables = new HashMap<>();
            variables.put("studentName", soloRegistration.getName());
            variables.put("eventTitle", soloRegistration.getEvent().getTitle());
            variables.put("registrationId", registrationId);
            variables.put("eventDateTime", soloRegistration.getEvent().getDateTime());
            variables.put("pocName", soloRegistration.getEvent().getCoordinatorName());
            variables.put("pocNumber", soloRegistration.getEvent().getCoordinatorNumber());

            MailData mailData = new MailData(
                    soloRegistration.getEmail(),
                    "Registration Complete",
                    "mail-templates/solo-registration",
                    variables
            );
            redisTemplate.convertAndSend("mail", mailData);

        } else if (teamRegistrationOptional.isPresent()) {
            //do team
            TeamRegistration teamRegistration = teamRegistrationOptional.get();
            validateIfTransactionIdExists(transactionId);

            if (!teamRegistration.getEmailVerified()) {
                log.debug("addPayment()-> Email Unverified!.. {}", teamRegistration.toString());
                throw new EmailNotVerifiedException("Email not verified! Please verify using OTP.");
            }

            if (teamRegistration.getEvent().getAmount() != amount) {
                log.debug("addPayment()-> Not equal payment amount!.. {}", teamRegistration.toString());
                throw new PaymentConflictException("Payment amount for " + teamRegistration.getEvent().getTitle() + " is " + teamRegistration.getEvent().getAmount());
            }
            Payment payment = Payment.builder()
                    .id(UUID.randomUUID().toString())
                    .transactionId(transactionId)
                    .amount(amount)
                    .build();

            String pathToImage = imageService.savePaymentImage(file, teamRegistration.getEvent().getId(), payment.getId());
            log.debug("addPayment()-> Path to payment:{}", pathToImage);
            payment.setPathToScreenshot(pathToImage);

            log.debug("addPayment()-> Payment: {}", payment.toString());


            teamRegistration.setPayment(payment);

            teamRepository.save(teamRegistration);
            log.debug("addPayment()->Team Registration saved : {}", teamRegistration);
            eventService.increaseRegistrationCount(teamRegistration.getEvent().getId());
            registrationCacheRepo.delete(teamRegistration);

            //email notification
            Map<String, Object> variables = new HashMap<>();
            List<String> teamMembers = teamRegistration.getTeamMembers().stream()
                    .map(TeamMember::getName)
                    .collect(Collectors.toList());

            variables.put("teamName", teamRegistration.getTeamName());
            variables.put("eventTitle", teamRegistration.getEvent().getTitle());
            variables.put("registrationId", registrationId);
            variables.put("eventDateTime", teamRegistration.getEvent().getDateTime());
            variables.put("pocName", teamRegistration.getEvent().getCoordinatorName());
            variables.put("pocNumber", teamRegistration.getEvent().getCoordinatorNumber());
            variables.put("teamMembers", teamMembers);
            variables.put("teamLeader", teamRegistration.getLeaderName());

            String subject = "Team " + teamRegistration.getTeamName() + " - Registration Confirmation for " + teamRegistration.getEvent().getTitle();

            MailData mailData = new MailData(
                    teamRegistration.getEmail(),
                    subject,
                    "mail-templates/team-registration",
                    variables
            );

            redisTemplate.convertAndSend("mail", mailData);
        } else {
            throw new RegistrationNotFoundException("Registration Timeout! Please register again.");
        }
    }

    @Override
    public byte[] getPaymentImage(String id) {
        Optional<Payment> optionalPayment = paymentRepository.findById(id);
        if (optionalPayment.isEmpty()) {
            log.warn("getPaymentImage()-> Screenshot Not exist! ID:{} ",id);
            throw new PaymentConflictException("Screenshot Not exist!");
        }
        Payment payment = optionalPayment.get();

        return imageService.getImage(payment.getPathToScreenshot());
    }

    private void validateIfTransactionIdExists(String transactionId) {
        Optional<Payment> optionalPayment = paymentRepository.findByTransactionId(transactionId);
        if (optionalPayment.isPresent()) {
            log.warn("validateIfTransactionIdExists()-> Dublicate Transaction! Transaction ID:{} ",transactionId);
            throw new PaymentConflictException("The transaction id " + transactionId + " already exists.");
        }
    }
}