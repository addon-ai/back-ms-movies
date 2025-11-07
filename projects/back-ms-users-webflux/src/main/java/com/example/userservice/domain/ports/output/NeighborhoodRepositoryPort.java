package com.example.userservice.domain.ports.output;


import com.example.userservice.domain.model.Neighborhood;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Domain repository port for Neighborhood operations.
 * <p>
 * This interface defines the contract for Neighborhood persistence operations,
 * serving as an output port in the Clean Architecture. It abstracts
 * the persistence layer from the domain logic using reactive types.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
public interface NeighborhoodRepositoryPort {
    
    Mono<Neighborhood> save(Neighborhood neighborhood);
    
    Mono<Neighborhood> findById(String id);
    
    Flux<Neighborhood> findAll();
    
    Flux<Neighborhood> findBySearchTerm(String search, Integer page, Integer size);
    
    Mono<Void> deleteById(String id);
    
    Mono<Boolean> existsById(String id);
}
