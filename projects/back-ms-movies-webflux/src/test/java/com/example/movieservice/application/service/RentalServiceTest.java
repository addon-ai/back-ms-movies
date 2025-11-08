package com.example.movieservice.application.service;

import com.example.movieservice.domain.ports.output.RentalRepositoryPort;
import com.example.movieservice.application.mapper.RentalMapper;
import com.example.movieservice.application.dto.movie.CreateRentalRequestContent;
import com.example.movieservice.application.dto.movie.CreateRentalResponseContent;
import com.example.movieservice.application.dto.movie.GetRentalResponseContent;
import com.example.movieservice.application.dto.movie.UpdateRentalRequestContent;
import com.example.movieservice.application.dto.movie.UpdateRentalResponseContent;
import com.example.movieservice.application.dto.movie.ListRentalsResponseContent;
import com.example.movieservice.domain.model.Rental;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mapstruct.factory.Mappers;

import java.util.Optional;
import java.util.List;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;

import com.example.movieservice.infrastructure.config.exceptions.NotFoundException;

/**
 * Unit tests for RentalService.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RentalServiceTest {

    @Mock
    private RentalRepositoryPort rentalRepositoryPort;

    @Spy
    private RentalMapper rentalMapper = Mappers.getMapper(RentalMapper.class);

    @InjectMocks
    private RentalService rentalService;

    @Test
    void create_ShouldReturnResponse_WhenValidRequest() {
        // Given
        CreateRentalRequestContent request = CreateRentalRequestContent.builder()
            .build();
        Rental domainRental = Rental.builder()
            .build();
        Rental savedRental = Rental.builder()
            .build();
        CreateRentalResponseContent expectedResponse = CreateRentalResponseContent.builder()
            .build();

        when(rentalMapper.fromCreateRequest(request)).thenReturn(domainRental);
        when(rentalRepositoryPort.save(domainRental)).thenReturn(savedRental);
        when(rentalMapper.toCreateResponse(savedRental)).thenReturn(expectedResponse);

        // When
        CreateRentalResponseContent result = rentalService.create(request);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(rentalRepositoryPort).save(domainRental);
    }

    @Test
    void create_ShouldThrowException_WhenRepositoryFails() {
        // Given
        CreateRentalRequestContent request = CreateRentalRequestContent.builder()
            .build();
        Rental domainRental = Rental.builder()
            .build();
        RuntimeException repositoryException = new RuntimeException("Database error");

        when(rentalMapper.fromCreateRequest(request)).thenReturn(domainRental);
        when(rentalRepositoryPort.save(domainRental)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> rentalService.create(request))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }

    @Test
    void get_ShouldReturnResponse_WhenEntityExists() {
        // Given
        String rentalId = "test-id";
        Rental domainRental = Rental.builder()
            .build();
        GetRentalResponseContent expectedResponse = GetRentalResponseContent.builder()
            .build();

        when(rentalRepositoryPort.findById(rentalId)).thenReturn(Optional.of(domainRental));
        when(rentalMapper.toGetResponse(domainRental)).thenReturn(expectedResponse);

        // When
        GetRentalResponseContent result = rentalService.get(rentalId);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(rentalRepositoryPort).findById(rentalId);
    }

    @Test
    void get_ShouldThrowNotFoundException_WhenEntityNotFound() {
        // Given
        String rentalId = "non-existent-id";
        when(rentalRepositoryPort.findById(rentalId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> rentalService.get(rentalId))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Rental not found");
    }

    @Test
    void get_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String rentalId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(rentalRepositoryPort.findById(rentalId)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> rentalService.get(rentalId))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }

    @Test
    void update_ShouldReturnResponse_WhenValidRequest() {
        // Given
        String rentalId = "test-id";
        UpdateRentalRequestContent request = UpdateRentalRequestContent.builder()
            .build();
        Rental existingRental = Rental.builder()
            .build();
        Rental updatedRental = Rental.builder()
            .build();
        UpdateRentalResponseContent expectedResponse = UpdateRentalResponseContent.builder()
            .build();

        when(rentalRepositoryPort.findById(rentalId)).thenReturn(Optional.of(existingRental));
        // Use void method for updating existing entity
        // The updateEntityFromRequest method modifies existingRental in place
        when(rentalRepositoryPort.save(any(Rental.class))).thenReturn(updatedRental);
        when(rentalMapper.toUpdateResponse(any(Rental.class))).thenReturn(expectedResponse);

        // When
        UpdateRentalResponseContent result = rentalService.update(rentalId, request);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(rentalRepositoryPort).save(any(Rental.class));
    }

    @Test
    void update_ShouldThrowNotFoundException_WhenEntityNotFound() {
        // Given
        String rentalId = "non-existent-id";
        UpdateRentalRequestContent request = UpdateRentalRequestContent.builder()
            .build();
        when(rentalRepositoryPort.findById(rentalId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> rentalService.update(rentalId, request))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Rental not found");
    }

    @Test
    void update_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String rentalId = "test-id";
        UpdateRentalRequestContent request = UpdateRentalRequestContent.builder()
            .build();
        Rental existingRental = Rental.builder()
            .build();
        RuntimeException repositoryException = new RuntimeException("Database error");

        when(rentalRepositoryPort.findById(rentalId)).thenReturn(Optional.of(existingRental));
        when(rentalRepositoryPort.save(any(Rental.class))).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> rentalService.update(rentalId, request))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }


    @Test
    void list_ShouldReturnResponse_WhenValidRequest() {
        // Given
        List<Rental> rentals = Collections.singletonList(Rental.builder().build());
        ListRentalsResponseContent expectedResponse = ListRentalsResponseContent.builder().build();
        
        when(rentalRepositoryPort.findAll()).thenReturn(rentals);
        when(rentalMapper.toListResponse(rentals, 1, 20)).thenReturn(expectedResponse);

        // When
        ListRentalsResponseContent result = rentalService.list(1, 20, null);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(rentalRepositoryPort).findAll();
    }

    @Test
    void list_ShouldReturnResponse_WhenSearchTermProvided() {
        // Given
        String searchTerm = "test search";
        Integer page = 1;
        Integer size = 10;
        List<Rental> rentals = Collections.singletonList(Rental.builder().build());
        ListRentalsResponseContent expectedResponse = ListRentalsResponseContent.builder().build();
        
        when(rentalRepositoryPort.findBySearchTerm(searchTerm, page, size)).thenReturn(rentals);
        when(rentalMapper.toListResponse(rentals, page, size)).thenReturn(expectedResponse);

        // When
        ListRentalsResponseContent result = rentalService.list(page, size, searchTerm);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(rentalRepositoryPort).findBySearchTerm(searchTerm, page, size);
    }

    @Test
    void list_ShouldReturnResponse_WhenNullParameters() {
        // Given
        List<Rental> rentals = Collections.emptyList();
        ListRentalsResponseContent expectedResponse = ListRentalsResponseContent.builder()
            .rentals(Collections.emptyList())
            .page(java.math.BigDecimal.valueOf(1))
            .size(java.math.BigDecimal.valueOf(20))
            .total(java.math.BigDecimal.valueOf(0))
            .totalPages(java.math.BigDecimal.valueOf(0))
            .build();
        
        when(rentalRepositoryPort.findAll()).thenReturn(rentals);
        when(rentalMapper.toListResponse(rentals, 1, 20)).thenReturn(expectedResponse);

        // When
        ListRentalsResponseContent result = rentalService.list(null, null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRentals()).isNotNull().isEmpty();
        assertThat(result.getPage()).isEqualTo(java.math.BigDecimal.valueOf(1));
        assertThat(result.getSize()).isEqualTo(java.math.BigDecimal.valueOf(20));
        assertThat(result.getTotal()).isEqualTo(java.math.BigDecimal.valueOf(0));
        assertThat(result.getTotalPages()).isEqualTo(java.math.BigDecimal.valueOf(0));
        verify(rentalRepositoryPort).findAll();
    }

    @Test
    void list_ShouldThrowException_WhenRepositoryFails() {
        // Given
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(rentalRepositoryPort.findAll()).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> rentalService.list(1, 20, null))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }
}