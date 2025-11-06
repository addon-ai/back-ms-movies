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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
            .thenReturn(response);

        // When
        ResponseEntity<CreateUserResponseContent> result = userController.createUser(request, "test-request-id", null, null);

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void getUser_ShouldReturnOk_WhenEntityExists() {
        // Given
        String userId = "test-id";
        GetUserResponseContent response = GetUserResponseContent.builder()
            .build();
        
        when(userUseCase.get(anyString()))
            .thenReturn(response);

        // When
        ResponseEntity<GetUserResponseContent> result = userController.getUser(userId, "test-request-id", null, null);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
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
            .thenReturn(response);

        // When
        ResponseEntity<UpdateUserResponseContent> result = userController.updateUser(userId, request, "test-request-id", null, null);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
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
            .thenReturn(response);

        // When
        ResponseEntity<DeleteUserResponseContent> result = userController.deleteUser(userId, "test-request-id", null, null);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void listUsers_ShouldReturnOk() {
        // Given
        ListUsersResponseContent response = ListUsersResponseContent.builder()
            .build();
        
        when(userUseCase.list(any(), any(), any()))
            .thenReturn(response);

        // When
        ResponseEntity<ListUsersResponseContent> result = userController.listUsers(1, 20, null, "test-request-id", null, null);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

}