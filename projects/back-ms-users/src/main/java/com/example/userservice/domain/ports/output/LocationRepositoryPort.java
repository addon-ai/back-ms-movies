package com.example.userservice.domain.ports.output;


import com.example.userservice.domain.model.Location;
import java.util.List;
import java.util.Optional;

/**
 * Domain repository port for Location operations.
 * <p>
 * This interface defines the contract for Location persistence operations,
 * serving as an output port in the Clean Architecture. It abstracts
 * the persistence layer from the domain logic.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
public interface LocationRepositoryPort {
    
    Location save(Location location);
    
    Optional<Location> findById(String id);
    
    List<Location> findAll();
    
    List<Location> findBySearchTerm(String search, Integer page, Integer size);
    
    void deleteById(String id);
    
    boolean existsById(String id);
}
