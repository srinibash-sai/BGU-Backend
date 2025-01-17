package com.anup.bgu.captcha.service.impl;

import cn.apiclub.captcha.Captcha;
import com.anup.bgu.captcha.dto.CaptchaResponse;
import com.anup.bgu.captcha.service.CaptchaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
@Service
public class CaptchaServiceImpl implements CaptchaService {

    @Value("${secret.captcha-secret}")
    private String CAPTCHA_SECRET;

    @Value("${secret.captcha-expiry}")
    private int CAPTCHA_EXPIRY;

    @Override
    public boolean validateCaptcha(String userAnswer, String hash) {
        if (userAnswer == null || hash == null) {
            return false;
        }

        String decoded = new String(Base64.getDecoder().decode(hash));

        String[] parts = decoded.split("\\|");
        if (parts.length != 3) {
            return false;  // Invalid data in the cookie
        }

        String hashedAnswerFromCookie = parts[0];
        long expirationTimestamp = Long.parseLong(parts[1]);
        String hmacFromCookie = parts[2];

        if (!validateHmac(hashedAnswerFromCookie + "|" + expirationTimestamp, hmacFromCookie, CAPTCHA_SECRET)) {
            return false;  // HMAC validation failed (data has been tampered)
        }

        // Check if the CAPTCHA has expired (expiration time is stored in seconds)
        if (Instant.now().getEpochSecond() > expirationTimestamp) {
            return false;  // CAPTCHA expired
        }

        String hashedUserAnswer =hashString(userAnswer);

        return hashedAnswerFromCookie.equals(hashedUserAnswer);  // Compare hashed input with the stored hash
    }

    @Override
    public CaptchaResponse getCaptcha()
    {
        Captcha captcha = createCaptcha();
        byte[] captchaImage = generateCaptchaImage(captcha);

        String hashAnswer= getHashAnswer(captcha);

        return new CaptchaResponse(captchaImage,hashAnswer);
    }

    private String getHashAnswer(Captcha captcha)
    {
        String captchaAnswer=captcha.getAnswer();
        String hashedAnswer=hashString(captchaAnswer);
        long expirationTimestamp= Instant.now().getEpochSecond()+CAPTCHA_EXPIRY;

        String hashWithExpiry = hashedAnswer + "|" + expirationTimestamp;

        String hmac = generateHmac(hashWithExpiry,CAPTCHA_SECRET);

        return hashWithExpiry + "|" + hmac;
    }

    private Captcha createCaptcha()
    {
        return new Captcha.Builder(200,80)
                .addBackground()
                .addText()
                .build();
    }

    private String generateHmac(String data, String secretKey)
    {
        Mac sha256_HMAC = null;
        try {
            sha256_HMAC = Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        try {
            sha256_HMAC.init(secret_key);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        byte[] hashBytes = sha256_HMAC.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(hashBytes);
    }

    private String hashString(String input)
    {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hashBytes = digest.digest(input.getBytes());
        return Base64.getEncoder().encodeToString(hashBytes);  // Base64 encode the hash to store in cookie
    }

    private boolean validateHmac(String data, String hmac, String secretKey)
    {
        String calculatedHmac = generateHmac(data, secretKey);
        return calculatedHmac.equals(hmac);
    }

    private byte[] generateCaptchaImage(Captcha captcha) {
        // Get the image of the CAPTCHA
        BufferedImage captchaImage = captcha.getImage();

        // Convert the image to a byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(captchaImage, "PNG", byteArrayOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
