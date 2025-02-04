package com.anup.bgu.event.repo;

import com.anup.bgu.event.entities.Event;
import com.anup.bgu.event.entities.EventType;
import com.anup.bgu.event.entities.Status;

import java.util.List;
import java.util.Optional;

public interface EventCacheRepo {
    Event save(Event event);

    void delete(Event event);

    Optional<List<Event>> findAll();

    Optional<Event> findById(String id);

    Optional<List<Event>> findByEventTypeAndStatus(EventType eventType, Status status);

    Optional<List<Event>> findByEventType(EventType eventType);

    Optional<List<Event>> findByStatus(Status status);

    void saveAll(List<Event> events);
}
