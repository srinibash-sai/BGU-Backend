package com.anup.bgu.registration.service;

import com.anup.bgu.otp.dto.OtpRequest;
import com.anup.bgu.otp.dto.OtpResponse;
import com.anup.bgu.registration.dto.RegSuccess;
import com.anup.bgu.registration.dto.RegistrationRequest;

public interface RegistrationService {
    OtpResponse register(String eventId, RegistrationRequest request);
    RegSuccess verifyOtp(OtpRequest otpRequest);
}
