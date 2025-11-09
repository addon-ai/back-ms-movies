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

    @BeforeEach
    void setUp() {
        domainNeighborhood = Neighborhood.builder()
            .build();
        
        neighborhoodDbo = NeighborhoodDbo.builder()
            .build();
    }

    @Test
    void save_ShouldReturnDomainEntity_WhenValidEntity() {
        // Given
        when(neighborhoodMapper.toDbo(domainNeighborhood)).thenReturn(neighborhoodDbo);
        when(jpaNeighborhoodRepository.save(neighborhoodDbo)).thenReturn(neighborhoodDbo);
        when(neighborhoodMapper.toDomain(neighborhoodDbo)).thenReturn(domainNeighborhood);

        // When
        Neighborhood result = neighborhoodRepositoryAdapter.save(domainNeighborhood);

        // Then
        assertThat(result).isNotNull();
        verify(neighborhoodMapper).toDbo(domainNeighborhood);
        verify(jpaNeighborhoodRepository).save(neighborhoodDbo);
        verify(neighborhoodMapper).toDomain(neighborhoodDbo);
    }

    @Test
    void save_ShouldThrowException_WhenRepositoryFails() {
        // Given
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(neighborhoodMapper.toDbo(domainNeighborhood)).thenReturn(neighborhoodDbo);
        when(jpaNeighborhoodRepository.save(neighborhoodDbo)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> neighborhoodRepositoryAdapter.save(domainNeighborhood))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to save Neighborhood");
    }

    @Test
    void findById_ShouldReturnOptionalWithEntity_WhenEntityExists() {
        // Given
        String neighborhoodId = "test-id";
        when(jpaNeighborhoodRepository.findById(neighborhoodId)).thenReturn(Optional.of(neighborhoodDbo));
        when(neighborhoodMapper.toDomain(neighborhoodDbo)).thenReturn(domainNeighborhood);

        // When
        Optional<Neighborhood> result = neighborhoodRepositoryAdapter.findById(neighborhoodId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(domainNeighborhood);
        verify(jpaNeighborhoodRepository).findById(neighborhoodId);
        verify(neighborhoodMapper).toDomain(neighborhoodDbo);
    }

    @Test
    void findById_ShouldReturnEmptyOptional_WhenEntityNotFound() {
        // Given
        String neighborhoodId = "non-existent-id";
        when(jpaNeighborhoodRepository.findById(neighborhoodId)).thenReturn(Optional.empty());

        // When
        Optional<Neighborhood> result = neighborhoodRepositoryAdapter.findById(neighborhoodId);

        // Then
        assertThat(result).isEmpty();
        verify(jpaNeighborhoodRepository).findById(neighborhoodId);
    }

    @Test
    void findById_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String neighborhoodId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaNeighborhoodRepository.findById(neighborhoodId)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> neighborhoodRepositoryAdapter.findById(neighborhoodId))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to find Neighborhood by id");
    }

    @Test
    void findAll_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        List<NeighborhoodDbo> neighborhoodDbos = Collections.singletonList(neighborhoodDbo);
        List<Neighborhood> neighborhoods = Collections.singletonList(domainNeighborhood);
        when(jpaNeighborhoodRepository.findAll()).thenReturn(neighborhoodDbos);
        when(neighborhoodMapper.toDomainList(neighborhoodDbos)).thenReturn(neighborhoods);

        // When
        List<Neighborhood> result = neighborhoodRepositoryAdapter.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(neighborhoods);
        verify(jpaNeighborhoodRepository).findAll();
        verify(neighborhoodMapper).toDomainList(neighborhoodDbos);
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoEntitiesExist() {
        // Given
        when(jpaNeighborhoodRepository.findAll()).thenReturn(Collections.emptyList());
        when(neighborhoodMapper.toDomainList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<Neighborhood> result = neighborhoodRepositoryAdapter.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(jpaNeighborhoodRepository).findAll();
        verify(neighborhoodMapper).toDomainList(Collections.emptyList());
    }

    @Test
    void findAll_ShouldThrowException_WhenRepositoryFails() {
        // Given
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaNeighborhoodRepository.findAll()).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> neighborhoodRepositoryAdapter.findAll())
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to find all Neighborhoods");
        
        verify(jpaNeighborhoodRepository).findAll();
    }

    @Test
    void deleteById_ShouldCallRepository_WhenValidId() {
        // Given
        String neighborhoodId = "test-id";

        // When
        neighborhoodRepositoryAdapter.deleteById(neighborhoodId);

        // Then
        verify(jpaNeighborhoodRepository).deleteById(neighborhoodId);
    }

    @Test
    void deleteById_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String neighborhoodId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        doThrow(repositoryException).when(jpaNeighborhoodRepository).deleteById(neighborhoodId);

        // When & Then
        assertThatThrownBy(() -> neighborhoodRepositoryAdapter.deleteById(neighborhoodId))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to delete Neighborhood by id");
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        String neighborhoodId = "test-id";
        when(jpaNeighborhoodRepository.existsById(neighborhoodId)).thenReturn(true);

        // When
        boolean result = neighborhoodRepositoryAdapter.existsById(neighborhoodId);

        // Then
        assertThat(result).isTrue();
        verify(jpaNeighborhoodRepository).existsById(neighborhoodId);
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityNotExists() {
        // Given
        String neighborhoodId = "non-existent-id";
        when(jpaNeighborhoodRepository.existsById(neighborhoodId)).thenReturn(false);

        // When
        boolean result = neighborhoodRepositoryAdapter.existsById(neighborhoodId);

        // Then
        assertThat(result).isFalse();
        verify(jpaNeighborhoodRepository).existsById(neighborhoodId);
    }

    @Test
    void existsById_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String neighborhoodId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaNeighborhoodRepository.existsById(neighborhoodId)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> neighborhoodRepositoryAdapter.existsById(neighborhoodId))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to check if Neighborhood exists by id");
    }

    @Test
    void findBySearchTerm_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        String searchTerm = "test";
        Integer page = 1;
        Integer size = 10;
        
        when(jpaNeighborhoodRepository.findBySearchTerm(anyString(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(neighborhoodDbo)));
        when(neighborhoodMapper.toDomain(neighborhoodDbo)).thenReturn(domainNeighborhood);

        // When
        List<Neighborhood> result = neighborhoodRepositoryAdapter.findBySearchTerm(searchTerm, page, size);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainNeighborhood);
    }

    @Test
    void findBySearchTerm_ShouldReturnListOfEntities_WhenSearchIsEmpty() {
        // Given
        String searchTerm = "";
        Integer page = 1;
        Integer size = 10;
        
        when(jpaNeighborhoodRepository.findAll()).thenReturn(Collections.singletonList(neighborhoodDbo));
        when(neighborhoodMapper.toDomainList(Collections.singletonList(neighborhoodDbo)))
            .thenReturn(Collections.singletonList(domainNeighborhood));

        // When
        List<Neighborhood> result = neighborhoodRepositoryAdapter.findBySearchTerm(searchTerm, page, size);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainNeighborhood);
    }

    @Test
    void findBySearchTerm_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String searchTerm = "test";
        Integer page = 1;
        Integer size = 10;
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaNeighborhoodRepository.findBySearchTerm(anyString(), any(Pageable.class)))
            .thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> neighborhoodRepositoryAdapter.findBySearchTerm(searchTerm, page, size))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to search Neighborhoods");
    }

    @Test
    void findBySearchTerm_ShouldThrowException_WhenRepositoryFailsOnFindAll() {
        // Given
        String searchTerm = null;
        Integer page = 1;
        Integer size = 10;
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaNeighborhoodRepository.findAll()).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> neighborhoodRepositoryAdapter.findBySearchTerm(searchTerm, page, size))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to search Neighborhoods");
    }

    @Test
    void findBySearchTermPaged_ShouldReturnPageOfEntities_WhenEntitiesExist() {
        // Given
        String searchTerm = "test";
        Pageable pageable = PageRequest.of(0, 10);
        List<NeighborhoodDbo> neighborhoodDbos = Collections.singletonList(neighborhoodDbo);
        Page<NeighborhoodDbo> dboPage = new PageImpl<>(neighborhoodDbos, pageable, 1);
        
        when(jpaNeighborhoodRepository.findBySearchTerm(searchTerm, pageable)).thenReturn(dboPage);
        when(neighborhoodMapper.toDomain(neighborhoodDbo)).thenReturn(domainNeighborhood);

        // When
        Page<Neighborhood> result = neighborhoodRepositoryAdapter.findBySearchTermPaged(searchTerm, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(domainNeighborhood);
        verify(jpaNeighborhoodRepository).findBySearchTerm(searchTerm, pageable);
        verify(neighborhoodMapper).toDomain(neighborhoodDbo);
    }

    @Test
    void findBySearchTermPaged_ShouldReturnEmptyPage_WhenNoEntitiesExist() {
        // Given
        String searchTerm = "nonexistent";
        Pageable pageable = PageRequest.of(0, 10);
        Page<NeighborhoodDbo> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        
        when(jpaNeighborhoodRepository.findBySearchTerm(searchTerm, pageable)).thenReturn(emptyPage);

        // When
        Page<Neighborhood> result = neighborhoodRepositoryAdapter.findBySearchTermPaged(searchTerm, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(jpaNeighborhoodRepository).findBySearchTerm(searchTerm, pageable);
    }

    @Test
    void findAllPaged_ShouldReturnPageOfEntities_WhenEntitiesExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<NeighborhoodDbo> neighborhoodDbos = Collections.singletonList(neighborhoodDbo);
        Page<NeighborhoodDbo> dboPage = new PageImpl<>(neighborhoodDbos, pageable, 1);
        
        when(jpaNeighborhoodRepository.findAllPaged(pageable)).thenReturn(dboPage);
        when(neighborhoodMapper.toDomain(neighborhoodDbo)).thenReturn(domainNeighborhood);

        // When
        Page<Neighborhood> result = neighborhoodRepositoryAdapter.findAllPaged(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(domainNeighborhood);
        verify(jpaNeighborhoodRepository).findAllPaged(pageable);
        verify(neighborhoodMapper).toDomain(neighborhoodDbo);
    }

    @Test
    void findAllPaged_ShouldReturnEmptyPage_WhenNoEntitiesExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<NeighborhoodDbo> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        
        when(jpaNeighborhoodRepository.findAllPaged(pageable)).thenReturn(emptyPage);

        // When
        Page<Neighborhood> result = neighborhoodRepositoryAdapter.findAllPaged(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(jpaNeighborhoodRepository).findAllPaged(pageable);
    }

    @Test
    void findBySearchTermPaged_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String searchTerm = "test";
        Pageable pageable = PageRequest.of(0, 10);
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaNeighborhoodRepository.findBySearchTerm(searchTerm, pageable)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> neighborhoodRepositoryAdapter.findBySearchTermPaged(searchTerm, pageable))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }

    @Test
    void findAllPaged_ShouldThrowException_WhenRepositoryFails() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaNeighborhoodRepository.findAllPaged(pageable)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> neighborhoodRepositoryAdapter.findAllPaged(pageable))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }
}