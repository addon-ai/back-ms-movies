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

import com.example.userservice.application.mapper.CountryMapper;
import com.example.userservice.domain.model.Country;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.CountryDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaCountryRepository;

@ExtendWith(MockitoExtension.class)
class CountryRepositoryAdapterTest {

    @Mock
    private JpaCountryRepository jpaCountryRepository;

    @Mock
    private CountryMapper countryMapper;

    @InjectMocks
    private CountryRepositoryAdapter countryRepositoryAdapter;

    private Country domainCountry;
    private CountryDbo countryDbo;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        
        domainCountry = Country.builder()
            .countryId(testId.toString())
            .build();
        
        countryDbo = CountryDbo.builder()
            .id(testId)
            .build();
    }

    @Test
    void save_ShouldReturnDomainEntity_WhenValidEntity() {
        // Given
        when(countryMapper.toDbo(domainCountry)).thenReturn(countryDbo);
        when(jpaCountryRepository.save(countryDbo)).thenReturn(Mono.just(countryDbo));
        when(countryMapper.toDomain(countryDbo)).thenReturn(domainCountry);

        // When
        Country result = countryRepositoryAdapter.save(domainCountry)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        verify(countryMapper).toDbo(domainCountry);
        verify(jpaCountryRepository).save(countryDbo);
        verify(countryMapper).toDomain(countryDbo);
    }

    @Test
    void findById_ShouldReturnEntity_WhenEntityExists() {
        // Given
        when(jpaCountryRepository.findById(testId)).thenReturn(Mono.just(countryDbo));
        when(countryMapper.toDomain(countryDbo)).thenReturn(domainCountry);

        // When
        Country result = countryRepositoryAdapter.findById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(domainCountry);
        verify(jpaCountryRepository).findById(testId);
        verify(countryMapper).toDomain(countryDbo);
    }

    @Test
    void findById_ShouldReturnNull_WhenEntityNotFound() {
        // Given
        when(jpaCountryRepository.findById(testId)).thenReturn(Mono.empty());

        // When
        Country result = countryRepositoryAdapter.findById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNull();
        verify(jpaCountryRepository).findById(testId);
    }

    @Test
    void findAll_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        when(jpaCountryRepository.findAll()).thenReturn(Flux.just(countryDbo));
        when(countryMapper.toDomain(countryDbo)).thenReturn(domainCountry);

        // When
        var result = countryRepositoryAdapter.findAll()
            .collectList()
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainCountry);
        verify(jpaCountryRepository).findAll();
    }

    @Test
    void deleteById_ShouldCallRepository_WhenValidId() {
        // Given
        when(jpaCountryRepository.deleteById(testId)).thenReturn(Mono.empty());

        // When
        countryRepositoryAdapter.deleteById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        verify(jpaCountryRepository).deleteById(testId);
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        when(jpaCountryRepository.existsById(testId)).thenReturn(Mono.just(true));

        // When
        Boolean result = countryRepositoryAdapter.existsById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isTrue();
        verify(jpaCountryRepository).existsById(testId);
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityNotExists() {
        // Given
        when(jpaCountryRepository.existsById(testId)).thenReturn(Mono.just(false));

        // When
        Boolean result = countryRepositoryAdapter.existsById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isFalse();
        verify(jpaCountryRepository).existsById(testId);
    }

    @Test
    void findBySearchTerm_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        String searchTerm = "test";
        Integer page = 0;
        Integer size = 10;
        Long offset = 0L;
        Long limit = 10L;
        
        when(jpaCountryRepository.findBySearchTerm(searchTerm, limit, offset))
            .thenReturn(Flux.just(countryDbo));
        when(countryMapper.toDomain(countryDbo)).thenReturn(domainCountry);

        // When
        var result = countryRepositoryAdapter.findBySearchTerm(searchTerm, page, size)
            .collectList()
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainCountry);
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
        
        when(jpaCountryRepository.findByFilters(search, status, dateFrom, dateTo, limit, offset))
            .thenReturn(Flux.just(countryDbo));
        when(countryMapper.toDomain(countryDbo)).thenReturn(domainCountry);

        // When
        var result = countryRepositoryAdapter.findByFilters(search, status, dateFrom, dateTo, page, size)
            .collectList()
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainCountry);
    }
}
