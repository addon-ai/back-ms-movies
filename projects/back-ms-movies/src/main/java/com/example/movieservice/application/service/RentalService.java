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
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Consolidated application service implementing all Rental use cases.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RentalService implements RentalUseCase {

    private static final LoggingUtils logger = LoggingUtils.getLogger(RentalService.class);
    
    private final RentalRepositoryPort rentalRepositoryPort;
    private final RentalMapper rentalMapper;

    @Override
    public CreateRentalResponseContent create(CreateRentalRequestContent request) {
        logger.info("Executing CreateRental with request: {}", request);
        
        try {
            Rental rental = rentalMapper.fromCreateRequest(request);
            Rental savedRental = rentalRepositoryPort.save(rental);
            logger.info("Rental created successfully with ID: {}", savedRental.getRentalId());
            return rentalMapper.toCreateResponse(savedRental);
        } catch (Exception e) {
            logger.error("Error in CreateRental", e, request);
            throw e;
        }
    }

    @Override
    public GetRentalResponseContent get(String rentalId) {
        logger.info("Executing GetRental with rentalId: {}", rentalId);
        
        try {
            Rental rental = rentalRepositoryPort.findById(rentalId)
                .orElseThrow(() -> new NotFoundException("Rental not found"));
            
            logger.info("Rental retrieved successfully with ID: {}", rentalId);
            return rentalMapper.toGetResponse(rental);
        } catch (NotFoundException e) {
            logger.error("Rental not found in GetRental", e, rentalId);
            throw e;
        } catch (Exception e) {
            logger.error("Error in GetRental", e, rentalId);
            throw e;
        }
    }

    @Override
    public UpdateRentalResponseContent update(String rentalId, UpdateRentalRequestContent request) {
        logger.info("Executing UpdateRental with rentalId: {} and request: {}", rentalId, request);
        
        try {
            Rental existingRental = rentalRepositoryPort.findById(rentalId)
                .orElseThrow(() -> new NotFoundException("Rental not found"));
            
            // Merge request data into existing entity
            rentalMapper.updateEntityFromRequest(request, existingRental);
            existingRental.setUpdatedAt(java.time.Instant.now().toString());
            
            Rental savedRental = rentalRepositoryPort.save(existingRental);
            logger.info("Rental updated successfully with ID: {}", rentalId);
            
            return rentalMapper.toUpdateResponse(savedRental);
        } catch (NotFoundException e) {
            logger.error("Rental not found in UpdateRental", e, rentalId);
            throw e;
        } catch (Exception e) {
            logger.error("Error in UpdateRental", e, rentalId);
            throw e;
        }
    }


    @Override
    public ListRentalsResponseContent list(Integer page, Integer size, String search) {
        logger.info("Executing ListRentals with page: {}, size: {}, search: {}", page, size, search);
        
        try {
            List<Rental> rentals;
            if (search != null && !search.trim().isEmpty()) {
                rentals = rentalRepositoryPort.findBySearchTerm(search, page, size);
            } else {
                rentals = rentalRepositoryPort.findAll();
            }
            logger.info("Retrieved {} rentals successfully", rentals.size());
            return rentalMapper.toListResponse(rentals, page != null ? page : 1, size != null ? size : 20);
        } catch (Exception e) {
            logger.error("Error in ListRentals", e);
            throw e;
        }
    }

}