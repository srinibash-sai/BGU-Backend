package com.anup.bgu.registration.repo;

import com.anup.bgu.event.entities.Event;
import com.anup.bgu.registration.entities.TeamRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRegistrationRepository extends JpaRepository<TeamRegistration, String> {
    Optional<TeamRegistration> findByEmailAndEvent(String email, Event event);
    List<TeamRegistration> findAllByEvent(Event event);
}