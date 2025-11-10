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

import com.example.userservice.application.mapper.NeighborhoodMapper;
import com.example.userservice.domain.model.Neighborhood;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.NeighborhoodDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaNeighborhoodRepository;

@ExtendWith(MockitoExtension.class)
class NeighborhoodRepositoryAdapterTest {

    @Mock
    private JpaNeighborhoodRepository jpaNeighborhoodRepository;

    @Mock
    private NeighborhoodMapper neighborhoodMapper;

    @InjectMocks
    private NeighborhoodRepositoryAdapter neighborhoodRepositoryAdapter;

    private Neighborhood domainNeighborhood;
    private NeighborhoodDbo neighborhoodDbo;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        
        domainNeighborhood = Neighborhood.builder()
            .neighborhoodId(testId.toString())
            .build();
        
        neighborhoodDbo = NeighborhoodDbo.builder()
            .id(testId)
            .build();
    }

    @Test
    void save_ShouldReturnDomainEntity_WhenValidEntity() {
        // Given
        when(neighborhoodMapper.toDbo(domainNeighborhood)).thenReturn(neighborhoodDbo);
        when(jpaNeighborhoodRepository.save(neighborhoodDbo)).thenReturn(Mono.just(neighborhoodDbo));
        when(neighborhoodMapper.toDomain(neighborhoodDbo)).thenReturn(domainNeighborhood);

        // When
        Neighborhood result = neighborhoodRepositoryAdapter.save(domainNeighborhood)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        verify(neighborhoodMapper).toDbo(domainNeighborhood);
        verify(jpaNeighborhoodRepository).save(neighborhoodDbo);
        verify(neighborhoodMapper).toDomain(neighborhoodDbo);
    }

    @Test
    void findById_ShouldReturnEntity_WhenEntityExists() {
        // Given
        when(jpaNeighborhoodRepository.findById(testId)).thenReturn(Mono.just(neighborhoodDbo));
        when(neighborhoodMapper.toDomain(neighborhoodDbo)).thenReturn(domainNeighborhood);

        // When
        Neighborhood result = neighborhoodRepositoryAdapter.findById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(domainNeighborhood);
        verify(jpaNeighborhoodRepository).findById(testId);
        verify(neighborhoodMapper).toDomain(neighborhoodDbo);
    }

    @Test
    void findById_ShouldReturnNull_WhenEntityNotFound() {
        // Given
        when(jpaNeighborhoodRepository.findById(testId)).thenReturn(Mono.empty());

        // When
        Neighborhood result = neighborhoodRepositoryAdapter.findById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNull();
        verify(jpaNeighborhoodRepository).findById(testId);
    }

    @Test
    void findAll_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        when(jpaNeighborhoodRepository.findAll()).thenReturn(Flux.just(neighborhoodDbo));
        when(neighborhoodMapper.toDomain(neighborhoodDbo)).thenReturn(domainNeighborhood);

        // When
        var result = neighborhoodRepositoryAdapter.findAll()
            .collectList()
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainNeighborhood);
        verify(jpaNeighborhoodRepository).findAll();
    }

    @Test
    void deleteById_ShouldCallRepository_WhenValidId() {
        // Given
        when(jpaNeighborhoodRepository.deleteById(testId)).thenReturn(Mono.empty());

        // When
        neighborhoodRepositoryAdapter.deleteById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        verify(jpaNeighborhoodRepository).deleteById(testId);
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        when(jpaNeighborhoodRepository.existsById(testId)).thenReturn(Mono.just(true));

        // When
        Boolean result = neighborhoodRepositoryAdapter.existsById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isTrue();
        verify(jpaNeighborhoodRepository).existsById(testId);
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityNotExists() {
        // Given
        when(jpaNeighborhoodRepository.existsById(testId)).thenReturn(Mono.just(false));

        // When
        Boolean result = neighborhoodRepositoryAdapter.existsById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isFalse();
        verify(jpaNeighborhoodRepository).existsById(testId);
    }

    @Test
    void findBySearchTerm_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        String searchTerm = "test";
        Integer page = 0;
        Integer size = 10;
        Long offset = 0L;
        Long limit = 10L;
        
        when(jpaNeighborhoodRepository.findBySearchTerm(searchTerm, limit, offset))
            .thenReturn(Flux.just(neighborhoodDbo));
        when(neighborhoodMapper.toDomain(neighborhoodDbo)).thenReturn(domainNeighborhood);

        // When
        var result = neighborhoodRepositoryAdapter.findBySearchTerm(searchTerm, page, size)
            .collectList()
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainNeighborhood);
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
        
        when(jpaNeighborhoodRepository.findByFilters(search, status, dateFrom, dateTo, limit, offset))
            .thenReturn(Flux.just(neighborhoodDbo));
        when(neighborhoodMapper.toDomain(neighborhoodDbo)).thenReturn(domainNeighborhood);

        // When
        var result = neighborhoodRepositoryAdapter.findByFilters(search, status, dateFrom, dateTo, page, size)
            .collectList()
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainNeighborhood);
    }
}
