package com.anup.bgu.registration.service;

import com.anup.bgu.registration.dto.RegSuccess;
import com.anup.bgu.registration.dto.RegistrationRequest;

public interface RegistrationService {
    RegSuccess register(String eventId, RegistrationRequest request);
}
