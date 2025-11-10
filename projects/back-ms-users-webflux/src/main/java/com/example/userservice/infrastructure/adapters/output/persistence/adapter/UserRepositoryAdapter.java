
package com.example.userservice.infrastructure.adapters.output.persistence.adapter;

import com.example.userservice.domain.ports.output.UserRepositoryPort;
import com.example.userservice.domain.model.User;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.UserDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaUserRepository;
import com.example.userservice.application.mapper.UserMapper;
import com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException;
import com.example.userservice.utils.LoggingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

/**
 * Reactive repository adapter implementing the User domain port.
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
public class UserRepositoryAdapter implements UserRepositoryPort {

    private static final LoggingUtils logger = LoggingUtils.getLogger(UserRepositoryAdapter.class);
    
    private final JpaUserRepository r2dbcRepository;
    private final UserMapper mapper;

    @Override
    public Mono<User> save(User user) {
        logger.debug("Saving User: {}", user);
        return Mono.fromCallable(() -> mapper.toDbo(user))
                .flatMap(r2dbcRepository::save)
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while saving User", e, user))
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
        return new InternalServerErrorException("Failed to save User", ex);
    }

    @Override
    public Mono<User> findById(String id) {
        logger.debug("Finding User by id: {}", id);
        return r2dbcRepository.findById(UUID.fromString(id))
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while finding User by id", e, id))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find User by id", e));
    }

    @Override
    public Flux<User> findAll() {
        logger.debug("Finding all Users");
        return r2dbcRepository.findAll()
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while finding all Users", e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find all Users", e));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        logger.debug("Deleting User by id: {}", id);
        return r2dbcRepository.deleteById(UUID.fromString(id))
                .doOnError(e -> logger.error("Database error while deleting User by id", e, id))
                .onErrorMap(e -> new InternalServerErrorException("Failed to delete User by id", e));
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        logger.debug("Checking if User exists by id: {}", id);
        return r2dbcRepository.existsById(UUID.fromString(id))
                .doOnError(e -> logger.error("Database error while checking if User exists by id", e, id))
                .onErrorMap(e -> new InternalServerErrorException("Failed to check if User exists by id", e));
    }

    @Override
    public Flux<User> findBySearchTerm(String search, Integer page, Integer size) {
        logger.debug("Searching Users with term: {}, page: {}, size: {}", search, page, size);
        
        long limit = size != null && size > 0 ? size : 20L;
        long offset = page != null && page > 0 ? (page - 1) * limit : 0L;
        
        return r2dbcRepository.findBySearchTerm(search, limit, offset)
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while searching Users", e, search))
                .onErrorMap(this::mapRepositoryException);
    }
    
    @Override
    public Flux<User> findByFilters(String search, String status, String dateFrom, String dateTo, Integer page, Integer size) {
        logger.debug("Searching Users with filters - search: {}, status: {}, dateFrom: {}, dateTo: {}, page: {}, size: {}", 
                    search, status, dateFrom, dateTo, page, size);
        
        long limit = size != null && size > 0 ? size : 20L;
        long offset = page != null && page > 0 ? (page - 1) * limit : 0L;
        
        return r2dbcRepository.findByFilters(search, status, dateFrom, dateTo, limit, offset)
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while searching Users with filters", e))
                .onErrorMap(this::mapRepositoryException);
    }
    
    // Additional business methods for reactive operations
    public Mono<Long> countBySearchTerm(String search) {
        logger.debug("Counting Users with search term: {}", search);
        return r2dbcRepository.countBySearchTerm(search)
                .doOnError(e -> logger.error("Database error while counting Users", e, search))
                .onErrorMap(e -> new InternalServerErrorException("Failed to count Users", e));
    }
    
    @Override
    public Flux<User> findAllPaged(Integer page, Integer size) {
        logger.debug("Finding all Users with pagination: page={}, size={}", page, size);
        
        long limit = size != null && size > 0 ? size : 20L;
        long offset = page != null && page > 0 ? (page - 1) * limit : 0L;
        
        return r2dbcRepository.findAllPaged(limit, offset)
                .map(mapper::toDomain)
                .doOnError(e -> logger.error("Database error while finding all Users paged", e))
                .onErrorMap(this::mapRepositoryException);
    }
    
    public Mono<Long> countAll() {
        logger.debug("Counting all Users");
        return r2dbcRepository.countAll()
                .doOnError(e -> logger.error("Database error while counting all Users", e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to count all Users", e));
    }
}
