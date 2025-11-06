
package com.example.userservice.infrastructure.adapters.output.persistence.adapter;

import com.example.userservice.domain.ports.output.UserRepositoryPort;
import com.example.userservice.domain.model.User;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.UserDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaUserRepository;
import com.example.userservice.application.mapper.UserMapper;
import com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
@Slf4j
@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final JpaUserRepository r2dbcRepository;
    private final UserMapper mapper;

    @Override
    public Mono<User> save(User user) {
        log.debug("Saving User: {}", user);
        return Mono.fromCallable(() -> mapper.toDbo(user))
                .flatMap(r2dbcRepository::save)
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while saving User: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to save User", e));
    }

    @Override
    public Mono<User> findById(String id) {
        log.debug("Finding User by id: {}", id);
        return r2dbcRepository.findById(id)
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while finding User by id {}: {}", id, e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find User by id", e));
    }

    @Override
    public Flux<User> findAll() {
        log.debug("Finding all Users");
        return r2dbcRepository.findAll()
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while finding all Users: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find all Users", e));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        log.debug("Deleting User by id: {}", id);
        return r2dbcRepository.deleteById(id)
                .doOnError(e -> log.error("Database error while deleting User by id {}: {}", id, e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to delete User by id", e));
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        log.debug("Checking if User exists by id: {}", id);
        return r2dbcRepository.existsById(id)
                .doOnError(e -> log.error("Database error while checking if User exists by id {}: {}", id, e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to check if User exists by id", e));
    }

    @Override
    public Flux<User> findBySearchTerm(String search, Integer page, Integer size) {
        log.debug("Searching Users with term: {}, page: {}, size: {}", search, page, size);
        
        long limit = size != null && size > 0 ? size : 20L;
        long offset = page != null && page > 0 ? (page - 1) * limit : 0L;
        
        if (search == null || search.trim().isEmpty()) {
            return r2dbcRepository.findAllPaged(limit, offset)
                    .map(mapper::toDomain)
                    .doOnError(e -> log.error("Database error while finding all Users: {}", e.getMessage(), e))
                    .onErrorMap(e -> new InternalServerErrorException("Failed to find all Users", e));
        }
        
        return r2dbcRepository.findBySearchTerm(search, limit, offset)
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while searching Users: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to search Users", e));
    }
    
    // Additional business methods for reactive operations
    public Mono<Long> countBySearchTerm(String search) {
        log.debug("Counting Users with search term: {}", search);
        return r2dbcRepository.countBySearchTerm(search)
                .doOnError(e -> log.error("Database error while counting Users: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to count Users", e));
    }
}
