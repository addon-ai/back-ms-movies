package com.example.userservice.application.service;

import com.example.userservice.domain.ports.output.LocationRepositoryPort;
import com.example.userservice.application.mapper.LocationMapper;
import com.example.userservice.application.dto.location.CreateLocationRequestContent;
import com.example.userservice.application.dto.location.CreateLocationResponseContent;
import com.example.userservice.application.dto.location.GetLocationResponseContent;
import com.example.userservice.application.dto.location.UpdateLocationRequestContent;
import com.example.userservice.application.dto.location.UpdateLocationResponseContent;
import com.example.userservice.application.dto.location.DeleteLocationResponseContent;
import com.example.userservice.application.dto.location.ListLocationsResponseContent;
import com.example.userservice.domain.model.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mapstruct.factory.Mappers;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Collections;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import com.example.userservice.infrastructure.config.exceptions.NotFoundException;

/**
 * Unit tests for LocationService.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LocationServiceTest {

    @Mock
    private LocationRepositoryPort locationRepositoryPort;

    @Spy
    private LocationMapper locationMapper = Mappers.getMapper(LocationMapper.class);

    @InjectMocks
    private LocationService locationService;

    @Test
    void create_ShouldReturnResponse_WhenValidRequest() {
        // Given
        CreateLocationRequestContent request = CreateLocationRequestContent.builder()
            .build();
        Location domainLocation = Location.builder()
            .build();
        Location savedLocation = Location.builder()
            .build();
        CreateLocationResponseContent expectedResponse = CreateLocationResponseContent.builder()
            .build();

        when(locationMapper.fromCreateRequest(request)).thenReturn(domainLocation);
        when(locationRepositoryPort.save(domainLocation)).thenReturn(Mono.just(savedLocation));
        when(locationMapper.toCreateResponse(savedLocation)).thenReturn(expectedResponse);

        // When
        CreateLocationResponseContent result = locationService.create(request)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(locationRepositoryPort).save(domainLocation);
    }

    @Test
    void create_ShouldThrowException_WhenRepositoryFails() {
        // Given
        CreateLocationRequestContent request = CreateLocationRequestContent.builder()
            .build();
        Location domainLocation = Location.builder()
            .build();
        RuntimeException repositoryException = new RuntimeException("Database error");

        when(locationMapper.fromCreateRequest(request)).thenReturn(domainLocation);
        when(locationRepositoryPort.save(domainLocation)).thenReturn(Mono.error(repositoryException));

        // When & Then
        assertThatThrownBy(() -> locationService.create(request).block(Duration.ofSeconds(5)))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }

    @Test
    void get_ShouldReturnResponse_WhenEntityExists() {
        // Given
        String locationId = "test-id";
        Location domainLocation = Location.builder()
            .build();
        GetLocationResponseContent expectedResponse = GetLocationResponseContent.builder()
            .build();

        when(locationRepositoryPort.findById(locationId)).thenReturn(Mono.just(domainLocation));
        when(locationMapper.toGetResponse(domainLocation)).thenReturn(expectedResponse);

        // When
        GetLocationResponseContent result = locationService.get(locationId)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(locationRepositoryPort).findById(locationId);
    }

    @Test
    void get_ShouldThrowNotFoundException_WhenEntityNotFound() {
        // Given
        String locationId = "non-existent-id";
        when(locationRepositoryPort.findById(locationId)).thenReturn(Mono.empty());

        // When & Then
        assertThatThrownBy(() -> locationService.get(locationId).block(Duration.ofSeconds(5)))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Location not found");
    }

    @Test
    void get_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String locationId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(locationRepositoryPort.findById(locationId)).thenReturn(Mono.error(repositoryException));

        // When & Then
        assertThatThrownBy(() -> locationService.get(locationId).block(Duration.ofSeconds(5)))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }

    @Test
    void update_ShouldReturnResponse_WhenValidRequest() {
        // Given
        String locationId = "test-id";
        UpdateLocationRequestContent request = UpdateLocationRequestContent.builder()
            .build();
        Location existingLocation = Location.builder()
            .build();
        Location updatedLocation = Location.builder()
            .build();
        UpdateLocationResponseContent expectedResponse = UpdateLocationResponseContent.builder()
            .build();

        when(locationRepositoryPort.findById(locationId)).thenReturn(Mono.just(existingLocation));
        when(locationRepositoryPort.save(any(Location.class))).thenReturn(Mono.just(updatedLocation));
        when(locationMapper.toUpdateResponse(any(Location.class))).thenReturn(expectedResponse);

        // When
        UpdateLocationResponseContent result = locationService.update(locationId, request)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(locationRepositoryPort).save(any(Location.class));
    }

    @Test
    void update_ShouldThrowNotFoundException_WhenEntityNotFound() {
        // Given
        String locationId = "non-existent-id";
        UpdateLocationRequestContent request = UpdateLocationRequestContent.builder()
            .build();
        when(locationRepositoryPort.findById(locationId)).thenReturn(Mono.empty());

        // When & Then
        assertThatThrownBy(() -> locationService.update(locationId, request).block(Duration.ofSeconds(5)))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Location not found");
    }

    @Test
    void update_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String locationId = "test-id";
        UpdateLocationRequestContent request = UpdateLocationRequestContent.builder()
            .build();
        Location existingLocation = Location.builder()
            .build();
        RuntimeException repositoryException = new RuntimeException("Database error");

        when(locationRepositoryPort.findById(locationId)).thenReturn(Mono.just(existingLocation));
        when(locationRepositoryPort.save(any(Location.class))).thenReturn(Mono.error(repositoryException));

        // When & Then
        assertThatThrownBy(() -> locationService.update(locationId, request).block(Duration.ofSeconds(5)))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }

    @Test
    void delete_ShouldReturnResponse_WhenEntityExists() {
        // Given
        String locationId = "test-id";
        Location domainLocation = Location.builder()
            .build();
        DeleteLocationResponseContent expectedResponse = DeleteLocationResponseContent.builder()
            .deleted(true)
            .message("Location deleted successfully")
            .build();

        when(locationRepositoryPort.findById(locationId)).thenReturn(Mono.just(domainLocation));
        when(locationRepositoryPort.save(any(Location.class))).thenReturn(Mono.just(domainLocation));

        // When
        DeleteLocationResponseContent result = locationService.delete(locationId)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(locationRepositoryPort).save(any(Location.class));
    }

    @Test
    void delete_ShouldThrowNotFoundException_WhenEntityNotFound() {
        // Given
        String locationId = "non-existent-id";
        when(locationRepositoryPort.findById(locationId)).thenReturn(Mono.empty());

        // When & Then
        assertThatThrownBy(() -> locationService.delete(locationId).block(Duration.ofSeconds(5)))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Location not found");
    }

    @Test
    void delete_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String locationId = "test-id";
        Location domainLocation = Location.builder()
            .build();
        RuntimeException repositoryException = new RuntimeException("Database error");

        when(locationRepositoryPort.findById(locationId)).thenReturn(Mono.just(domainLocation));
        when(locationRepositoryPort.save(any(Location.class))).thenReturn(Mono.error(repositoryException));

        // When & Then
        assertThatThrownBy(() -> locationService.delete(locationId).block(Duration.ofSeconds(5)))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }

    @Test
    void list_ShouldReturnResponse_WhenValidRequest() {
        // Given
        List<Location> locations = Collections.singletonList(Location.builder().build());
        ListLocationsResponseContent expectedResponse = ListLocationsResponseContent.builder().build();
        
        when(locationRepositoryPort.findByFilters(any(), any(), any(), any(), any(), any())).thenReturn(Flux.fromIterable(locations));
        when(locationMapper.toListResponse(locations, 1, 20)).thenReturn(expectedResponse);

        // When
        ListLocationsResponseContent result = locationService.list(1, 20, null, null, null, null)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(locationRepositoryPort).findByFilters(any(), any(), any(), any(), any(), any());
    }

    @Test
    void list_ShouldReturnResponse_WhenSearchTermProvided() {
        // Given
        String searchTerm = "test search";
        Integer page = 1;
        Integer size = 10;
        List<Location> locations = Collections.singletonList(Location.builder().build());
        ListLocationsResponseContent expectedResponse = ListLocationsResponseContent.builder().build();
        
        when(locationRepositoryPort.findByFilters(any(), any(), any(), any(), any(), any())).thenReturn(Flux.fromIterable(locations));
        when(locationMapper.toListResponse(locations, page, size)).thenReturn(expectedResponse);

        // When
        ListLocationsResponseContent result = locationService.list(page, size, searchTerm, null, null, null)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(locationRepositoryPort).findByFilters(any(), any(), any(), any(), any(), any());
    }

    @Test
    void list_ShouldReturnResponse_WhenNullParameters() {
        // Given
        List<Location> locations = Collections.emptyList();
        ListLocationsResponseContent expectedResponse = ListLocationsResponseContent.builder()
            .locations(Collections.emptyList())
            .page(java.math.BigDecimal.valueOf(1))
            .size(java.math.BigDecimal.valueOf(20))
            .total(java.math.BigDecimal.valueOf(0))
            .totalPages(java.math.BigDecimal.valueOf(0))
            .build();
        
        when(locationRepositoryPort.findByFilters(any(), any(), any(), any(), any(), any())).thenReturn(Flux.fromIterable(locations));
        when(locationMapper.toListResponse(locations, 1, 20)).thenReturn(expectedResponse);

        // When
        ListLocationsResponseContent result = locationService.list(null, null, null, null, null, null)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLocations()).isNotNull().isEmpty();
        assertThat(result.getPage()).isEqualTo(java.math.BigDecimal.valueOf(1));
        assertThat(result.getSize()).isEqualTo(java.math.BigDecimal.valueOf(20));
        assertThat(result.getTotal()).isEqualTo(java.math.BigDecimal.valueOf(0));
        assertThat(result.getTotalPages()).isEqualTo(java.math.BigDecimal.valueOf(0));
        verify(locationRepositoryPort).findByFilters(any(), any(), any(), any(), any(), any());
    }

    @Test
    void list_ShouldThrowException_WhenRepositoryFails() {
        // Given
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(locationRepositoryPort.findByFilters(any(), any(), any(), any(), any(), any())).thenReturn(Flux.error(repositoryException));

        // When & Then
        assertThatThrownBy(() -> locationService.list(1, 20, null, null, null, null).block(Duration.ofSeconds(5)))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }
}
