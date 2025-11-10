package com.example.userservice.infrastructure.adapters.output.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

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
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        
        domainUser = User.builder()
            .userId(testId.toString())
            .build();
        
        userDbo = UserDbo.builder()
            .id(testId)
            .build();
    }

    @Test
    void save_ShouldReturnDomainEntity_WhenValidEntity() {
        // Given
        when(userMapper.toDbo(domainUser)).thenReturn(userDbo);
        when(jpaUserRepository.save(userDbo)).thenReturn(Mono.just(userDbo));
        when(userMapper.toDomain(userDbo)).thenReturn(domainUser);

        // When
        User result = userRepositoryAdapter.save(domainUser)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        verify(userMapper).toDbo(domainUser);
        verify(jpaUserRepository).save(userDbo);
        verify(userMapper).toDomain(userDbo);
    }

    @Test
    void findById_ShouldReturnEntity_WhenEntityExists() {
        // Given
        when(jpaUserRepository.findById(testId)).thenReturn(Mono.just(userDbo));
        when(userMapper.toDomain(userDbo)).thenReturn(domainUser);

        // When
        User result = userRepositoryAdapter.findById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(domainUser);
        verify(jpaUserRepository).findById(testId);
        verify(userMapper).toDomain(userDbo);
    }

    @Test
    void findById_ShouldReturnNull_WhenEntityNotFound() {
        // Given
        when(jpaUserRepository.findById(testId)).thenReturn(Mono.empty());

        // When
        User result = userRepositoryAdapter.findById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNull();
        verify(jpaUserRepository).findById(testId);
    }

    @Test
    void findAll_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        when(jpaUserRepository.findAll()).thenReturn(Flux.just(userDbo));
        when(userMapper.toDomain(userDbo)).thenReturn(domainUser);

        // When
        var result = userRepositoryAdapter.findAll()
            .collectList()
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainUser);
        verify(jpaUserRepository).findAll();
    }

    @Test
    void deleteById_ShouldCallRepository_WhenValidId() {
        // Given
        when(jpaUserRepository.deleteById(testId)).thenReturn(Mono.empty());

        // When
        userRepositoryAdapter.deleteById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        verify(jpaUserRepository).deleteById(testId);
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        when(jpaUserRepository.existsById(testId)).thenReturn(Mono.just(true));

        // When
        Boolean result = userRepositoryAdapter.existsById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isTrue();
        verify(jpaUserRepository).existsById(testId);
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityNotExists() {
        // Given
        when(jpaUserRepository.existsById(testId)).thenReturn(Mono.just(false));

        // When
        Boolean result = userRepositoryAdapter.existsById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isFalse();
        verify(jpaUserRepository).existsById(testId);
    }

    @Test
    void findBySearchTerm_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        String searchTerm = "test";
        Integer page = 0;
        Integer size = 10;
        Long offset = 0L;
        Long limit = 10L;
        
        when(jpaUserRepository.findBySearchTerm(searchTerm, limit, offset))
            .thenReturn(Flux.just(userDbo));
        when(userMapper.toDomain(userDbo)).thenReturn(domainUser);

        // When
        var result = userRepositoryAdapter.findBySearchTerm(searchTerm, page, size)
            .collectList()
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainUser);
    }

    @Test
    void findByFilters_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        String search = "test";
        String status = "ACTIVE";
        String dateFrom = "2024-01-01T00:00:00Z";
        String dateTo = "2024-12-31T23:59:59Z";
        Integer page = 0;
        Integer size = 10;
        Long offset = 0L;
        Long limit = 10L;
        
        when(jpaUserRepository.findByFilters(search, status, dateFrom, dateTo, limit, offset))
            .thenReturn(Flux.just(userDbo));
        when(userMapper.toDomain(userDbo)).thenReturn(domainUser);

        // When
        var result = userRepositoryAdapter.findByFilters(search, status, dateFrom, dateTo, page, size)
            .collectList()
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainUser);
    }
}
