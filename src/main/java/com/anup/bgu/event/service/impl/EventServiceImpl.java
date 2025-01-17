package com.anup.bgu.event.service.impl;

import com.anup.bgu.event.dto.EventRequest;
import com.anup.bgu.event.dto.EventUpdateRequest;
import com.anup.bgu.event.entities.Event;
import com.anup.bgu.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    @Override
    public Event createEvent(EventRequest request, MultipartFile file) {
        return null;
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