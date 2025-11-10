package com.example.userservice.infrastructure.adapters.output.persistence.repository;

import com.example.userservice.infrastructure.adapters.output.persistence.entity.CountryDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaCountryRepository;
import com.example.userservice.domain.model.EntityStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for JpaCountryRepository.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@DataR2dbcTest
class JpaCountryRepositoryTest {

    @Autowired
    private JpaCountryRepository countryRepository;

    private CountryDbo createCountryDbo() {
        UUID randomUUID = UUID.randomUUID();
        return CountryDbo.builder()
            .name("test-name-" + randomUUID)
            .code("test-code-" + randomUUID)
            .status(EntityStatus.ACTIVE)
            .build();
    }

    @Test
    void findById_ShouldReturnEntity_WhenExists() {
        // Given
        CountryDbo country = createCountryDbo();
        CountryDbo savedCountry = countryRepository.save(country)
            .block(Duration.ofSeconds(5));

        // When
        CountryDbo result = countryRepository.findById(savedCountry.getId())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedCountry.getId());
    }

    @Test
    void save_ShouldPersistEntity() {
        // Given
        CountryDbo country = createCountryDbo();

        // When
        CountryDbo savedCountry = countryRepository.save(country)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(savedCountry.getId()).isNotNull();
        
        CountryDbo foundCountry = countryRepository.findById(savedCountry.getId())
            .block(Duration.ofSeconds(5));
        assertThat(foundCountry).isNotNull();
    }

    @Test
    void deleteById_ShouldRemoveEntity() {
        // Given
        CountryDbo country = createCountryDbo();
        CountryDbo savedCountry = countryRepository.save(country)
            .block(Duration.ofSeconds(5));

        // When
        countryRepository.deleteById(savedCountry.getId())
            .block(Duration.ofSeconds(5));

        // Then
        CountryDbo foundCountry = countryRepository.findById(savedCountry.getId())
            .block(Duration.ofSeconds(5));
        assertThat(foundCountry).isNull();
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        CountryDbo country = createCountryDbo();
        CountryDbo savedCountry = countryRepository.save(country)
            .block(Duration.ofSeconds(5));

        // When
        Boolean exists = countryRepository.existsById(savedCountry.getId())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityDoesNotExist() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        
        // When
        Boolean exists = countryRepository.existsById(nonExistentId)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(exists).isFalse();
    }
}