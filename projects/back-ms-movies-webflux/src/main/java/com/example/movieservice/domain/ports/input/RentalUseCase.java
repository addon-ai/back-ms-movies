package com.example.movieservice.domain.ports.input;

import com.example.movieservice.application.dto.movie.CreateRentalRequestContent;
import com.example.movieservice.application.dto.movie.CreateRentalResponseContent;
import com.example.movieservice.application.dto.movie.GetRentalResponseContent;
import com.example.movieservice.application.dto.movie.UpdateRentalRequestContent;
import com.example.movieservice.application.dto.movie.UpdateRentalResponseContent;

import com.example.movieservice.application.dto.movie.ListRentalsResponseContent;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

/**
 * Consolidated use case interface for all Rental operations.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
public interface RentalUseCase {
    
    Mono<CreateRentalResponseContent> create(CreateRentalRequestContent request);

    Mono<GetRentalResponseContent> get(String rentalId);

    Mono<UpdateRentalResponseContent> update(String rentalId, UpdateRentalRequestContent request);


    Mono<ListRentalsResponseContent> list(Integer page, Integer size, String search, String status, String dateFrom, String dateTo);

}