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

import com.example.userservice.application.mapper.LocationMapper;
import com.example.userservice.domain.model.Location;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.LocationDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaLocationRepository;

@ExtendWith(MockitoExtension.class)
class LocationRepositoryAdapterTest {

    @Mock
    private JpaLocationRepository jpaLocationRepository;

    @Mock
    private LocationMapper locationMapper;

    @InjectMocks
    private LocationRepositoryAdapter locationRepositoryAdapter;

    private Location domainLocation;
    private LocationDbo locationDbo;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        
        domainLocation = Location.builder()
            .locationId(testId.toString())
            .build();
        
        locationDbo = LocationDbo.builder()
            .id(testId)
            .build();
    }

    @Test
    void save_ShouldReturnDomainEntity_WhenValidEntity() {
        // Given
        when(locationMapper.toDbo(domainLocation)).thenReturn(locationDbo);
        when(jpaLocationRepository.save(locationDbo)).thenReturn(Mono.just(locationDbo));
        when(locationMapper.toDomain(locationDbo)).thenReturn(domainLocation);

        // When
        Location result = locationRepositoryAdapter.save(domainLocation)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        verify(locationMapper).toDbo(domainLocation);
        verify(jpaLocationRepository).save(locationDbo);
        verify(locationMapper).toDomain(locationDbo);
    }

    @Test
    void findById_ShouldReturnEntity_WhenEntityExists() {
        // Given
        when(jpaLocationRepository.findById(testId)).thenReturn(Mono.just(locationDbo));
        when(locationMapper.toDomain(locationDbo)).thenReturn(domainLocation);

        // When
        Location result = locationRepositoryAdapter.findById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(domainLocation);
        verify(jpaLocationRepository).findById(testId);
        verify(locationMapper).toDomain(locationDbo);
    }

    @Test
    void findById_ShouldReturnNull_WhenEntityNotFound() {
        // Given
        when(jpaLocationRepository.findById(testId)).thenReturn(Mono.empty());

        // When
        Location result = locationRepositoryAdapter.findById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNull();
        verify(jpaLocationRepository).findById(testId);
    }

    @Test
    void findAll_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        when(jpaLocationRepository.findAll()).thenReturn(Flux.just(locationDbo));
        when(locationMapper.toDomain(locationDbo)).thenReturn(domainLocation);

        // When
        var result = locationRepositoryAdapter.findAll()
            .collectList()
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainLocation);
        verify(jpaLocationRepository).findAll();
    }

    @Test
    void deleteById_ShouldCallRepository_WhenValidId() {
        // Given
        when(jpaLocationRepository.deleteById(testId)).thenReturn(Mono.empty());

        // When
        locationRepositoryAdapter.deleteById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        verify(jpaLocationRepository).deleteById(testId);
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        when(jpaLocationRepository.existsById(testId)).thenReturn(Mono.just(true));

        // When
        Boolean result = locationRepositoryAdapter.existsById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isTrue();
        verify(jpaLocationRepository).existsById(testId);
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityNotExists() {
        // Given
        when(jpaLocationRepository.existsById(testId)).thenReturn(Mono.just(false));

        // When
        Boolean result = locationRepositoryAdapter.existsById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isFalse();
        verify(jpaLocationRepository).existsById(testId);
    }

    @Test
    void findBySearchTerm_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        String searchTerm = "test";
        Integer page = 0;
        Integer size = 10;
        Long offset = 0L;
        Long limit = 10L;
        
        when(jpaLocationRepository.findBySearchTerm(searchTerm, limit, offset))
            .thenReturn(Flux.just(locationDbo));
        when(locationMapper.toDomain(locationDbo)).thenReturn(domainLocation);

        // When
        var result = locationRepositoryAdapter.findBySearchTerm(searchTerm, page, size)
            .collectList()
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainLocation);
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
        
        when(jpaLocationRepository.findByFilters(search, status, dateFrom, dateTo, limit, offset))
            .thenReturn(Flux.just(locationDbo));
        when(locationMapper.toDomain(locationDbo)).thenReturn(domainLocation);

        // When
        var result = locationRepositoryAdapter.findByFilters(search, status, dateFrom, dateTo, page, size)
            .collectList()
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainLocation);
    }
}
