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

    @BeforeEach
    void setUp() {
        domainRegion = Region.builder()
            .build();
        
        regionDbo = RegionDbo.builder()
            .build();
    }

    @Test
    void save_ShouldReturnDomainEntity_WhenValidEntity() {
        // Given
        when(regionMapper.toDbo(domainRegion)).thenReturn(regionDbo);
        when(jpaRegionRepository.save(regionDbo)).thenReturn(regionDbo);
        when(regionMapper.toDomain(regionDbo)).thenReturn(domainRegion);

        // When
        Region result = regionRepositoryAdapter.save(domainRegion);

        // Then
        assertThat(result).isNotNull();
        verify(regionMapper).toDbo(domainRegion);
        verify(jpaRegionRepository).save(regionDbo);
        verify(regionMapper).toDomain(regionDbo);
    }

    @Test
    void save_ShouldThrowException_WhenRepositoryFails() {
        // Given
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(regionMapper.toDbo(domainRegion)).thenReturn(regionDbo);
        when(jpaRegionRepository.save(regionDbo)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> regionRepositoryAdapter.save(domainRegion))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to save Region");
    }

    @Test
    void findById_ShouldReturnOptionalWithEntity_WhenEntityExists() {
        // Given
        String regionId = "test-id";
        when(jpaRegionRepository.findById(regionId)).thenReturn(Optional.of(regionDbo));
        when(regionMapper.toDomain(regionDbo)).thenReturn(domainRegion);

        // When
        Optional<Region> result = regionRepositoryAdapter.findById(regionId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(domainRegion);
        verify(jpaRegionRepository).findById(regionId);
        verify(regionMapper).toDomain(regionDbo);
    }

    @Test
    void findById_ShouldReturnEmptyOptional_WhenEntityNotFound() {
        // Given
        String regionId = "non-existent-id";
        when(jpaRegionRepository.findById(regionId)).thenReturn(Optional.empty());

        // When
        Optional<Region> result = regionRepositoryAdapter.findById(regionId);

        // Then
        assertThat(result).isEmpty();
        verify(jpaRegionRepository).findById(regionId);
    }

    @Test
    void findById_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String regionId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaRegionRepository.findById(regionId)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> regionRepositoryAdapter.findById(regionId))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to find Region by id");
    }

    @Test
    void findAll_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        List<RegionDbo> regionDbos = Collections.singletonList(regionDbo);
        List<Region> regions = Collections.singletonList(domainRegion);
        when(jpaRegionRepository.findAll()).thenReturn(regionDbos);
        when(regionMapper.toDomainList(regionDbos)).thenReturn(regions);

        // When
        List<Region> result = regionRepositoryAdapter.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(regions);
        verify(jpaRegionRepository).findAll();
        verify(regionMapper).toDomainList(regionDbos);
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoEntitiesExist() {
        // Given
        when(jpaRegionRepository.findAll()).thenReturn(Collections.emptyList());
        when(regionMapper.toDomainList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<Region> result = regionRepositoryAdapter.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(jpaRegionRepository).findAll();
        verify(regionMapper).toDomainList(Collections.emptyList());
    }

    @Test
    void findAll_ShouldThrowException_WhenRepositoryFails() {
        // Given
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaRegionRepository.findAll()).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> regionRepositoryAdapter.findAll())
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to find all Regions");
        
        verify(jpaRegionRepository).findAll();
    }

    @Test
    void deleteById_ShouldCallRepository_WhenValidId() {
        // Given
        String regionId = "test-id";

        // When
        regionRepositoryAdapter.deleteById(regionId);

        // Then
        verify(jpaRegionRepository).deleteById(regionId);
    }

    @Test
    void deleteById_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String regionId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        doThrow(repositoryException).when(jpaRegionRepository).deleteById(regionId);

        // When & Then
        assertThatThrownBy(() -> regionRepositoryAdapter.deleteById(regionId))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to delete Region by id");
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        String regionId = "test-id";
        when(jpaRegionRepository.existsById(regionId)).thenReturn(true);

        // When
        boolean result = regionRepositoryAdapter.existsById(regionId);

        // Then
        assertThat(result).isTrue();
        verify(jpaRegionRepository).existsById(regionId);
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityNotExists() {
        // Given
        String regionId = "non-existent-id";
        when(jpaRegionRepository.existsById(regionId)).thenReturn(false);

        // When
        boolean result = regionRepositoryAdapter.existsById(regionId);

        // Then
        assertThat(result).isFalse();
        verify(jpaRegionRepository).existsById(regionId);
    }

    @Test
    void existsById_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String regionId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaRegionRepository.existsById(regionId)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> regionRepositoryAdapter.existsById(regionId))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to check if Region exists by id");
    }

    @Test
    void findBySearchTerm_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        String searchTerm = "test";
        Integer page = 1;
        Integer size = 10;
        
        when(jpaRegionRepository.findBySearchTerm(anyString(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(regionDbo)));
        when(regionMapper.toDomain(regionDbo)).thenReturn(domainRegion);

        // When
        List<Region> result = regionRepositoryAdapter.findBySearchTerm(searchTerm, page, size);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainRegion);
    }

    @Test
    void findBySearchTerm_ShouldReturnListOfEntities_WhenSearchIsEmpty() {
        // Given
        String searchTerm = "";
        Integer page = 1;
        Integer size = 10;
        
        when(jpaRegionRepository.findAll()).thenReturn(Collections.singletonList(regionDbo));
        when(regionMapper.toDomainList(Collections.singletonList(regionDbo)))
            .thenReturn(Collections.singletonList(domainRegion));

        // When
        List<Region> result = regionRepositoryAdapter.findBySearchTerm(searchTerm, page, size);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainRegion);
    }

    @Test
    void findBySearchTerm_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String searchTerm = "test";
        Integer page = 1;
        Integer size = 10;
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaRegionRepository.findBySearchTerm(anyString(), any(Pageable.class)))
            .thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> regionRepositoryAdapter.findBySearchTerm(searchTerm, page, size))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to search Regions");
    }

    @Test
    void findBySearchTerm_ShouldThrowException_WhenRepositoryFailsOnFindAll() {
        // Given
        String searchTerm = null;
        Integer page = 1;
        Integer size = 10;
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaRegionRepository.findAll()).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> regionRepositoryAdapter.findBySearchTerm(searchTerm, page, size))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to search Regions");
    }

    @Test
    void findBySearchTermPaged_ShouldReturnPageOfEntities_WhenEntitiesExist() {
        // Given
        String searchTerm = "test";
        Pageable pageable = PageRequest.of(0, 10);
        List<RegionDbo> regionDbos = Collections.singletonList(regionDbo);
        Page<RegionDbo> dboPage = new PageImpl<>(regionDbos, pageable, 1);
        
        when(jpaRegionRepository.findBySearchTerm(searchTerm, pageable)).thenReturn(dboPage);
        when(regionMapper.toDomain(regionDbo)).thenReturn(domainRegion);

        // When
        Page<Region> result = regionRepositoryAdapter.findBySearchTermPaged(searchTerm, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(domainRegion);
        verify(jpaRegionRepository).findBySearchTerm(searchTerm, pageable);
        verify(regionMapper).toDomain(regionDbo);
    }

    @Test
    void findBySearchTermPaged_ShouldReturnEmptyPage_WhenNoEntitiesExist() {
        // Given
        String searchTerm = "nonexistent";
        Pageable pageable = PageRequest.of(0, 10);
        Page<RegionDbo> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        
        when(jpaRegionRepository.findBySearchTerm(searchTerm, pageable)).thenReturn(emptyPage);

        // When
        Page<Region> result = regionRepositoryAdapter.findBySearchTermPaged(searchTerm, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(jpaRegionRepository).findBySearchTerm(searchTerm, pageable);
    }

    @Test
    void findAllPaged_ShouldReturnPageOfEntities_WhenEntitiesExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<RegionDbo> regionDbos = Collections.singletonList(regionDbo);
        Page<RegionDbo> dboPage = new PageImpl<>(regionDbos, pageable, 1);
        
        when(jpaRegionRepository.findAllPaged(pageable)).thenReturn(dboPage);
        when(regionMapper.toDomain(regionDbo)).thenReturn(domainRegion);

        // When
        Page<Region> result = regionRepositoryAdapter.findAllPaged(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(domainRegion);
        verify(jpaRegionRepository).findAllPaged(pageable);
        verify(regionMapper).toDomain(regionDbo);
    }

    @Test
    void findAllPaged_ShouldReturnEmptyPage_WhenNoEntitiesExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<RegionDbo> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        
        when(jpaRegionRepository.findAllPaged(pageable)).thenReturn(emptyPage);

        // When
        Page<Region> result = regionRepositoryAdapter.findAllPaged(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(jpaRegionRepository).findAllPaged(pageable);
    }

    @Test
    void findBySearchTermPaged_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String searchTerm = "test";
        Pageable pageable = PageRequest.of(0, 10);
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaRegionRepository.findBySearchTerm(searchTerm, pageable)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> regionRepositoryAdapter.findBySearchTermPaged(searchTerm, pageable))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }

    @Test
    void findAllPaged_ShouldThrowException_WhenRepositoryFails() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaRegionRepository.findAllPaged(pageable)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> regionRepositoryAdapter.findAllPaged(pageable))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }
}