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
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Consolidated application service implementing all Movie use cases.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MovieService implements MovieUseCase {

    private static final LoggingUtils logger = LoggingUtils.getLogger(MovieService.class);
    
    private final MovieRepositoryPort movieRepositoryPort;
    private final MovieMapper movieMapper;

    @Override
    public CreateMovieResponseContent create(CreateMovieRequestContent request) {
        logger.info("Executing CreateMovie with request: {}", request);
        
        try {
            Movie movie = movieMapper.fromCreateRequest(request);
            Movie savedMovie = movieRepositoryPort.save(movie);
            logger.info("Movie created successfully with ID: {}", savedMovie.getMovieId());
            return movieMapper.toCreateResponse(savedMovie);
        } catch (Exception e) {
            logger.error("Error in CreateMovie", e, request);
            throw e;
        }
    }

    @Override
    public GetMovieResponseContent get(String movieId) {
        logger.info("Executing GetMovie with movieId: {}", movieId);
        
        try {
            Movie movie = movieRepositoryPort.findById(movieId)
                .orElseThrow(() -> new NotFoundException("Movie not found"));
            
            logger.info("Movie retrieved successfully with ID: {}", movieId);
            return movieMapper.toGetResponse(movie);
        } catch (NotFoundException e) {
            logger.error("Movie not found in GetMovie", e, movieId);
            throw e;
        } catch (Exception e) {
            logger.error("Error in GetMovie", e, movieId);
            throw e;
        }
    }

    @Override
    public UpdateMovieResponseContent update(String movieId, UpdateMovieRequestContent request) {
        logger.info("Executing UpdateMovie with movieId: {} and request: {}", movieId, request);
        
        try {
            Movie existingMovie = movieRepositoryPort.findById(movieId)
                .orElseThrow(() -> new NotFoundException("Movie not found"));
            
            // Merge request data into existing entity
            movieMapper.updateEntityFromRequest(request, existingMovie);
            existingMovie.setUpdatedAt(java.time.Instant.now().toString());
            
            Movie savedMovie = movieRepositoryPort.save(existingMovie);
            logger.info("Movie updated successfully with ID: {}", movieId);
            
            return movieMapper.toUpdateResponse(savedMovie);
        } catch (NotFoundException e) {
            logger.error("Movie not found in UpdateMovie", e, movieId);
            throw e;
        } catch (Exception e) {
            logger.error("Error in UpdateMovie", e, movieId);
            throw e;
        }
    }

    @Override
    public DeleteMovieResponseContent delete(String movieId) {
        logger.info("Executing DeleteMovie with movieId: {}", movieId);
        
        try {
            Movie movie = movieRepositoryPort.findById(movieId)
                .orElseThrow(() -> new NotFoundException("Movie not found"));
            
            movieRepositoryPort.deleteById(movieId);
            logger.info("Movie deleted successfully with ID: {}", movieId);
            
            return DeleteMovieResponseContent.builder()
                .deleted(true)
                .message("Movie deleted successfully")
                .build();
        } catch (NotFoundException e) {
            logger.error("Movie not found in DeleteMovie", e, movieId);
            throw e;
        } catch (Exception e) {
            logger.error("Error in DeleteMovie", e, movieId);
            throw e;
        }
    }

    @Override
    public ListMoviesResponseContent list(Integer page, Integer size, String search) {
        logger.info("Executing ListMovies with page: {}, size: {}, search: {}", page, size, search);
        
        try {
            List<Movie> movies;
            if (search != null && !search.trim().isEmpty()) {
                movies = movieRepositoryPort.findBySearchTerm(search, page, size);
            } else {
                movies = movieRepositoryPort.findAll();
            }
            logger.info("Retrieved {} movies successfully", movies.size());
            return movieMapper.toListResponse(movies, page != null ? page : 1, size != null ? size : 20);
        } catch (Exception e) {
            logger.error("Error in ListMovies", e);
            throw e;
        }
    }

}