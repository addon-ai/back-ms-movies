
package com.example.userservice.infrastructure.adapters.output.persistence.adapter;

import com.example.userservice.domain.ports.output.CityRepositoryPort;
import com.example.userservice.domain.model.City;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.CityDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaCityRepository;
import com.example.userservice.application.mapper.CityMapper;
import com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException;
import com.example.userservice.utils.LoggingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

/**
 * Reactive repository adapter implementing the City domain port.
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
public class CityRepositoryAdapter implements CityRepositoryPort {

    private static final LoggingUtils logger = LoggingUtils.getLogger(CityRepositoryAdapter.class);
    
    private final JpaCityRepository r2dbcRepository;
    private final CityMapper mapper;

    @Override
    public Mono<City> save(City city) {
        logger.debug("Saving City: {}", city);
        return Mono.fromCallable(() -> mapper.toDbo(city))
                .flatMap(r2dbcRepository::save)
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while saving City", e, city))
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
        return new InternalServerErrorException("Failed to save City", ex);
    }

    @Override
    public Mono<City> findById(String id) {
        logger.debug("Finding City by id: {}", id);
        return r2dbcRepository.findById(UUID.fromString(id))
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while finding City by id", e, id))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find City by id", e));
    }

    @Override
    public Flux<City> findAll() {
        logger.debug("Finding all Cities");
        return r2dbcRepository.findAll()
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while finding all Cities", e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find all Cities", e));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        logger.debug("Deleting City by id: {}", id);
        return r2dbcRepository.deleteById(UUID.fromString(id))
                .doOnError(e -> logger.error("Database error while deleting City by id", e, id))
                .onErrorMap(e -> new InternalServerErrorException("Failed to delete City by id", e));
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        logger.debug("Checking if City exists by id: {}", id);
        return r2dbcRepository.existsById(UUID.fromString(id))
                .doOnError(e -> logger.error("Database error while checking if City exists by id", e, id))
                .onErrorMap(e -> new InternalServerErrorException("Failed to check if City exists by id", e));
    }

    @Override
    public Flux<City> findBySearchTerm(String search, Integer page, Integer size) {
        logger.debug("Searching Cities with term: {}, page: {}, size: {}", search, page, size);
        
        long limit = size != null && size > 0 ? size : 20L;
        long offset = page != null && page > 0 ? (page - 1) * limit : 0L;
        
        return r2dbcRepository.findBySearchTerm(search, limit, offset)
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while searching Cities", e, search))
                .onErrorMap(this::mapRepositoryException);
    }
    
    @Override
    public Flux<City> findByFilters(String search, String status, String dateFrom, String dateTo, Integer page, Integer size) {
        logger.debug("Searching Cities with filters - search: {}, status: {}, dateFrom: {}, dateTo: {}, page: {}, size: {}", 
                    search, status, dateFrom, dateTo, page, size);
        
        long limit = size != null && size > 0 ? size : 20L;
        long offset = page != null && page > 0 ? (page - 1) * limit : 0L;
        
        return r2dbcRepository.findByFilters(search, status, dateFrom, dateTo, limit, offset)
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while searching Cities with filters", e))
                .onErrorMap(this::mapRepositoryException);
    }
    
    // Additional business methods for reactive operations
    public Mono<Long> countBySearchTerm(String search) {
        logger.debug("Counting Cities with search term: {}", search);
        return r2dbcRepository.countBySearchTerm(search)
                .doOnError(e -> logger.error("Database error while counting Cities", e, search))
                .onErrorMap(e -> new InternalServerErrorException("Failed to count Cities", e));
    }
    
    @Override
    public Flux<City> findAllPaged(Integer page, Integer size) {
        logger.debug("Finding all Cities with pagination: page={}, size={}", page, size);
        
        long limit = size != null && size > 0 ? size : 20L;
        long offset = page != null && page > 0 ? (page - 1) * limit : 0L;
        
        return r2dbcRepository.findAllPaged(limit, offset)
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while finding all Cities paged", e))
                .onErrorMap(this::mapRepositoryException);
    }
    
    public Mono<Long> countAll() {
        logger.debug("Counting all Cities");
        return r2dbcRepository.countAll()
                .doOnError(e -> logger.error("Database error while counting all Cities", e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to count all Cities", e));
    }
}
