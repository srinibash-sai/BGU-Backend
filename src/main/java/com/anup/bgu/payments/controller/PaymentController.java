package com.anup.bgu.payments.controller;

import com.anup.bgu.payments.entities.Payment;
import com.anup.bgu.payments.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    public ResponseEntity<Void> addPayment(
            @RequestParam(value = "file", required = true)
            @Valid
            @NotNull
            MultipartFile file,

            @RequestParam(value = "transactionId", required = true)
            @NotEmpty @Size(max = 32, message = "Transaction ID can not be more than 32.")
            String transactionId,

            @RequestParam(value = "amount", required = true)
            String amount,

            @RequestParam(value = "registrationId", required = true)
            String registrationId
    ) {
        paymentService.addPayment(file, transactionId, amount, registrationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> getPaymentImage(
            @PathVariable("id") @NotEmpty String id
    ) {
        byte[] imageBytes = paymentService.getPaymentImage(id);
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageBytes);
    }
}
