package com.example.movieservice.infrastructure.config.exceptions;

/**
 * Exception thrown when an internal server error occurs.
 * <p>
 * This exception is typically thrown when there's a database error
 * or any other infrastructure-related failure.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
public class InternalServerErrorException extends RuntimeException {
    
    public InternalServerErrorException(String message) {
        super(message);
    }
    
    public InternalServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}