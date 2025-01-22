package com.anup.bgu.registration.controller;

import com.anup.bgu.event.dto.EventResponse;
import com.anup.bgu.otp.dto.OtpResponse;
import com.anup.bgu.registration.dto.RegSuccess;
import com.anup.bgu.registration.dto.RegistrationRequest;
import com.anup.bgu.registration.service.RegistrationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
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

    @PostMapping("/register/{id}")
    public ResponseEntity<OtpResponse> register(
            @PathVariable("id") @NotEmpty String id,
            @RequestBody @Valid RegistrationRequest registrationRequest
    ) {
        OtpResponse otpResponse = registrationService.register(id, registrationRequest);
        return new ResponseEntity<>(otpResponse, HttpStatus.OK);
    }

    @PostMapping("/verifyotp")
    public ResponseEntity<RegSuccess> verifyOTP(
            @RequestParam(value = "registrationId", required = true)
            String registrationId,

            @RequestParam(value = "otp", required = true)
            String otp
    ) {
        RegSuccess regSuccess = registrationService.verifyOtp(registrationId, otp);
        return new ResponseEntity<>(regSuccess, HttpStatus.OK);
    }
}
