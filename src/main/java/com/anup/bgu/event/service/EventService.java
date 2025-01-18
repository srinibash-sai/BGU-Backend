package com.anup.bgu.event.service;

import com.anup.bgu.event.dto.EventRequest;
import com.anup.bgu.event.dto.EventResponse;
import com.anup.bgu.event.dto.EventUpdateRequest;
import com.anup.bgu.event.entities.Event;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EventService {
    Event createEvent(EventRequest request, MultipartFile file);
    Event updateEvent(String id, EventUpdateRequest request,MultipartFile file);
    List<EventResponse> getAllEvents(String eventType, String status);
    Event getEventById(String id);
    byte[] getEventImage(String id);
}
