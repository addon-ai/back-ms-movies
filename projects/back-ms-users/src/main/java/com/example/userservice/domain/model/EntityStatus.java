package com.example.userservice.domain.model;

/**
 * Enumeration representing the status of entities in the system.
 * <p>
 * This enum defines the possible states that any entity can have,
 * providing a standardized way to manage entity lifecycle states
 * across the Clean Architecture layers.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
public enum EntityStatus {
    
    /**
     * Entity is active and available for operations.
     */
    ACTIVE,
    
    /**
     * Entity is inactive but not deleted.
     */
    INACTIVE,
    
    /**
     * Entity is pending activation or approval.
     */
    PENDING,
    
    /**
     * Entity is suspended temporarily.
     */
    SUSPENDED,
    
    /**
     * Entity is marked for deletion (soft delete).
     */
    DELETED
}