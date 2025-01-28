package com.anup.bgu.event.service.impl;

import com.anup.bgu.event.dto.EventRequest;
import com.anup.bgu.event.dto.EventResponse;
import com.anup.bgu.event.dto.EventUpdateRequest;
import com.anup.bgu.event.entities.Event;
import com.anup.bgu.event.entities.EventTeamType;
import com.anup.bgu.event.entities.EventType;
import com.anup.bgu.event.entities.Status;
import com.anup.bgu.event.mapper.EventMapper;
import com.anup.bgu.event.repo.EventRepository;
import com.anup.bgu.event.service.EventService;
import com.anup.bgu.exceptions.models.EventNotFoundException;
import com.anup.bgu.exceptions.models.InvalidRequestException;
import com.anup.bgu.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final ImageService imageService;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

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

        if(event.getTeamType().equals(EventTeamType.TEAM))
        {
            event.setMinMember(eventRequest.minTeamSize());
            event.setMaxMember(eventRequest.maxTeamSize());
        }

        event = eventRepository.save(event);

        //send notification

        return event;
    }

    @Override
    public Event updateEvent(String id, EventUpdateRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(()->new EventNotFoundException("Event id: " + id + "does not exist!"));

        if(request.title() != null) {
            event.setTitle(request.title());
        }
        if(request.description()!=null){
            event.setDescription(request.description());
        }
        if(request.status()!=null){
            event.setStatus(Status.valueOf(request.status()));
            if(event.getStatus().equals(Status.ONGOING))
            {
                //send notification
            }
            if(event.getStatus().equals(Status.CLOSED))
            {
                //send notification
            }
        }
        if(request.rules()!=null){
            event.setRules(request.rules());
        }
        if(request.dateTime()!=null){
            event.setDateTime(request.dateTime());
        }
        if(request.coordinatorName()!=null){
            event.setCoordinatorName(request.coordinatorName());
        }
        if(request.coordinatorNumber()!=null){
            event.setCoordinatorNumber(request.coordinatorNumber());
        }
        if(request.amount()!=null){
            event.setAmount(request.amount());
        }
        if(request.teamType()!=null){
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
        if(request.eventType()!=null){
            event.setEventType(EventType.valueOf(request.eventType()));
        }
        event = eventRepository.save(event);
        return event;
    }

    @Override
    public Event deleteEvent(String id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(()->new EventNotFoundException("Event id: " + id + "does not exist!"));
        eventRepository.delete(event);
        return event;
    }

    @Override
    public List<EventResponse> getAllEvents(String eventType, String status) {
        List<Event> events;
        if (eventType != null && status != null) {
            events = eventRepository.findByEventTypeAndStatus(
                    EventType.valueOf(eventType.toUpperCase()),
                    Status.valueOf(status.toUpperCase())
            );
        } else if (eventType != null) {
            // If only eventType is provided, return all statuses for the given eventType
            events = eventRepository.findByEventType(EventType.valueOf(eventType.toUpperCase()));
        } else if (status != null) {
            // If only status is provided, return all event types for the given status
            events = eventRepository.findByStatus(Status.valueOf(status.toUpperCase()));
        } else {
            // If neither parameter is provided, return all events
            events = eventRepository.findAll();
        }

        // Map events to EventResponse DTOs
        return eventMapper.toEventResponseList(events);
    }

    @Override
    public Event getEventById(String id) {
        return eventRepository.findById(id)
                .orElseThrow(()->new EventNotFoundException("Event id: " + id + "does not exist!"));
    }

    @Override
    public byte[] getEventImage(String id) {
        Event event = getEventById(id);
        return imageService.getImage(event.getPathToImage());
    }

    @Override
    public String updateEventImage(String id, MultipartFile file) {
        Event event = eventRepository.findById(id)
                .orElseThrow(()->new EventNotFoundException("Event id: " + id + "does not exist!"));
        String imagePath = imageService.saveImage(file, id);
        event.setPathToImage(imagePath);
        eventRepository.save(event);
        return imagePath;
    }

    @Override
    public void increaseRegistrationCount(String id) {
        eventRepository.findById(id).ifPresent(e -> {
            e.setCurrentRegistration(e.getCurrentRegistration() + 1);
            eventRepository.save(e);
        });
    }
}