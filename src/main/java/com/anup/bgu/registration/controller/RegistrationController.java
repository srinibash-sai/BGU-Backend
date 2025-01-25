package com.anup.bgu.registration.controller;

import com.anup.bgu.captcha.service.CaptchaService;
import com.anup.bgu.otp.dto.OtpRequest;
import com.anup.bgu.otp.dto.OtpResponse;
import com.anup.bgu.registration.dto.RegSuccess;
import com.anup.bgu.registration.dto.RegistrationRequest;
import com.anup.bgu.registration.dto.RegistrationResponse;
import com.anup.bgu.registration.service.RegistrationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Validated
@Slf4j
public class RegistrationController {
    private final RegistrationService registrationService;
    private final CaptchaService captchaService;

    @PostMapping("/register/{id}")
    public ResponseEntity<OtpResponse> register(
            @PathVariable("id") @NotEmpty String id,
            @RequestBody @Valid RegistrationRequest registrationRequest,
            @CookieValue(value = "captcha_hash", defaultValue = "") String captchaHash
    ) {
        captchaService.validateCaptcha(registrationRequest.captcha(),captchaHash);

        OtpResponse otpResponse = registrationService.register(id, registrationRequest);

        return new ResponseEntity<>(otpResponse, HttpStatus.OK);
    }

    @GetMapping("/getAllRegistration/{id}")
    public ResponseEntity<List<RegistrationResponse>> getAllRegistration(
            @PathVariable("id") @NotEmpty String id
    ) {
        List<RegistrationResponse> allRegistration = registrationService.getAllRegistration(id);
        log.info("getAllRegistration() -> {}",allRegistration.toString());

        return new ResponseEntity<>(allRegistration, HttpStatus.OK);
    }

    @PostMapping("/verifyotp")
    public ResponseEntity<RegSuccess> verifyOTP(
            @RequestBody @Valid OtpRequest otpRequest
    ) {
        RegSuccess regSuccess = registrationService.verifyOtp(otpRequest);
        return new ResponseEntity<>(regSuccess, HttpStatus.OK);
    }
}
