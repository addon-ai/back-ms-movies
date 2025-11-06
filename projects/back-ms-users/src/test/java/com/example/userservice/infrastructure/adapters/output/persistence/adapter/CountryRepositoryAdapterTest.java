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

    @BeforeEach
    void setUp() {
        domainCountry = Country.builder()
            .build();
        
        countryDbo = CountryDbo.builder()
            .build();
    }

    @Test
    void save_ShouldReturnDomainEntity_WhenValidEntity() {
        // Given
        when(countryMapper.toDbo(domainCountry)).thenReturn(countryDbo);
        when(jpaCountryRepository.save(countryDbo)).thenReturn(countryDbo);
        when(countryMapper.toDomain(countryDbo)).thenReturn(domainCountry);

        // When
        Country result = countryRepositoryAdapter.save(domainCountry);

        // Then
        assertThat(result).isNotNull();
        verify(countryMapper).toDbo(domainCountry);
        verify(jpaCountryRepository).save(countryDbo);
        verify(countryMapper).toDomain(countryDbo);
    }

    @Test
    void save_ShouldThrowException_WhenRepositoryFails() {
        // Given
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(countryMapper.toDbo(domainCountry)).thenReturn(countryDbo);
        when(jpaCountryRepository.save(countryDbo)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> countryRepositoryAdapter.save(domainCountry))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to save Country");
    }

    @Test
    void findById_ShouldReturnOptionalWithEntity_WhenEntityExists() {
        // Given
        String countryId = "test-id";
        when(jpaCountryRepository.findById(countryId)).thenReturn(Optional.of(countryDbo));
        when(countryMapper.toDomain(countryDbo)).thenReturn(domainCountry);

        // When
        Optional<Country> result = countryRepositoryAdapter.findById(countryId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(domainCountry);
        verify(jpaCountryRepository).findById(countryId);
        verify(countryMapper).toDomain(countryDbo);
    }

    @Test
    void findById_ShouldReturnEmptyOptional_WhenEntityNotFound() {
        // Given
        String countryId = "non-existent-id";
        when(jpaCountryRepository.findById(countryId)).thenReturn(Optional.empty());

        // When
        Optional<Country> result = countryRepositoryAdapter.findById(countryId);

        // Then
        assertThat(result).isEmpty();
        verify(jpaCountryRepository).findById(countryId);
    }

    @Test
    void findById_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String countryId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaCountryRepository.findById(countryId)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> countryRepositoryAdapter.findById(countryId))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to find Country by id");
    }

    @Test
    void findAll_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        List<CountryDbo> countryDbos = Collections.singletonList(countryDbo);
        List<Country> countrys = Collections.singletonList(domainCountry);
        when(jpaCountryRepository.findAll()).thenReturn(countryDbos);
        when(countryMapper.toDomainList(countryDbos)).thenReturn(countrys);

        // When
        List<Country> result = countryRepositoryAdapter.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(countrys);
        verify(jpaCountryRepository).findAll();
        verify(countryMapper).toDomainList(countryDbos);
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoEntitiesExist() {
        // Given
        when(jpaCountryRepository.findAll()).thenReturn(Collections.emptyList());
        when(countryMapper.toDomainList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<Country> result = countryRepositoryAdapter.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(jpaCountryRepository).findAll();
        verify(countryMapper).toDomainList(Collections.emptyList());
    }

    @Test
    void findAll_ShouldThrowException_WhenRepositoryFails() {
        // Given
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaCountryRepository.findAll()).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> countryRepositoryAdapter.findAll())
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to find all Countries");
        
        verify(jpaCountryRepository).findAll();
    }

    @Test
    void deleteById_ShouldCallRepository_WhenValidId() {
        // Given
        String countryId = "test-id";

        // When
        countryRepositoryAdapter.deleteById(countryId);

        // Then
        verify(jpaCountryRepository).deleteById(countryId);
    }

    @Test
    void deleteById_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String countryId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        doThrow(repositoryException).when(jpaCountryRepository).deleteById(countryId);

        // When & Then
        assertThatThrownBy(() -> countryRepositoryAdapter.deleteById(countryId))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to delete Country by id");
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        String countryId = "test-id";
        when(jpaCountryRepository.existsById(countryId)).thenReturn(true);

        // When
        boolean result = countryRepositoryAdapter.existsById(countryId);

        // Then
        assertThat(result).isTrue();
        verify(jpaCountryRepository).existsById(countryId);
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityNotExists() {
        // Given
        String countryId = "non-existent-id";
        when(jpaCountryRepository.existsById(countryId)).thenReturn(false);

        // When
        boolean result = countryRepositoryAdapter.existsById(countryId);

        // Then
        assertThat(result).isFalse();
        verify(jpaCountryRepository).existsById(countryId);
    }

    @Test
    void existsById_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String countryId = "test-id";
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaCountryRepository.existsById(countryId)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> countryRepositoryAdapter.existsById(countryId))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to check if Country exists by id");
    }

    @Test
    void findBySearchTerm_ShouldReturnListOfEntities_WhenEntitiesExist() {
        // Given
        String searchTerm = "test";
        Integer page = 1;
        Integer size = 10;
        
        when(jpaCountryRepository.findBySearchTerm(anyString(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(countryDbo)));
        when(countryMapper.toDomain(countryDbo)).thenReturn(domainCountry);

        // When
        List<Country> result = countryRepositoryAdapter.findBySearchTerm(searchTerm, page, size);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainCountry);
    }

    @Test
    void findBySearchTerm_ShouldReturnListOfEntities_WhenSearchIsEmpty() {
        // Given
        String searchTerm = "";
        Integer page = 1;
        Integer size = 10;
        
        when(jpaCountryRepository.findAll()).thenReturn(Collections.singletonList(countryDbo));
        when(countryMapper.toDomainList(Collections.singletonList(countryDbo)))
            .thenReturn(Collections.singletonList(domainCountry));

        // When
        List<Country> result = countryRepositoryAdapter.findBySearchTerm(searchTerm, page, size);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(domainCountry);
    }

    @Test
    void findBySearchTerm_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String searchTerm = "test";
        Integer page = 1;
        Integer size = 10;
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaCountryRepository.findBySearchTerm(anyString(), any(Pageable.class)))
            .thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> countryRepositoryAdapter.findBySearchTerm(searchTerm, page, size))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to search Countries");
    }

    @Test
    void findBySearchTerm_ShouldThrowException_WhenRepositoryFailsOnFindAll() {
        // Given
        String searchTerm = null;
        Integer page = 1;
        Integer size = 10;
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaCountryRepository.findAll()).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> countryRepositoryAdapter.findBySearchTerm(searchTerm, page, size))
            .isInstanceOf(com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException.class)
            .hasMessage("Failed to search Countries");
    }

    @Test
    void findBySearchTermPaged_ShouldReturnPageOfEntities_WhenEntitiesExist() {
        // Given
        String searchTerm = "test";
        Pageable pageable = PageRequest.of(0, 10);
        List<CountryDbo> countryDbos = Collections.singletonList(countryDbo);
        Page<CountryDbo> dboPage = new PageImpl<>(countryDbos, pageable, 1);
        
        when(jpaCountryRepository.findBySearchTerm(searchTerm, pageable)).thenReturn(dboPage);
        when(countryMapper.toDomain(countryDbo)).thenReturn(domainCountry);

        // When
        Page<Country> result = countryRepositoryAdapter.findBySearchTermPaged(searchTerm, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(domainCountry);
        verify(jpaCountryRepository).findBySearchTerm(searchTerm, pageable);
        verify(countryMapper).toDomain(countryDbo);
    }

    @Test
    void findBySearchTermPaged_ShouldReturnEmptyPage_WhenNoEntitiesExist() {
        // Given
        String searchTerm = "nonexistent";
        Pageable pageable = PageRequest.of(0, 10);
        Page<CountryDbo> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        
        when(jpaCountryRepository.findBySearchTerm(searchTerm, pageable)).thenReturn(emptyPage);

        // When
        Page<Country> result = countryRepositoryAdapter.findBySearchTermPaged(searchTerm, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(jpaCountryRepository).findBySearchTerm(searchTerm, pageable);
    }

    @Test
    void findAllPaged_ShouldReturnPageOfEntities_WhenEntitiesExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<CountryDbo> countryDbos = Collections.singletonList(countryDbo);
        Page<CountryDbo> dboPage = new PageImpl<>(countryDbos, pageable, 1);
        
        when(jpaCountryRepository.findAllPaged(pageable)).thenReturn(dboPage);
        when(countryMapper.toDomain(countryDbo)).thenReturn(domainCountry);

        // When
        Page<Country> result = countryRepositoryAdapter.findAllPaged(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(domainCountry);
        verify(jpaCountryRepository).findAllPaged(pageable);
        verify(countryMapper).toDomain(countryDbo);
    }

    @Test
    void findAllPaged_ShouldReturnEmptyPage_WhenNoEntitiesExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<CountryDbo> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        
        when(jpaCountryRepository.findAllPaged(pageable)).thenReturn(emptyPage);

        // When
        Page<Country> result = countryRepositoryAdapter.findAllPaged(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(jpaCountryRepository).findAllPaged(pageable);
    }

    @Test
    void findBySearchTermPaged_ShouldThrowException_WhenRepositoryFails() {
        // Given
        String searchTerm = "test";
        Pageable pageable = PageRequest.of(0, 10);
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaCountryRepository.findBySearchTerm(searchTerm, pageable)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> countryRepositoryAdapter.findBySearchTermPaged(searchTerm, pageable))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }

    @Test
    void findAllPaged_ShouldThrowException_WhenRepositoryFails() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(jpaCountryRepository.findAllPaged(pageable)).thenThrow(repositoryException);

        // When & Then
        assertThatThrownBy(() -> countryRepositoryAdapter.findAllPaged(pageable))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }
}