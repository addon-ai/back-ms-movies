package com.example.movieservice.infrastructure.adapters.input.rest;

import com.example.movieservice.domain.ports.input.MovieUseCase;
import com.example.movieservice.application.dto.movie.CreateMovieRequestContent;
import com.example.movieservice.application.dto.movie.CreateMovieResponseContent;
import com.example.movieservice.application.dto.movie.GetMovieResponseContent;
import com.example.movieservice.application.dto.movie.UpdateMovieRequestContent;
import com.example.movieservice.application.dto.movie.UpdateMovieResponseContent;
import com.example.movieservice.application.dto.movie.DeleteMovieResponseContent;
import com.example.movieservice.application.dto.movie.ListMoviesResponseContent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for MovieController.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class MovieControllerTest {

    @Mock
    private MovieUseCase movieUseCase;

    @InjectMocks
    private MovieController movieController;

    @Test
    void createMovie_ShouldReturnCreated_WhenValidRequest() {
        // Given
        CreateMovieRequestContent request = CreateMovieRequestContent.builder()
            .title("test-title")
            .director("test-director")
            .genre("test-genre")
            .build();
        CreateMovieResponseContent response = CreateMovieResponseContent.builder()
            .build();
        
        when(movieUseCase.create(any(CreateMovieRequestContent.class)))
            .thenReturn(response);

        // When
        ResponseEntity<CreateMovieResponseContent> result = movieController.createMovie(request, "test-request-id", null, null);

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void getMovie_ShouldReturnOk_WhenEntityExists() {
        // Given
        String movieId = "test-id";
        GetMovieResponseContent response = GetMovieResponseContent.builder()
            .build();
        
        when(movieUseCase.get(anyString()))
            .thenReturn(response);

        // When
        ResponseEntity<GetMovieResponseContent> result = movieController.getMovie(movieId, "test-request-id", null, null);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void updateMovie_ShouldReturnOk_WhenValidRequest() {
        // Given
        String movieId = "test-id";
        UpdateMovieRequestContent request = UpdateMovieRequestContent.builder()
            .title("updated-title")
            .director("updated-director")
            .genre("updated-genre")
            .description("updated-description")
            .build();
        UpdateMovieResponseContent response = UpdateMovieResponseContent.builder()
            .build();
        
        when(movieUseCase.update(anyString(), any(UpdateMovieRequestContent.class)))
            .thenReturn(response);

        // When
        ResponseEntity<UpdateMovieResponseContent> result = movieController.updateMovie(movieId, request, "test-request-id", null, null);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void deleteMovie_ShouldReturnOk_WhenEntityExists() {
        // Given
        String movieId = "test-id";
        DeleteMovieResponseContent response = DeleteMovieResponseContent.builder()
            .deleted(true)
            .message("Movie deleted successfully")
            .build();
        
        when(movieUseCase.delete(anyString()))
            .thenReturn(response);

        // When
        ResponseEntity<DeleteMovieResponseContent> result = movieController.deleteMovie(movieId, "test-request-id", null, null);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void listMovies_ShouldReturnOk() {
        // Given
        ListMoviesResponseContent response = ListMoviesResponseContent.builder()
            .build();
        
        when(movieUseCase.list(any(), any(), any()))
            .thenReturn(response);

        // When
        ResponseEntity<ListMoviesResponseContent> result = movieController.listMovies(1, 20, null, "test-request-id", null, null);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

}