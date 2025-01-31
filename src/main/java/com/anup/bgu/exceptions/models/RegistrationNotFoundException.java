package com.anup.bgu.exceptions.models;

public class RegistrationNotFoundException extends RuntimeException{
    public RegistrationNotFoundException(String message) {
        super(message);
    }
}
