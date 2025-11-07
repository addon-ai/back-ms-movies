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
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

/**
 * Consolidated application service implementing all Location use cases.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class LocationService implements LocationUseCase {

    private static final LoggingUtils logger = LoggingUtils.getLogger(LocationService.class);
    
    private final LocationRepositoryPort locationRepositoryPort;
    private final LocationMapper locationMapper;

    @Override
    public Mono<CreateLocationResponseContent> create(CreateLocationRequestContent request) {
        logger.info("Executing CreateLocation with request: {}", request);
        
        return Mono.fromCallable(() -> locationMapper.fromCreateRequest(request))
                .flatMap(locationRepositoryPort::save)
                .map(savedLocation -> {
                    logger.info("Location created successfully with ID: {}", savedLocation.getLocationId());
                    return locationMapper.toCreateResponse(savedLocation);
                })
                .doOnError(e -> logger.error("Error in CreateLocation", e, request));
    }

    @Override
    public Mono<GetLocationResponseContent> get(String locationId) {
        logger.info("Executing GetLocation with locationId: {}", locationId);
        
        return locationRepositoryPort.findById(locationId)
                .switchIfEmpty(Mono.error(new NotFoundException("Location not found")))
                .map(location -> {
                    logger.info("Location retrieved successfully with ID: {}", locationId);
                    return locationMapper.toGetResponse(location);
                })
                .doOnError(e -> logger.error("Error in GetLocation", e, locationId));
    }

    @Override
    public Mono<UpdateLocationResponseContent> update(String locationId, UpdateLocationRequestContent request) {
        logger.info("Executing UpdateLocation with locationId: {} and request: {}", locationId, request);
        
        return locationRepositoryPort.findById(locationId)
                .switchIfEmpty(Mono.error(new NotFoundException("Location not found")))
                .map(existingLocation -> {
                    locationMapper.updateEntityFromRequest(request, existingLocation);
                    existingLocation.setUpdatedAt(java.time.Instant.now().toString());
                    return existingLocation;
                })
                .flatMap(locationRepositoryPort::save)
                .map(savedLocation -> {
                    logger.info("Location updated successfully with ID: {}", locationId);
                    return locationMapper.toUpdateResponse(savedLocation);
                })
                .doOnError(e -> logger.error("Error in UpdateLocation", e, locationId));
    }

    @Override
    public Mono<DeleteLocationResponseContent> delete(String locationId) {
        logger.info("Executing DeleteLocation with locationId: {}", locationId);
        
        return locationRepositoryPort.findById(locationId)
                .switchIfEmpty(Mono.error(new NotFoundException("Location not found")))
                .flatMap(location -> locationRepositoryPort.deleteById(locationId))
                .then(Mono.fromCallable(() -> {
                    logger.info("Location deleted successfully with ID: {}", locationId);
                    return DeleteLocationResponseContent.builder()
                            .deleted(true)
                            .message("Location deleted successfully")
                            .build();
                }))
                .doOnError(e -> logger.error("Error in DeleteLocation", e, locationId));
    }

    @Override
    public Mono<ListLocationsResponseContent> list(Integer page, Integer size, String search) {
        logger.info("Executing ListLocations with page: {}, size: {}, search: {}", page, size, search);
        
        Flux<Location> locationFlux;
        if (search != null && !search.trim().isEmpty()) {
            locationFlux = locationRepositoryPort.findBySearchTerm(search, page, size);
        } else {
            locationFlux = locationRepositoryPort.findAll();
        }
        
        return locationFlux
                .collectList()
                .map(locations -> {
                    logger.info("Retrieved {} locations successfully", locations.size());
                    int pageNum = page != null ? page : 1;
                    int pageSize = size != null ? size : 20;
                    return locationMapper.toListResponse(locations, pageNum, pageSize);
                })
                .doOnError(e -> logger.error("Error in ListLocations", e));
    }

    @Override
    public Mono<GetNeighborhoodsByCityResponseContent> getNeighborhoodsByCity() {
        logger.info("Executing GetNeighborhoodsByCity");
        
        return locationRepositoryPort.findNeighborhoodsByCity("defaultCityId")
                .collectList()
                .map(locations -> {
                    logger.info("Retrieved {} locations successfully", locations.size());
                    return GetNeighborhoodsByCityResponseContent.builder().build();
                })
                .doOnError(e -> logger.error("Error in GetNeighborhoodsByCity", e));
    }

    @Override
    public Mono<GetRegionsByCountryResponseContent> getRegionsByCountry() {
        logger.info("Executing GetRegionsByCountry");
        
        return locationRepositoryPort.findRegionsByCountry("defaultCountryId")
                .collectList()
                .map(locations -> {
                    logger.info("Retrieved {} locations successfully", locations.size());
                    return GetRegionsByCountryResponseContent.builder().build();
                })
                .doOnError(e -> logger.error("Error in GetRegionsByCountry", e));
    }

    @Override
    public Mono<GetCitiesByRegionResponseContent> getCitiesByRegion() {
        logger.info("Executing GetCitiesByRegion");
        
        return locationRepositoryPort.findCitiesByRegion("defaultRegionId")
                .collectList()
                .map(locations -> {
                    logger.info("Retrieved {} locations successfully", locations.size());
                    return GetCitiesByRegionResponseContent.builder().build();
                })
                .doOnError(e -> logger.error("Error in GetCitiesByRegion", e));
    }

}