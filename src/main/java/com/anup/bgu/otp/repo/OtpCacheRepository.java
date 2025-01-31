package com.anup.bgu.otp.repo;

import com.anup.bgu.otp.entities.OtpCache;

import java.util.Optional;

public interface OtpCacheRepository {
    OtpCache save(OtpCache otpCache);
    void delete(OtpCache otpCache);
    Optional<OtpCache> findById(String registrationId);
}
