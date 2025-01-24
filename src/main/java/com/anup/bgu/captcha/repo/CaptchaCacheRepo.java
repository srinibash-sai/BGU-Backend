package com.anup.bgu.captcha.repo;

public interface CaptchaCacheRepo {
    void addValueToSet(String captchaHash);
    Boolean isMemberOfSet(String captchaHash);
}
