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

import com.example.userservice.application.mapper.RegionMapper;
import com.example.userservice.domain.model.Region;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.RegionDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaRegionRepository;

@ExtendWith(MockitoExtension.class)
class RegionRepositoryAdapterTest {

    @Mock
    private JpaRegionRepository jpaRegionRepository;

    @Mock
    private RegionMapper regionMapper;

    @InjectMocks
    private RegionRepositoryAdapter regionRepositoryAdapter;

    private Region domainRegion;
    private RegionDbo regionDbo;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        
        domainRegion = Region.builder()
            .regionId(testId.toString())
            .build();
        
        regionDbo = RegionDbo.builder()
            .id(testId)
            .build();
    }

    @Test
    void save_ShouldReturnDomainEntity_WhenValidEntity() {
        // Given
        when(regionMapper.toDbo(domainRegion)).thenReturn(regionDbo);
        when(jpaRegionRepository.save(regionDbo)).thenReturn(Mono.just(regionDbo));
        when(regionMapper.toDomain(regionDbo)).thenReturn(domainRegion);

        // When
        Region result = regionRepositoryAdapter.save(domainRegion)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        verify(regionMapper).toDbo(domainRegion);
        verify(jpaRegionRepository).save(regionDbo);
        verify(regionMapper).toDomain(regionDbo);
    }

    @Test
    void findById_ShouldReturnEntity_WhenEntityExists() {
        // Given
        when(jpaRegionRepository.findById(testId)).thenReturn(Mono.just(regionDbo));
        when(regionMapper.toDomain(regionDbo)).thenReturn(domainRegion);

        // When
        Region result = regionRepositoryAdapter.findById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(domainRegion);
        verify(jpaRegionRepository).findById(testId);
        verify(regionMapper).toDomain(regionDbo);
    }

    @Test
    void findById_ShouldReturnNull_WhenEntityNotFound() {
        // Given
        when(jpaRegionRepository.findById(testId)).thenReturn(Mono.empty());

        // When
        Region result = regionRepositoryAdapter.findById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNull();
        verify(jpaRegionRepository).findById(testId);
    }

    @Test
    void findAll_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        when(jpaRegionRepository.findAll()).thenReturn(Flux.just(regionDbo));
        when(regionMapper.toDomain(regionDbo)).thenReturn(domainRegion);

        // When
        var result = regionRepositoryAdapter.findAll()
            .collectList()
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainRegion);
        verify(jpaRegionRepository).findAll();
    }

    @Test
    void deleteById_ShouldCallRepository_WhenValidId() {
        // Given
        when(jpaRegionRepository.deleteById(testId)).thenReturn(Mono.empty());

        // When
        regionRepositoryAdapter.deleteById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        verify(jpaRegionRepository).deleteById(testId);
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        when(jpaRegionRepository.existsById(testId)).thenReturn(Mono.just(true));

        // When
        Boolean result = regionRepositoryAdapter.existsById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isTrue();
        verify(jpaRegionRepository).existsById(testId);
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityNotExists() {
        // Given
        when(jpaRegionRepository.existsById(testId)).thenReturn(Mono.just(false));

        // When
        Boolean result = regionRepositoryAdapter.existsById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isFalse();
        verify(jpaRegionRepository).existsById(testId);
    }

    @Test
    void findBySearchTerm_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        String searchTerm = "test";
        Integer page = 0;
        Integer size = 10;
        Long offset = 0L;
        Long limit = 10L;
        
        when(jpaRegionRepository.findBySearchTerm(searchTerm, limit, offset))
            .thenReturn(Flux.just(regionDbo));
        when(regionMapper.toDomain(regionDbo)).thenReturn(domainRegion);

        // When
        var result = regionRepositoryAdapter.findBySearchTerm(searchTerm, page, size)
            .collectList()
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainRegion);
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
        
        when(jpaRegionRepository.findByFilters(search, status, dateFrom, dateTo, limit, offset))
            .thenReturn(Flux.just(regionDbo));
        when(regionMapper.toDomain(regionDbo)).thenReturn(domainRegion);

        // When
        var result = regionRepositoryAdapter.findByFilters(search, status, dateFrom, dateTo, page, size)
            .collectList()
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainRegion);
    }
}
