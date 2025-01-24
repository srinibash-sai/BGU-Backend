package com.anup.bgu.payments.service;

import com.anup.bgu.payments.entities.Payment;
import org.springframework.web.multipart.MultipartFile;

public interface PaymentService {
    Payment addPayment(MultipartFile file, String transactionId, Integer amount, String registrationId);
    byte[] getPaymentImage(String id);
}
