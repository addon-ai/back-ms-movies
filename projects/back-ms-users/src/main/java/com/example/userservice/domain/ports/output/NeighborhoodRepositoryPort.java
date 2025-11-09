package com.example.userservice.domain.ports.output;


import com.example.userservice.domain.model.Neighborhood;
import java.util.List;
import java.util.Optional;

/**
 * Domain repository port for Neighborhood operations.
 * <p>
 * This interface defines the contract for Neighborhood persistence operations,
 * serving as an output port in the Clean Architecture. It abstracts
 * the persistence layer from the domain logic.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
public interface NeighborhoodRepositoryPort {
    
    Neighborhood save(Neighborhood neighborhood);
    
    Optional<Neighborhood> findById(String id);
    
    List<Neighborhood> findAll();
    
    List<Neighborhood> findBySearchTerm(String search, Integer page, Integer size);
    
    List<Neighborhood> findByFilters(String search, String status, String dateFrom, String dateTo, Integer page, Integer size);
    
    List<Neighborhood> findAllPaged(Integer page, Integer size);
    
    void deleteById(String id);
    
    boolean existsById(String id);
}
