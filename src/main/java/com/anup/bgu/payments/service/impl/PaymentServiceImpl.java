package com.anup.bgu.payments.service.impl;

import com.anup.bgu.event.entities.Event;
import com.anup.bgu.exceptions.models.PaymentConflictException;
import com.anup.bgu.image.service.ImageService;
import com.anup.bgu.payments.entities.Payment;
import com.anup.bgu.payments.repo.PaymentRepository;
import com.anup.bgu.payments.service.PaymentService;
import com.anup.bgu.registration.entities.SoloRegistration;
import com.anup.bgu.registration.entities.TeamRegistration;
import com.anup.bgu.registration.repo.RegistrationCacheRepo;
import com.anup.bgu.registration.repo.SoloRegistrationRepository;
import com.anup.bgu.registration.repo.TeamRegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final SoloRegistrationRepository soloRepository;
    private final TeamRegistrationRepository teamRepository;
    private final RegistrationCacheRepo registrationCacheRepo;
    private final PaymentRepository paymentRepository;
    private final ImageService imageService;

    @Override
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
            String pathToImage = imageService.saveImage(file, transactionId);
            Payment payment=Payment.builder()
                    .id(UUID.randomUUID().toString())
                    .transactionId(transactionId)
                    .amount(amount)
                    .pathToScreenshot(pathToImage)
                    .build();

            soloRegistration.setPayment(payment);

            soloRepository.save(soloRegistration);
        }
        else if(teamRegistrationOptional.isPresent()) {
            //do team
            TeamRegistration teamRegistration = teamRegistrationOptional.get();

            if (teamRegistration.getEvent().getAmount() != amount) {
                throw new PaymentConflictException("Payment amount for " + teamRegistration.getEvent().getTitle() + " is " + teamRegistration.getEvent().getAmount());
            }
            String pathToImage = imageService.saveImage(file, transactionId);
            Payment payment=Payment.builder()
                    .id(UUID.randomUUID().toString())
                    .transactionId(transactionId)
                    .amount(amount)
                    .pathToScreenshot(pathToImage)
                    .build();

            teamRegistration.setPayment(payment);

            teamRepository.save(teamRegistration);
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