package com.example.medicalsystem.exception;

public class ModelOperationException extends RuntimeException {
    public ModelOperationException(String message) {
        super(message);
    }

    public ModelOperationException(String message, Throwable cause) {
        super(message, cause);
    }
} 