
package com.example.userservice.infrastructure.adapters.output.persistence.adapter;

import com.example.userservice.domain.ports.output.CityRepositoryPort;
import com.example.userservice.domain.model.City;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.CityDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaCityRepository;
import com.example.userservice.application.mapper.CityMapper;
import com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
@Slf4j
@Component
@RequiredArgsConstructor
public class CityRepositoryAdapter implements CityRepositoryPort {

    private final JpaCityRepository r2dbcRepository;
    private final CityMapper mapper;

    @Override
    public Mono<City> save(City city) {
        log.debug("Saving City: {}", city);
        return Mono.fromCallable(() -> mapper.toDbo(city))
                .flatMap(r2dbcRepository::save)
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while saving City: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to save City", e));
    }

    @Override
    public Mono<City> findById(String id) {
        log.debug("Finding City by id: {}", id);
        return r2dbcRepository.findById(id)
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while finding City by id {}: {}", id, e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find City by id", e));
    }

    @Override
    public Flux<City> findAll() {
        log.debug("Finding all Cities");
        return r2dbcRepository.findAll()
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while finding all Cities: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find all Cities", e));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        log.debug("Deleting City by id: {}", id);
        return r2dbcRepository.deleteById(id)
                .doOnError(e -> log.error("Database error while deleting City by id {}: {}", id, e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to delete City by id", e));
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        log.debug("Checking if City exists by id: {}", id);
        return r2dbcRepository.existsById(id)
                .doOnError(e -> log.error("Database error while checking if City exists by id {}: {}", id, e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to check if City exists by id", e));
    }

    @Override
    public Flux<City> findBySearchTerm(String search, Integer page, Integer size) {
        log.debug("Searching Cities with term: {}, page: {}, size: {}", search, page, size);
        
        long limit = size != null && size > 0 ? size : 20L;
        long offset = page != null && page > 0 ? (page - 1) * limit : 0L;
        
        if (search == null || search.trim().isEmpty()) {
            return r2dbcRepository.findAllPaged(limit, offset)
                    .map(mapper::toDomain)
                    .doOnError(e -> log.error("Database error while finding all Cities: {}", e.getMessage(), e))
                    .onErrorMap(e -> new InternalServerErrorException("Failed to find all Cities", e));
        }
        
        return r2dbcRepository.findBySearchTerm(search, limit, offset)
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while searching Cities: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to search Cities", e));
    }
    
    // Additional business methods for reactive operations
    public Mono<Long> countBySearchTerm(String search) {
        log.debug("Counting Cities with search term: {}", search);
        return r2dbcRepository.countBySearchTerm(search)
                .doOnError(e -> log.error("Database error while counting Cities: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to count Cities", e));
    }
}
