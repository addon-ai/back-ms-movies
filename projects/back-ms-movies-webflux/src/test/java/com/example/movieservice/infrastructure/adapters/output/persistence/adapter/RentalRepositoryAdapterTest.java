package com.example.movieservice.infrastructure.adapters.output.persistence.adapter;

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

import com.example.movieservice.application.mapper.RentalMapper;
import com.example.movieservice.domain.model.Rental;
import com.example.movieservice.infrastructure.adapters.output.persistence.entity.RentalDbo;
import com.example.movieservice.infrastructure.adapters.output.persistence.repository.JpaRentalRepository;

@ExtendWith(MockitoExtension.class)
class RentalRepositoryAdapterTest {

    @Mock
    private JpaRentalRepository jpaRentalRepository;

    @Mock
    private RentalMapper rentalMapper;

    @InjectMocks
    private RentalRepositoryAdapter rentalRepositoryAdapter;

    private Rental domainRental;
    private RentalDbo rentalDbo;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        
        domainRental = Rental.builder()
            .rentalId(testId.toString())
            .build();
        
        rentalDbo = RentalDbo.builder()
            .id(testId)
            .build();
    }

    @Test
    void save_ShouldReturnDomainEntity_WhenValidEntity() {
        // Given
        when(rentalMapper.toDbo(domainRental)).thenReturn(rentalDbo);
        when(jpaRentalRepository.save(rentalDbo)).thenReturn(Mono.just(rentalDbo));
        when(rentalMapper.toDomain(rentalDbo)).thenReturn(domainRental);

        // When
        Rental result = rentalRepositoryAdapter.save(domainRental)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        verify(rentalMapper).toDbo(domainRental);
        verify(jpaRentalRepository).save(rentalDbo);
        verify(rentalMapper).toDomain(rentalDbo);
    }

    @Test
    void findById_ShouldReturnEntity_WhenEntityExists() {
        // Given
        when(jpaRentalRepository.findById(testId)).thenReturn(Mono.just(rentalDbo));
        when(rentalMapper.toDomain(rentalDbo)).thenReturn(domainRental);

        // When
        Rental result = rentalRepositoryAdapter.findById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(domainRental);
        verify(jpaRentalRepository).findById(testId);
        verify(rentalMapper).toDomain(rentalDbo);
    }

    @Test
    void findById_ShouldReturnNull_WhenEntityNotFound() {
        // Given
        when(jpaRentalRepository.findById(testId)).thenReturn(Mono.empty());

        // When
        Rental result = rentalRepositoryAdapter.findById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNull();
        verify(jpaRentalRepository).findById(testId);
    }

    @Test
    void findAll_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        when(jpaRentalRepository.findAll()).thenReturn(Flux.just(rentalDbo));
        when(rentalMapper.toDomain(rentalDbo)).thenReturn(domainRental);

        // When
        var result = rentalRepositoryAdapter.findAll()
            .collectList()
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainRental);
        verify(jpaRentalRepository).findAll();
    }

    @Test
    void deleteById_ShouldCallRepository_WhenValidId() {
        // Given
        when(jpaRentalRepository.deleteById(testId)).thenReturn(Mono.empty());

        // When
        rentalRepositoryAdapter.deleteById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        verify(jpaRentalRepository).deleteById(testId);
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        when(jpaRentalRepository.existsById(testId)).thenReturn(Mono.just(true));

        // When
        Boolean result = rentalRepositoryAdapter.existsById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isTrue();
        verify(jpaRentalRepository).existsById(testId);
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityNotExists() {
        // Given
        when(jpaRentalRepository.existsById(testId)).thenReturn(Mono.just(false));

        // When
        Boolean result = rentalRepositoryAdapter.existsById(testId.toString())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isFalse();
        verify(jpaRentalRepository).existsById(testId);
    }

    @Test
    void findBySearchTerm_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        String searchTerm = "test";
        Integer page = 0;
        Integer size = 10;
        Long offset = 0L;
        Long limit = 10L;
        
        when(jpaRentalRepository.findBySearchTerm(searchTerm, limit, offset))
            .thenReturn(Flux.just(rentalDbo));
        when(rentalMapper.toDomain(rentalDbo)).thenReturn(domainRental);

        // When
        var result = rentalRepositoryAdapter.findBySearchTerm(searchTerm, page, size)
            .collectList()
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainRental);
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
        
        when(jpaRentalRepository.findByFilters(search, status, dateFrom, dateTo, limit, offset))
            .thenReturn(Flux.just(rentalDbo));
        when(rentalMapper.toDomain(rentalDbo)).thenReturn(domainRental);

        // When
        var result = rentalRepositoryAdapter.findByFilters(search, status, dateFrom, dateTo, page, size)
            .collectList()
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainRental);
    }
}
