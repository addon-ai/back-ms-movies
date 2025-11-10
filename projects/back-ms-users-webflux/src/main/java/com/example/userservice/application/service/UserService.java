package com.example.userservice.application.service;

import com.example.userservice.domain.ports.input.UserUseCase;
import com.example.userservice.domain.ports.output.UserRepositoryPort;
import com.example.userservice.application.dto.user.CreateUserRequestContent;
import com.example.userservice.application.dto.user.CreateUserResponseContent;
import com.example.userservice.application.dto.user.GetUserResponseContent;
import com.example.userservice.application.dto.user.UpdateUserRequestContent;
import com.example.userservice.application.dto.user.UpdateUserResponseContent;
import com.example.userservice.application.dto.user.DeleteUserResponseContent;
import com.example.userservice.application.dto.user.ListUsersResponseContent;
import com.example.userservice.domain.model.User;
import com.example.userservice.application.mapper.UserMapper;
import com.example.userservice.infrastructure.config.exceptions.NotFoundException;
import com.example.userservice.utils.LoggingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

/**
 * Consolidated application service implementing all User use cases.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private static final LoggingUtils logger = LoggingUtils.getLogger(UserService.class);
    
    private final UserRepositoryPort userRepositoryPort;
    private final UserMapper userMapper;

    @Override
    public Mono<CreateUserResponseContent> create(CreateUserRequestContent request) {
        logger.info("Executing CreateUser with request: {}", request);
        
        return Mono.fromCallable(() -> userMapper.fromCreateRequest(request))
                .flatMap(userRepositoryPort::save)
                .map(savedUser -> {
                    logger.info("User created successfully with ID: {}", savedUser.getUserId());
                    return userMapper.toCreateResponse(savedUser);
                })
                .doOnError(e -> logger.error("Error in CreateUser", e, request));
    }

    @Override
    public Mono<GetUserResponseContent> get(String userId) {
        logger.info("Executing GetUser with userId: {}", userId);
        
        return userRepositoryPort.findById(userId)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                .map(user -> {
                    logger.info("User retrieved successfully with ID: {}", userId);
                    return userMapper.toGetResponse(user);
                })
                .doOnError(e -> logger.error("Error in GetUser", e, userId));
    }

    @Override
    public Mono<UpdateUserResponseContent> update(String userId, UpdateUserRequestContent request) {
        logger.info("Executing UpdateUser with userId: {} and request: {}", userId, request);
        
        return userRepositoryPort.findById(userId)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                .map(existingUser -> {
                    userMapper.updateEntityFromRequest(request, existingUser);
                    existingUser.setUpdatedAt(java.time.Instant.now().toString());
                    return existingUser;
                })
                .flatMap(userRepositoryPort::save)
                .map(savedUser -> {
                    logger.info("User updated successfully with ID: {}", userId);
                    return userMapper.toUpdateResponse(savedUser);
                })
                .doOnError(e -> logger.error("Error in UpdateUser", e, userId));
    }

    @Override
    public Mono<DeleteUserResponseContent> delete(String userId) {
        logger.info("Executing DeleteUser with userId: {}", userId);
        
        return userRepositoryPort.findById(userId)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                .map(user -> {
                    // Soft delete: update status to INACTIVE and set updatedAt
                    user.setStatus("INACTIVE");
                    user.setUpdatedAt(java.time.Instant.now().toString());
                    return user;
                })
                .flatMap(userRepositoryPort::save)
                .map(updatedUser -> {
                    logger.info("User soft deleted (status set to INACTIVE) with ID: {}", userId);
                    return DeleteUserResponseContent.builder()
                            .deleted(true)
                            .message("User deleted successfully")
                            .build();
                })
                .doOnError(e -> logger.error("Error in DeleteUser", e, userId));
    }

    @Override
    public Mono<ListUsersResponseContent> list(Integer page, Integer size, String search, String status, String dateFrom, String dateTo) {
        // Apply default values
        String effectiveStatus = (status == null || status.trim().isEmpty()) ? "ACTIVE" : status;
        String effectiveDateFrom = (dateFrom == null || dateFrom.trim().isEmpty()) ? 
            java.time.Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS).toString() : dateFrom;
        String effectiveDateTo = (dateTo == null || dateTo.trim().isEmpty()) ? 
            java.time.Instant.now().toString() : dateTo;
        
        logger.info("Executing ListUsers with page: {}, size: {}, search: {}, status: {} (effective: {}), dateFrom: {} (effective: {}), dateTo: {} (effective: {})", 
                   page, size, search, status, effectiveStatus, dateFrom, effectiveDateFrom, dateTo, effectiveDateTo);
        
        return userRepositoryPort.findByFilters(search, effectiveStatus, effectiveDateFrom, effectiveDateTo, page, size)
                .collectList()
                .map(users -> {
                    logger.info("Retrieved {} users successfully", users.size());
                    int pageNum = page != null ? page : 1;
                    int pageSize = size != null ? size : 20;
                    return userMapper.toListResponse(users, pageNum, pageSize);
                })
                .doOnError(e -> logger.error("Error in ListUsers", e));
    }

}