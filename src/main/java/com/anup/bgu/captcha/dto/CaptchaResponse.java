package com.anup.bgu.captcha.dto;

public record CaptchaResponse(
        byte[] captchaImage,
        String hash
) {
}
