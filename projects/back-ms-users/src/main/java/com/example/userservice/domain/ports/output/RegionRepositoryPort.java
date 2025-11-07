package com.example.userservice.domain.ports.output;


import com.example.userservice.domain.model.Region;
import java.util.List;
import java.util.Optional;

/**
 * Domain repository port for Region operations.
 * <p>
 * This interface defines the contract for Region persistence operations,
 * serving as an output port in the Clean Architecture. It abstracts
 * the persistence layer from the domain logic.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
public interface RegionRepositoryPort {
    
    Region save(Region region);
    
    Optional<Region> findById(String id);
    
    List<Region> findAll();
    
    List<Region> findBySearchTerm(String search, Integer page, Integer size);
    
    void deleteById(String id);
    
    boolean existsById(String id);
}
