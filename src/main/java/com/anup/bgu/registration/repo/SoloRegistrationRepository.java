package com.anup.bgu.registration.repo;

import com.anup.bgu.event.entities.Event;
import com.anup.bgu.registration.entities.SoloRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SoloRegistrationRepository extends JpaRepository<SoloRegistration, String> {
    Optional<SoloRegistration> findByEmailAndEvent(String email, Event event);
    List<SoloRegistration> findAllByEvent(Event event);
    void deleteByEvent(Event event);
}
