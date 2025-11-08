package com.example.movieservice.infrastructure.adapters.input.rest;

import com.example.movieservice.domain.ports.input.MovieUseCase;
import com.example.movieservice.application.dto.movie.CreateMovieRequestContent;
import com.example.movieservice.application.dto.movie.CreateMovieResponseContent;
import com.example.movieservice.application.dto.movie.GetMovieResponseContent;
import com.example.movieservice.application.dto.movie.UpdateMovieRequestContent;
import com.example.movieservice.application.dto.movie.UpdateMovieResponseContent;
import com.example.movieservice.application.dto.movie.DeleteMovieResponseContent;
import com.example.movieservice.application.dto.movie.ListMoviesResponseContent;
import com.example.movieservice.utils.LoggingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Reactive REST Controller for Movie operations.
 * <p>
 * This controller serves as the input adapter in the Clean Architecture,
 * handling HTTP requests reactively and delegating business logic to use cases.
 * It follows REST conventions and provides reactive endpoints for CRUD operations
 * using Spring WebFlux and Project Reactor.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
@Tag(name = "Movie", description = "Movie management operations")
public class MovieController {

    private static final LoggingUtils logger = LoggingUtils.getLogger(MovieController.class);

    private final MovieUseCase movieUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new Movie", description = "Creates a new Movie with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Movie created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Movie already exists")
    })
    public Mono<CreateMovieResponseContent> createMovie(
            @Parameter(description = "Movie creation request", required = true)
            @Valid @RequestBody CreateMovieRequestContent request,
            @Parameter(description = "Unique request identifier", required = true)
            @RequestHeader("X-Request-ID") String requestId,
            @Parameter(description = "Correlation identifier for transaction tracking")
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @Parameter(description = "Client service identifier")
            @RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        return Mono.fromRunnable(() -> LoggingUtils.setRequestContext(requestId, correlationId, clientId))
                .then(Mono.fromCallable(() -> {
                    logger.info("Creating movie with request: {}", request);
                    return request;
                }))
                .flatMap(movieUseCase::create)
                .doFinally(signal -> LoggingUtils.clearRequestContext());
    }

    @GetMapping("/{movieId}")
    @Operation(summary = "Get Movie by ID", description = "Retrieves a Movie by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movie found"),
        @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public Mono<GetMovieResponseContent> getMovie(
            @Parameter(description = "Movie unique identifier", required = true)
            @PathVariable String movieId,
            @Parameter(description = "Unique request identifier", required = true)
            @RequestHeader("X-Request-ID") String requestId,
            @Parameter(description = "Correlation identifier for transaction tracking")
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @Parameter(description = "Client service identifier")
            @RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        return Mono.fromRunnable(() -> LoggingUtils.setRequestContext(requestId, correlationId, clientId))
                .then(Mono.fromCallable(() -> {
                    logger.info("Getting movie with id: {}", movieId);
                    return movieId;
                }))
                .flatMap(movieUseCase::get)
                .doFinally(signal -> LoggingUtils.clearRequestContext());
    }

    @PutMapping("/{movieId}")
    @Operation(summary = "Update Movie", description = "Updates an existing Movie with new information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movie updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public Mono<UpdateMovieResponseContent> updateMovie(
            @Parameter(description = "Movie unique identifier", required = true)
            @PathVariable String movieId,
            @Parameter(description = "Movie update request", required = true)
            @Valid @RequestBody UpdateMovieRequestContent request,
            @Parameter(description = "Unique request identifier", required = true)
            @RequestHeader("X-Request-ID") String requestId,
            @Parameter(description = "Correlation identifier for transaction tracking")
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @Parameter(description = "Client service identifier")
            @RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        return Mono.fromRunnable(() -> LoggingUtils.setRequestContext(requestId, correlationId, clientId))
                .then(Mono.fromCallable(() -> {
                    logger.info("Updating movie {} with request: {}", movieId, request);
                    return request;
                }))
                .flatMap(req -> movieUseCase.update(movieId, req))
                .doFinally(signal -> LoggingUtils.clearRequestContext());
    }

    @DeleteMapping("/{movieId}")
    @Operation(summary = "Delete Movie", description = "Deletes a Movie by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movie deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public Mono<DeleteMovieResponseContent> deleteMovie(
            @Parameter(description = "Movie unique identifier", required = true)
            @PathVariable String movieId,
            @Parameter(description = "Unique request identifier", required = true)
            @RequestHeader("X-Request-ID") String requestId,
            @Parameter(description = "Correlation identifier for transaction tracking")
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @Parameter(description = "Client service identifier")
            @RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        return Mono.fromRunnable(() -> LoggingUtils.setRequestContext(requestId, correlationId, clientId))
                .then(Mono.fromCallable(() -> {
                    logger.info("Deleting movie with id: {}", movieId);
                    return movieId;
                }))
                .flatMap(movieUseCase::delete)
                .doFinally(signal -> LoggingUtils.clearRequestContext());
    }

    @GetMapping
    @Operation(summary = "List Movies", description = "Retrieves a paginated list of Movies with optional search")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movies retrieved successfully")
    })
    public Mono<ListMoviesResponseContent> listMovies(
            @Parameter(description = "Page number (1-based)", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Search term for filtering")
            @RequestParam(required = false) String search,
            @Parameter(description = "Unique request identifier", required = true)
            @RequestHeader("X-Request-ID") String requestId,
            @Parameter(description = "Correlation identifier for transaction tracking")
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @Parameter(description = "Client service identifier")
            @RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        return Mono.fromRunnable(() -> LoggingUtils.setRequestContext(requestId, correlationId, clientId))
                .then(Mono.fromCallable(() -> {
                    logger.info("Listing movies with page: {}, size: {}, search: {}", page, size, search);
                    return search;
                }))
                .flatMap(searchTerm -> movieUseCase.list(page, size, searchTerm))
                .doFinally(signal -> LoggingUtils.clearRequestContext());
    }

}
