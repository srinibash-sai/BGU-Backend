package com.anup.bgu.registration.repo;

import com.anup.bgu.registration.entities.SoloRegistration;
import com.anup.bgu.registration.entities.TeamRegistration;

import java.util.Optional;

public interface NonBguRegistrationCacheRepo {
    SoloRegistration save(SoloRegistration soloRegistration);
    TeamRegistration save(TeamRegistration teamRegistration);
    Optional<SoloRegistration> findSoloRegistrationById(String id);
    Optional<TeamRegistration> findTeamRegistrationById(String id);
}
