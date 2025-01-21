package com.anup.bgu.registration.controller;

import com.anup.bgu.registration.dto.RegSuccess;
import com.anup.bgu.registration.dto.RegistrationRequest;
import com.anup.bgu.registration.service.RegistrationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Validated
@Slf4j
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping("/register/{id}")
    public ResponseEntity<RegSuccess> updateEvent(
            @PathVariable("id") @NotEmpty String id,
            @RequestBody @Valid RegistrationRequest registrationRequest
    ) {
        RegSuccess regSuccess = registrationService.register(id, registrationRequest);
        return new ResponseEntity<>(regSuccess, HttpStatus.OK);
    }

}
