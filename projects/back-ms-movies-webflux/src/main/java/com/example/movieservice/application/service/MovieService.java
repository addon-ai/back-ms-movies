package com.example.movieservice.application.service;

import com.example.movieservice.domain.ports.input.MovieUseCase;
import com.example.movieservice.domain.ports.output.MovieRepositoryPort;
import com.example.movieservice.application.dto.movie.CreateMovieRequestContent;
import com.example.movieservice.application.dto.movie.CreateMovieResponseContent;
import com.example.movieservice.application.dto.movie.GetMovieResponseContent;
import com.example.movieservice.application.dto.movie.UpdateMovieRequestContent;
import com.example.movieservice.application.dto.movie.UpdateMovieResponseContent;
import com.example.movieservice.application.dto.movie.DeleteMovieResponseContent;
import com.example.movieservice.application.dto.movie.ListMoviesResponseContent;
import com.example.movieservice.domain.model.Movie;
import com.example.movieservice.application.mapper.MovieMapper;
import com.example.movieservice.infrastructure.config.exceptions.NotFoundException;
import com.example.movieservice.utils.LoggingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

/**
 * Consolidated application service implementing all Movie use cases.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class MovieService implements MovieUseCase {

    private static final LoggingUtils logger = LoggingUtils.getLogger(MovieService.class);
    
    private final MovieRepositoryPort movieRepositoryPort;
    private final MovieMapper movieMapper;

    @Override
    public Mono<CreateMovieResponseContent> create(CreateMovieRequestContent request) {
        logger.info("Executing CreateMovie with request: {}", request);
        
        return Mono.fromCallable(() -> movieMapper.fromCreateRequest(request))
                .flatMap(movieRepositoryPort::save)
                .map(savedMovie -> {
                    logger.info("Movie created successfully with ID: {}", savedMovie.getMovieId());
                    return movieMapper.toCreateResponse(savedMovie);
                })
                .doOnError(e -> logger.error("Error in CreateMovie", e, request));
    }

    @Override
    public Mono<GetMovieResponseContent> get(String movieId) {
        logger.info("Executing GetMovie with movieId: {}", movieId);
        
        return movieRepositoryPort.findById(movieId)
                .switchIfEmpty(Mono.error(new NotFoundException("Movie not found")))
                .map(movie -> {
                    logger.info("Movie retrieved successfully with ID: {}", movieId);
                    return movieMapper.toGetResponse(movie);
                })
                .doOnError(e -> logger.error("Error in GetMovie", e, movieId));
    }

    @Override
    public Mono<UpdateMovieResponseContent> update(String movieId, UpdateMovieRequestContent request) {
        logger.info("Executing UpdateMovie with movieId: {} and request: {}", movieId, request);
        
        return movieRepositoryPort.findById(movieId)
                .switchIfEmpty(Mono.error(new NotFoundException("Movie not found")))
                .map(existingMovie -> {
                    movieMapper.updateEntityFromRequest(request, existingMovie);
                    existingMovie.setUpdatedAt(java.time.Instant.now().toString());
                    return existingMovie;
                })
                .flatMap(movieRepositoryPort::save)
                .map(savedMovie -> {
                    logger.info("Movie updated successfully with ID: {}", movieId);
                    return movieMapper.toUpdateResponse(savedMovie);
                })
                .doOnError(e -> logger.error("Error in UpdateMovie", e, movieId));
    }

    @Override
    public Mono<DeleteMovieResponseContent> delete(String movieId) {
        logger.info("Executing DeleteMovie with movieId: {}", movieId);
        
        return movieRepositoryPort.findById(movieId)
                .switchIfEmpty(Mono.error(new NotFoundException("Movie not found")))
                .flatMap(movie -> movieRepositoryPort.deleteById(movieId))
                .then(Mono.fromCallable(() -> {
                    logger.info("Movie deleted successfully with ID: {}", movieId);
                    return DeleteMovieResponseContent.builder()
                            .deleted(true)
                            .message("Movie deleted successfully")
                            .build();
                }))
                .doOnError(e -> logger.error("Error in DeleteMovie", e, movieId));
    }

    @Override
    public Mono<ListMoviesResponseContent> list(Integer page, Integer size, String search) {
        logger.info("Executing ListMovies with page: {}, size: {}, search: {}", page, size, search);
        
        Flux<Movie> movieFlux;
        if (search != null && !search.trim().isEmpty()) {
            movieFlux = movieRepositoryPort.findBySearchTerm(search, page, size);
        } else {
            movieFlux = movieRepositoryPort.findAll();
        }
        
        return movieFlux
                .collectList()
                .map(movies -> {
                    logger.info("Retrieved {} movies successfully", movies.size());
                    int pageNum = page != null ? page : 1;
                    int pageSize = size != null ? size : 20;
                    return movieMapper.toListResponse(movies, pageNum, pageSize);
                })
                .doOnError(e -> logger.error("Error in ListMovies", e));
    }

}