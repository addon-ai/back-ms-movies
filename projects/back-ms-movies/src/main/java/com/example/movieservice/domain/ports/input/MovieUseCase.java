package com.example.movieservice.domain.ports.input;

import com.example.movieservice.application.dto.movie.CreateMovieRequestContent;
import com.example.movieservice.application.dto.movie.CreateMovieResponseContent;
import com.example.movieservice.application.dto.movie.GetMovieResponseContent;
import com.example.movieservice.application.dto.movie.UpdateMovieRequestContent;
import com.example.movieservice.application.dto.movie.UpdateMovieResponseContent;
import com.example.movieservice.application.dto.movie.DeleteMovieResponseContent;
import com.example.movieservice.application.dto.movie.ListMoviesResponseContent;

/**
 * Consolidated use case interface for all Movie operations.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
public interface MovieUseCase {
    
    CreateMovieResponseContent create(CreateMovieRequestContent request);

    GetMovieResponseContent get(String movieId);

    UpdateMovieResponseContent update(String movieId, UpdateMovieRequestContent request);

    DeleteMovieResponseContent delete(String movieId);

    ListMoviesResponseContent list(Integer page, Integer size, String search, String status, String dateFrom, String dateTo);

}