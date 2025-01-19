package com.anup.bgu.event.controller;

import com.anup.bgu.event.dto.EventRequest;
import com.anup.bgu.event.dto.EventResponse;
import com.anup.bgu.event.dto.EventUpdateRequest;
import com.anup.bgu.event.entities.Event;
import com.anup.bgu.event.mapper.EventMapper;
import com.anup.bgu.event.service.EventService;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Validated
@Slf4j
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    // Create a new event
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @RequestBody @Valid EventRequest eventRequest
    ) {
        Event event = eventService.createEvent(eventRequest);
        return new ResponseEntity<>(eventMapper.toEventResponse(event), HttpStatus.CREATED);
    }

    // Update an event by ID
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable("id") @NotEmpty String id,
            @RequestBody @Valid EventUpdateRequest eventUpdateRequest
    ) {
        var event = eventService.updateEvent(id, eventUpdateRequest);
        return new ResponseEntity<>(eventMapper.toEventResponse(event), HttpStatus.OK);
    }

    // Get all events by type and status
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents(
            @Pattern(regexp = "^(SPECTRA|ATOS|YOLO)$", message = "Please provide valid event type. SPECTRA|ATOS|YOLO")
            @RequestParam(value = "eventType", required = false)
            String eventType,

            @Pattern(regexp = "^(ONGOING|CLOSED|UPCOMING)$", message = "Please provide valid status. ONGOING|CLOSED|UPCOMING")
            @RequestParam(value = "status", required = false)
            String status
    ) {
        List<EventResponse> events = eventService.getAllEvents(eventType, status);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    // Get a single event by ID
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(
            @PathVariable("id") @NotEmpty String id
    ) {
        var event = eventService.getEventById(id);
        return new ResponseEntity<>(eventMapper.toEventResponse(event), HttpStatus.OK);
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> getEventImage(
            @PathVariable("id") @NotEmpty String id
    ) {
        byte[] imageBytes = eventService.getEventImage(id);
        return ResponseEntity
            .ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(imageBytes);
    }

    @PutMapping ("/image/{id}")
    public ResponseEntity<Void> updateEventImage(
            @PathVariable("id") @NotEmpty String id,
            @RequestParam @Valid @NotNull MultipartFile file
    ) {
        eventService.updateEventImage(id,file);
        return ResponseEntity.ok().build();
    }
}
