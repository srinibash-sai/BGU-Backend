package com.anup.bgu.admin.dto;

public record AuthRequest(
        String email,
        String password
) {
}
