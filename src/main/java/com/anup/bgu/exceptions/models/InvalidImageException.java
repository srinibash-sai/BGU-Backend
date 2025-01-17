package com.anup.bgu.exceptions.models;

public class InvalidImageException extends RuntimeException{
    public InvalidImageException(String message){
        super(message);
    }
}
