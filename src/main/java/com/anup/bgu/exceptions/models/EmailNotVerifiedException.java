package com.anup.bgu.exceptions.models;

public class EmailNotVerifiedException extends RuntimeException{
    public EmailNotVerifiedException(String message) {
        super(message);
    }
}
