
package com.example.movieservice.infrastructure.adapters.output.persistence.adapter;

import com.example.movieservice.domain.ports.output.MovieRepositoryPort;
import com.example.movieservice.domain.model.Movie;
import com.example.movieservice.infrastructure.adapters.output.persistence.entity.MovieDbo;
import com.example.movieservice.infrastructure.adapters.output.persistence.repository.JpaMovieRepository;
import com.example.movieservice.application.mapper.MovieMapper;
import com.example.movieservice.infrastructure.config.exceptions.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

/**
 * Reactive repository adapter implementing the Movie domain port.
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
public class MovieRepositoryAdapter implements MovieRepositoryPort {

    private final JpaMovieRepository r2dbcRepository;
    private final MovieMapper mapper;

    @Override
    public Mono<Movie> save(Movie movie) {
        log.debug("Saving Movie: {}", movie);
        return Mono.fromCallable(() -> mapper.toDbo(movie))
                .flatMap(r2dbcRepository::save)
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while saving Movie: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to save Movie", e));
    }

    @Override
    public Mono<Movie> findById(String id) {
        log.debug("Finding Movie by id: {}", id);
        return r2dbcRepository.findById(UUID.fromString(id))
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while finding Movie by id {}: {}", id, e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find Movie by id", e));
    }

    @Override
    public Flux<Movie> findAll() {
        log.debug("Finding all Movies");
        return r2dbcRepository.findAll()
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while finding all Movies: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find all Movies", e));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        log.debug("Deleting Movie by id: {}", id);
        return r2dbcRepository.deleteById(UUID.fromString(id))
                .doOnError(e -> log.error("Database error while deleting Movie by id {}: {}", id, e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to delete Movie by id", e));
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        log.debug("Checking if Movie exists by id: {}", id);
        return r2dbcRepository.existsById(UUID.fromString(id))
                .doOnError(e -> log.error("Database error while checking if Movie exists by id {}: {}", id, e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to check if Movie exists by id", e));
    }

    @Override
    public Flux<Movie> findBySearchTerm(String search, Integer page, Integer size) {
        log.debug("Searching Movies with term: {}, page: {}, size: {}", search, page, size);
        
        long limit = size != null && size > 0 ? size : 20L;
        long offset = page != null && page > 0 ? (page - 1) * limit : 0L;
        
        if (search == null || search.trim().isEmpty()) {
            return r2dbcRepository.findAllPaged(limit, offset)
                    .map(mapper::toDomain)
                    .doOnError(e -> log.error("Database error while finding all Movies: {}", e.getMessage(), e))
                    .onErrorMap(e -> new InternalServerErrorException("Failed to find all Movies", e));
        }
        
        return r2dbcRepository.findBySearchTerm(search, limit, offset)
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while searching Movies: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to search Movies", e));
    }
    
    // Additional business methods for reactive operations
    public Mono<Long> countBySearchTerm(String search) {
        log.debug("Counting Movies with search term: {}", search);
        return r2dbcRepository.countBySearchTerm(search)
                .doOnError(e -> log.error("Database error while counting Movies: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to count Movies", e));
    }
}
