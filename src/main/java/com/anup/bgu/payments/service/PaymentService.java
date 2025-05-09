package com.anup.bgu.payments.service;

import org.springframework.web.multipart.MultipartFile;

public interface PaymentService {
    void addPayment(MultipartFile file, String transactionId, String amount, String registrationId);
    byte[] getPaymentImage(String id);
}
