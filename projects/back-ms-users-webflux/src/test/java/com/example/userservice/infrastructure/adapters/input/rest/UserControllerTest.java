package com.example.userservice.infrastructure.adapters.input.rest;

import com.example.userservice.domain.ports.input.UserUseCase;
import com.example.userservice.application.dto.user.CreateUserRequestContent;
import com.example.userservice.application.dto.user.CreateUserResponseContent;
import com.example.userservice.application.dto.user.GetUserResponseContent;
import com.example.userservice.application.dto.user.UpdateUserRequestContent;
import com.example.userservice.application.dto.user.UpdateUserResponseContent;
import com.example.userservice.application.dto.user.DeleteUserResponseContent;
import com.example.userservice.application.dto.user.ListUsersResponseContent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for UserController.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserUseCase userUseCase;

    @InjectMocks
    private UserController userController;

    @Test
    void createUser_ShouldReturnCreated_WhenValidRequest() {
        // Given
        CreateUserRequestContent request = CreateUserRequestContent.builder()
            .username("test-username")
            .email("test@example.com")
            .password("test-password")
            .build();
        CreateUserResponseContent response = CreateUserResponseContent.builder()
            .build();
        
        when(userUseCase.create(any(CreateUserRequestContent.class)))
            .thenReturn(Mono.just(response));

        // When
        CreateUserResponseContent result = userController.createUser(request, "test-request-id", null, null)
            .block(Duration.ofSeconds(5));

        // Then
        assertEquals(response, result);
    }

    @Test
    void getUser_ShouldReturnOk_WhenEntityExists() {
        // Given
        String userId = "test-id";
        GetUserResponseContent response = GetUserResponseContent.builder()
            .build();
        
        when(userUseCase.get(anyString()))
            .thenReturn(Mono.just(response));

        // When
        GetUserResponseContent result = userController.getUser(userId, "test-request-id", null, null)
            .block(Duration.ofSeconds(5));

        // Then
        assertEquals(response, result);
    }

    @Test
    void updateUser_ShouldReturnOk_WhenValidRequest() {
        // Given
        String userId = "test-id";
        UpdateUserRequestContent request = UpdateUserRequestContent.builder()
            .firstName("updated-firstName")
            .lastName("updated-lastName")
            .email("updated@example.com")
            .build();
        UpdateUserResponseContent response = UpdateUserResponseContent.builder()
            .build();
        
        when(userUseCase.update(anyString(), any(UpdateUserRequestContent.class)))
            .thenReturn(Mono.just(response));

        // When
        UpdateUserResponseContent result = userController.updateUser(userId, request, "test-request-id", null, null)
            .block(Duration.ofSeconds(5));

        // Then
        assertEquals(response, result);
    }

    @Test
    void deleteUser_ShouldReturnOk_WhenEntityExists() {
        // Given
        String userId = "test-id";
        DeleteUserResponseContent response = DeleteUserResponseContent.builder()
            .deleted(true)
            .message("User deleted successfully")
            .build();
        
        when(userUseCase.delete(anyString()))
            .thenReturn(Mono.just(response));

        // When
        DeleteUserResponseContent result = userController.deleteUser(userId, "test-request-id", null, null)
            .block(Duration.ofSeconds(5));

        // Then
        assertEquals(response, result);
    }

    @Test
    void listUsers_ShouldReturnOk() {
        // Given
        ListUsersResponseContent response = ListUsersResponseContent.builder()
            .build();
        
        when(userUseCase.list(any(), any(), any(), any(), any(), any()))
            .thenReturn(Mono.just(response));

        // When
        ListUsersResponseContent result = userController.listUsers(1, 20, null, null, null, null, "test-request-id", null, null)
            .block(Duration.ofSeconds(5));

        // Then
        assertEquals(response, result);
    }

}