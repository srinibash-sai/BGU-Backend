package com.anup.bgu.otp.service;

import com.anup.bgu.otp.dto.OtpResponse;

public interface OtpService {
    OtpResponse sendOtp(String registrationId,String email);
    void verifyOtp(String registrationId,String otp);
}
