package com.anup.bgu.event.repo;

import com.anup.bgu.event.entities.Event;
import com.anup.bgu.event.entities.EventType;
import com.anup.bgu.event.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, String> {
    List<Event> findByEventTypeAndStatus(EventType eventType, Status status);
    List<Event> findByEventType(EventType eventType);
    List<Event> findByStatus(Status status);
}
