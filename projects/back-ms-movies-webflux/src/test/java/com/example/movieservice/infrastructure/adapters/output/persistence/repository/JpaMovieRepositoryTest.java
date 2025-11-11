package com.example.movieservice.infrastructure.adapters.output.persistence.repository;

import com.example.movieservice.infrastructure.adapters.output.persistence.entity.MovieDbo;
import com.example.movieservice.infrastructure.adapters.output.persistence.repository.JpaMovieRepository;
import com.example.movieservice.domain.model.EntityStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for JpaMovieRepository.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@DataR2dbcTest
class JpaMovieRepositoryTest {

    @Autowired
    private JpaMovieRepository movieRepository;

    private MovieDbo createMovieDbo() {
        UUID randomUUID = UUID.randomUUID();
        return MovieDbo.builder()
            .title("test-title-" + randomUUID)
            .director("test-director-" + randomUUID)
            .genre("test-genre-" + randomUUID)
            .releaseYear(java.math.BigDecimal.valueOf(1.0))
            .duration(java.math.BigDecimal.valueOf(1.0))
            .availableCopies(java.math.BigDecimal.valueOf(1.0))
            .rentalPrice(1.0)
            .status(EntityStatus.ACTIVE)
            .build();
    }

    @Test
    void findById_ShouldReturnEntity_WhenExists() {
        // Given
        MovieDbo movie = createMovieDbo();
        MovieDbo savedMovie = movieRepository.save(movie)
            .block(Duration.ofSeconds(5));

        // When
        MovieDbo result = movieRepository.findById(savedMovie.getId())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedMovie.getId());
    }

    @Test
    void save_ShouldPersistEntity() {
        // Given
        MovieDbo movie = createMovieDbo();

        // When
        MovieDbo savedMovie = movieRepository.save(movie)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(savedMovie.getId()).isNotNull();
        
        MovieDbo foundMovie = movieRepository.findById(savedMovie.getId())
            .block(Duration.ofSeconds(5));
        assertThat(foundMovie).isNotNull();
    }

    @Test
    void deleteById_ShouldRemoveEntity() {
        // Given
        MovieDbo movie = createMovieDbo();
        MovieDbo savedMovie = movieRepository.save(movie)
            .block(Duration.ofSeconds(5));

        // When
        movieRepository.deleteById(savedMovie.getId())
            .block(Duration.ofSeconds(5));

        // Then
        MovieDbo foundMovie = movieRepository.findById(savedMovie.getId())
            .block(Duration.ofSeconds(5));
        assertThat(foundMovie).isNull();
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        MovieDbo movie = createMovieDbo();
        MovieDbo savedMovie = movieRepository.save(movie)
            .block(Duration.ofSeconds(5));

        // When
        Boolean exists = movieRepository.existsById(savedMovie.getId())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityDoesNotExist() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        
        // When
        Boolean exists = movieRepository.existsById(nonExistentId)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(exists).isFalse();
    }
}