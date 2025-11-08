
package com.example.userservice.infrastructure.adapters.output.persistence.adapter;

import com.example.userservice.domain.ports.output.NeighborhoodRepositoryPort;
import com.example.userservice.domain.model.Neighborhood;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.NeighborhoodDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaNeighborhoodRepository;
import com.example.userservice.application.mapper.NeighborhoodMapper;
import com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Component
@RequiredArgsConstructor
public class NeighborhoodRepositoryAdapter implements NeighborhoodRepositoryPort {

    private final JpaNeighborhoodRepository r2dbcRepository;
    private final NeighborhoodMapper mapper;

    @Override
    public Mono<Neighborhood> save(Neighborhood neighborhood) {
        log.debug("Saving Neighborhood: {}", neighborhood);
        return Mono.fromCallable(() -> mapper.toDbo(neighborhood))
                .flatMap(r2dbcRepository::save)
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while saving Neighborhood: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to save Neighborhood", e));
    }

    @Override
    public Mono<Neighborhood> findById(String id) {
        log.debug("Finding Neighborhood by id: {}", id);
        return r2dbcRepository.findById(UUID.fromString(id))
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while finding Neighborhood by id {}: {}", id, e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find Neighborhood by id", e));
    }

    @Override
    public Flux<Neighborhood> findAll() {
        log.debug("Finding all Neighborhoods");
        return r2dbcRepository.findAll()
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while finding all Neighborhoods: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find all Neighborhoods", e));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        log.debug("Deleting Neighborhood by id: {}", id);
        return r2dbcRepository.deleteById(UUID.fromString(id))
                .doOnError(e -> log.error("Database error while deleting Neighborhood by id {}: {}", id, e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to delete Neighborhood by id", e));
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        log.debug("Checking if Neighborhood exists by id: {}", id);
        return r2dbcRepository.existsById(UUID.fromString(id))
                .doOnError(e -> log.error("Database error while checking if Neighborhood exists by id {}: {}", id, e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to check if Neighborhood exists by id", e));
    }

    @Override
    public Flux<Neighborhood> findBySearchTerm(String search, Integer page, Integer size) {
        log.debug("Searching Neighborhoods with term: {}, page: {}, size: {}", search, page, size);
        
        long limit = size != null && size > 0 ? size : 20L;
        long offset = page != null && page > 0 ? (page - 1) * limit : 0L;
        
        if (search == null || search.trim().isEmpty()) {
            return r2dbcRepository.findAllPaged(limit, offset)
                    .map(mapper::toDomain)
                    .doOnError(e -> log.error("Database error while finding all Neighborhoods: {}", e.getMessage(), e))
                    .onErrorMap(e -> new InternalServerErrorException("Failed to find all Neighborhoods", e));
        }
        
        return r2dbcRepository.findBySearchTerm(search, limit, offset)
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while searching Neighborhoods: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to search Neighborhoods", e));
    }
    
    // Additional business methods for reactive operations
    public Mono<Long> countBySearchTerm(String search) {
        log.debug("Counting Neighborhoods with search term: {}", search);
        return r2dbcRepository.countBySearchTerm(search)
                .doOnError(e -> log.error("Database error while counting Neighborhoods: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to count Neighborhoods", e));
    }
}
