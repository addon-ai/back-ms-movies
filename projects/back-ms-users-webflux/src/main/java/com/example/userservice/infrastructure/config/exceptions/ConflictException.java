package com.example.userservice.infrastructure.config.exceptions;

/**
 * Exception thrown when a resource conflict occurs.
 * <p>
 * This exception is typically thrown when attempting to create a resource
 * that already exists or when there's a business rule conflict.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
public class ConflictException extends RuntimeException {
    
    public ConflictException(String message) {
        super(message);
    }
    
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}