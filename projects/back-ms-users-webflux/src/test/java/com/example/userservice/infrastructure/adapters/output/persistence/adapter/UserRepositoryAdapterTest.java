package com.example.userservice.infrastructure.adapters.output.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.userservice.application.mapper.UserMapper;
import com.example.userservice.domain.model.User;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.UserDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaUserRepository;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private JpaUserRepository jpaUserRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;

    private User domainUser;
    private UserDbo userDbo;

    @BeforeEach
    void setUp() {
        domainUser = User.builder()
            .build();
        
        userDbo = UserDbo.builder()
            .build();
    }

    @Test
    void save_ShouldReturnDomainEntity_WhenValidEntity() {
        // Given
        when(userMapper.toDbo(domainUser)).thenReturn(userDbo);
        when(jpaUserRepository.save(userDbo)).thenReturn(userDbo);
        when(userMapper.toDomain(userDbo)).thenReturn(domainUser);

        // When
        User result = userRepositoryAdapter.save(domainUser);

        // Then
        assertThat(result).isNotNull();
        verify(userMapper).toDbo(domainUser);
        verify(jpaUserRepository).save(userDbo);
        verify(userMapper).toDomain(userDbo);
    }

    @Test
    void save_ShouldThrowException_WhenRepositoryFails() {
        // Given
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(userMapper.toDbo(domainUser)).thenReturn(userDbo);
        when(jpaUserRepository.save(userDbo)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> userRepositoryAdapter.save(domainUser))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to save User");
    }

    @Test
    void findById_ShouldReturnOptionalWithEntity_WhenEntityExists() {
        // Given
        String userId = "test-id";
        when(jpaUserRepository.findById(userId)).thenReturn(Optional.of(userDbo));
        when(userMapper.toDomain(userDbo)).thenReturn(domainUser);

        // When
        Optional<User> result = userRepositoryAdapter.findById(userId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(domainUser);
        verify(jpaUserRepository).findById(userId);
        verify(userMapper).toDomain(userDbo);
    }

    @Test
    void findById_ShouldReturnEmptyOptional_WhenEntityNotFound() {
        // Given
        String userId = "non-existent-id";
        when(jpaUserRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userRepositoryAdapter.findById(userId);

        // Then
        assertThat(result).isEmpty();
        verify(jpaUserRepository).findById(userId);
    }

    @Test
    void findById_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String userId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaUserRepository.findById(userId)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> userRepositoryAdapter.findById(userId))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to find User by id");
    }

    @Test
    void findAll_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        List<UserDbo> userDbos = Collections.singletonList(userDbo);
        List<User> users = Collections.singletonList(domainUser);
        when(jpaUserRepository.findAll()).thenReturn(userDbos);
        when(userMapper.toDomainList(userDbos)).thenReturn(users);

        // When
        List<User> result = userRepositoryAdapter.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(users);
        verify(jpaUserRepository).findAll();
        verify(userMapper).toDomainList(userDbos);
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoEntitiesExist() {
        // Given
        when(jpaUserRepository.findAll()).thenReturn(Collections.emptyList());
        when(userMapper.toDomainList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<User> result = userRepositoryAdapter.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(jpaUserRepository).findAll();
        verify(userMapper).toDomainList(Collections.emptyList());
    }

    @Test
    void findAll_ShouldThrowException_WhenRepositoryFails() {
        // Given
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaUserRepository.findAll()).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> userRepositoryAdapter.findAll())
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to find all Users");
        
        verify(jpaUserRepository).findAll();
    }

    @Test
    void deleteById_ShouldCallRepository_WhenValidId() {
        // Given
        String userId = "test-id";

        // When
        userRepositoryAdapter.deleteById(userId);

        // Then
        verify(jpaUserRepository).deleteById(userId);
    }

    @Test
    void deleteById_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String userId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        doThrow(repositoryException).when(jpaUserRepository).deleteById(userId);

        // When & Then
        assertThatThrownBy(() -> userRepositoryAdapter.deleteById(userId))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to delete User by id");
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        String userId = "test-id";
        when(jpaUserRepository.existsById(userId)).thenReturn(true);

        // When
        boolean result = userRepositoryAdapter.existsById(userId);

        // Then
        assertThat(result).isTrue();
        verify(jpaUserRepository).existsById(userId);
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityNotExists() {
        // Given
        String userId = "non-existent-id";
        when(jpaUserRepository.existsById(userId)).thenReturn(false);

        // When
        boolean result = userRepositoryAdapter.existsById(userId);

        // Then
        assertThat(result).isFalse();
        verify(jpaUserRepository).existsById(userId);
    }

    @Test
    void existsById_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String userId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaUserRepository.existsById(userId)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> userRepositoryAdapter.existsById(userId))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to check if User exists by id");
    }

    @Test
    void findBySearchTerm_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        String searchTerm = "test";
        Integer page = 1;
        Integer size = 10;
        
        when(jpaUserRepository.findBySearchTerm(anyString(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(userDbo)));
        when(userMapper.toDomain(userDbo)).thenReturn(domainUser);

        // When
        List<User> result = userRepositoryAdapter.findBySearchTerm(searchTerm, page, size);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainUser);
    }

    @Test
    void findBySearchTerm_ShouldReturnListOfEntities_WhenSearchIsEmpty() {
        // Given
        String searchTerm = "";
        Integer page = 1;
        Integer size = 10;
        
        when(jpaUserRepository.findAll()).thenReturn(Collections.singletonList(userDbo));
        when(userMapper.toDomainList(Collections.singletonList(userDbo)))
            .thenReturn(Collections.singletonList(domainUser));

        // When
        List<User> result = userRepositoryAdapter.findBySearchTerm(searchTerm, page, size);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainUser);
    }

    @Test
    void findBySearchTerm_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String searchTerm = "test";
        Integer page = 1;
        Integer size = 10;
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaUserRepository.findBySearchTerm(anyString(), any(Pageable.class)))
            .thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> userRepositoryAdapter.findBySearchTerm(searchTerm, page, size))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to search Users");
    }

    @Test
    void findBySearchTerm_ShouldThrowException_WhenRepositoryFailsOnFindAll() {
        // Given
        String searchTerm = null;
        Integer page = 1;
        Integer size = 10;
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaUserRepository.findAll()).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> userRepositoryAdapter.findBySearchTerm(searchTerm, page, size))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to search Users");
    }

    @Test
    void findBySearchTermPaged_ShouldReturnPageOfEntities_WhenEntitiesExist() {
        // Given
        String searchTerm = "test";
        Pageable pageable = PageRequest.of(0, 10);
        List<UserDbo> userDbos = Collections.singletonList(userDbo);
        Page<UserDbo> dboPage = new PageImpl<>(userDbos, pageable, 1);
        
        when(jpaUserRepository.findBySearchTerm(searchTerm, pageable)).thenReturn(dboPage);
        when(userMapper.toDomain(userDbo)).thenReturn(domainUser);

        // When
        Page<User> result = userRepositoryAdapter.findBySearchTermPaged(searchTerm, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(domainUser);
        verify(jpaUserRepository).findBySearchTerm(searchTerm, pageable);
        verify(userMapper).toDomain(userDbo);
    }

    @Test
    void findBySearchTermPaged_ShouldReturnEmptyPage_WhenNoEntitiesExist() {
        // Given
        String searchTerm = "nonexistent";
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserDbo> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        
        when(jpaUserRepository.findBySearchTerm(searchTerm, pageable)).thenReturn(emptyPage);

        // When
        Page<User> result = userRepositoryAdapter.findBySearchTermPaged(searchTerm, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(jpaUserRepository).findBySearchTerm(searchTerm, pageable);
    }

    @Test
    void findAllPaged_ShouldReturnPageOfEntities_WhenEntitiesExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<UserDbo> userDbos = Collections.singletonList(userDbo);
        Page<UserDbo> dboPage = new PageImpl<>(userDbos, pageable, 1);
        
        when(jpaUserRepository.findAllPaged(pageable)).thenReturn(dboPage);
        when(userMapper.toDomain(userDbo)).thenReturn(domainUser);

        // When
        Page<User> result = userRepositoryAdapter.findAllPaged(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(domainUser);
        verify(jpaUserRepository).findAllPaged(pageable);
        verify(userMapper).toDomain(userDbo);
    }

    @Test
    void findAllPaged_ShouldReturnEmptyPage_WhenNoEntitiesExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserDbo> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        
        when(jpaUserRepository.findAllPaged(pageable)).thenReturn(emptyPage);

        // When
        Page<User> result = userRepositoryAdapter.findAllPaged(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(jpaUserRepository).findAllPaged(pageable);
    }

    @Test
    void findBySearchTermPaged_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String searchTerm = "test";
        Pageable pageable = PageRequest.of(0, 10);
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaUserRepository.findBySearchTerm(searchTerm, pageable)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> userRepositoryAdapter.findBySearchTermPaged(searchTerm, pageable))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }

    @Test
    void findAllPaged_ShouldThrowException_WhenRepositoryFails() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaUserRepository.findAllPaged(pageable)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> userRepositoryAdapter.findAllPaged(pageable))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }
}