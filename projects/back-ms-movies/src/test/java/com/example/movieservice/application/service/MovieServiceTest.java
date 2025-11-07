package com.example.movieservice.application.service;

import com.example.movieservice.domain.ports.output.MovieRepositoryPort;
import com.example.movieservice.application.mapper.MovieMapper;
import com.example.movieservice.application.dto.movie.CreateMovieRequestContent;
import com.example.movieservice.application.dto.movie.CreateMovieResponseContent;
import com.example.movieservice.application.dto.movie.GetMovieResponseContent;
import com.example.movieservice.application.dto.movie.UpdateMovieRequestContent;
import com.example.movieservice.application.dto.movie.UpdateMovieResponseContent;
import com.example.movieservice.application.dto.movie.DeleteMovieResponseContent;
import com.example.movieservice.application.dto.movie.ListMoviesResponseContent;
import com.example.movieservice.domain.model.Movie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mapstruct.factory.Mappers;

import java.util.Optional;
import java.util.List;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;

import com.example.movieservice.infrastructure.config.exceptions.NotFoundException;

/**
 * Unit tests for MovieService.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MovieServiceTest {

    @Mock
    private MovieRepositoryPort movieRepositoryPort;

    @Spy
    private MovieMapper movieMapper = Mappers.getMapper(MovieMapper.class);

    @InjectMocks
    private MovieService movieService;

    @Test
    void create_ShouldReturnResponse_WhenValidRequest() {
        // Given
        CreateMovieRequestContent request = CreateMovieRequestContent.builder()
            .build();
        Movie domainMovie = Movie.builder()
            .build();
        Movie savedMovie = Movie.builder()
            .build();
        CreateMovieResponseContent expectedResponse = CreateMovieResponseContent.builder()
            .build();

        when(movieMapper.fromCreateRequest(request)).thenReturn(domainMovie);
        when(movieRepositoryPort.save(domainMovie)).thenReturn(savedMovie);
        when(movieMapper.toCreateResponse(savedMovie)).thenReturn(expectedResponse);

        // When
        CreateMovieResponseContent result = movieService.create(request);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(movieRepositoryPort).save(domainMovie);
    }

    @Test
    void create_ShouldThrowException_WhenRepositoryFails() {
        // Given
        CreateMovieRequestContent request = CreateMovieRequestContent.builder()
            .build();
        Movie domainMovie = Movie.builder()
            .build();
        RuntimeException repositoryException = new RuntimeException("Database error");

        when(movieMapper.fromCreateRequest(request)).thenReturn(domainMovie);
        when(movieRepositoryPort.save(domainMovie)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> movieService.create(request))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }

    @Test
    void get_ShouldReturnResponse_WhenEntityExists() {
        // Given
        String movieId = "test-id";
        Movie domainMovie = Movie.builder()
            .build();
        GetMovieResponseContent expectedResponse = GetMovieResponseContent.builder()
            .build();

        when(movieRepositoryPort.findById(movieId)).thenReturn(Optional.of(domainMovie));
        when(movieMapper.toGetResponse(domainMovie)).thenReturn(expectedResponse);

        // When
        GetMovieResponseContent result = movieService.get(movieId);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(movieRepositoryPort).findById(movieId);
    }

    @Test
    void get_ShouldThrowNotFoundException_WhenEntityNotFound() {
        // Given
        String movieId = "non-existent-id";
        when(movieRepositoryPort.findById(movieId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> movieService.get(movieId))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Movie not found");
    }

    @Test
    void get_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String movieId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(movieRepositoryPort.findById(movieId)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> movieService.get(movieId))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }

    @Test
    void update_ShouldReturnResponse_WhenValidRequest() {
        // Given
        String movieId = "test-id";
        UpdateMovieRequestContent request = UpdateMovieRequestContent.builder()
            .build();
        Movie existingMovie = Movie.builder()
            .build();
        Movie updatedMovie = Movie.builder()
            .build();
        UpdateMovieResponseContent expectedResponse = UpdateMovieResponseContent.builder()
            .build();

        when(movieRepositoryPort.findById(movieId)).thenReturn(Optional.of(existingMovie));
        // Use void method for updating existing entity
        // The updateEntityFromRequest method modifies existingMovie in place
        when(movieRepositoryPort.save(any(Movie.class))).thenReturn(updatedMovie);
        when(movieMapper.toUpdateResponse(any(Movie.class))).thenReturn(expectedResponse);

        // When
        UpdateMovieResponseContent result = movieService.update(movieId, request);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(movieRepositoryPort).save(any(Movie.class));
    }

    @Test
    void update_ShouldThrowNotFoundException_WhenEntityNotFound() {
        // Given
        String movieId = "non-existent-id";
        UpdateMovieRequestContent request = UpdateMovieRequestContent.builder()
            .build();
        when(movieRepositoryPort.findById(movieId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> movieService.update(movieId, request))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Movie not found");
    }

    @Test
    void update_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String movieId = "test-id";
        UpdateMovieRequestContent request = UpdateMovieRequestContent.builder()
            .build();
        Movie existingMovie = Movie.builder()
            .build();
        RuntimeException repositoryException = new RuntimeException("Database error");

        when(movieRepositoryPort.findById(movieId)).thenReturn(Optional.of(existingMovie));
        when(movieRepositoryPort.save(any(Movie.class))).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> movieService.update(movieId, request))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }

    @Test
    void delete_ShouldReturnResponse_WhenEntityExists() {
        // Given
        String movieId = "test-id";
        Movie domainMovie = Movie.builder()
            .build();
        DeleteMovieResponseContent expectedResponse = DeleteMovieResponseContent.builder()
            .deleted(true)
            .message("Movie deleted successfully")
            .build();

        when(movieRepositoryPort.findById(movieId)).thenReturn(Optional.of(domainMovie));
        when(movieMapper.toDeleteResponse(domainMovie)).thenReturn(expectedResponse);

        // When
        DeleteMovieResponseContent result = movieService.delete(movieId);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(movieRepositoryPort).deleteById(movieId);
    }

    @Test
    void delete_ShouldThrowNotFoundException_WhenEntityNotFound() {
        // Given
        String movieId = "non-existent-id";
        when(movieRepositoryPort.findById(movieId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> movieService.delete(movieId))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Movie not found");
    }

    @Test
    void delete_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String movieId = "test-id";
        Movie domainMovie = Movie.builder()
            .build();
        RuntimeException repositoryException = new RuntimeException("Database error");

        when(movieRepositoryPort.findById(movieId)).thenReturn(Optional.of(domainMovie));
        doThrow(repositoryException).when(movieRepositoryPort).deleteById(movieId);

        // When & Then
        assertThatThrownBy(() -> movieService.delete(movieId))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }

    @Test
    void list_ShouldReturnResponse_WhenValidRequest() {
        // Given
        List<Movie> movies = Collections.singletonList(Movie.builder().build());
        ListMoviesResponseContent expectedResponse = ListMoviesResponseContent.builder().build();
        
        when(movieRepositoryPort.findAll()).thenReturn(movies);
        when(movieMapper.toListResponse(movies, 1, 20)).thenReturn(expectedResponse);

        // When
        ListMoviesResponseContent result = movieService.list(1, 20, null);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(movieRepositoryPort).findAll();
    }

    @Test
    void list_ShouldReturnResponse_WhenSearchTermProvided() {
        // Given
        String searchTerm = "test search";
        Integer page = 1;
        Integer size = 10;
        List<Movie> movies = Collections.singletonList(Movie.builder().build());
        ListMoviesResponseContent expectedResponse = ListMoviesResponseContent.builder().build();
        
        when(movieRepositoryPort.findBySearchTerm(searchTerm, page, size)).thenReturn(movies);
        when(movieMapper.toListResponse(movies, page, size)).thenReturn(expectedResponse);

        // When
        ListMoviesResponseContent result = movieService.list(page, size, searchTerm);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(movieRepositoryPort).findBySearchTerm(searchTerm, page, size);
    }

    @Test
    void list_ShouldReturnResponse_WhenNullParameters() {
        // Given
        List<Movie> movies = Collections.emptyList();
        ListMoviesResponseContent expectedResponse = ListMoviesResponseContent.builder()
            .movies(Collections.emptyList())
            .page(java.math.BigDecimal.valueOf(1))
            .size(java.math.BigDecimal.valueOf(20))
            .total(java.math.BigDecimal.valueOf(0))
            .totalPages(java.math.BigDecimal.valueOf(0))
            .build();
        
        when(movieRepositoryPort.findAll()).thenReturn(movies);
        when(movieMapper.toListResponse(movies, 1, 20)).thenReturn(expectedResponse);

        // When
        ListMoviesResponseContent result = movieService.list(null, null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMovies()).isNotNull().isEmpty();
        assertThat(result.getPage()).isEqualTo(java.math.BigDecimal.valueOf(1));
        assertThat(result.getSize()).isEqualTo(java.math.BigDecimal.valueOf(20));
        assertThat(result.getTotal()).isEqualTo(java.math.BigDecimal.valueOf(0));
        assertThat(result.getTotalPages()).isEqualTo(java.math.BigDecimal.valueOf(0));
        verify(movieRepositoryPort).findAll();
    }

    @Test
    void list_ShouldThrowException_WhenRepositoryFails() {
        // Given
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(movieRepositoryPort.findAll()).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> movieService.list(1, 20, null))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }
}