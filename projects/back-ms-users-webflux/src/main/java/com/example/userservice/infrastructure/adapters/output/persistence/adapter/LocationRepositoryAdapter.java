
package com.example.userservice.infrastructure.adapters.output.persistence.adapter;

import com.example.userservice.domain.ports.output.LocationRepositoryPort;
import com.example.userservice.domain.model.Location;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.LocationDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaLocationRepository;
import com.example.userservice.application.mapper.LocationMapper;
import com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive repository adapter implementing the Location domain port.
 * <p>
 * This adapter serves as the output adapter in Clean Architecture,
 * implementing the domain repository interface and delegating to
 * Spring Data R2DBC repository. It handles the conversion between
 * domain objects and database entities using MapStruct in a reactive manner.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LocationRepositoryAdapter implements LocationRepositoryPort {

    private final JpaLocationRepository r2dbcRepository;
    private final LocationMapper mapper;

    @Override
    public Mono<Location> save(Location location) {
        log.debug("Saving Location: {}", location);
        return Mono.fromCallable(() -> mapper.toDbo(location))
                .flatMap(r2dbcRepository::save)
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while saving Location: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to save Location", e));
    }

    @Override
    public Mono<Location> findById(String id) {
        log.debug("Finding Location by id: {}", id);
        return r2dbcRepository.findById(id)
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while finding Location by id {}: {}", id, e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find Location by id", e));
    }

    @Override
    public Flux<Location> findAll() {
        log.debug("Finding all Locations");
        return r2dbcRepository.findAll()
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while finding all Locations: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find all Locations", e));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        log.debug("Deleting Location by id: {}", id);
        return r2dbcRepository.deleteById(id)
                .doOnError(e -> log.error("Database error while deleting Location by id {}: {}", id, e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to delete Location by id", e));
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        log.debug("Checking if Location exists by id: {}", id);
        return r2dbcRepository.existsById(id)
                .doOnError(e -> log.error("Database error while checking if Location exists by id {}: {}", id, e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to check if Location exists by id", e));
    }

    @Override
    public Flux<Location> findBySearchTerm(String search, Integer page, Integer size) {
        log.debug("Searching Locations with term: {}, page: {}, size: {}", search, page, size);
        
        long limit = size != null && size > 0 ? size : 20L;
        long offset = page != null && page > 0 ? (page - 1) * limit : 0L;
        
        if (search == null || search.trim().isEmpty()) {
            return r2dbcRepository.findAllPaged(limit, offset)
                    .map(mapper::toDomain)
                    .doOnError(e -> log.error("Database error while finding all Locations: {}", e.getMessage(), e))
                    .onErrorMap(e -> new InternalServerErrorException("Failed to find all Locations", e));
        }
        
        return r2dbcRepository.findBySearchTerm(search, limit, offset)
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while searching Locations: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to search Locations", e));
    }
    
    // Additional business methods for reactive operations
    public Mono<Long> countBySearchTerm(String search) {
        log.debug("Counting Locations with search term: {}", search);
        return r2dbcRepository.countBySearchTerm(search)
                .doOnError(e -> log.error("Database error while counting Locations: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to count Locations", e));
    }
    
    @Override
    public Flux<Location> findNeighborhoodsByCity(String cityId) {
        log.debug("Executing findNeighborhoodsByCity with parameters: {}", cityId);
        // TODO: Implement custom query for findNeighborhoodsByCity
        return r2dbcRepository.findAll()
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error in findNeighborhoodsByCity: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to execute findNeighborhoodsByCity", e));
    }
    
    @Override
    public Flux<Location> findRegionsByCountry(String countryId) {
        log.debug("Executing findRegionsByCountry with parameters: {}", countryId);
        // TODO: Implement custom query for findRegionsByCountry
        return r2dbcRepository.findAll()
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error in findRegionsByCountry: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to execute findRegionsByCountry", e));
    }
    
    @Override
    public Flux<Location> findCitiesByRegion(String regionId) {
        log.debug("Executing findCitiesByRegion with parameters: {}", regionId);
        // TODO: Implement custom query for findCitiesByRegion
        return r2dbcRepository.findAll()
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error in findCitiesByRegion: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to execute findCitiesByRegion", e));
    }
    
}
