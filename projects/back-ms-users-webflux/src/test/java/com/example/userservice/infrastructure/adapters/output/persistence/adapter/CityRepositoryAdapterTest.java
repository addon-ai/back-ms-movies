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

import com.example.userservice.application.mapper.CityMapper;
import com.example.userservice.domain.model.City;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.CityDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaCityRepository;

@ExtendWith(MockitoExtension.class)
class CityRepositoryAdapterTest {

    @Mock
    private JpaCityRepository jpaCityRepository;

    @Mock
    private CityMapper cityMapper;

    @InjectMocks
    private CityRepositoryAdapter cityRepositoryAdapter;

    private City domainCity;
    private CityDbo cityDbo;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        
        domainCity = City.builder()
            .cityId(testId.toString())
            .build();
        
        cityDbo = CityDbo.builder()
            .id(testId)
            .build();
    }

    @Test
    void save_ShouldReturnDomainEntity_WhenValidEntity() {
        // Given
        when(cityMapper.toDbo(domainCity)).thenReturn(cityDbo);
        when(jpaCityRepository.save(cityDbo)).thenReturn(Mono.just(cityDbo));
        when(cityMapper.toDomain(cityDbo)).thenReturn(domainCity);

        // When
        City result = cityRepositoryAdapter.save(domainCity)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        verify(cityMapper).toDbo(domainCity);
        verify(jpaCityRepository).save(cityDbo);
        verify(cityMapper).toDomain(cityDbo);
    }

    @Test
    void findById_ShouldReturnEntity_WhenEntityExists() {
        // Given
        when(jpaCityRepository.findById(testId)).thenReturn(Mono.just(cityDbo));
        when(cityMapper.toDomain(cityDbo)).thenReturn(domainCity);

        // When
        City result = cityRepositoryAdapter.findById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(domainCity);
        verify(jpaCityRepository).findById(testId);
        verify(cityMapper).toDomain(cityDbo);
    }

    @Test
    void findById_ShouldReturnNull_WhenEntityNotFound() {
        // Given
        when(jpaCityRepository.findById(testId)).thenReturn(Mono.empty());

        // When
        City result = cityRepositoryAdapter.findById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNull();
        verify(jpaCityRepository).findById(testId);
    }

    @Test
    void findAll_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        when(jpaCityRepository.findAll()).thenReturn(Flux.just(cityDbo));
        when(cityMapper.toDomain(cityDbo)).thenReturn(domainCity);

        // When
        var result = cityRepositoryAdapter.findAll()
            .collectList()
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainCity);
        verify(jpaCityRepository).findAll();
    }

    @Test
    void deleteById_ShouldCallRepository_WhenValidId() {
        // Given
        when(jpaCityRepository.deleteById(testId)).thenReturn(Mono.empty());

        // When
        cityRepositoryAdapter.deleteById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        verify(jpaCityRepository).deleteById(testId);
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        when(jpaCityRepository.existsById(testId)).thenReturn(Mono.just(true));

        // When
        Boolean result = cityRepositoryAdapter.existsById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isTrue();
        verify(jpaCityRepository).existsById(testId);
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityNotExists() {
        // Given
        when(jpaCityRepository.existsById(testId)).thenReturn(Mono.just(false));

        // When
        Boolean result = cityRepositoryAdapter.existsById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isFalse();
        verify(jpaCityRepository).existsById(testId);
    }

    @Test
    void findBySearchTerm_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        String searchTerm = "test";
        Integer page = 0;
        Integer size = 10;
        Long offset = 0L;
        Long limit = 10L;
        
        when(jpaCityRepository.findBySearchTerm(searchTerm, limit, offset))
            .thenReturn(Flux.just(cityDbo));
        when(cityMapper.toDomain(cityDbo)).thenReturn(domainCity);

        // When
        var result = cityRepositoryAdapter.findBySearchTerm(searchTerm, page, size)
            .collectList()
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainCity);
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
        
        when(jpaCityRepository.findByFilters(search, status, dateFrom, dateTo, limit, offset))
            .thenReturn(Flux.just(cityDbo));
        when(cityMapper.toDomain(cityDbo)).thenReturn(domainCity);

        // When
        var result = cityRepositoryAdapter.findByFilters(search, status, dateFrom, dateTo, page, size)
            .collectList()
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainCity);
    }
}
