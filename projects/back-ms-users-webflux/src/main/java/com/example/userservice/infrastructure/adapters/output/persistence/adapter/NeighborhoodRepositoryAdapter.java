
package com.example.userservice.infrastructure.adapters.output.persistence.adapter;

import com.example.userservice.domain.ports.output.NeighborhoodRepositoryPort;
import com.example.userservice.domain.model.Neighborhood;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.NeighborhoodDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaNeighborhoodRepository;
import com.example.userservice.application.mapper.NeighborhoodMapper;
import com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException;
import com.example.userservice.utils.LoggingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

/**
 * Reactive repository adapter implementing the Neighborhood domain port.
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
public class NeighborhoodRepositoryAdapter implements NeighborhoodRepositoryPort {

    private static final LoggingUtils logger = LoggingUtils.getLogger(NeighborhoodRepositoryAdapter.class);
    
    private final JpaNeighborhoodRepository r2dbcRepository;
    private final NeighborhoodMapper mapper;

    @Override
    public Mono<Neighborhood> save(Neighborhood neighborhood) {
        logger.debug("Saving Neighborhood: {}", neighborhood);
        return Mono.fromCallable(() -> mapper.toDbo(neighborhood))
                .flatMap(r2dbcRepository::save)
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while saving Neighborhood", e, neighborhood))
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
        return new InternalServerErrorException("Failed to save Neighborhood", ex);
    }

    @Override
    public Mono<Neighborhood> findById(String id) {
        logger.debug("Finding Neighborhood by id: {}", id);
        return r2dbcRepository.findById(UUID.fromString(id))
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while finding Neighborhood by id", e, id))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find Neighborhood by id", e));
    }

    @Override
    public Flux<Neighborhood> findAll() {
        logger.debug("Finding all Neighborhoods");
        return r2dbcRepository.findAll()
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while finding all Neighborhoods", e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find all Neighborhoods", e));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        logger.debug("Deleting Neighborhood by id: {}", id);
        return r2dbcRepository.deleteById(UUID.fromString(id))
                .doOnError(e -> logger.error("Database error while deleting Neighborhood by id", e, id))
                .onErrorMap(e -> new InternalServerErrorException("Failed to delete Neighborhood by id", e));
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        logger.debug("Checking if Neighborhood exists by id: {}", id);
        return r2dbcRepository.existsById(UUID.fromString(id))
                .doOnError(e -> logger.error("Database error while checking if Neighborhood exists by id", e, id))
                .onErrorMap(e -> new InternalServerErrorException("Failed to check if Neighborhood exists by id", e));
    }

    @Override
    public Flux<Neighborhood> findBySearchTerm(String search, Integer page, Integer size) {
        logger.debug("Searching Neighborhoods with term: {}, page: {}, size: {}", search, page, size);
        
        long limit = size != null && size > 0 ? size : 20L;
        long offset = page != null && page > 0 ? (page - 1) * limit : 0L;
        
        return r2dbcRepository.findBySearchTerm(search, limit, offset)
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while searching Neighborhoods", e, search))
                .onErrorMap(this::mapRepositoryException);
    }
    
    @Override
    public Flux<Neighborhood> findByFilters(String search, String status, String dateFrom, String dateTo, Integer page, Integer size) {
        logger.debug("Searching Neighborhoods with filters - search: {}, status: {}, dateFrom: {}, dateTo: {}, page: {}, size: {}", 
                    search, status, dateFrom, dateTo, page, size);
        
        long limit = size != null && size > 0 ? size : 20L;
        long offset = page != null && page > 0 ? (page - 1) * limit : 0L;
        
        return r2dbcRepository.findByFilters(search, status, dateFrom, dateTo, limit, offset)
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while searching Neighborhoods with filters", e))
                .onErrorMap(this::mapRepositoryException);
    }
    
    // Additional business methods for reactive operations
    public Mono<Long> countBySearchTerm(String search) {
        logger.debug("Counting Neighborhoods with search term: {}", search);
        return r2dbcRepository.countBySearchTerm(search)
                .doOnError(e -> logger.error("Database error while counting Neighborhoods", e, search))
                .onErrorMap(e -> new InternalServerErrorException("Failed to count Neighborhoods", e));
    }
    
    @Override
    public Flux<Neighborhood> findAllPaged(Integer page, Integer size) {
        logger.debug("Finding all Neighborhoods with pagination: page={}, size={}", page, size);
        
        long limit = size != null && size > 0 ? size : 20L;
        long offset = page != null && page > 0 ? (page - 1) * limit : 0L;
        
        return r2dbcRepository.findAllPaged(limit, offset)
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while finding all Neighborhoods paged", e))
                .onErrorMap(this::mapRepositoryException);
    }
    
    public Mono<Long> countAll() {
        logger.debug("Counting all Neighborhoods");
        return r2dbcRepository.countAll()
                .doOnError(e -> logger.error("Database error while counting all Neighborhoods", e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to count all Neighborhoods", e));
    }
}
