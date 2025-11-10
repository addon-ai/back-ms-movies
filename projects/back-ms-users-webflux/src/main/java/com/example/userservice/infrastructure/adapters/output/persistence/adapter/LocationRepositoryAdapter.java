
package com.example.userservice.infrastructure.adapters.output.persistence.adapter;

import com.example.userservice.domain.ports.output.LocationRepositoryPort;
import com.example.userservice.domain.model.Location;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.LocationDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaLocationRepository;
import com.example.userservice.application.mapper.LocationMapper;
import com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException;
import com.example.userservice.utils.LoggingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

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
@Component
@RequiredArgsConstructor
public class LocationRepositoryAdapter implements LocationRepositoryPort {

    private static final LoggingUtils logger = LoggingUtils.getLogger(LocationRepositoryAdapter.class);
    
    private final JpaLocationRepository r2dbcRepository;
    private final LocationMapper mapper;

    @Override
    public Mono<Location> save(Location location) {
        logger.debug("Saving Location: {}", location);
        return Mono.fromCallable(() -> mapper.toDbo(location))
                .flatMap(r2dbcRepository::save)
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while saving Location", e, location))
                .onErrorMap(this::mapRepositoryException);
    }

    private Throwable mapRepositoryException(Throwable ex) {
        // Business logic exceptions - propagate to service layer
        if (ex instanceof org.springframework.dao.DuplicateKeyException) {
            logger.debug("Duplicate key constraint violation: {}", ex.getMessage());
            return ex;
        }
        if (ex instanceof org.springframework.dao.DataIntegrityViolationException) {
            logger.debug("Data integrity violation: {}", ex.getMessage());
            return ex;
        }
        // Technical exceptions - convert to infrastructure errors
        logger.error("Technical database error", ex);
        return new InternalServerErrorException("Failed to save Location", ex);
    }

    @Override
    public Mono<Location> findById(String id) {
        logger.debug("Finding Location by id: {}", id);
        return r2dbcRepository.findById(UUID.fromString(id))
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while finding Location by id", e, id))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find Location by id", e));
    }

    @Override
    public Flux<Location> findAll() {
        logger.debug("Finding all Locations");
        return r2dbcRepository.findAll()
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while finding all Locations", e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find all Locations", e));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        logger.debug("Deleting Location by id: {}", id);
        return r2dbcRepository.deleteById(UUID.fromString(id))
                .doOnError(e -> logger.error("Database error while deleting Location by id", e, id))
                .onErrorMap(e -> new InternalServerErrorException("Failed to delete Location by id", e));
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        logger.debug("Checking if Location exists by id: {}", id);
        return r2dbcRepository.existsById(UUID.fromString(id))
                .doOnError(e -> logger.error("Database error while checking if Location exists by id", e, id))
                .onErrorMap(e -> new InternalServerErrorException("Failed to check if Location exists by id", e));
    }

    @Override
    public Flux<Location> findBySearchTerm(String search, Integer page, Integer size) {
        logger.debug("Searching Locations with term: {}, page: {}, size: {}", search, page, size);
        
        long limit = size != null && size > 0 ? size : 20L;
        long offset = page != null && page > 0 ? (page - 1) * limit : 0L;
        
        return r2dbcRepository.findBySearchTerm(search, limit, offset)
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while searching Locations", e, search))
                .onErrorMap(this::mapRepositoryException);
    }
    
    @Override
    public Flux<Location> findByFilters(String search, String status, String dateFrom, String dateTo, Integer page, Integer size) {
        logger.debug("Searching Locations with filters - search: {}, status: {}, dateFrom: {}, dateTo: {}, page: {}, size: {}", 
                    search, status, dateFrom, dateTo, page, size);
        
        long limit = size != null && size > 0 ? size : 20L;
        long offset = page != null && page > 0 ? (page - 1) * limit : 0L;
        
        return r2dbcRepository.findByFilters(search, status, dateFrom, dateTo, limit, offset)
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while searching Locations with filters", e))
                .onErrorMap(this::mapRepositoryException);
    }
    
    // Additional business methods for reactive operations
    public Mono<Long> countBySearchTerm(String search) {
        logger.debug("Counting Locations with search term: {}", search);
        return r2dbcRepository.countBySearchTerm(search)
                .doOnError(e -> logger.error("Database error while counting Locations", e, search))
                .onErrorMap(e -> new InternalServerErrorException("Failed to count Locations", e));
    }
    
    @Override
    public Flux<Location> findAllPaged(Integer page, Integer size) {
        logger.debug("Finding all Locations with pagination: page={}, size={}", page, size);
        
        long limit = size != null && size > 0 ? size : 20L;
        long offset = page != null && page > 0 ? (page - 1) * limit : 0L;
        
        return r2dbcRepository.findAllPaged(limit, offset)
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while finding all Locations paged", e))
                .onErrorMap(this::mapRepositoryException);
    }
    
    public Mono<Long> countAll() {
        logger.debug("Counting all Locations");
        return r2dbcRepository.countAll()
                .doOnError(e -> logger.error("Database error while counting all Locations", e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to count all Locations", e));
    }
    
    @Override
    public Flux<Location> findNeighborhoodsByCity(String cityId) {
        logger.debug("Executing findNeighborhoodsByCity with parameters: {}", cityId);
        // TODO: Implement custom query for findNeighborhoodsByCity
        return r2dbcRepository.findAll()
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error in findNeighborhoodsByCity", e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to execute findNeighborhoodsByCity", e));
    }
    
    @Override
    public Flux<Location> findRegionsByCountry(String countryId) {
        logger.debug("Executing findRegionsByCountry with parameters: {}", countryId);
        // TODO: Implement custom query for findRegionsByCountry
        return r2dbcRepository.findAll()
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error in findRegionsByCountry", e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to execute findRegionsByCountry", e));
    }
    
    @Override
    public Flux<Location> findCitiesByRegion(String regionId) {
        logger.debug("Executing findCitiesByRegion with parameters: {}", regionId);
        // TODO: Implement custom query for findCitiesByRegion
        return r2dbcRepository.findAll()
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error in findCitiesByRegion", e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to execute findCitiesByRegion", e));
    }
    
}
