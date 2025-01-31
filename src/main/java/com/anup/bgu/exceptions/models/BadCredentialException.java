package com.anup.bgu.exceptions.models;

public class BadCredentialException extends RuntimeException{
    public BadCredentialException(String message) {
        super(message);
    }
}
