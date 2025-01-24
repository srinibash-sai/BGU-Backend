package com.anup.bgu.registration.service;

import com.anup.bgu.otp.dto.OtpRequest;
import com.anup.bgu.otp.dto.OtpResponse;
import com.anup.bgu.registration.dto.RegSuccess;
import com.anup.bgu.registration.dto.RegistrationRequest;
import com.anup.bgu.registration.dto.RegistrationResponse;

import java.util.List;

public interface RegistrationService {
    OtpResponse register(String eventId, RegistrationRequest request);
    RegSuccess verifyOtp(OtpRequest otpRequest);
    List<RegistrationResponse> getAllRegistration(String eventId);
}
