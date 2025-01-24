package com.anup.bgu.captcha.repo.impl;

import com.anup.bgu.captcha.repo.CaptchaCacheRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class CaptchaCacheRepoImpl implements CaptchaCacheRepo {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String SET_KEY = "CAPTCHA_CACHE";
    private final Duration timeToLive = Duration.ofMinutes(5);

    @Override
    public void addValueToSet(String captchaHash) {
        redisTemplate.opsForSet().add(SET_KEY, captchaHash);
        redisTemplate.expire(SET_KEY, timeToLive);
    }

    @Override
    public Boolean isMemberOfSet(String captchaHash) {
        return redisTemplate.opsForSet().isMember(SET_KEY, captchaHash);
    }
}
