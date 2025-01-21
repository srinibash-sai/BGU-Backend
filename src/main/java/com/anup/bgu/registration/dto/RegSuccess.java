package com.anup.bgu.registration.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RegSuccess(
        String registrationId,
        Boolean paymentRequired,
        Integer amount
) {
}
