package com.anup.bgu.exceptions.models;

public class BadOtpException extends RuntimeException{
    public BadOtpException(String message) {
        super(message);
    }
}
