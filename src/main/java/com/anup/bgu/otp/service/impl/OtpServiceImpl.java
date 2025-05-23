package com.anup.bgu.otp.service.impl;

import com.anup.bgu.exceptions.models.BadOtpException;
import com.anup.bgu.mail.dto.MailData;
import com.anup.bgu.otp.dto.OtpResponse;
import com.anup.bgu.otp.entities.OtpCache;
import com.anup.bgu.otp.repo.OtpCacheRepository;
import com.anup.bgu.otp.service.OtpService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpCacheRepository otpCacheRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final int OTP_LENGTH = 6;

    public String generateOTP() {
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(secureRandom.nextInt(10));
        }
        return otp.toString();
    }

    @Override
    public OtpResponse sendOtp(String registrationId, String email) {
        String otp = generateOTP();
        OtpCache otpCache = OtpCache.builder()
                .registrationId(registrationId)
                .otp(otp)
                .email(email)
                .build();

        otpCacheRepository.save(otpCache);

        log.debug("sendOtp()->  {}", otpCache);

        Map<String, Object> variables = new HashMap<>();
        variables.put("otp", otp);

        MailData mailData = new MailData(
                email,
                "OTP Verification",
                "mail-templates/otp-template",
                variables
        );

        redisTemplate.convertAndSend("mail", mailData);

        return new OtpResponse(registrationId);
    }

    @Override
    public void verifyOtp(String registrationId, String otp) {
        Optional<OtpCache> otpCacheOptional = otpCacheRepository.findById(registrationId);
        if (otpCacheOptional.isEmpty()) {
            log.debug("verifyOtp()-> Otp Expired! OTP: {}, Registration ID: {}", otp, registrationId);
            throw new BadOtpException("Otp Expired! Please try again.");
        }
        OtpCache otpCache = otpCacheOptional.get();
        if (!otpCache.getOtp().equals(otp)) {
            log.debug("verifyOtp()-> Otp Not match! OTP: {}, Registration ID: {}", otp, registrationId);
            throw new BadOtpException("Otp Not match! Please try again.");
        }
        otpCacheRepository.delete(otpCache);
    }
}
