package com.example.movieservice.infrastructure.adapters.input.rest;

import com.example.movieservice.domain.ports.input.RentalUseCase;
import com.example.movieservice.application.dto.movie.CreateRentalRequestContent;
import com.example.movieservice.application.dto.movie.CreateRentalResponseContent;
import com.example.movieservice.application.dto.movie.GetRentalResponseContent;
import com.example.movieservice.application.dto.movie.UpdateRentalRequestContent;
import com.example.movieservice.application.dto.movie.UpdateRentalResponseContent;
import com.example.movieservice.application.dto.movie.ListRentalsResponseContent;
import com.example.movieservice.utils.LoggingUtils;
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
 * Reactive REST Controller for Rental operations.
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
@RequestMapping("/rentals")
@RequiredArgsConstructor
@Tag(name = "Rental", description = "Rental management operations")
public class RentalController {

    private static final LoggingUtils logger = LoggingUtils.getLogger(RentalController.class);

    private final RentalUseCase rentalUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new Rental", description = "Creates a new Rental with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Rental created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Rental already exists")
    })
    public Mono<CreateRentalResponseContent> createRental(
            @Parameter(description = "Rental creation request", required = true)
            @Valid @RequestBody CreateRentalRequestContent request,
            @Parameter(description = "Unique request identifier", required = true)
            @RequestHeader("X-Request-ID") String requestId,
            @Parameter(description = "Correlation identifier for transaction tracking")
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @Parameter(description = "Client service identifier")
            @RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        return Mono.fromRunnable(() -> LoggingUtils.setRequestContext(requestId, correlationId, clientId))
                .then(Mono.fromCallable(() -> {
                    logger.info("Creating rental with request: {}", request);
                    return request;
                }))
                .flatMap(rentalUseCase::create)
                .doFinally(signal -> LoggingUtils.clearRequestContext());
    }

    @GetMapping("/{rentalId}")
    @Operation(summary = "Get Rental by ID", description = "Retrieves a Rental by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rental found"),
        @ApiResponse(responseCode = "404", description = "Rental not found")
    })
    public Mono<GetRentalResponseContent> getRental(
            @Parameter(description = "Rental unique identifier", required = true)
            @PathVariable String rentalId,
            @Parameter(description = "Unique request identifier", required = true)
            @RequestHeader("X-Request-ID") String requestId,
            @Parameter(description = "Correlation identifier for transaction tracking")
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @Parameter(description = "Client service identifier")
            @RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        return Mono.fromRunnable(() -> LoggingUtils.setRequestContext(requestId, correlationId, clientId))
                .then(Mono.fromCallable(() -> {
                    logger.info("Getting rental with id: {}", rentalId);
                    return rentalId;
                }))
                .flatMap(rentalUseCase::get)
                .doFinally(signal -> LoggingUtils.clearRequestContext());
    }

    @PutMapping("/{rentalId}")
    @Operation(summary = "Update Rental", description = "Updates an existing Rental with new information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rental updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Rental not found")
    })
    public Mono<UpdateRentalResponseContent> updateRental(
            @Parameter(description = "Rental unique identifier", required = true)
            @PathVariable String rentalId,
            @Parameter(description = "Rental update request", required = true)
            @Valid @RequestBody UpdateRentalRequestContent request,
            @Parameter(description = "Unique request identifier", required = true)
            @RequestHeader("X-Request-ID") String requestId,
            @Parameter(description = "Correlation identifier for transaction tracking")
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @Parameter(description = "Client service identifier")
            @RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        return Mono.fromRunnable(() -> LoggingUtils.setRequestContext(requestId, correlationId, clientId))
                .then(Mono.fromCallable(() -> {
                    logger.info("Updating rental {} with request: {}", rentalId, request);
                    return request;
                }))
                .flatMap(req -> rentalUseCase.update(rentalId, req))
                .doFinally(signal -> LoggingUtils.clearRequestContext());
    }


    @GetMapping
    @Operation(summary = "List Rentals", description = "Retrieves a paginated list of Rentals with optional search")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rentals retrieved successfully")
    })
    public Mono<ListRentalsResponseContent> listRentals(
            @Parameter(description = "Page number (1-based)", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Search term for filtering")
            @RequestParam(required = false) String search,
            @Parameter(description = "Unique request identifier", required = true)
            @RequestHeader("X-Request-ID") String requestId,
            @Parameter(description = "Correlation identifier for transaction tracking")
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @Parameter(description = "Client service identifier")
            @RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        return Mono.fromRunnable(() -> LoggingUtils.setRequestContext(requestId, correlationId, clientId))
                .then(Mono.fromCallable(() -> {
                    logger.info("Listing rentals with page: {}, size: {}, search: {}", page, size, search);
                    return search;
                }))
                .flatMap(searchTerm -> rentalUseCase.list(page, size, searchTerm))
                .doFinally(signal -> LoggingUtils.clearRequestContext());
    }

}
