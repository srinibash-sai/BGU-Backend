package com.anup.bgu.exceptions.dto;

public record ErrorResponse(
        String status,
        String message
) {
}
