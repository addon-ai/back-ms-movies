package com.example.userservice.application.service;

import com.example.userservice.domain.ports.output.UserRepositoryPort;
import com.example.userservice.application.mapper.UserMapper;
import com.example.userservice.application.dto.user.CreateUserRequestContent;
import com.example.userservice.application.dto.user.CreateUserResponseContent;
import com.example.userservice.application.dto.user.GetUserResponseContent;
import com.example.userservice.application.dto.user.UpdateUserRequestContent;
import com.example.userservice.application.dto.user.UpdateUserResponseContent;
import com.example.userservice.application.dto.user.DeleteUserResponseContent;
import com.example.userservice.application.dto.user.ListUsersResponseContent;
import com.example.userservice.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mapstruct.factory.Mappers;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Collections;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import com.example.userservice.infrastructure.config.exceptions.NotFoundException;

/**
 * Unit tests for UserService.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private UserService userService;

    @Test
    void create_ShouldReturnResponse_WhenValidRequest() {
        // Given
        CreateUserRequestContent request = CreateUserRequestContent.builder()
            .build();
        User domainUser = User.builder()
            .build();
        User savedUser = User.builder()
            .build();
        CreateUserResponseContent expectedResponse = CreateUserResponseContent.builder()
            .build();

        when(userMapper.fromCreateRequest(request)).thenReturn(domainUser);
        when(userRepositoryPort.save(domainUser)).thenReturn(Mono.just(savedUser));
        when(userMapper.toCreateResponse(savedUser)).thenReturn(expectedResponse);

        // When
        CreateUserResponseContent result = userService.create(request)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(userRepositoryPort).save(domainUser);
    }

    @Test
    void create_ShouldThrowException_WhenRepositoryFails() {
        // Given
        CreateUserRequestContent request = CreateUserRequestContent.builder()
            .build();
        User domainUser = User.builder()
            .build();
        RuntimeException repositoryException = new RuntimeException("Database error");

        when(userMapper.fromCreateRequest(request)).thenReturn(domainUser);
        when(userRepositoryPort.save(domainUser)).thenReturn(Mono.error(repositoryException));

        // When & Then
        assertThatThrownBy(() -> userService.create(request).block(Duration.ofSeconds(5)))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }

    @Test
    void get_ShouldReturnResponse_WhenEntityExists() {
        // Given
        String userId = "test-id";
        User domainUser = User.builder()
            .build();
        GetUserResponseContent expectedResponse = GetUserResponseContent.builder()
            .build();

        when(userRepositoryPort.findById(userId)).thenReturn(Mono.just(domainUser));
        when(userMapper.toGetResponse(domainUser)).thenReturn(expectedResponse);

        // When
        GetUserResponseContent result = userService.get(userId)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(userRepositoryPort).findById(userId);
    }

    @Test
    void get_ShouldThrowNotFoundException_WhenEntityNotFound() {
        // Given
        String userId = "non-existent-id";
        when(userRepositoryPort.findById(userId)).thenReturn(Mono.empty());

        // When & Then
        assertThatThrownBy(() -> userService.get(userId).block(Duration.ofSeconds(5)))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("User not found");
    }

    @Test
    void get_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String userId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(userRepositoryPort.findById(userId)).thenReturn(Mono.error(repositoryException));

        // When & Then
        assertThatThrownBy(() -> userService.get(userId).block(Duration.ofSeconds(5)))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }

    @Test
    void update_ShouldReturnResponse_WhenValidRequest() {
        // Given
        String userId = "test-id";
        UpdateUserRequestContent request = UpdateUserRequestContent.builder()
            .build();
        User existingUser = User.builder()
            .build();
        User updatedUser = User.builder()
            .build();
        UpdateUserResponseContent expectedResponse = UpdateUserResponseContent.builder()
            .build();

        when(userRepositoryPort.findById(userId)).thenReturn(Mono.just(existingUser));
        when(userRepositoryPort.save(any(User.class))).thenReturn(Mono.just(updatedUser));
        when(userMapper.toUpdateResponse(any(User.class))).thenReturn(expectedResponse);

        // When
        UpdateUserResponseContent result = userService.update(userId, request)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(userRepositoryPort).save(any(User.class));
    }

    @Test
    void update_ShouldThrowNotFoundException_WhenEntityNotFound() {
        // Given
        String userId = "non-existent-id";
        UpdateUserRequestContent request = UpdateUserRequestContent.builder()
            .build();
        when(userRepositoryPort.findById(userId)).thenReturn(Mono.empty());

        // When & Then
        assertThatThrownBy(() -> userService.update(userId, request).block(Duration.ofSeconds(5)))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("User not found");
    }

    @Test
    void update_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String userId = "test-id";
        UpdateUserRequestContent request = UpdateUserRequestContent.builder()
            .build();
        User existingUser = User.builder()
            .build();
        RuntimeException repositoryException = new RuntimeException("Database error");

        when(userRepositoryPort.findById(userId)).thenReturn(Mono.just(existingUser));
        when(userRepositoryPort.save(any(User.class))).thenReturn(Mono.error(repositoryException));

        // When & Then
        assertThatThrownBy(() -> userService.update(userId, request).block(Duration.ofSeconds(5)))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }

    @Test
    void delete_ShouldReturnResponse_WhenEntityExists() {
        // Given
        String userId = "test-id";
        User domainUser = User.builder()
            .build();
        DeleteUserResponseContent expectedResponse = DeleteUserResponseContent.builder()
            .deleted(true)
            .message("User deleted successfully")
            .build();

        when(userRepositoryPort.findById(userId)).thenReturn(Mono.just(domainUser));
        when(userRepositoryPort.save(any(User.class))).thenReturn(Mono.just(domainUser));

        // When
        DeleteUserResponseContent result = userService.delete(userId)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(userRepositoryPort).save(any(User.class));
    }

    @Test
    void delete_ShouldThrowNotFoundException_WhenEntityNotFound() {
        // Given
        String userId = "non-existent-id";
        when(userRepositoryPort.findById(userId)).thenReturn(Mono.empty());

        // When & Then
        assertThatThrownBy(() -> userService.delete(userId).block(Duration.ofSeconds(5)))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("User not found");
    }

    @Test
    void delete_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String userId = "test-id";
        User domainUser = User.builder()
            .build();
        RuntimeException repositoryException = new RuntimeException("Database error");

        when(userRepositoryPort.findById(userId)).thenReturn(Mono.just(domainUser));
        when(userRepositoryPort.save(any(User.class))).thenReturn(Mono.error(repositoryException));

        // When & Then
        assertThatThrownBy(() -> userService.delete(userId).block(Duration.ofSeconds(5)))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }

    @Test
    void list_ShouldReturnResponse_WhenValidRequest() {
        // Given
        List<User> users = Collections.singletonList(User.builder().build());
        ListUsersResponseContent expectedResponse = ListUsersResponseContent.builder().build();
        
        when(userRepositoryPort.findByFilters(any(), any(), any(), any(), any(), any())).thenReturn(Flux.fromIterable(users));
        when(userMapper.toListResponse(users, 1, 20)).thenReturn(expectedResponse);

        // When
        ListUsersResponseContent result = userService.list(1, 20, null, null, null, null)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(userRepositoryPort).findByFilters(any(), any(), any(), any(), any(), any());
    }

    @Test
    void list_ShouldReturnResponse_WhenSearchTermProvided() {
        // Given
        String searchTerm = "test search";
        Integer page = 1;
        Integer size = 10;
        List<User> users = Collections.singletonList(User.builder().build());
        ListUsersResponseContent expectedResponse = ListUsersResponseContent.builder().build();
        
        when(userRepositoryPort.findByFilters(any(), any(), any(), any(), any(), any())).thenReturn(Flux.fromIterable(users));
        when(userMapper.toListResponse(users, page, size)).thenReturn(expectedResponse);

        // When
        ListUsersResponseContent result = userService.list(page, size, searchTerm, null, null, null)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(userRepositoryPort).findByFilters(any(), any(), any(), any(), any(), any());
    }

    @Test
    void list_ShouldReturnResponse_WhenNullParameters() {
        // Given
        List<User> users = Collections.emptyList();
        ListUsersResponseContent expectedResponse = ListUsersResponseContent.builder()
            .users(Collections.emptyList())
            .page(java.math.BigDecimal.valueOf(1))
            .size(java.math.BigDecimal.valueOf(20))
            .total(java.math.BigDecimal.valueOf(0))
            .totalPages(java.math.BigDecimal.valueOf(0))
            .build();
        
        when(userRepositoryPort.findByFilters(any(), any(), any(), any(), any(), any())).thenReturn(Flux.fromIterable(users));
        when(userMapper.toListResponse(users, 1, 20)).thenReturn(expectedResponse);

        // When
        ListUsersResponseContent result = userService.list(null, null, null, null, null, null)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsers()).isNotNull().isEmpty();
        assertThat(result.getPage()).isEqualTo(java.math.BigDecimal.valueOf(1));
        assertThat(result.getSize()).isEqualTo(java.math.BigDecimal.valueOf(20));
        assertThat(result.getTotal()).isEqualTo(java.math.BigDecimal.valueOf(0));
        assertThat(result.getTotalPages()).isEqualTo(java.math.BigDecimal.valueOf(0));
        verify(userRepositoryPort).findByFilters(any(), any(), any(), any(), any(), any());
    }

    @Test
    void list_ShouldThrowException_WhenRepositoryFails() {
        // Given
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(userRepositoryPort.findByFilters(any(), any(), any(), any(), any(), any())).thenReturn(Flux.error(repositoryException));

        // When & Then
        assertThatThrownBy(() -> userService.list(1, 20, null, null, null, null).block(Duration.ofSeconds(5)))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }
}
