package com.example.movieservice.infrastructure.adapters.input.rest;

import com.example.movieservice.domain.ports.input.RentalUseCase;
import com.example.movieservice.application.dto.movie.CreateRentalRequestContent;
import com.example.movieservice.application.dto.movie.CreateRentalResponseContent;
import com.example.movieservice.application.dto.movie.GetRentalResponseContent;
import com.example.movieservice.application.dto.movie.UpdateRentalRequestContent;
import com.example.movieservice.application.dto.movie.UpdateRentalResponseContent;
import com.example.movieservice.application.dto.movie.ListRentalsResponseContent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for RentalController.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class RentalControllerTest {

    @Mock
    private RentalUseCase rentalUseCase;

    @InjectMocks
    private RentalController rentalController;

    @Test
    void createRental_ShouldReturnCreated_WhenValidRequest() {
        // Given
        CreateRentalRequestContent request = CreateRentalRequestContent.builder()
            .movieId("test-movieId")
            .userId("test-userId")
            .build();
        CreateRentalResponseContent response = CreateRentalResponseContent.builder()
            .build();
        
        when(rentalUseCase.create(any(CreateRentalRequestContent.class)))
            .thenReturn(Mono.just(response));

        // When
        CreateRentalResponseContent result = rentalController.createRental(request, "test-request-id", null, null)
            .block(Duration.ofSeconds(5));

        // Then
        assertEquals(response, result);
    }

    @Test
    void getRental_ShouldReturnOk_WhenEntityExists() {
        // Given
        String rentalId = "test-id";
        GetRentalResponseContent response = GetRentalResponseContent.builder()
            .build();
        
        when(rentalUseCase.get(anyString()))
            .thenReturn(Mono.just(response));

        // When
        GetRentalResponseContent result = rentalController.getRental(rentalId, "test-request-id", null, null)
            .block(Duration.ofSeconds(5));

        // Then
        assertEquals(response, result);
    }

    @Test
    void updateRental_ShouldReturnOk_WhenValidRequest() {
        // Given
        String rentalId = "test-id";
        UpdateRentalRequestContent request = UpdateRentalRequestContent.builder()
            .returnDate("updated-returnDate")
            .status("updated-status")
            .build();
        UpdateRentalResponseContent response = UpdateRentalResponseContent.builder()
            .build();
        
        when(rentalUseCase.update(anyString(), any(UpdateRentalRequestContent.class)))
            .thenReturn(Mono.just(response));

        // When
        UpdateRentalResponseContent result = rentalController.updateRental(rentalId, request, "test-request-id", null, null)
            .block(Duration.ofSeconds(5));

        // Then
        assertEquals(response, result);
    }


    @Test
    void listRentals_ShouldReturnOk() {
        // Given
        ListRentalsResponseContent response = ListRentalsResponseContent.builder()
            .build();
        
        when(rentalUseCase.list(any(), any(), any(), any(), any(), any()))
            .thenReturn(Mono.just(response));

        // When
        ListRentalsResponseContent result = rentalController.listRentals(1, 20, null, null, null, null, "test-request-id", null, null)
            .block(Duration.ofSeconds(5));

        // Then
        assertEquals(response, result);
    }

}