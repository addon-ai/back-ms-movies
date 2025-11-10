package com.example.movieservice.infrastructure.adapters.output.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import com.example.movieservice.application.mapper.MovieMapper;
import com.example.movieservice.domain.model.Movie;
import com.example.movieservice.infrastructure.adapters.output.persistence.entity.MovieDbo;
import com.example.movieservice.infrastructure.adapters.output.persistence.repository.JpaMovieRepository;

@ExtendWith(MockitoExtension.class)
class MovieRepositoryAdapterTest {

    @Mock
    private JpaMovieRepository jpaMovieRepository;

    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private MovieRepositoryAdapter movieRepositoryAdapter;

    private Movie domainMovie;
    private MovieDbo movieDbo;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        
        domainMovie = Movie.builder()
            .movieId(testId.toString())
            .build();
        
        movieDbo = MovieDbo.builder()
            .id(testId)
            .build();
    }

    @Test
    void save_ShouldReturnDomainEntity_WhenValidEntity() {
        // Given
        when(movieMapper.toDbo(domainMovie)).thenReturn(movieDbo);
        when(jpaMovieRepository.save(movieDbo)).thenReturn(Mono.just(movieDbo));
        when(movieMapper.toDomain(movieDbo)).thenReturn(domainMovie);

        // When
        Movie result = movieRepositoryAdapter.save(domainMovie)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        verify(movieMapper).toDbo(domainMovie);
        verify(jpaMovieRepository).save(movieDbo);
        verify(movieMapper).toDomain(movieDbo);
    }

    @Test
    void findById_ShouldReturnEntity_WhenEntityExists() {
        // Given
        when(jpaMovieRepository.findById(testId)).thenReturn(Mono.just(movieDbo));
        when(movieMapper.toDomain(movieDbo)).thenReturn(domainMovie);

        // When
        Movie result = movieRepositoryAdapter.findById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(domainMovie);
        verify(jpaMovieRepository).findById(testId);
        verify(movieMapper).toDomain(movieDbo);
    }

    @Test
    void findById_ShouldReturnNull_WhenEntityNotFound() {
        // Given
        when(jpaMovieRepository.findById(testId)).thenReturn(Mono.empty());

        // When
        Movie result = movieRepositoryAdapter.findById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNull();
        verify(jpaMovieRepository).findById(testId);
    }

    @Test
    void findAll_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        when(jpaMovieRepository.findAll()).thenReturn(Flux.just(movieDbo));
        when(movieMapper.toDomain(movieDbo)).thenReturn(domainMovie);

        // When
        var result = movieRepositoryAdapter.findAll()
            .collectList()
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainMovie);
        verify(jpaMovieRepository).findAll();
    }

    @Test
    void deleteById_ShouldCallRepository_WhenValidId() {
        // Given
        when(jpaMovieRepository.deleteById(testId)).thenReturn(Mono.empty());

        // When
        movieRepositoryAdapter.deleteById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        verify(jpaMovieRepository).deleteById(testId);
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        when(jpaMovieRepository.existsById(testId)).thenReturn(Mono.just(true));

        // When
        Boolean result = movieRepositoryAdapter.existsById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isTrue();
        verify(jpaMovieRepository).existsById(testId);
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityNotExists() {
        // Given
        when(jpaMovieRepository.existsById(testId)).thenReturn(Mono.just(false));

        // When
        Boolean result = movieRepositoryAdapter.existsById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isFalse();
        verify(jpaMovieRepository).existsById(testId);
    }

    @Test
    void findBySearchTerm_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        String searchTerm = "test";
        Integer page = 0;
        Integer size = 10;
        Long offset = 0L;
        Long limit = 10L;
        
        when(jpaMovieRepository.findBySearchTerm(searchTerm, limit, offset))
            .thenReturn(Flux.just(movieDbo));
        when(movieMapper.toDomain(movieDbo)).thenReturn(domainMovie);

        // When
        var result = movieRepositoryAdapter.findBySearchTerm(searchTerm, page, size)
            .collectList()
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainMovie);
    }

    @Test
    void findByFilters_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        String search = "test";
        String status = "ACTIVE";
        String dateFrom = "2024-01-01T00:00:00Z";
        String dateTo = "2024-12-31T23:59:59Z";
        Integer page = 0;
        Integer size = 10;
        Long offset = 0L;
        Long limit = 10L;
        
        when(jpaMovieRepository.findByFilters(search, status, dateFrom, dateTo, limit, offset))
            .thenReturn(Flux.just(movieDbo));
        when(movieMapper.toDomain(movieDbo)).thenReturn(domainMovie);

        // When
        var result = movieRepositoryAdapter.findByFilters(search, status, dateFrom, dateTo, page, size)
            .collectList()
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainMovie);
    }
}
