package com.example.movieservice.infrastructure.adapters.output.persistence.adapter;

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

    @BeforeEach
    void setUp() {
        domainRental = Rental.builder()
            .build();
        
        rentalDbo = RentalDbo.builder()
            .build();
    }

    @Test
    void save_ShouldReturnDomainEntity_WhenValidEntity() {
        // Given
        when(rentalMapper.toDbo(domainRental)).thenReturn(rentalDbo);
        when(jpaRentalRepository.save(rentalDbo)).thenReturn(rentalDbo);
        when(rentalMapper.toDomain(rentalDbo)).thenReturn(domainRental);

        // When
        Rental result = rentalRepositoryAdapter.save(domainRental);

        // Then
        assertThat(result).isNotNull();
        verify(rentalMapper).toDbo(domainRental);
        verify(jpaRentalRepository).save(rentalDbo);
        verify(rentalMapper).toDomain(rentalDbo);
    }

    @Test
    void save_ShouldThrowException_WhenRepositoryFails() {
        // Given
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(rentalMapper.toDbo(domainRental)).thenReturn(rentalDbo);
        when(jpaRentalRepository.save(rentalDbo)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> rentalRepositoryAdapter.save(domainRental))
            .isInstanceOf(com.example.movieservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to save Rental");
    }

    @Test
    void findById_ShouldReturnOptionalWithEntity_WhenEntityExists() {
        // Given
        String rentalId = "test-id";
        when(jpaRentalRepository.findById(rentalId)).thenReturn(Optional.of(rentalDbo));
        when(rentalMapper.toDomain(rentalDbo)).thenReturn(domainRental);

        // When
        Optional<Rental> result = rentalRepositoryAdapter.findById(rentalId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(domainRental);
        verify(jpaRentalRepository).findById(rentalId);
        verify(rentalMapper).toDomain(rentalDbo);
    }

    @Test
    void findById_ShouldReturnEmptyOptional_WhenEntityNotFound() {
        // Given
        String rentalId = "non-existent-id";
        when(jpaRentalRepository.findById(rentalId)).thenReturn(Optional.empty());

        // When
        Optional<Rental> result = rentalRepositoryAdapter.findById(rentalId);

        // Then
        assertThat(result).isEmpty();
        verify(jpaRentalRepository).findById(rentalId);
    }

    @Test
    void findById_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String rentalId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaRentalRepository.findById(rentalId)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> rentalRepositoryAdapter.findById(rentalId))
            .isInstanceOf(com.example.movieservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to find Rental by id");
    }

    @Test
    void findAll_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        List<RentalDbo> rentalDbos = Collections.singletonList(rentalDbo);
        List<Rental> rentals = Collections.singletonList(domainRental);
        when(jpaRentalRepository.findAll()).thenReturn(rentalDbos);
        when(rentalMapper.toDomainList(rentalDbos)).thenReturn(rentals);

        // When
        List<Rental> result = rentalRepositoryAdapter.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(rentals);
        verify(jpaRentalRepository).findAll();
        verify(rentalMapper).toDomainList(rentalDbos);
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoEntitiesExist() {
        // Given
        when(jpaRentalRepository.findAll()).thenReturn(Collections.emptyList());
        when(rentalMapper.toDomainList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<Rental> result = rentalRepositoryAdapter.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(jpaRentalRepository).findAll();
        verify(rentalMapper).toDomainList(Collections.emptyList());
    }

    @Test
    void findAll_ShouldThrowException_WhenRepositoryFails() {
        // Given
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaRentalRepository.findAll()).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> rentalRepositoryAdapter.findAll())
            .isInstanceOf(com.example.movieservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to find all Rentals");
        
        verify(jpaRentalRepository).findAll();
    }

    @Test
    void deleteById_ShouldCallRepository_WhenValidId() {
        // Given
        String rentalId = "test-id";

        // When
        rentalRepositoryAdapter.deleteById(rentalId);

        // Then
        verify(jpaRentalRepository).deleteById(rentalId);
    }

    @Test
    void deleteById_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String rentalId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        doThrow(repositoryException).when(jpaRentalRepository).deleteById(rentalId);

        // When & Then
        assertThatThrownBy(() -> rentalRepositoryAdapter.deleteById(rentalId))
            .isInstanceOf(com.example.movieservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to delete Rental by id");
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        String rentalId = "test-id";
        when(jpaRentalRepository.existsById(rentalId)).thenReturn(true);

        // When
        boolean result = rentalRepositoryAdapter.existsById(rentalId);

        // Then
        assertThat(result).isTrue();
        verify(jpaRentalRepository).existsById(rentalId);
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityNotExists() {
        // Given
        String rentalId = "non-existent-id";
        when(jpaRentalRepository.existsById(rentalId)).thenReturn(false);

        // When
        boolean result = rentalRepositoryAdapter.existsById(rentalId);

        // Then
        assertThat(result).isFalse();
        verify(jpaRentalRepository).existsById(rentalId);
    }

    @Test
    void existsById_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String rentalId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaRentalRepository.existsById(rentalId)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> rentalRepositoryAdapter.existsById(rentalId))
            .isInstanceOf(com.example.movieservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to check if Rental exists by id");
    }

    @Test
    void findBySearchTerm_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        String searchTerm = "test";
        Integer page = 1;
        Integer size = 10;
        
        when(jpaRentalRepository.findBySearchTerm(anyString(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(rentalDbo)));
        when(rentalMapper.toDomain(rentalDbo)).thenReturn(domainRental);

        // When
        List<Rental> result = rentalRepositoryAdapter.findBySearchTerm(searchTerm, page, size);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainRental);
    }

    @Test
    void findBySearchTerm_ShouldReturnListOfEntities_WhenSearchIsEmpty() {
        // Given
        String searchTerm = "";
        Integer page = 1;
        Integer size = 10;
        
        when(jpaRentalRepository.findAll()).thenReturn(Collections.singletonList(rentalDbo));
        when(rentalMapper.toDomainList(Collections.singletonList(rentalDbo)))
            .thenReturn(Collections.singletonList(domainRental));

        // When
        List<Rental> result = rentalRepositoryAdapter.findBySearchTerm(searchTerm, page, size);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainRental);
    }

    @Test
    void findBySearchTerm_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String searchTerm = "test";
        Integer page = 1;
        Integer size = 10;
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaRentalRepository.findBySearchTerm(anyString(), any(Pageable.class)))
            .thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> rentalRepositoryAdapter.findBySearchTerm(searchTerm, page, size))
            .isInstanceOf(com.example.movieservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to search Rentals");
    }

    @Test
    void findBySearchTerm_ShouldThrowException_WhenRepositoryFailsOnFindAll() {
        // Given
        String searchTerm = null;
        Integer page = 1;
        Integer size = 10;
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaRentalRepository.findAll()).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> rentalRepositoryAdapter.findBySearchTerm(searchTerm, page, size))
            .isInstanceOf(com.example.movieservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to search Rentals");
    }

    @Test
    void findBySearchTermPaged_ShouldReturnPageOfEntities_WhenEntitiesExist() {
        // Given
        String searchTerm = "test";
        Pageable pageable = PageRequest.of(0, 10);
        List<RentalDbo> rentalDbos = Collections.singletonList(rentalDbo);
        Page<RentalDbo> dboPage = new PageImpl<>(rentalDbos, pageable, 1);
        
        when(jpaRentalRepository.findBySearchTerm(searchTerm, pageable)).thenReturn(dboPage);
        when(rentalMapper.toDomain(rentalDbo)).thenReturn(domainRental);

        // When
        Page<Rental> result = rentalRepositoryAdapter.findBySearchTermPaged(searchTerm, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(domainRental);
        verify(jpaRentalRepository).findBySearchTerm(searchTerm, pageable);
        verify(rentalMapper).toDomain(rentalDbo);
    }

    @Test
    void findBySearchTermPaged_ShouldReturnEmptyPage_WhenNoEntitiesExist() {
        // Given
        String searchTerm = "nonexistent";
        Pageable pageable = PageRequest.of(0, 10);
        Page<RentalDbo> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        
        when(jpaRentalRepository.findBySearchTerm(searchTerm, pageable)).thenReturn(emptyPage);

        // When
        Page<Rental> result = rentalRepositoryAdapter.findBySearchTermPaged(searchTerm, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(jpaRentalRepository).findBySearchTerm(searchTerm, pageable);
    }

    @Test
    void findAllPaged_ShouldReturnPageOfEntities_WhenEntitiesExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<RentalDbo> rentalDbos = Collections.singletonList(rentalDbo);
        Page<RentalDbo> dboPage = new PageImpl<>(rentalDbos, pageable, 1);
        
        when(jpaRentalRepository.findAllPaged(pageable)).thenReturn(dboPage);
        when(rentalMapper.toDomain(rentalDbo)).thenReturn(domainRental);

        // When
        Page<Rental> result = rentalRepositoryAdapter.findAllPaged(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(domainRental);
        verify(jpaRentalRepository).findAllPaged(pageable);
        verify(rentalMapper).toDomain(rentalDbo);
    }

    @Test
    void findAllPaged_ShouldReturnEmptyPage_WhenNoEntitiesExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<RentalDbo> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        
        when(jpaRentalRepository.findAllPaged(pageable)).thenReturn(emptyPage);

        // When
        Page<Rental> result = rentalRepositoryAdapter.findAllPaged(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(jpaRentalRepository).findAllPaged(pageable);
    }

    @Test
    void findBySearchTermPaged_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String searchTerm = "test";
        Pageable pageable = PageRequest.of(0, 10);
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaRentalRepository.findBySearchTerm(searchTerm, pageable)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> rentalRepositoryAdapter.findBySearchTermPaged(searchTerm, pageable))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }

    @Test
    void findAllPaged_ShouldThrowException_WhenRepositoryFails() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaRentalRepository.findAllPaged(pageable)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> rentalRepositoryAdapter.findAllPaged(pageable))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }
}