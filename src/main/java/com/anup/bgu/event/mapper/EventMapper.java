package com.anup.bgu.event.mapper;

import com.anup.bgu.event.dto.EventResponse;
import com.anup.bgu.event.entities.Event;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventMapper {

    @Value("${secret.api-base-url}")
    private String API_BASE_URL;

    public EventResponse toEventResponse(Event event) {
        return new EventResponse(
            event.getId(),
            event.getTitle(),
            event.getDescription(),
            event.getStatus().toString(),
            event.getRules(),
            event.getDateTime(),
            event.getCoordinatorName(),
            event.getCoordinatorNumber(),
            event.getEventType().toString(),
            event.getTeamType().toString(),
            event.getMaxMember(),
            event.getMinMember(),
            event.getCurrentRegistration(),
            API_BASE_URL+ "/events/image/" + event.getId(),
            event.getAmount()
        );
    }

    public List<EventResponse> toEventResponseList(List<Event> events) {
        return events.stream()
                .map(this::toEventResponse)
                .collect(Collectors.toList());
    }
}