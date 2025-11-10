package com.example.userservice.infrastructure.adapters.input.rest;

import com.example.userservice.domain.ports.input.LocationUseCase;
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
import com.example.userservice.utils.LoggingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Reactive REST Controller for Location operations.
 * <p>
 * This controller serves as the input adapter in the Clean Architecture,
 * handling HTTP requests reactively and delegating business logic to use cases.
 * It follows REST conventions and provides reactive endpoints for CRUD operations
 * using Spring WebFlux and Project Reactor.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
@Tag(name = "Location", description = "Location management operations")
public class LocationController {

    private static final LoggingUtils logger = LoggingUtils.getLogger(LocationController.class);

    private final LocationUseCase locationUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new Location", description = "Creates a new Location with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Location created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Location already exists")
    })
    public Mono<CreateLocationResponseContent> createLocation(
            @Parameter(description = "Location creation request", required = true)
            @Valid @RequestBody CreateLocationRequestContent request,
            @Parameter(description = "Unique request identifier", required = true)
            @RequestHeader("X-Request-ID") String requestId,
            @Parameter(description = "Correlation identifier for transaction tracking")
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @Parameter(description = "Client service identifier")
            @RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        return Mono.fromRunnable(() -> LoggingUtils.setRequestContext(requestId, correlationId, clientId))
                .then(Mono.fromCallable(() -> {
                    logger.info("Creating location with request: {}", request);
                    return request;
                }))
                .flatMap(locationUseCase::create)
                .doFinally(signal -> LoggingUtils.clearRequestContext());
    }

    @GetMapping("/{locationId}")
    @Operation(summary = "Get Location by ID", description = "Retrieves a Location by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Location found"),
        @ApiResponse(responseCode = "404", description = "Location not found")
    })
    public Mono<GetLocationResponseContent> getLocation(
            @Parameter(description = "Location unique identifier", required = true)
            @PathVariable String locationId,
            @Parameter(description = "Unique request identifier", required = true)
            @RequestHeader("X-Request-ID") String requestId,
            @Parameter(description = "Correlation identifier for transaction tracking")
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @Parameter(description = "Client service identifier")
            @RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        return Mono.fromRunnable(() -> LoggingUtils.setRequestContext(requestId, correlationId, clientId))
                .then(Mono.fromCallable(() -> {
                    logger.info("Getting location with id: {}", locationId);
                    return locationId;
                }))
                .flatMap(locationUseCase::get)
                .doFinally(signal -> LoggingUtils.clearRequestContext());
    }

    @PutMapping("/{locationId}")
    @Operation(summary = "Update Location", description = "Updates an existing Location with new information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Location updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Location not found")
    })
    public Mono<UpdateLocationResponseContent> updateLocation(
            @Parameter(description = "Location unique identifier", required = true)
            @PathVariable String locationId,
            @Parameter(description = "Location update request", required = true)
            @Valid @RequestBody UpdateLocationRequestContent request,
            @Parameter(description = "Unique request identifier", required = true)
            @RequestHeader("X-Request-ID") String requestId,
            @Parameter(description = "Correlation identifier for transaction tracking")
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @Parameter(description = "Client service identifier")
            @RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        return Mono.fromRunnable(() -> LoggingUtils.setRequestContext(requestId, correlationId, clientId))
                .then(Mono.fromCallable(() -> {
                    logger.info("Updating location {} with request: {}", locationId, request);
                    return request;
                }))
                .flatMap(req -> locationUseCase.update(locationId, req))
                .doFinally(signal -> LoggingUtils.clearRequestContext());
    }

    @DeleteMapping("/{locationId}")
    @Operation(summary = "Delete Location", description = "Soft deletes a Location by setting status to INACTIVE")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Location deleted successfully (status set to INACTIVE)"),
        @ApiResponse(responseCode = "404", description = "Location not found")
    })
    public Mono<DeleteLocationResponseContent> deleteLocation(
            @Parameter(description = "Location unique identifier", required = true)
            @PathVariable String locationId,
            @Parameter(description = "Unique request identifier", required = true)
            @RequestHeader("X-Request-ID") String requestId,
            @Parameter(description = "Correlation identifier for transaction tracking")
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @Parameter(description = "Client service identifier")
            @RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        return Mono.fromRunnable(() -> LoggingUtils.setRequestContext(requestId, correlationId, clientId))
                .then(Mono.fromCallable(() -> {
                    logger.info("Deleting location with id: {}", locationId);
                    return locationId;
                }))
                .flatMap(locationUseCase::delete)
                .doFinally(signal -> LoggingUtils.clearRequestContext());
    }

    @GetMapping
    @Operation(summary = "List Locations", description = "Retrieves a paginated list of Locations with optional search, status filter and date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Locations retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid date range or format")
    })
    public Mono<ListLocationsResponseContent> listLocations(
            @Parameter(description = "Page number (1-based)", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Search term for filtering")
            @RequestParam(required = false) String search,
            @Parameter(description = "Location status filter (ACTIVE, INACTIVE, PENDING, SUSPENDED, DELETED). Default: ACTIVE")
            @RequestParam(required = false) String status,
            @Parameter(description = "Start date for filtering by createdAt (ISO format: 2024-01-01T00:00:00Z)")
            @RequestParam(required = false) String dateFrom,
            @Parameter(description = "End date for filtering by createdAt (ISO format: 2024-12-31T23:59:59Z)")
            @RequestParam(required = false) String dateTo,
            @Parameter(description = "Unique request identifier", required = true)
            @RequestHeader("X-Request-ID") String requestId,
            @Parameter(description = "Correlation identifier for transaction tracking")
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @Parameter(description = "Client service identifier")
            @RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        return Mono.fromRunnable(() -> LoggingUtils.setRequestContext(requestId, correlationId, clientId))
                .then(Mono.fromCallable(() -> {
                    // Validate date range
                    if (dateFrom != null && dateTo != null && !dateFrom.trim().isEmpty() && !dateTo.trim().isEmpty()) {
                        try {
                            java.time.Instant fromInstant = java.time.Instant.parse(dateFrom);
                            java.time.Instant toInstant = java.time.Instant.parse(dateTo);
                            if (fromInstant.isAfter(toInstant)) {
                                throw new IllegalArgumentException("dateFrom cannot be after dateTo");
                            }
                        } catch (java.time.format.DateTimeParseException e) {
                            throw new IllegalArgumentException("Invalid date format. Use ISO format: 2024-01-01T00:00:00Z");
                        }
                    }
                    
                    logger.info("Listing locations with page: {}, size: {}, search: {}, status: {}, dateFrom: {}, dateTo: {}", 
                               page, size, search, status, dateFrom, dateTo);
                    return search == null ? "": search;
                }))
                .flatMap(searchTerm -> locationUseCase.list(page, size, searchTerm, status, dateFrom, dateTo))
                .doFinally(signal -> LoggingUtils.clearRequestContext());
    }

    @GetMapping("/cities/{cityId}/neighborhoods")
    @Operation(summary = "GetNeighborhoodsByCity", description = "Complex operation: GetNeighborhoodsByCity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operation completed successfully")
    })
    public Mono<GetNeighborhoodsByCityResponseContent> getNeighborhoodsByCity(
            @Parameter(description = "cityId identifier", required = true)
            @PathVariable String cityId,
            @Parameter(description = "Unique request identifier", required = true)
            @RequestHeader("X-Request-ID") String requestId,
            @Parameter(description = "Correlation identifier for transaction tracking")
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @Parameter(description = "Client service identifier")
            @RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        return Mono.fromRunnable(() -> LoggingUtils.setRequestContext(requestId, correlationId, clientId))
                .then(Mono.fromCallable(() -> {
                    logger.info("Executing GetNeighborhoodsByCity with cityId: {}", cityId);
                    return "GetNeighborhoodsByCity";
                }))
                .flatMap(op -> locationUseCase.getNeighborhoodsByCity(cityId))
                .doFinally(signal -> LoggingUtils.clearRequestContext());
    }

    @GetMapping("/countries/{countryId}/regions")
    @Operation(summary = "GetRegionsByCountry", description = "Complex operation: GetRegionsByCountry")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operation completed successfully")
    })
    public Mono<GetRegionsByCountryResponseContent> getRegionsByCountry(
            @Parameter(description = "countryId identifier", required = true)
            @PathVariable String countryId,
            @Parameter(description = "Unique request identifier", required = true)
            @RequestHeader("X-Request-ID") String requestId,
            @Parameter(description = "Correlation identifier for transaction tracking")
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @Parameter(description = "Client service identifier")
            @RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        return Mono.fromRunnable(() -> LoggingUtils.setRequestContext(requestId, correlationId, clientId))
                .then(Mono.fromCallable(() -> {
                    logger.info("Executing GetRegionsByCountry with countryId: {}", countryId);
                    return "GetRegionsByCountry";
                }))
                .flatMap(op -> locationUseCase.getRegionsByCountry(countryId))
                .doFinally(signal -> LoggingUtils.clearRequestContext());
    }

    @GetMapping("/regions/{regionId}/cities")
    @Operation(summary = "GetCitiesByRegion", description = "Complex operation: GetCitiesByRegion")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operation completed successfully")
    })
    public Mono<GetCitiesByRegionResponseContent> getCitiesByRegion(
            @Parameter(description = "regionId identifier", required = true)
            @PathVariable String regionId,
            @Parameter(description = "Unique request identifier", required = true)
            @RequestHeader("X-Request-ID") String requestId,
            @Parameter(description = "Correlation identifier for transaction tracking")
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @Parameter(description = "Client service identifier")
            @RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        return Mono.fromRunnable(() -> LoggingUtils.setRequestContext(requestId, correlationId, clientId))
                .then(Mono.fromCallable(() -> {
                    logger.info("Executing GetCitiesByRegion with regionId: {}", regionId);
                    return "GetCitiesByRegion";
                }))
                .flatMap(op -> locationUseCase.getCitiesByRegion(regionId))
                .doFinally(signal -> LoggingUtils.clearRequestContext());
    }

}
