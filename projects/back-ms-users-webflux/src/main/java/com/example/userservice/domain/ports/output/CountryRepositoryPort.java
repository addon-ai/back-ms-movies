package com.example.userservice.domain.ports.output;


import com.example.userservice.domain.model.Country;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Domain repository port for Country operations.
 * <p>
 * This interface defines the contract for Country persistence operations,
 * serving as an output port in the Clean Architecture. It abstracts
 * the persistence layer from the domain logic using reactive types.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
public interface CountryRepositoryPort {
    
    Mono<Country> save(Country country);
    
    Mono<Country> findById(String id);
    
    Flux<Country> findAll();
    
    Flux<Country> findBySearchTerm(String search, Integer page, Integer size);
    
    Flux<Country> findByFilters(String search, String status, String dateFrom, String dateTo, Integer page, Integer size);
    
    Flux<Country> findAllPaged(Integer page, Integer size);
    
    Mono<Void> deleteById(String id);
    
    Mono<Boolean> existsById(String id);
}
