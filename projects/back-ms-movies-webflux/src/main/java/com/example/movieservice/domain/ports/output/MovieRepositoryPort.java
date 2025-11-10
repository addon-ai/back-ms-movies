package com.example.movieservice.domain.ports.output;


import com.example.movieservice.domain.model.Movie;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Domain repository port for Movie operations.
 * <p>
 * This interface defines the contract for Movie persistence operations,
 * serving as an output port in the Clean Architecture. It abstracts
 * the persistence layer from the domain logic using reactive types.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
public interface MovieRepositoryPort {
    
    Mono<Movie> save(Movie movie);
    
    Mono<Movie> findById(String id);
    
    Flux<Movie> findAll();
    
    Flux<Movie> findBySearchTerm(String search, Integer page, Integer size);
    
    Flux<Movie> findByFilters(String search, String status, String dateFrom, String dateTo, Integer page, Integer size);
    
    Flux<Movie> findAllPaged(Integer page, Integer size);
    
    Mono<Void> deleteById(String id);
    
    Mono<Boolean> existsById(String id);
}
