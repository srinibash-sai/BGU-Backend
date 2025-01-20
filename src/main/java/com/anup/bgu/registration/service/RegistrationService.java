package com.anup.bgu.registration.service;

import com.anup.bgu.registration.dto.RegistrationRequest;

public interface RegistrationService {
    void register(String eventId, RegistrationRequest request);
}
