package com.example.userservice.application.service;

import com.example.userservice.domain.ports.input.LocationUseCase;
import com.example.userservice.domain.ports.output.LocationRepositoryPort;
import com.example.userservice.application.dto.location.CreateLocationRequestContent;
import com.example.userservice.application.dto.location.CreateLocationResponseContent;
import com.example.userservice.application.dto.location.GetLocationResponseContent;
import com.example.userservice.application.dto.location.UpdateLocationRequestContent;
import com.example.userservice.application.dto.location.UpdateLocationResponseContent;
import com.example.userservice.application.dto.location.DeleteLocationResponseContent;
import com.example.userservice.application.dto.location.ListLocationsResponseContent;
import com.example.userservice.application.dto.location.GetNeighborhoodsByCityResponseContent;
import com.example.userservice.application.dto.location.GetRegionsByCountryResponseContent;
import com.example.userservice.application.dto.location.GetCitiesByRegionResponseContent;
import com.example.userservice.domain.model.Location;
import com.example.userservice.application.mapper.LocationMapper;
import com.example.userservice.infrastructure.config.exceptions.NotFoundException;
import com.example.userservice.utils.LoggingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Consolidated application service implementing all Location use cases.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LocationService implements LocationUseCase {

    private static final LoggingUtils logger = LoggingUtils.getLogger(LocationService.class);
    
    private final LocationRepositoryPort locationRepositoryPort;
    private final LocationMapper locationMapper;

    @Override
    public CreateLocationResponseContent create(CreateLocationRequestContent request) {
        logger.info("Executing CreateLocation with request: {}", request);
        
        try {
            Location location = locationMapper.fromCreateRequest(request);
            Location savedLocation = locationRepositoryPort.save(location);
            logger.info("Location created successfully with ID: {}", savedLocation.getLocationId());
            return locationMapper.toCreateResponse(savedLocation);
        } catch (Exception e) {
            logger.error("Error in CreateLocation", e, request);
            throw e;
        }
    }

    @Override
    public GetLocationResponseContent get(String locationId) {
        logger.info("Executing GetLocation with locationId: {}", locationId);
        
        try {
            Location location = locationRepositoryPort.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Location not found"));
            
            logger.info("Location retrieved successfully with ID: {}", locationId);
            return locationMapper.toGetResponse(location);
        } catch (NotFoundException e) {
            logger.error("Location not found in GetLocation", e, locationId);
            throw e;
        } catch (Exception e) {
            logger.error("Error in GetLocation", e, locationId);
            throw e;
        }
    }

    @Override
    public UpdateLocationResponseContent update(String locationId, UpdateLocationRequestContent request) {
        logger.info("Executing UpdateLocation with locationId: {} and request: {}", locationId, request);
        
        try {
            Location existingLocation = locationRepositoryPort.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Location not found"));
            
            // Merge request data into existing entity
            locationMapper.updateEntityFromRequest(request, existingLocation);
            existingLocation.setUpdatedAt(java.time.Instant.now().toString());
            
            Location savedLocation = locationRepositoryPort.save(existingLocation);
            logger.info("Location updated successfully with ID: {}", locationId);
            
            return locationMapper.toUpdateResponse(savedLocation);
        } catch (NotFoundException e) {
            logger.error("Location not found in UpdateLocation", e, locationId);
            throw e;
        } catch (Exception e) {
            logger.error("Error in UpdateLocation", e, locationId);
            throw e;
        }
    }

    @Override
    public DeleteLocationResponseContent delete(String locationId) {
        logger.info("Executing DeleteLocation with locationId: {}", locationId);
        
        try {
            Location location = locationRepositoryPort.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Location not found"));
            
            locationRepositoryPort.deleteById(locationId);
            logger.info("Location deleted successfully with ID: {}", locationId);
            
            return DeleteLocationResponseContent.builder()
                .deleted(true)
                .message("Location deleted successfully")
                .build();
        } catch (NotFoundException e) {
            logger.error("Location not found in DeleteLocation", e, locationId);
            throw e;
        } catch (Exception e) {
            logger.error("Error in DeleteLocation", e, locationId);
            throw e;
        }
    }

    @Override
    public ListLocationsResponseContent list(Integer page, Integer size, String search) {
        logger.info("Executing ListLocations with page: {}, size: {}, search: {}", page, size, search);
        
        try {
            List<Location> locations;
            if (search != null && !search.trim().isEmpty()) {
                locations = locationRepositoryPort.findBySearchTerm(search, page, size);
            } else {
                locations = locationRepositoryPort.findAll();
            }
            logger.info("Retrieved {} locations successfully", locations.size());
            return locationMapper.toListResponse(locations, page != null ? page : 1, size != null ? size : 20);
        } catch (Exception e) {
            logger.error("Error in ListLocations", e);
            throw e;
        }
    }

    @Override
    public GetNeighborhoodsByCityResponseContent getNeighborhoodsByCity() {
        logger.info("Executing GetNeighborhoodsByCity");
        
        try {
            // TODO: Implement GetNeighborhoodsByCity business logic
            logger.info("GetNeighborhoodsByCity executed successfully");
            throw new UnsupportedOperationException("GetNeighborhoodsByCity not yet implemented");
        } catch (Exception e) {
            logger.error("Error in GetNeighborhoodsByCity", e);
            throw e;
        }
    }

    @Override
    public GetRegionsByCountryResponseContent getRegionsByCountry() {
        logger.info("Executing GetRegionsByCountry");
        
        try {
            // TODO: Implement GetRegionsByCountry business logic
            logger.info("GetRegionsByCountry executed successfully");
            throw new UnsupportedOperationException("GetRegionsByCountry not yet implemented");
        } catch (Exception e) {
            logger.error("Error in GetRegionsByCountry", e);
            throw e;
        }
    }

    @Override
    public GetCitiesByRegionResponseContent getCitiesByRegion() {
        logger.info("Executing GetCitiesByRegion");
        
        try {
            // TODO: Implement GetCitiesByRegion business logic
            logger.info("GetCitiesByRegion executed successfully");
            throw new UnsupportedOperationException("GetCitiesByRegion not yet implemented");
        } catch (Exception e) {
            logger.error("Error in GetCitiesByRegion", e);
            throw e;
        }
    }

}