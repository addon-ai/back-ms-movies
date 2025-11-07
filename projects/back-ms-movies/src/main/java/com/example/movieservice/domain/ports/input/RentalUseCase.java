package com.example.movieservice.domain.ports.input;

import com.example.movieservice.application.dto.movie.CreateRentalRequestContent;
import com.example.movieservice.application.dto.movie.CreateRentalResponseContent;
import com.example.movieservice.application.dto.movie.GetRentalResponseContent;
import com.example.movieservice.application.dto.movie.UpdateRentalRequestContent;
import com.example.movieservice.application.dto.movie.UpdateRentalResponseContent;

import com.example.movieservice.application.dto.movie.ListRentalsResponseContent;

/**
 * Consolidated use case interface for all Rental operations.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
public interface RentalUseCase {
    
    CreateRentalResponseContent create(CreateRentalRequestContent request);

    GetRentalResponseContent get(String rentalId);

    UpdateRentalResponseContent update(String rentalId, UpdateRentalRequestContent request);


    ListRentalsResponseContent list(Integer page, Integer size, String search);

}