
package com.example.userservice.infrastructure.adapters.output.persistence.adapter;

import com.example.userservice.domain.ports.output.RegionRepositoryPort;
import com.example.userservice.domain.model.Region;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.RegionDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaRegionRepository;
import com.example.userservice.application.mapper.RegionMapper;
import com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException;
import com.example.userservice.utils.LoggingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

/**
 * Reactive repository adapter implementing the Region domain port.
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
public class RegionRepositoryAdapter implements RegionRepositoryPort {

    private static final LoggingUtils logger = LoggingUtils.getLogger(RegionRepositoryAdapter.class);
    
    private final JpaRegionRepository r2dbcRepository;
    private final RegionMapper mapper;

    @Override
    public Mono<Region> save(Region region) {
        logger.debug("Saving Region: {}", region);
        return Mono.fromCallable(() -> mapper.toDbo(region))
                .flatMap(r2dbcRepository::save)
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while saving Region", e, region))
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
        return new InternalServerErrorException("Failed to save Region", ex);
    }

    @Override
    public Mono<Region> findById(String id) {
        logger.debug("Finding Region by id: {}", id);
        return r2dbcRepository.findById(UUID.fromString(id))
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while finding Region by id", e, id))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find Region by id", e));
    }

    @Override
    public Flux<Region> findAll() {
        logger.debug("Finding all Regions");
        return r2dbcRepository.findAll()
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while finding all Regions", e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find all Regions", e));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        logger.debug("Deleting Region by id: {}", id);
        return r2dbcRepository.deleteById(UUID.fromString(id))
                .doOnError(e -> logger.error("Database error while deleting Region by id", e, id))
                .onErrorMap(e -> new InternalServerErrorException("Failed to delete Region by id", e));
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        logger.debug("Checking if Region exists by id: {}", id);
        return r2dbcRepository.existsById(UUID.fromString(id))
                .doOnError(e -> logger.error("Database error while checking if Region exists by id", e, id))
                .onErrorMap(e -> new InternalServerErrorException("Failed to check if Region exists by id", e));
    }

    @Override
    public Flux<Region> findBySearchTerm(String search, Integer page, Integer size) {
        logger.debug("Searching Regions with term: {}, page: {}, size: {}", search, page, size);
        
        long limit = size != null && size > 0 ? size : 20L;
        long offset = page != null && page > 0 ? (page - 1) * limit : 0L;
        
        return r2dbcRepository.findBySearchTerm(search, limit, offset)
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while searching Regions", e, search))
                .onErrorMap(this::mapRepositoryException);
    }
    
    @Override
    public Flux<Region> findByFilters(String search, String status, String dateFrom, String dateTo, Integer page, Integer size) {
        logger.debug("Searching Regions with filters - search: {}, status: {}, dateFrom: {}, dateTo: {}, page: {}, size: {}", 
                    search, status, dateFrom, dateTo, page, size);
        
        long limit = size != null && size > 0 ? size : 20L;
        long offset = page != null && page > 0 ? (page - 1) * limit : 0L;
        
        return r2dbcRepository.findByFilters(search, status, dateFrom, dateTo, limit, offset)
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while searching Regions with filters", e))
                .onErrorMap(this::mapRepositoryException);
    }
    
    // Additional business methods for reactive operations
    public Mono<Long> countBySearchTerm(String search) {
        logger.debug("Counting Regions with search term: {}", search);
        return r2dbcRepository.countBySearchTerm(search)
                .doOnError(e -> logger.error("Database error while counting Regions", e, search))
                .onErrorMap(e -> new InternalServerErrorException("Failed to count Regions", e));
    }
    
    @Override
    public Flux<Region> findAllPaged(Integer page, Integer size) {
        logger.debug("Finding all Regions with pagination: page={}, size={}", page, size);
        
        long limit = size != null && size > 0 ? size : 20L;
        long offset = page != null && page > 0 ? (page - 1) * limit : 0L;
        
        return r2dbcRepository.findAllPaged(limit, offset)
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while finding all Regions paged", e))
                .onErrorMap(this::mapRepositoryException);
    }
    
    public Mono<Long> countAll() {
        logger.debug("Counting all Regions");
        return r2dbcRepository.countAll()
                .doOnError(e -> logger.error("Database error while counting all Regions", e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to count all Regions", e));
    }
}
