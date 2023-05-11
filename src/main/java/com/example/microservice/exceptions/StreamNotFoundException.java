package com.example.microservice.exceptions;

public class StreamNotFoundException extends RuntimeException {
    public StreamNotFoundException(String message) {
        super(message);
    }
}
