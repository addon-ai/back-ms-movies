package com.example.userservice.domain.ports.output;


import com.example.userservice.domain.model.Location;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Domain repository port for Location operations.
 * <p>
 * This interface defines the contract for Location persistence operations,
 * serving as an output port in the Clean Architecture. It abstracts
 * the persistence layer from the domain logic using reactive types.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
public interface LocationRepositoryPort {
    
    Mono<Location> save(Location location);
    
    Mono<Location> findById(String id);
    
    Flux<Location> findAll();
    
    Flux<Location> findBySearchTerm(String search, Integer page, Integer size);
    
    Mono<Void> deleteById(String id);
    
    Mono<Boolean> existsById(String id);
    
    Flux<Location> findNeighborhoodsByCity(String cityId);
    
    Flux<Location> findRegionsByCountry(String countryId);
    
    Flux<Location> findCitiesByRegion(String regionId);
    
}
