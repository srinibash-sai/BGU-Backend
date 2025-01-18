package com.anup.bgu.event.service.impl;

import com.anup.bgu.event.dto.EventRequest;
import com.anup.bgu.event.dto.EventUpdateRequest;
import com.anup.bgu.event.entities.Event;
import com.anup.bgu.event.entities.EventTeamType;
import com.anup.bgu.event.entities.EventType;
import com.anup.bgu.event.entities.Status;
import com.anup.bgu.event.repo.EventRepository;
import com.anup.bgu.event.service.EventService;
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

    @Override
    public Event createEvent(EventRequest eventRequest, MultipartFile file) {
        final String id = UUID.randomUUID().toString();

        String imagePath = imageService.saveImage(file, id);

        Event event = Event.builder()
                .id(id)
                .title(eventRequest.title())
                .description(eventRequest.description())
                .pathToImage(imagePath)
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
    public Event updateEvent(String id, EventUpdateRequest request, MultipartFile file) {
        return null;
    }

    @Override
    public List<Event> getAllEventsByType(String eventType, String status) {
        return null;
    }

    @Override
    public Event getEventById(String id) {
        return null;
    }

    @Override
    public String getEventPicture(String id) {
        return null;
    }
}