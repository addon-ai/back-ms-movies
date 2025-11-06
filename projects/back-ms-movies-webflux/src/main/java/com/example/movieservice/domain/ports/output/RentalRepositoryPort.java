package com.example.movieservice.domain.ports.output;


import com.example.movieservice.domain.model.Rental;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Domain repository port for Rental operations.
 * <p>
 * This interface defines the contract for Rental persistence operations,
 * serving as an output port in the Clean Architecture. It abstracts
 * the persistence layer from the domain logic using reactive types.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
public interface RentalRepositoryPort {
    
    Mono<Rental> save(Rental rental);
    
    Mono<Rental> findById(String id);
    
    Flux<Rental> findAll();
    
    Flux<Rental> findBySearchTerm(String search, Integer page, Integer size);
    
    Mono<Void> deleteById(String id);
    
    Mono<Boolean> existsById(String id);
}
