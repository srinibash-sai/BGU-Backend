package com.anup.bgu.noticeboard.dto;

public record NoticeResponse(
        String id,
        String notice,
        String expire
) {
}
