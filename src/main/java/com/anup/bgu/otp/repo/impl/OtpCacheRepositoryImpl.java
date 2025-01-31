package com.anup.bgu.otp.repo.impl;

import com.anup.bgu.otp.entities.OtpCache;
import com.anup.bgu.otp.repo.OtpCacheRepository;
import com.anup.bgu.registration.entities.SoloRegistration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OtpCacheRepositoryImpl implements OtpCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String HASH_KEY = "OTP_CACHE";
    private final Duration timeToLive = Duration.ofMinutes(5);

    @Override
    public OtpCache save(OtpCache otpCache) {
        redisTemplate.opsForHash().put(HASH_KEY, otpCache.getRegistrationId(), otpCache);
        redisTemplate.expire(HASH_KEY, timeToLive);

        return otpCache;
    }

    @Override
    public void delete(OtpCache otpCache) {
        redisTemplate.opsForHash().delete(HASH_KEY, otpCache.getRegistrationId());
    }

    @Override
    public Optional<OtpCache> findById(String registrationId) {
        Object o = redisTemplate.opsForHash().get(HASH_KEY, registrationId);
        if(o==null)
        {
            return Optional.empty();
        }
        return Optional.of((OtpCache) o);
    }
}
