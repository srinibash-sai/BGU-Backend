package com.anup.bgu.event.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EventResponse(
        String id,
        String title,
        String description,
        String category,
        String status,
        String rules,
        String dateTime,
        String coordinatorName,
        String coordinatorNumber,
        String eventType,
        Integer maxMembers,
        Integer minMembers,
        Integer currentRegistration,
        String imageUri
) {
}
