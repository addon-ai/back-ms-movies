package com.example.movieservice.domain.ports.output;


import com.example.movieservice.domain.model.Rental;
import java.util.List;
import java.util.Optional;

/**
 * Domain repository port for Rental operations.
 * <p>
 * This interface defines the contract for Rental persistence operations,
 * serving as an output port in the Clean Architecture. It abstracts
 * the persistence layer from the domain logic.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
public interface RentalRepositoryPort {
    
    Rental save(Rental rental);
    
    Optional<Rental> findById(String id);
    
    List<Rental> findAll();
    
    List<Rental> findBySearchTerm(String search, Integer page, Integer size);
    
    void deleteById(String id);
    
    boolean existsById(String id);
}
