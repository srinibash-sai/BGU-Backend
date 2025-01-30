package com.anup.bgu.event.service.impl;

import com.anup.bgu.event.dto.EventRequest;
import com.anup.bgu.event.dto.EventResponse;
import com.anup.bgu.event.dto.EventUpdateRequest;
import com.anup.bgu.event.entities.Event;
import com.anup.bgu.event.entities.EventTeamType;
import com.anup.bgu.event.entities.EventType;
import com.anup.bgu.event.entities.Status;
import com.anup.bgu.event.mapper.EventMapper;
import com.anup.bgu.event.repo.EventCacheRepo;
import com.anup.bgu.event.repo.EventRepository;
import com.anup.bgu.event.service.EventService;
import com.anup.bgu.exceptions.models.EventNotFoundException;
import com.anup.bgu.exceptions.models.InvalidRequestException;
import com.anup.bgu.image.service.ImageService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final ImageService imageService;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventCacheRepo eventCacheRepo;

    @Override
    public Event createEvent(EventRequest eventRequest) {
        final String id = UUID.randomUUID().toString();

        Event event = Event.builder()
                .id(id)
                .title(eventRequest.title())
                .description(eventRequest.description())
                .status(Status.valueOf(eventRequest.status()))
                .rules(eventRequest.rules())
                .dateTime(eventRequest.dateTime())
                .amount(eventRequest.amount())
                .eventType(EventType.valueOf(eventRequest.eventType()))
                .coordinatorName(eventRequest.coordinatorName())
                .coordinatorNumber(eventRequest.coordinatorNumber())
                .teamType(EventTeamType.valueOf(eventRequest.teamType()))
                .build();

        if (event.getTeamType().equals(EventTeamType.TEAM)) {
            event.setMinMember(eventRequest.minTeamSize());
            event.setMaxMember(eventRequest.maxTeamSize());
        }

        event = eventRepository.save(event);
        eventCacheRepo.save(event);

        //send notification

        return event;
    }

    @Override
    public Event updateEvent(String id, EventUpdateRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event id: " + id + "does not exist!"));

        if (request.title() != null) {
            event.setTitle(request.title());
        }
        if (request.description() != null) {
            event.setDescription(request.description());
        }
        if (request.status() != null) {
            event.setStatus(Status.valueOf(request.status()));
            if (event.getStatus().equals(Status.ONGOING)) {
                //send notification
            }
            if (event.getStatus().equals(Status.CLOSED)) {
                //send notification
            }
        }
        if (request.rules() != null) {
            event.setRules(request.rules());
        }
        if (request.dateTime() != null) {
            event.setDateTime(request.dateTime());
        }
        if (request.coordinatorName() != null) {
            event.setCoordinatorName(request.coordinatorName());
        }
        if (request.coordinatorNumber() != null) {
            event.setCoordinatorNumber(request.coordinatorNumber());
        }
        if (request.amount() != null) {
            event.setAmount(request.amount());
        }
        if (request.teamType() != null) {
            event.setTeamType(EventTeamType.valueOf(request.teamType()));
            if (request.teamType().equals("TEAM") && request.maxTeamSize() < request.minTeamSize()) {
                throw new InvalidRequestException("Team events require at least minimum 2 or higher.");
            } else if (request.teamType().equals("TEAM")) {
                event.setMinMember(request.minTeamSize());
                event.setMaxMember(request.maxTeamSize());
            } else {
                event.setMinMember(0);
                event.setMaxMember(0);
            }
        }
        if (request.eventType() != null) {
            event.setEventType(EventType.valueOf(request.eventType()));
        }
        event = eventRepository.save(event);
        eventCacheRepo.save(event);
        return event;
    }

    @Override
    public Event deleteEvent(String id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event id: " + id + "does not exist!"));
        eventRepository.delete(event);
        return event;
    }

    @Override
    public List<EventResponse> getAllEvents(String eventType, String status) {
        List<Event> events;
        // Check the cache for events based on the provided filters
        if (eventType != null && status != null) {
            // Try to fetch events by EventType and Status from cache
            Optional<List<Event>> cachedEvents = eventCacheRepo.findByEventTypeAndStatus(
                    EventType.valueOf(eventType.toUpperCase()),
                    Status.valueOf(status.toUpperCase())
            );
            if (cachedEvents.isPresent()) {
                events = cachedEvents.get();
            } else {
                events = eventRepository.findByEventTypeAndStatus(
                        EventType.valueOf(eventType.toUpperCase()),
                        Status.valueOf(status.toUpperCase())
                );
                eventCacheRepo.saveAll(events);
            }
        } else if (eventType != null) {
            // Try to fetch events by EventType from cache
            Optional<List<Event>> cachedEvents = eventCacheRepo.findByEventType(
                    EventType.valueOf(eventType.toUpperCase())
            );
            if (cachedEvents.isPresent()) {
                events = cachedEvents.get();
            } else {
                events = eventRepository.findByEventType(EventType.valueOf(eventType.toUpperCase()));
                eventCacheRepo.saveAll(events);
            }
        } else if (status != null) {
            // Try to fetch events by Status from cache
            Optional<List<Event>> cachedEvents = eventCacheRepo.findByStatus(
                    Status.valueOf(status.toUpperCase())
            );
            if (cachedEvents.isPresent()) {
                events = cachedEvents.get();
            } else {
                events = eventRepository.findByStatus(Status.valueOf(status.toUpperCase()));
                eventCacheRepo.saveAll(events);
            }
        } else {
            // Try to fetch all events from cache
            Optional<List<Event>> cachedEvents = eventCacheRepo.findAll();
            if (cachedEvents.isPresent()) {
                events = cachedEvents.get();
            } else {
                events = eventRepository.findAll();
                eventCacheRepo.saveAll(events);
            }
        }

        // Map events to EventResponse DTOs
        return eventMapper.toEventResponseList(events);
    }

    @Override
    public Event getEventById(String id) {
        Optional<Event> cachedEvent = eventCacheRepo.findById(id);

        if (cachedEvent.isPresent()) return cachedEvent.get();

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event id: " + id + " does not exist!"));

        eventCacheRepo.save(event);
        return event;
    }

    @Override
    public byte[] getEventImage(String id) {
        Event event = getEventById(id);
        return imageService.getImage(event.getPathToImage());
    }

    @Override
    public String updateEventImage(String id, MultipartFile file) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event id: " + id + "does not exist!"));
        String imagePath = imageService.saveImage(file, id);
        event.setPathToImage(imagePath);
        eventRepository.save(event);
        eventCacheRepo.save(event);
        return imagePath;
    }

    @Override
    public void increaseRegistrationCount(String id) {
        eventRepository.findById(id).ifPresent(e -> {
            e.setCurrentRegistration(e.getCurrentRegistration() + 1);
            eventRepository.save(e);
            eventCacheRepo.save(e);
        });
    }
}