package com.example.movieservice.infrastructure.adapters.output.persistence.repository;

import com.example.movieservice.infrastructure.adapters.output.persistence.entity.MovieDbo;
import com.example.movieservice.infrastructure.adapters.output.persistence.repository.JpaMovieRepository;
import com.example.movieservice.domain.model.EntityStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for JpaMovieRepository.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JpaMovieRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JpaMovieRepository movieRepository;

    private MovieDbo createMovieDbo() {
        return MovieDbo.builder()
            .title("test-title")
            .director("test-director")
            .genre("test-genre")
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
        MovieDbo savedMovie = entityManager.persistAndFlush(movie);

        // When
        Optional<MovieDbo> result = movieRepository.findById(savedMovie.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedMovie.getId());
    }

    @Test
    void save_ShouldPersistEntity() {
        // Given
        MovieDbo movie = createMovieDbo();

        // When
        MovieDbo savedMovie = movieRepository.save(movie);

        // Then
        assertThat(savedMovie.getId()).isNotNull();
        
        MovieDbo foundMovie = entityManager.find(MovieDbo.class, savedMovie.getId());
        assertThat(foundMovie).isNotNull();
    }

    @Test
    void deleteById_ShouldRemoveEntity() {
        // Given
        MovieDbo movie = createMovieDbo();
        MovieDbo savedMovie = entityManager.persistAndFlush(movie);

        // When
        movieRepository.deleteById(savedMovie.getId());
        entityManager.flush();

        // Then
        MovieDbo foundMovie = entityManager.find(MovieDbo.class, savedMovie.getId());
        assertThat(foundMovie).isNull();
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        MovieDbo movie = createMovieDbo();
        MovieDbo savedMovie = entityManager.persistAndFlush(movie);

        // When
        boolean exists = movieRepository.existsById(savedMovie.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityDoesNotExist() {
        // When
        boolean exists = movieRepository.existsById("non-existent-id");

        // Then
        assertThat(exists).isFalse();
    }
}