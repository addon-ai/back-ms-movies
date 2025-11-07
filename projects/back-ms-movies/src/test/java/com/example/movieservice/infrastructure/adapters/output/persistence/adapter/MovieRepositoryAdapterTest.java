package com.example.movieservice.infrastructure.adapters.output.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    @BeforeEach
    void setUp() {
        domainMovie = Movie.builder()
            .build();
        
        movieDbo = MovieDbo.builder()
            .build();
    }

    @Test
    void save_ShouldReturnDomainEntity_WhenValidEntity() {
        // Given
        when(movieMapper.toDbo(domainMovie)).thenReturn(movieDbo);
        when(jpaMovieRepository.save(movieDbo)).thenReturn(movieDbo);
        when(movieMapper.toDomain(movieDbo)).thenReturn(domainMovie);

        // When
        Movie result = movieRepositoryAdapter.save(domainMovie);

        // Then
        assertThat(result).isNotNull();
        verify(movieMapper).toDbo(domainMovie);
        verify(jpaMovieRepository).save(movieDbo);
        verify(movieMapper).toDomain(movieDbo);
    }

    @Test
    void save_ShouldThrowException_WhenRepositoryFails() {
        // Given
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(movieMapper.toDbo(domainMovie)).thenReturn(movieDbo);
        when(jpaMovieRepository.save(movieDbo)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> movieRepositoryAdapter.save(domainMovie))
            .isInstanceOf(com.example.movieservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to save Movie");
    }

    @Test
    void findById_ShouldReturnOptionalWithEntity_WhenEntityExists() {
        // Given
        String movieId = "test-id";
        when(jpaMovieRepository.findById(movieId)).thenReturn(Optional.of(movieDbo));
        when(movieMapper.toDomain(movieDbo)).thenReturn(domainMovie);

        // When
        Optional<Movie> result = movieRepositoryAdapter.findById(movieId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(domainMovie);
        verify(jpaMovieRepository).findById(movieId);
        verify(movieMapper).toDomain(movieDbo);
    }

    @Test
    void findById_ShouldReturnEmptyOptional_WhenEntityNotFound() {
        // Given
        String movieId = "non-existent-id";
        when(jpaMovieRepository.findById(movieId)).thenReturn(Optional.empty());

        // When
        Optional<Movie> result = movieRepositoryAdapter.findById(movieId);

        // Then
        assertThat(result).isEmpty();
        verify(jpaMovieRepository).findById(movieId);
    }

    @Test
    void findById_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String movieId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaMovieRepository.findById(movieId)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> movieRepositoryAdapter.findById(movieId))
            .isInstanceOf(com.example.movieservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to find Movie by id");
    }

    @Test
    void findAll_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        List<MovieDbo> movieDbos = Collections.singletonList(movieDbo);
        List<Movie> movies = Collections.singletonList(domainMovie);
        when(jpaMovieRepository.findAll()).thenReturn(movieDbos);
        when(movieMapper.toDomainList(movieDbos)).thenReturn(movies);

        // When
        List<Movie> result = movieRepositoryAdapter.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(movies);
        verify(jpaMovieRepository).findAll();
        verify(movieMapper).toDomainList(movieDbos);
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoEntitiesExist() {
        // Given
        when(jpaMovieRepository.findAll()).thenReturn(Collections.emptyList());
        when(movieMapper.toDomainList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<Movie> result = movieRepositoryAdapter.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(jpaMovieRepository).findAll();
        verify(movieMapper).toDomainList(Collections.emptyList());
    }

    @Test
    void findAll_ShouldThrowException_WhenRepositoryFails() {
        // Given
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaMovieRepository.findAll()).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> movieRepositoryAdapter.findAll())
            .isInstanceOf(com.example.movieservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to find all Movies");
        
        verify(jpaMovieRepository).findAll();
    }

    @Test
    void deleteById_ShouldCallRepository_WhenValidId() {
        // Given
        String movieId = "test-id";

        // When
        movieRepositoryAdapter.deleteById(movieId);

        // Then
        verify(jpaMovieRepository).deleteById(movieId);
    }

    @Test
    void deleteById_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String movieId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        doThrow(repositoryException).when(jpaMovieRepository).deleteById(movieId);

        // When & Then
        assertThatThrownBy(() -> movieRepositoryAdapter.deleteById(movieId))
            .isInstanceOf(com.example.movieservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to delete Movie by id");
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        String movieId = "test-id";
        when(jpaMovieRepository.existsById(movieId)).thenReturn(true);

        // When
        boolean result = movieRepositoryAdapter.existsById(movieId);

        // Then
        assertThat(result).isTrue();
        verify(jpaMovieRepository).existsById(movieId);
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityNotExists() {
        // Given
        String movieId = "non-existent-id";
        when(jpaMovieRepository.existsById(movieId)).thenReturn(false);

        // When
        boolean result = movieRepositoryAdapter.existsById(movieId);

        // Then
        assertThat(result).isFalse();
        verify(jpaMovieRepository).existsById(movieId);
    }

    @Test
    void existsById_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String movieId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaMovieRepository.existsById(movieId)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> movieRepositoryAdapter.existsById(movieId))
            .isInstanceOf(com.example.movieservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to check if Movie exists by id");
    }

    @Test
    void findBySearchTerm_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        String searchTerm = "test";
        Integer page = 1;
        Integer size = 10;
        
        when(jpaMovieRepository.findBySearchTerm(anyString(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(movieDbo)));
        when(movieMapper.toDomain(movieDbo)).thenReturn(domainMovie);

        // When
        List<Movie> result = movieRepositoryAdapter.findBySearchTerm(searchTerm, page, size);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainMovie);
    }

    @Test
    void findBySearchTerm_ShouldReturnListOfEntities_WhenSearchIsEmpty() {
        // Given
        String searchTerm = "";
        Integer page = 1;
        Integer size = 10;
        
        when(jpaMovieRepository.findAll()).thenReturn(Collections.singletonList(movieDbo));
        when(movieMapper.toDomainList(Collections.singletonList(movieDbo)))
            .thenReturn(Collections.singletonList(domainMovie));

        // When
        List<Movie> result = movieRepositoryAdapter.findBySearchTerm(searchTerm, page, size);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainMovie);
    }

    @Test
    void findBySearchTerm_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String searchTerm = "test";
        Integer page = 1;
        Integer size = 10;
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaMovieRepository.findBySearchTerm(anyString(), any(Pageable.class)))
            .thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> movieRepositoryAdapter.findBySearchTerm(searchTerm, page, size))
            .isInstanceOf(com.example.movieservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to search Movies");
    }

    @Test
    void findBySearchTerm_ShouldThrowException_WhenRepositoryFailsOnFindAll() {
        // Given
        String searchTerm = null;
        Integer page = 1;
        Integer size = 10;
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaMovieRepository.findAll()).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> movieRepositoryAdapter.findBySearchTerm(searchTerm, page, size))
            .isInstanceOf(com.example.movieservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to search Movies");
    }

    @Test
    void findBySearchTermPaged_ShouldReturnPageOfEntities_WhenEntitiesExist() {
        // Given
        String searchTerm = "test";
        Pageable pageable = PageRequest.of(0, 10);
        List<MovieDbo> movieDbos = Collections.singletonList(movieDbo);
        Page<MovieDbo> dboPage = new PageImpl<>(movieDbos, pageable, 1);
        
        when(jpaMovieRepository.findBySearchTerm(searchTerm, pageable)).thenReturn(dboPage);
        when(movieMapper.toDomain(movieDbo)).thenReturn(domainMovie);

        // When
        Page<Movie> result = movieRepositoryAdapter.findBySearchTermPaged(searchTerm, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(domainMovie);
        verify(jpaMovieRepository).findBySearchTerm(searchTerm, pageable);
        verify(movieMapper).toDomain(movieDbo);
    }

    @Test
    void findBySearchTermPaged_ShouldReturnEmptyPage_WhenNoEntitiesExist() {
        // Given
        String searchTerm = "nonexistent";
        Pageable pageable = PageRequest.of(0, 10);
        Page<MovieDbo> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        
        when(jpaMovieRepository.findBySearchTerm(searchTerm, pageable)).thenReturn(emptyPage);

        // When
        Page<Movie> result = movieRepositoryAdapter.findBySearchTermPaged(searchTerm, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(jpaMovieRepository).findBySearchTerm(searchTerm, pageable);
    }

    @Test
    void findAllPaged_ShouldReturnPageOfEntities_WhenEntitiesExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<MovieDbo> movieDbos = Collections.singletonList(movieDbo);
        Page<MovieDbo> dboPage = new PageImpl<>(movieDbos, pageable, 1);
        
        when(jpaMovieRepository.findAllPaged(pageable)).thenReturn(dboPage);
        when(movieMapper.toDomain(movieDbo)).thenReturn(domainMovie);

        // When
        Page<Movie> result = movieRepositoryAdapter.findAllPaged(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(domainMovie);
        verify(jpaMovieRepository).findAllPaged(pageable);
        verify(movieMapper).toDomain(movieDbo);
    }

    @Test
    void findAllPaged_ShouldReturnEmptyPage_WhenNoEntitiesExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<MovieDbo> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        
        when(jpaMovieRepository.findAllPaged(pageable)).thenReturn(emptyPage);

        // When
        Page<Movie> result = movieRepositoryAdapter.findAllPaged(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(jpaMovieRepository).findAllPaged(pageable);
    }

    @Test
    void findBySearchTermPaged_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String searchTerm = "test";
        Pageable pageable = PageRequest.of(0, 10);
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaMovieRepository.findBySearchTerm(searchTerm, pageable)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> movieRepositoryAdapter.findBySearchTermPaged(searchTerm, pageable))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }

    @Test
    void findAllPaged_ShouldThrowException_WhenRepositoryFails() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaMovieRepository.findAllPaged(pageable)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> movieRepositoryAdapter.findAllPaged(pageable))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }
}