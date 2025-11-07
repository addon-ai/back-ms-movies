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

    @BeforeEach
    void setUp() {
        domainLocation = Location.builder()
            .build();
        
        locationDbo = LocationDbo.builder()
            .build();
    }

    @Test
    void save_ShouldReturnDomainEntity_WhenValidEntity() {
        // Given
        when(locationMapper.toDbo(domainLocation)).thenReturn(locationDbo);
        when(jpaLocationRepository.save(locationDbo)).thenReturn(locationDbo);
        when(locationMapper.toDomain(locationDbo)).thenReturn(domainLocation);

        // When
        Location result = locationRepositoryAdapter.save(domainLocation);

        // Then
        assertThat(result).isNotNull();
        verify(locationMapper).toDbo(domainLocation);
        verify(jpaLocationRepository).save(locationDbo);
        verify(locationMapper).toDomain(locationDbo);
    }

    @Test
    void save_ShouldThrowException_WhenRepositoryFails() {
        // Given
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(locationMapper.toDbo(domainLocation)).thenReturn(locationDbo);
        when(jpaLocationRepository.save(locationDbo)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> locationRepositoryAdapter.save(domainLocation))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to save Location");
    }

    @Test
    void findById_ShouldReturnOptionalWithEntity_WhenEntityExists() {
        // Given
        String locationId = "test-id";
        when(jpaLocationRepository.findById(locationId)).thenReturn(Optional.of(locationDbo));
        when(locationMapper.toDomain(locationDbo)).thenReturn(domainLocation);

        // When
        Optional<Location> result = locationRepositoryAdapter.findById(locationId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(domainLocation);
        verify(jpaLocationRepository).findById(locationId);
        verify(locationMapper).toDomain(locationDbo);
    }

    @Test
    void findById_ShouldReturnEmptyOptional_WhenEntityNotFound() {
        // Given
        String locationId = "non-existent-id";
        when(jpaLocationRepository.findById(locationId)).thenReturn(Optional.empty());

        // When
        Optional<Location> result = locationRepositoryAdapter.findById(locationId);

        // Then
        assertThat(result).isEmpty();
        verify(jpaLocationRepository).findById(locationId);
    }

    @Test
    void findById_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String locationId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaLocationRepository.findById(locationId)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> locationRepositoryAdapter.findById(locationId))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to find Location by id");
    }

    @Test
    void findAll_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        List<LocationDbo> locationDbos = Collections.singletonList(locationDbo);
        List<Location> locations = Collections.singletonList(domainLocation);
        when(jpaLocationRepository.findAll()).thenReturn(locationDbos);
        when(locationMapper.toDomainList(locationDbos)).thenReturn(locations);

        // When
        List<Location> result = locationRepositoryAdapter.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(locations);
        verify(jpaLocationRepository).findAll();
        verify(locationMapper).toDomainList(locationDbos);
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoEntitiesExist() {
        // Given
        when(jpaLocationRepository.findAll()).thenReturn(Collections.emptyList());
        when(locationMapper.toDomainList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<Location> result = locationRepositoryAdapter.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(jpaLocationRepository).findAll();
        verify(locationMapper).toDomainList(Collections.emptyList());
    }

    @Test
    void findAll_ShouldThrowException_WhenRepositoryFails() {
        // Given
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaLocationRepository.findAll()).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> locationRepositoryAdapter.findAll())
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to find all Locations");
        
        verify(jpaLocationRepository).findAll();
    }

    @Test
    void deleteById_ShouldCallRepository_WhenValidId() {
        // Given
        String locationId = "test-id";

        // When
        locationRepositoryAdapter.deleteById(locationId);

        // Then
        verify(jpaLocationRepository).deleteById(locationId);
    }

    @Test
    void deleteById_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String locationId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        doThrow(repositoryException).when(jpaLocationRepository).deleteById(locationId);

        // When & Then
        assertThatThrownBy(() -> locationRepositoryAdapter.deleteById(locationId))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to delete Location by id");
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        String locationId = "test-id";
        when(jpaLocationRepository.existsById(locationId)).thenReturn(true);

        // When
        boolean result = locationRepositoryAdapter.existsById(locationId);

        // Then
        assertThat(result).isTrue();
        verify(jpaLocationRepository).existsById(locationId);
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityNotExists() {
        // Given
        String locationId = "non-existent-id";
        when(jpaLocationRepository.existsById(locationId)).thenReturn(false);

        // When
        boolean result = locationRepositoryAdapter.existsById(locationId);

        // Then
        assertThat(result).isFalse();
        verify(jpaLocationRepository).existsById(locationId);
    }

    @Test
    void existsById_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String locationId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaLocationRepository.existsById(locationId)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> locationRepositoryAdapter.existsById(locationId))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to check if Location exists by id");
    }

    @Test
    void findBySearchTerm_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        String searchTerm = "test";
        Integer page = 1;
        Integer size = 10;
        
        when(jpaLocationRepository.findBySearchTerm(anyString(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(locationDbo)));
        when(locationMapper.toDomain(locationDbo)).thenReturn(domainLocation);

        // When
        List<Location> result = locationRepositoryAdapter.findBySearchTerm(searchTerm, page, size);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainLocation);
    }

    @Test
    void findBySearchTerm_ShouldReturnListOfEntities_WhenSearchIsEmpty() {
        // Given
        String searchTerm = "";
        Integer page = 1;
        Integer size = 10;
        
        when(jpaLocationRepository.findAll()).thenReturn(Collections.singletonList(locationDbo));
        when(locationMapper.toDomainList(Collections.singletonList(locationDbo)))
            .thenReturn(Collections.singletonList(domainLocation));

        // When
        List<Location> result = locationRepositoryAdapter.findBySearchTerm(searchTerm, page, size);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainLocation);
    }

    @Test
    void findBySearchTerm_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String searchTerm = "test";
        Integer page = 1;
        Integer size = 10;
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaLocationRepository.findBySearchTerm(anyString(), any(Pageable.class)))
            .thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> locationRepositoryAdapter.findBySearchTerm(searchTerm, page, size))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to search Locations");
    }

    @Test
    void findBySearchTerm_ShouldThrowException_WhenRepositoryFailsOnFindAll() {
        // Given
        String searchTerm = null;
        Integer page = 1;
        Integer size = 10;
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaLocationRepository.findAll()).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> locationRepositoryAdapter.findBySearchTerm(searchTerm, page, size))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to search Locations");
    }

    @Test
    void findBySearchTermPaged_ShouldReturnPageOfEntities_WhenEntitiesExist() {
        // Given
        String searchTerm = "test";
        Pageable pageable = PageRequest.of(0, 10);
        List<LocationDbo> locationDbos = Collections.singletonList(locationDbo);
        Page<LocationDbo> dboPage = new PageImpl<>(locationDbos, pageable, 1);
        
        when(jpaLocationRepository.findBySearchTerm(searchTerm, pageable)).thenReturn(dboPage);
        when(locationMapper.toDomain(locationDbo)).thenReturn(domainLocation);

        // When
        Page<Location> result = locationRepositoryAdapter.findBySearchTermPaged(searchTerm, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(domainLocation);
        verify(jpaLocationRepository).findBySearchTerm(searchTerm, pageable);
        verify(locationMapper).toDomain(locationDbo);
    }

    @Test
    void findBySearchTermPaged_ShouldReturnEmptyPage_WhenNoEntitiesExist() {
        // Given
        String searchTerm = "nonexistent";
        Pageable pageable = PageRequest.of(0, 10);
        Page<LocationDbo> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        
        when(jpaLocationRepository.findBySearchTerm(searchTerm, pageable)).thenReturn(emptyPage);

        // When
        Page<Location> result = locationRepositoryAdapter.findBySearchTermPaged(searchTerm, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(jpaLocationRepository).findBySearchTerm(searchTerm, pageable);
    }

    @Test
    void findAllPaged_ShouldReturnPageOfEntities_WhenEntitiesExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<LocationDbo> locationDbos = Collections.singletonList(locationDbo);
        Page<LocationDbo> dboPage = new PageImpl<>(locationDbos, pageable, 1);
        
        when(jpaLocationRepository.findAllPaged(pageable)).thenReturn(dboPage);
        when(locationMapper.toDomain(locationDbo)).thenReturn(domainLocation);

        // When
        Page<Location> result = locationRepositoryAdapter.findAllPaged(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(domainLocation);
        verify(jpaLocationRepository).findAllPaged(pageable);
        verify(locationMapper).toDomain(locationDbo);
    }

    @Test
    void findAllPaged_ShouldReturnEmptyPage_WhenNoEntitiesExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<LocationDbo> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        
        when(jpaLocationRepository.findAllPaged(pageable)).thenReturn(emptyPage);

        // When
        Page<Location> result = locationRepositoryAdapter.findAllPaged(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(jpaLocationRepository).findAllPaged(pageable);
    }

    @Test
    void findBySearchTermPaged_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String searchTerm = "test";
        Pageable pageable = PageRequest.of(0, 10);
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaLocationRepository.findBySearchTerm(searchTerm, pageable)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> locationRepositoryAdapter.findBySearchTermPaged(searchTerm, pageable))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }

    @Test
    void findAllPaged_ShouldThrowException_WhenRepositoryFails() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaLocationRepository.findAllPaged(pageable)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> locationRepositoryAdapter.findAllPaged(pageable))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }
}