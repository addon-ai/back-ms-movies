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
import org.springframework.http.ResponseEntity;
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
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST Controller for User operations.
 * <p>
 * This controller serves as the input adapter in the Clean Architecture,
 * handling HTTP requests and delegating business logic to use cases.
 * It follows REST conventions and provides endpoints for CRUD operations.
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
    @Operation(summary = "Create a new User", description = "Creates a new User with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "User already exists")
    })
    public ResponseEntity<CreateUserResponseContent> createUser(
            @Parameter(description = "User creation request", required = true)
            @Valid @RequestBody CreateUserRequestContent request,
            @Parameter(description = "Unique request identifier", required = true)
            @RequestHeader("X-Request-ID") String requestId,
            @Parameter(description = "Correlation identifier for transaction tracking")
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @Parameter(description = "Client service identifier")
            @RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        LoggingUtils.setRequestContext(requestId, correlationId, clientId);
        try {
            logger.info("Creating user with request: {}", request);
            CreateUserResponseContent response = userUseCase.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } finally {
            LoggingUtils.clearRequestContext();
        }
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get User by ID", description = "Retrieves a User by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<GetUserResponseContent> getUser(
            @Parameter(description = "User unique identifier", required = true)
            @PathVariable String userId,
            @Parameter(description = "Unique request identifier", required = true)
            @RequestHeader("X-Request-ID") String requestId,
            @Parameter(description = "Correlation identifier for transaction tracking")
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @Parameter(description = "Client service identifier")
            @RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        LoggingUtils.setRequestContext(requestId, correlationId, clientId);
        try {
            logger.info("Getting user with id: {}", userId);
            GetUserResponseContent response = userUseCase.get(userId);
            return ResponseEntity.ok(response);
        } finally {
            LoggingUtils.clearRequestContext();
        }
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update User", description = "Updates an existing User with new information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UpdateUserResponseContent> updateUser(
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
        LoggingUtils.setRequestContext(requestId, correlationId, clientId);
        try {
            logger.info("Updating user {} with request: {}", userId, request);
            UpdateUserResponseContent response = userUseCase.update(userId, request);
            return ResponseEntity.ok(response);
        } finally {
            LoggingUtils.clearRequestContext();
        }
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete User", description = "Soft deletes a User by setting status to INACTIVE")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted successfully (status set to INACTIVE)"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<DeleteUserResponseContent> deleteUser(
            @Parameter(description = "User unique identifier", required = true)
            @PathVariable String userId,
            @Parameter(description = "Unique request identifier", required = true)
            @RequestHeader("X-Request-ID") String requestId,
            @Parameter(description = "Correlation identifier for transaction tracking")
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @Parameter(description = "Client service identifier")
            @RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        LoggingUtils.setRequestContext(requestId, correlationId, clientId);
        try {
            logger.info("Deleting user with id: {}", userId);
            DeleteUserResponseContent response = userUseCase.delete(userId);
            return ResponseEntity.ok(response);
        } finally {
            LoggingUtils.clearRequestContext();
        }
    }

    @GetMapping
    @Operation(summary = "List Users", description = "Retrieves a paginated list of Users with optional search, status filter and date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid date range or format")
    })
    public ResponseEntity<ListUsersResponseContent> listUsers(
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
        LoggingUtils.setRequestContext(requestId, correlationId, clientId);
        try {
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
            ListUsersResponseContent response = userUseCase.list(page, size, search, status, dateFrom, dateTo);
            return ResponseEntity.ok(response);
        } finally {
            LoggingUtils.clearRequestContext();
        }
    }

}