package com.anup.bgu.payments.controller;

import com.anup.bgu.payments.entities.Payment;
import com.anup.bgu.payments.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
@Validated
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Payment> addPayment(
            @RequestParam(value = "file", required = true)
            @Valid
            @NotNull
            MultipartFile file,

            @RequestParam(value = "transactionId", required = true)
            String transactionId,

            @RequestParam(value = "amount", required = true)
            Integer amount,

            @RequestParam(value = "registrationId", required = true)
            String registrationId
    ) {
        Payment payment = paymentService.addPayment(file, transactionId, amount, registrationId);
        return new ResponseEntity<>(payment,HttpStatus.OK);
    }
}
