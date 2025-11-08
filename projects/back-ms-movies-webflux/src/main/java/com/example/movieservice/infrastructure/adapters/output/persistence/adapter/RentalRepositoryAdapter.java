
package com.example.movieservice.infrastructure.adapters.output.persistence.adapter;

import com.example.movieservice.domain.ports.output.RentalRepositoryPort;
import com.example.movieservice.domain.model.Rental;
import com.example.movieservice.infrastructure.adapters.output.persistence.entity.RentalDbo;
import com.example.movieservice.infrastructure.adapters.output.persistence.repository.JpaRentalRepository;
import com.example.movieservice.application.mapper.RentalMapper;
import com.example.movieservice.infrastructure.config.exceptions.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

/**
 * Reactive repository adapter implementing the Rental domain port.
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
public class RentalRepositoryAdapter implements RentalRepositoryPort {

    private final JpaRentalRepository r2dbcRepository;
    private final RentalMapper mapper;

    @Override
    public Mono<Rental> save(Rental rental) {
        log.debug("Saving Rental: {}", rental);
        return Mono.fromCallable(() -> mapper.toDbo(rental))
                .flatMap(r2dbcRepository::save)
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while saving Rental: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to save Rental", e));
    }

    @Override
    public Mono<Rental> findById(String id) {
        log.debug("Finding Rental by id: {}", id);
        return r2dbcRepository.findById(UUID.fromString(id))
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while finding Rental by id {}: {}", id, e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find Rental by id", e));
    }

    @Override
    public Flux<Rental> findAll() {
        log.debug("Finding all Rentals");
        return r2dbcRepository.findAll()
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while finding all Rentals: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find all Rentals", e));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        log.debug("Deleting Rental by id: {}", id);
        return r2dbcRepository.deleteById(UUID.fromString(id))
                .doOnError(e -> log.error("Database error while deleting Rental by id {}: {}", id, e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to delete Rental by id", e));
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        log.debug("Checking if Rental exists by id: {}", id);
        return r2dbcRepository.existsById(UUID.fromString(id))
                .doOnError(e -> log.error("Database error while checking if Rental exists by id {}: {}", id, e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to check if Rental exists by id", e));
    }

    @Override
    public Flux<Rental> findBySearchTerm(String search, Integer page, Integer size) {
        log.debug("Searching Rentals with term: {}, page: {}, size: {}", search, page, size);
        
        long limit = size != null && size > 0 ? size : 20L;
        long offset = page != null && page > 0 ? (page - 1) * limit : 0L;
        
        if (search == null || search.trim().isEmpty()) {
            return r2dbcRepository.findAllPaged(limit, offset)
                    .map(mapper::toDomain)
                    .doOnError(e -> log.error("Database error while finding all Rentals: {}", e.getMessage(), e))
                    .onErrorMap(e -> new InternalServerErrorException("Failed to find all Rentals", e));
        }
        
        return r2dbcRepository.findBySearchTerm(search, limit, offset)
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while searching Rentals: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to search Rentals", e));
    }
    
    // Additional business methods for reactive operations
    public Mono<Long> countBySearchTerm(String search) {
        log.debug("Counting Rentals with search term: {}", search);
        return r2dbcRepository.countBySearchTerm(search)
                .doOnError(e -> log.error("Database error while counting Rentals: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to count Rentals", e));
    }
}
