package com.anup.bgu.payments.service;

import org.springframework.web.multipart.MultipartFile;

public interface PaymentService {
    void addPayment(MultipartFile file, String transactionId, Integer amount, String registrationId);
    byte[] getPaymentImage(String id);
}
