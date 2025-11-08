
package com.example.userservice.infrastructure.adapters.output.persistence.adapter;

import com.example.userservice.domain.ports.output.CountryRepositoryPort;
import com.example.userservice.domain.model.Country;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.CountryDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaCountryRepository;
import com.example.userservice.application.mapper.CountryMapper;
import com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

/**
 * Reactive repository adapter implementing the Country domain port.
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
public class CountryRepositoryAdapter implements CountryRepositoryPort {

    private final JpaCountryRepository r2dbcRepository;
    private final CountryMapper mapper;

    @Override
    public Mono<Country> save(Country country) {
        log.debug("Saving Country: {}", country);
        return Mono.fromCallable(() -> mapper.toDbo(country))
                .flatMap(r2dbcRepository::save)
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while saving Country: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to save Country", e));
    }

    @Override
    public Mono<Country> findById(String id) {
        log.debug("Finding Country by id: {}", id);
        return r2dbcRepository.findById(UUID.fromString(id))
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while finding Country by id {}: {}", id, e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find Country by id", e));
    }

    @Override
    public Flux<Country> findAll() {
        log.debug("Finding all Countries");
        return r2dbcRepository.findAll()
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while finding all Countries: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find all Countries", e));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        log.debug("Deleting Country by id: {}", id);
        return r2dbcRepository.deleteById(UUID.fromString(id))
                .doOnError(e -> log.error("Database error while deleting Country by id {}: {}", id, e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to delete Country by id", e));
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        log.debug("Checking if Country exists by id: {}", id);
        return r2dbcRepository.existsById(UUID.fromString(id))
                .doOnError(e -> log.error("Database error while checking if Country exists by id {}: {}", id, e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to check if Country exists by id", e));
    }

    @Override
    public Flux<Country> findBySearchTerm(String search, Integer page, Integer size) {
        log.debug("Searching Countries with term: {}, page: {}, size: {}", search, page, size);
        
        long limit = size != null && size > 0 ? size : 20L;
        long offset = page != null && page > 0 ? (page - 1) * limit : 0L;
        
        if (search == null || search.trim().isEmpty()) {
            return r2dbcRepository.findAllPaged(limit, offset)
                    .map(mapper::toDomain)
                    .doOnError(e -> log.error("Database error while finding all Countries: {}", e.getMessage(), e))
                    .onErrorMap(e -> new InternalServerErrorException("Failed to find all Countries", e));
        }
        
        return r2dbcRepository.findBySearchTerm(search, limit, offset)
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while searching Countries: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to search Countries", e));
    }
    
    // Additional business methods for reactive operations
    public Mono<Long> countBySearchTerm(String search) {
        log.debug("Counting Countries with search term: {}", search);
        return r2dbcRepository.countBySearchTerm(search)
                .doOnError(e -> log.error("Database error while counting Countries: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to count Countries", e));
    }
}
