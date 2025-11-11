package com.example.userservice.infrastructure.adapters.input.rest;

import com.example.userservice.domain.ports.input.LocationUseCase;
import com.example.userservice.application.dto.location.CreateLocationRequestContent;
import com.example.userservice.application.dto.location.CreateLocationResponseContent;
import com.example.userservice.application.dto.location.GetLocationResponseContent;
import com.example.userservice.application.dto.location.UpdateLocationRequestContent;
import com.example.userservice.application.dto.location.UpdateLocationResponseContent;
import com.example.userservice.application.dto.location.DeleteLocationResponseContent;
import com.example.userservice.application.dto.location.ListLocationsResponseContent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for LocationController.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class LocationControllerTest {

    @Mock
    private LocationUseCase locationUseCase;

    @InjectMocks
    private LocationController locationController;

    @Test
    void createLocation_ShouldReturnCreated_WhenValidRequest() {
        // Given
        CreateLocationRequestContent request = CreateLocationRequestContent.builder()
            .userId("test-userId")
            .countryId("test-countryId")
            .regionId("test-regionId")
            .cityId("test-cityId")
            .address("test-address")
            .locationType("test-locationType")
            .build();
        CreateLocationResponseContent response = CreateLocationResponseContent.builder()
            .build();
        
        when(locationUseCase.create(any(CreateLocationRequestContent.class)))
            .thenReturn(response);

        // When
        ResponseEntity<CreateLocationResponseContent> result = locationController.createLocation(request, "test-request-id", null, null);

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void getLocation_ShouldReturnOk_WhenEntityExists() {
        // Given
        String locationId = "test-id";
        GetLocationResponseContent response = GetLocationResponseContent.builder()
            .build();
        
        when(locationUseCase.get(anyString()))
            .thenReturn(response);

        // When
        ResponseEntity<GetLocationResponseContent> result = locationController.getLocation(locationId, "test-request-id", null, null);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void updateLocation_ShouldReturnOk_WhenValidRequest() {
        // Given
        String locationId = "test-id";
        UpdateLocationRequestContent request = UpdateLocationRequestContent.builder()
            .countryId("updated-countryId")
            .regionId("updated-regionId")
            .cityId("updated-cityId")
            .neighborhoodId("updated-neighborhoodId")
            .address("updated-address")
            .postalCode("updated-postalCode")
            .locationType("updated-locationType")
            .build();
        UpdateLocationResponseContent response = UpdateLocationResponseContent.builder()
            .build();
        
        when(locationUseCase.update(anyString(), any(UpdateLocationRequestContent.class)))
            .thenReturn(response);

        // When
        ResponseEntity<UpdateLocationResponseContent> result = locationController.updateLocation(locationId, request, "test-request-id", null, null);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void deleteLocation_ShouldReturnOk_WhenEntityExists() {
        // Given
        String locationId = "test-id";
        DeleteLocationResponseContent response = DeleteLocationResponseContent.builder()
            .deleted(true)
            .message("Location deleted successfully")
            .build();
        
        when(locationUseCase.delete(anyString()))
            .thenReturn(response);

        // When
        ResponseEntity<DeleteLocationResponseContent> result = locationController.deleteLocation(locationId, "test-request-id", null, null);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void listLocations_ShouldReturnOk() {
        // Given
        ListLocationsResponseContent response = ListLocationsResponseContent.builder()
            .build();
        
        when(locationUseCase.list(any(), any(), any()))
            .thenReturn(response);

        // When
        ResponseEntity<ListLocationsResponseContent> result = locationController.listLocations(1, 20, null, "test-request-id", null, null);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

}