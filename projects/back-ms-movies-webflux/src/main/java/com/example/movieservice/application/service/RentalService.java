package com.example.movieservice.application.service;

import com.example.movieservice.domain.ports.input.RentalUseCase;
import com.example.movieservice.domain.ports.output.RentalRepositoryPort;
import com.example.movieservice.application.dto.movie.CreateRentalRequestContent;
import com.example.movieservice.application.dto.movie.CreateRentalResponseContent;
import com.example.movieservice.application.dto.movie.GetRentalResponseContent;
import com.example.movieservice.application.dto.movie.UpdateRentalRequestContent;
import com.example.movieservice.application.dto.movie.UpdateRentalResponseContent;

import com.example.movieservice.application.dto.movie.ListRentalsResponseContent;
import com.example.movieservice.domain.model.Rental;
import com.example.movieservice.application.mapper.RentalMapper;
import com.example.movieservice.infrastructure.config.exceptions.NotFoundException;
import com.example.movieservice.utils.LoggingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

/**
 * Consolidated application service implementing all Rental use cases.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class RentalService implements RentalUseCase {

    private static final LoggingUtils logger = LoggingUtils.getLogger(RentalService.class);
    
    private final RentalRepositoryPort rentalRepositoryPort;
    private final RentalMapper rentalMapper;

    @Override
    public Mono<CreateRentalResponseContent> create(CreateRentalRequestContent request) {
        logger.info("Executing CreateRental with request: {}", request);
        
        return Mono.fromCallable(() -> rentalMapper.fromCreateRequest(request))
                .flatMap(rentalRepositoryPort::save)
                .map(savedRental -> {
                    logger.info("Rental created successfully with ID: {}", savedRental.getRentalId());
                    return rentalMapper.toCreateResponse(savedRental);
                })
                .doOnError(e -> logger.error("Error in CreateRental", e, request));
    }

    @Override
    public Mono<GetRentalResponseContent> get(String rentalId) {
        logger.info("Executing GetRental with rentalId: {}", rentalId);
        
        return rentalRepositoryPort.findById(rentalId)
                .switchIfEmpty(Mono.error(new NotFoundException("Rental not found")))
                .map(rental -> {
                    logger.info("Rental retrieved successfully with ID: {}", rentalId);
                    return rentalMapper.toGetResponse(rental);
                })
                .doOnError(e -> logger.error("Error in GetRental", e, rentalId));
    }

    @Override
    public Mono<UpdateRentalResponseContent> update(String rentalId, UpdateRentalRequestContent request) {
        logger.info("Executing UpdateRental with rentalId: {} and request: {}", rentalId, request);
        
        return rentalRepositoryPort.findById(rentalId)
                .switchIfEmpty(Mono.error(new NotFoundException("Rental not found")))
                .map(existingRental -> {
                    rentalMapper.updateEntityFromRequest(request, existingRental);
                    existingRental.setUpdatedAt(java.time.Instant.now().toString());
                    return existingRental;
                })
                .flatMap(rentalRepositoryPort::save)
                .map(savedRental -> {
                    logger.info("Rental updated successfully with ID: {}", rentalId);
                    return rentalMapper.toUpdateResponse(savedRental);
                })
                .doOnError(e -> logger.error("Error in UpdateRental", e, rentalId));
    }


    @Override
    public Mono<ListRentalsResponseContent> list(Integer page, Integer size, String search) {
        logger.info("Executing ListRentals with page: {}, size: {}, search: {}", page, size, search);
        
        Flux<Rental> rentalFlux;
        if (search != null && !search.trim().isEmpty()) {
            rentalFlux = rentalRepositoryPort.findBySearchTerm(search, page, size);
        } else {
            rentalFlux = rentalRepositoryPort.findAll();
        }
        
        return rentalFlux
                .collectList()
                .map(rentals -> {
                    logger.info("Retrieved {} rentals successfully", rentals.size());
                    int pageNum = page != null ? page : 1;
                    int pageSize = size != null ? size : 20;
                    return rentalMapper.toListResponse(rentals, pageNum, pageSize);
                })
                .doOnError(e -> logger.error("Error in ListRentals", e));
    }

}