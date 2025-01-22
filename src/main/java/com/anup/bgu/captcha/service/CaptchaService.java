package com.anup.bgu.captcha.service;

import com.anup.bgu.captcha.dto.CaptchaResponse;

public interface CaptchaService {
    public void validateCaptcha(String userAnswer,String hash);
    public CaptchaResponse getCaptcha();
}
