
package com.example.userservice.infrastructure.adapters.output.persistence.adapter;

import com.example.userservice.domain.ports.output.RegionRepositoryPort;
import com.example.userservice.domain.model.Region;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.RegionDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaRegionRepository;
import com.example.userservice.application.mapper.RegionMapper;
import com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
@Slf4j
@Component
@RequiredArgsConstructor
public class RegionRepositoryAdapter implements RegionRepositoryPort {

    private final JpaRegionRepository r2dbcRepository;
    private final RegionMapper mapper;

    @Override
    public Mono<Region> save(Region region) {
        log.debug("Saving Region: {}", region);
        return Mono.fromCallable(() -> mapper.toDbo(region))
                .flatMap(r2dbcRepository::save)
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while saving Region: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to save Region", e));
    }

    @Override
    public Mono<Region> findById(String id) {
        log.debug("Finding Region by id: {}", id);
        return r2dbcRepository.findById(id)
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while finding Region by id {}: {}", id, e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find Region by id", e));
    }

    @Override
    public Flux<Region> findAll() {
        log.debug("Finding all Regions");
        return r2dbcRepository.findAll()
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while finding all Regions: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find all Regions", e));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        log.debug("Deleting Region by id: {}", id);
        return r2dbcRepository.deleteById(id)
                .doOnError(e -> log.error("Database error while deleting Region by id {}: {}", id, e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to delete Region by id", e));
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        log.debug("Checking if Region exists by id: {}", id);
        return r2dbcRepository.existsById(id)
                .doOnError(e -> log.error("Database error while checking if Region exists by id {}: {}", id, e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to check if Region exists by id", e));
    }

    @Override
    public Flux<Region> findBySearchTerm(String search, Integer page, Integer size) {
        log.debug("Searching Regions with term: {}, page: {}, size: {}", search, page, size);
        
        long limit = size != null && size > 0 ? size : 20L;
        long offset = page != null && page > 0 ? (page - 1) * limit : 0L;
        
        if (search == null || search.trim().isEmpty()) {
            return r2dbcRepository.findAllPaged(limit, offset)
                    .map(mapper::toDomain)
                    .doOnError(e -> log.error("Database error while finding all Regions: {}", e.getMessage(), e))
                    .onErrorMap(e -> new InternalServerErrorException("Failed to find all Regions", e));
        }
        
        return r2dbcRepository.findBySearchTerm(search, limit, offset)
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while searching Regions: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to search Regions", e));
    }
    
    // Additional business methods for reactive operations
    public Mono<Long> countBySearchTerm(String search) {
        log.debug("Counting Regions with search term: {}", search);
        return r2dbcRepository.countBySearchTerm(search)
                .doOnError(e -> log.error("Database error while counting Regions: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to count Regions", e));
    }
}
