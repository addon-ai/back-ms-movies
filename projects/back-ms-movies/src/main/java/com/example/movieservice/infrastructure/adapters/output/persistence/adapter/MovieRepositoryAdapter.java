
package com.example.movieservice.infrastructure.adapters.output.persistence.adapter;

import com.example.movieservice.domain.ports.output.MovieRepositoryPort;
import com.example.movieservice.domain.model.Movie;
import com.example.movieservice.infrastructure.adapters.output.persistence.entity.MovieDbo;
import com.example.movieservice.infrastructure.adapters.output.persistence.repository.JpaMovieRepository;
import com.example.movieservice.application.mapper.MovieMapper;
import com.example.movieservice.infrastructure.config.exceptions.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

/**
 * Repository adapter implementing the Movie domain port.
 * <p>
 * This adapter serves as the output adapter in Clean Architecture,
 * implementing the domain repository interface and delegating to
 * Spring Data JPA repository. It handles the conversion between
 * domain objects and database entities using MapStruct.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MovieRepositoryAdapter implements MovieRepositoryPort {

    private final JpaMovieRepository jpaRepository;
    private final MovieMapper mapper;

    @Override
    public Movie save(Movie movie) {
        log.debug("Saving Movie: {}", movie);
        try {
            MovieDbo dbo = mapper.toDbo(movie);
            MovieDbo savedDbo = jpaRepository.save(dbo);
            return mapper.toDomain(savedDbo);
        } catch (Exception e) {
            log.error("Database error while saving Movie: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to save Movie", e);
        }
    }

    @Override
    public Optional<Movie> findById(String id) {
        log.debug("Finding Movie by id: {}", id);
        try {
            return jpaRepository.findById(id)
                    .map(mapper::toDomain);
        } catch (Exception e) {
            log.error("Database error while finding Movie by id {}: {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("Failed to find Movie by id", e);
        }
    }

    @Override
    public List<Movie> findAll() {
        log.debug("Finding all Movies");
        try {
            return mapper.toDomainList(jpaRepository.findAll());
        } catch (Exception e) {
            log.error("Database error while finding all Movies: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to find all Movies", e);
        }
    }

    @Override
    public void deleteById(String id) {
        log.debug("Deleting Movie by id: {}", id);
        try {
            jpaRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Database error while deleting Movie by id {}: {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("Failed to delete Movie by id", e);
        }
    }

    @Override
    public boolean existsById(String id) {
        log.debug("Checking if Movie exists by id: {}", id);
        try {
            return jpaRepository.existsById(id);
        } catch (Exception e) {
            log.error("Database error while checking if Movie exists by id {}: {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("Failed to check if Movie exists by id", e);
        }
    }

    @Override
    public List<Movie> findBySearchTerm(String search, Integer page, Integer size) {
        log.debug("Searching Movies with term: {}, page: {}, size: {}", search, page, size);
        try {
            if (search == null || search.trim().isEmpty()) {
                return mapper.toDomainList(jpaRepository.findAll());
            }
            
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page != null && page > 0 ? page - 1 : 0, 
                size != null && size > 0 ? size : 20
            );
            
            return jpaRepository.findBySearchTerm(search, pageable)
                    .map(mapper::toDomain)
                    .getContent();
        } catch (Exception e) {
            log.error("Database error while searching Movies: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to search Movies", e);
        }
    }
    
    // Additional business methods with pagination
    public Page<Movie> findBySearchTermPaged(String search, org.springframework.data.domain.Pageable pageable) {
        log.debug("Searching Movies with term: {} and pagination: {}", search, pageable);
        return jpaRepository.findBySearchTerm(search, pageable)
                .map(mapper::toDomain);
    }
    
    public Page<Movie> findAllPaged(org.springframework.data.domain.Pageable pageable) {
        log.debug("Finding all Movies with pagination: {}", pageable);
        return jpaRepository.findAllPaged(pageable)
                .map(mapper::toDomain);
    }
}
