package com.example.userservice.infrastructure.config.exceptions;

/**
 * Exception thrown when a requested resource is not found.
 * <p>
 * This exception is used in the Hexagonal Architecture infrastructure layer
 * to indicate that a requested entity or resource could not be located.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
public class NotFoundException extends RuntimeException {

    /**
     * Constructs a new NotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new NotFoundException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}