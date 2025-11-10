package com.example.userservice.infrastructure.adapters.input.rest;

import com.example.userservice.domain.ports.input.UserUseCase;
import com.example.userservice.application.dto.user.CreateUserRequestContent;
import com.example.userservice.application.dto.user.CreateUserResponseContent;
import com.example.userservice.application.dto.user.GetUserResponseContent;
import com.example.userservice.application.dto.user.UpdateUserRequestContent;
import com.example.userservice.application.dto.user.UpdateUserResponseContent;
import com.example.userservice.application.dto.user.DeleteUserResponseContent;
import com.example.userservice.application.dto.user.ListUsersResponseContent;
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
 * Reactive REST Controller for User operations.
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
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User management operations")
public class UserController {

    private static final LoggingUtils logger = LoggingUtils.getLogger(UserController.class);

    private final UserUseCase userUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new User", description = "Creates a new User with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "User already exists")
    })
    public Mono<CreateUserResponseContent> createUser(
            @Parameter(description = "User creation request", required = true)
            @Valid @RequestBody CreateUserRequestContent request,
            @Parameter(description = "Unique request identifier", required = true)
            @RequestHeader("X-Request-ID") String requestId,
            @Parameter(description = "Correlation identifier for transaction tracking")
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @Parameter(description = "Client service identifier")
            @RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        return Mono.fromRunnable(() -> LoggingUtils.setRequestContext(requestId, correlationId, clientId))
                .then(Mono.fromCallable(() -> {
                    logger.info("Creating user with request: {}", request);
                    return request;
                }))
                .flatMap(userUseCase::create)
                .doFinally(signal -> LoggingUtils.clearRequestContext());
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get User by ID", description = "Retrieves a User by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public Mono<GetUserResponseContent> getUser(
            @Parameter(description = "User unique identifier", required = true)
            @PathVariable String userId,
            @Parameter(description = "Unique request identifier", required = true)
            @RequestHeader("X-Request-ID") String requestId,
            @Parameter(description = "Correlation identifier for transaction tracking")
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @Parameter(description = "Client service identifier")
            @RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        return Mono.fromRunnable(() -> LoggingUtils.setRequestContext(requestId, correlationId, clientId))
                .then(Mono.fromCallable(() -> {
                    logger.info("Getting user with id: {}", userId);
                    return userId;
                }))
                .flatMap(userUseCase::get)
                .doFinally(signal -> LoggingUtils.clearRequestContext());
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update User", description = "Updates an existing User with new information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public Mono<UpdateUserResponseContent> updateUser(
            @Parameter(description = "User unique identifier", required = true)
            @PathVariable String userId,
            @Parameter(description = "User update request", required = true)
            @Valid @RequestBody UpdateUserRequestContent request,
            @Parameter(description = "Unique request identifier", required = true)
            @RequestHeader("X-Request-ID") String requestId,
            @Parameter(description = "Correlation identifier for transaction tracking")
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @Parameter(description = "Client service identifier")
            @RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        return Mono.fromRunnable(() -> LoggingUtils.setRequestContext(requestId, correlationId, clientId))
                .then(Mono.fromCallable(() -> {
                    logger.info("Updating user {} with request: {}", userId, request);
                    return request;
                }))
                .flatMap(req -> userUseCase.update(userId, req))
                .doFinally(signal -> LoggingUtils.clearRequestContext());
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete User", description = "Soft deletes a User by setting status to INACTIVE")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted successfully (status set to INACTIVE)"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public Mono<DeleteUserResponseContent> deleteUser(
            @Parameter(description = "User unique identifier", required = true)
            @PathVariable String userId,
            @Parameter(description = "Unique request identifier", required = true)
            @RequestHeader("X-Request-ID") String requestId,
            @Parameter(description = "Correlation identifier for transaction tracking")
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @Parameter(description = "Client service identifier")
            @RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        return Mono.fromRunnable(() -> LoggingUtils.setRequestContext(requestId, correlationId, clientId))
                .then(Mono.fromCallable(() -> {
                    logger.info("Deleting user with id: {}", userId);
                    return userId;
                }))
                .flatMap(userUseCase::delete)
                .doFinally(signal -> LoggingUtils.clearRequestContext());
    }

    @GetMapping
    @Operation(summary = "List Users", description = "Retrieves a paginated list of Users with optional search, status filter and date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid date range or format")
    })
    public Mono<ListUsersResponseContent> listUsers(
            @Parameter(description = "Page number (1-based)", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Search term for filtering")
            @RequestParam(required = false) String search,
            @Parameter(description = "User status filter (ACTIVE, INACTIVE, PENDING, SUSPENDED, DELETED). Default: ACTIVE")
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
                    
                    logger.info("Listing users with page: {}, size: {}, search: {}, status: {}, dateFrom: {}, dateTo: {}", 
                               page, size, search, status, dateFrom, dateTo);
                    return search == null ? "": search;
                }))
                .flatMap(searchTerm -> userUseCase.list(page, size, searchTerm, status, dateFrom, dateTo))
                .doFinally(signal -> LoggingUtils.clearRequestContext());
    }

}
