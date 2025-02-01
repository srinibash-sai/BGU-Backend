package com.anup.bgu.Noticeboard.dto;

public record NoticeResponse(
        String id,
        String notice,
        String expire
) {
}
