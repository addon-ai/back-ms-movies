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

    @BeforeEach
    void setUp() {
        domainCity = City.builder()
            .build();
        
        cityDbo = CityDbo.builder()
            .build();
    }

    @Test
    void save_ShouldReturnDomainEntity_WhenValidEntity() {
        // Given
        when(cityMapper.toDbo(domainCity)).thenReturn(cityDbo);
        when(jpaCityRepository.save(cityDbo)).thenReturn(cityDbo);
        when(cityMapper.toDomain(cityDbo)).thenReturn(domainCity);

        // When
        City result = cityRepositoryAdapter.save(domainCity);

        // Then
        assertThat(result).isNotNull();
        verify(cityMapper).toDbo(domainCity);
        verify(jpaCityRepository).save(cityDbo);
        verify(cityMapper).toDomain(cityDbo);
    }

    @Test
    void save_ShouldThrowException_WhenRepositoryFails() {
        // Given
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(cityMapper.toDbo(domainCity)).thenReturn(cityDbo);
        when(jpaCityRepository.save(cityDbo)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> cityRepositoryAdapter.save(domainCity))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to save City");
    }

    @Test
    void findById_ShouldReturnOptionalWithEntity_WhenEntityExists() {
        // Given
        String cityId = "test-id";
        when(jpaCityRepository.findById(cityId)).thenReturn(Optional.of(cityDbo));
        when(cityMapper.toDomain(cityDbo)).thenReturn(domainCity);

        // When
        Optional<City> result = cityRepositoryAdapter.findById(cityId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(domainCity);
        verify(jpaCityRepository).findById(cityId);
        verify(cityMapper).toDomain(cityDbo);
    }

    @Test
    void findById_ShouldReturnEmptyOptional_WhenEntityNotFound() {
        // Given
        String cityId = "non-existent-id";
        when(jpaCityRepository.findById(cityId)).thenReturn(Optional.empty());

        // When
        Optional<City> result = cityRepositoryAdapter.findById(cityId);

        // Then
        assertThat(result).isEmpty();
        verify(jpaCityRepository).findById(cityId);
    }

    @Test
    void findById_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String cityId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaCityRepository.findById(cityId)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> cityRepositoryAdapter.findById(cityId))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to find City by id");
    }

    @Test
    void findAll_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        List<CityDbo> cityDbos = Collections.singletonList(cityDbo);
        List<City> citys = Collections.singletonList(domainCity);
        when(jpaCityRepository.findAll()).thenReturn(cityDbos);
        when(cityMapper.toDomainList(cityDbos)).thenReturn(citys);

        // When
        List<City> result = cityRepositoryAdapter.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(citys);
        verify(jpaCityRepository).findAll();
        verify(cityMapper).toDomainList(cityDbos);
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoEntitiesExist() {
        // Given
        when(jpaCityRepository.findAll()).thenReturn(Collections.emptyList());
        when(cityMapper.toDomainList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<City> result = cityRepositoryAdapter.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(jpaCityRepository).findAll();
        verify(cityMapper).toDomainList(Collections.emptyList());
    }

    @Test
    void findAll_ShouldThrowException_WhenRepositoryFails() {
        // Given
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaCityRepository.findAll()).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> cityRepositoryAdapter.findAll())
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to find all Cities");
        
        verify(jpaCityRepository).findAll();
    }

    @Test
    void deleteById_ShouldCallRepository_WhenValidId() {
        // Given
        String cityId = "test-id";

        // When
        cityRepositoryAdapter.deleteById(cityId);

        // Then
        verify(jpaCityRepository).deleteById(cityId);
    }

    @Test
    void deleteById_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String cityId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        doThrow(repositoryException).when(jpaCityRepository).deleteById(cityId);

        // When & Then
        assertThatThrownBy(() -> cityRepositoryAdapter.deleteById(cityId))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to delete City by id");
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        String cityId = "test-id";
        when(jpaCityRepository.existsById(cityId)).thenReturn(true);

        // When
        boolean result = cityRepositoryAdapter.existsById(cityId);

        // Then
        assertThat(result).isTrue();
        verify(jpaCityRepository).existsById(cityId);
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityNotExists() {
        // Given
        String cityId = "non-existent-id";
        when(jpaCityRepository.existsById(cityId)).thenReturn(false);

        // When
        boolean result = cityRepositoryAdapter.existsById(cityId);

        // Then
        assertThat(result).isFalse();
        verify(jpaCityRepository).existsById(cityId);
    }

    @Test
    void existsById_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String cityId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaCityRepository.existsById(cityId)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> cityRepositoryAdapter.existsById(cityId))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to check if City exists by id");
    }

    @Test
    void findBySearchTerm_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        String searchTerm = "test";
        Integer page = 1;
        Integer size = 10;
        
        when(jpaCityRepository.findBySearchTerm(anyString(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(cityDbo)));
        when(cityMapper.toDomain(cityDbo)).thenReturn(domainCity);

        // When
        List<City> result = cityRepositoryAdapter.findBySearchTerm(searchTerm, page, size);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainCity);
    }

    @Test
    void findBySearchTerm_ShouldReturnListOfEntities_WhenSearchIsEmpty() {
        // Given
        String searchTerm = "";
        Integer page = 1;
        Integer size = 10;
        
        when(jpaCityRepository.findAll()).thenReturn(Collections.singletonList(cityDbo));
        when(cityMapper.toDomainList(Collections.singletonList(cityDbo)))
            .thenReturn(Collections.singletonList(domainCity));

        // When
        List<City> result = cityRepositoryAdapter.findBySearchTerm(searchTerm, page, size);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainCity);
    }

    @Test
    void findBySearchTerm_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String searchTerm = "test";
        Integer page = 1;
        Integer size = 10;
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaCityRepository.findBySearchTerm(anyString(), any(Pageable.class)))
            .thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> cityRepositoryAdapter.findBySearchTerm(searchTerm, page, size))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to search Cities");
    }

    @Test
    void findBySearchTerm_ShouldThrowException_WhenRepositoryFailsOnFindAll() {
        // Given
        String searchTerm = null;
        Integer page = 1;
        Integer size = 10;
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaCityRepository.findAll()).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> cityRepositoryAdapter.findBySearchTerm(searchTerm, page, size))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to search Cities");
    }

    @Test
    void findBySearchTermPaged_ShouldReturnPageOfEntities_WhenEntitiesExist() {
        // Given
        String searchTerm = "test";
        Pageable pageable = PageRequest.of(0, 10);
        List<CityDbo> cityDbos = Collections.singletonList(cityDbo);
        Page<CityDbo> dboPage = new PageImpl<>(cityDbos, pageable, 1);
        
        when(jpaCityRepository.findBySearchTerm(searchTerm, pageable)).thenReturn(dboPage);
        when(cityMapper.toDomain(cityDbo)).thenReturn(domainCity);

        // When
        Page<City> result = cityRepositoryAdapter.findBySearchTermPaged(searchTerm, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(domainCity);
        verify(jpaCityRepository).findBySearchTerm(searchTerm, pageable);
        verify(cityMapper).toDomain(cityDbo);
    }

    @Test
    void findBySearchTermPaged_ShouldReturnEmptyPage_WhenNoEntitiesExist() {
        // Given
        String searchTerm = "nonexistent";
        Pageable pageable = PageRequest.of(0, 10);
        Page<CityDbo> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        
        when(jpaCityRepository.findBySearchTerm(searchTerm, pageable)).thenReturn(emptyPage);

        // When
        Page<City> result = cityRepositoryAdapter.findBySearchTermPaged(searchTerm, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(jpaCityRepository).findBySearchTerm(searchTerm, pageable);
    }

    @Test
    void findAllPaged_ShouldReturnPageOfEntities_WhenEntitiesExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<CityDbo> cityDbos = Collections.singletonList(cityDbo);
        Page<CityDbo> dboPage = new PageImpl<>(cityDbos, pageable, 1);
        
        when(jpaCityRepository.findAllPaged(pageable)).thenReturn(dboPage);
        when(cityMapper.toDomain(cityDbo)).thenReturn(domainCity);

        // When
        Page<City> result = cityRepositoryAdapter.findAllPaged(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(domainCity);
        verify(jpaCityRepository).findAllPaged(pageable);
        verify(cityMapper).toDomain(cityDbo);
    }

    @Test
    void findAllPaged_ShouldReturnEmptyPage_WhenNoEntitiesExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<CityDbo> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        
        when(jpaCityRepository.findAllPaged(pageable)).thenReturn(emptyPage);

        // When
        Page<City> result = cityRepositoryAdapter.findAllPaged(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(jpaCityRepository).findAllPaged(pageable);
    }

    @Test
    void findBySearchTermPaged_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String searchTerm = "test";
        Pageable pageable = PageRequest.of(0, 10);
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaCityRepository.findBySearchTerm(searchTerm, pageable)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> cityRepositoryAdapter.findBySearchTermPaged(searchTerm, pageable))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }

    @Test
    void findAllPaged_ShouldThrowException_WhenRepositoryFails() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaCityRepository.findAllPaged(pageable)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> cityRepositoryAdapter.findAllPaged(pageable))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }
}