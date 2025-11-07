package com.example.movieservice.domain.ports.input;

import com.example.movieservice.application.dto.movie.CreateMovieRequestContent;
import com.example.movieservice.application.dto.movie.CreateMovieResponseContent;
import com.example.movieservice.application.dto.movie.GetMovieResponseContent;
import com.example.movieservice.application.dto.movie.UpdateMovieRequestContent;
import com.example.movieservice.application.dto.movie.UpdateMovieResponseContent;
import com.example.movieservice.application.dto.movie.DeleteMovieResponseContent;
import com.example.movieservice.application.dto.movie.ListMoviesResponseContent;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

/**
 * Consolidated use case interface for all Movie operations.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
public interface MovieUseCase {
    
    Mono<CreateMovieResponseContent> create(CreateMovieRequestContent request);

    Mono<GetMovieResponseContent> get(String movieId);

    Mono<UpdateMovieResponseContent> update(String movieId, UpdateMovieRequestContent request);

    Mono<DeleteMovieResponseContent> delete(String movieId);

    Mono<ListMoviesResponseContent> list(Integer page, Integer size, String search);

}