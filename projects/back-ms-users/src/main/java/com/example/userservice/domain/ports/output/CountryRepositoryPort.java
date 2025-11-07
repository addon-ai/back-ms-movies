package com.example.userservice.domain.ports.output;


import com.example.userservice.domain.model.Country;
import java.util.List;
import java.util.Optional;

/**
 * Domain repository port for Country operations.
 * <p>
 * This interface defines the contract for Country persistence operations,
 * serving as an output port in the Clean Architecture. It abstracts
 * the persistence layer from the domain logic.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
public interface CountryRepositoryPort {
    
    Country save(Country country);
    
    Optional<Country> findById(String id);
    
    List<Country> findAll();
    
    List<Country> findBySearchTerm(String search, Integer page, Integer size);
    
    void deleteById(String id);
    
    boolean existsById(String id);
}
