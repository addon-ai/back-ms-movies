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
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Consolidated application service implementing all User use cases.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserUseCase {

    private static final LoggingUtils logger = LoggingUtils.getLogger(UserService.class);
    
    private final UserRepositoryPort userRepositoryPort;
    private final UserMapper userMapper;

    @Override
    public CreateUserResponseContent create(CreateUserRequestContent request) {
        logger.info("Executing CreateUser with request: {}", request);
        
        try {
            User user = userMapper.fromCreateRequest(request);
            User savedUser = userRepositoryPort.save(user);
            logger.info("User created successfully with ID: {}", savedUser.getUserId());
            return userMapper.toCreateResponse(savedUser);
        } catch (Exception e) {
            logger.error("Error in CreateUser", e, request);
            throw e;
        }
    }

    @Override
    public GetUserResponseContent get(String userId) {
        logger.info("Executing GetUser with userId: {}", userId);
        
        try {
            User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
            
            logger.info("User retrieved successfully with ID: {}", userId);
            return userMapper.toGetResponse(user);
        } catch (NotFoundException e) {
            logger.error("User not found in GetUser", e, userId);
            throw e;
        } catch (Exception e) {
            logger.error("Error in GetUser", e, userId);
            throw e;
        }
    }

    @Override
    public UpdateUserResponseContent update(String userId, UpdateUserRequestContent request) {
        logger.info("Executing UpdateUser with userId: {} and request: {}", userId, request);
        
        try {
            User existingUser = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
            
            // Merge request data into existing entity
            userMapper.updateEntityFromRequest(request, existingUser);
            existingUser.setUpdatedAt(java.time.Instant.now().toString());
            
            User savedUser = userRepositoryPort.save(existingUser);
            logger.info("User updated successfully with ID: {}", userId);
            
            return userMapper.toUpdateResponse(savedUser);
        } catch (NotFoundException e) {
            logger.error("User not found in UpdateUser", e, userId);
            throw e;
        } catch (Exception e) {
            logger.error("Error in UpdateUser", e, userId);
            throw e;
        }
    }

    @Override
    public DeleteUserResponseContent delete(String userId) {
        logger.info("Executing DeleteUser with userId: {}", userId);
        
        try {
            User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
            
            userRepositoryPort.deleteById(userId);
            logger.info("User deleted successfully with ID: {}", userId);
            
            return DeleteUserResponseContent.builder()
                .deleted(true)
                .message("User deleted successfully")
                .build();
        } catch (NotFoundException e) {
            logger.error("User not found in DeleteUser", e, userId);
            throw e;
        } catch (Exception e) {
            logger.error("Error in DeleteUser", e, userId);
            throw e;
        }
    }

    @Override
    public ListUsersResponseContent list(Integer page, Integer size, String search) {
        logger.info("Executing ListUsers with page: {}, size: {}, search: {}", page, size, search);
        
        try {
            List<User> users;
            if (search != null && !search.trim().isEmpty()) {
                users = userRepositoryPort.findBySearchTerm(search, page, size);
            } else {
                users = userRepositoryPort.findAll();
            }
            logger.info("Retrieved {} users successfully", users.size());
            return userMapper.toListResponse(users, page != null ? page : 1, size != null ? size : 20);
        } catch (Exception e) {
            logger.error("Error in ListUsers", e);
            throw e;
        }
    }

}