package com.anup.bgu.captcha.controller;

import com.anup.bgu.captcha.dto.CaptchaResponse;
import com.anup.bgu.captcha.service.CaptchaService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;

    @Value("${secret.captcha-expiry}")
    private int CAPTCHA_EXPIRY;

    @GetMapping(value = "/getCaptcha", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getCaptcha(HttpServletResponse response) {
        CaptchaResponse captchaResponse = captchaService.getCaptcha();

        Cookie captchaCookie = new Cookie(
                "captcha_hash",
                Base64.getEncoder().encodeToString(captchaResponse.hash().getBytes())
        );

        captchaCookie.setPath("/");
        captchaCookie.setMaxAge(CAPTCHA_EXPIRY);
        response.addCookie(captchaCookie);

        byte[] captchaImage = captchaResponse.captchaImage();

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(captchaImage);
    }
}
