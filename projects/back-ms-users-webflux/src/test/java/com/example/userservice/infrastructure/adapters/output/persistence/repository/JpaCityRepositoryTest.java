package com.example.userservice.infrastructure.adapters.output.persistence.repository;

import com.example.userservice.infrastructure.adapters.output.persistence.entity.CityDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaCityRepository;
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
 * Integration tests for JpaCityRepository.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@DataR2dbcTest
class JpaCityRepositoryTest {

    @Autowired
    private JpaCityRepository cityRepository;

    private CityDbo createCityDbo() {
        UUID randomUUID = UUID.randomUUID();
        return CityDbo.builder()
            .name("test-name-" + randomUUID)
            .regionId("test-regionId-" + randomUUID)
            .status(EntityStatus.ACTIVE)
            .build();
    }

    @Test
    void findById_ShouldReturnEntity_WhenExists() {
        // Given
        CityDbo city = createCityDbo();
        CityDbo savedCity = cityRepository.save(city)
            .block(Duration.ofSeconds(5));

        // When
        CityDbo result = cityRepository.findById(savedCity.getId())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedCity.getId());
    }

    @Test
    void save_ShouldPersistEntity() {
        // Given
        CityDbo city = createCityDbo();

        // When
        CityDbo savedCity = cityRepository.save(city)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(savedCity.getId()).isNotNull();
        
        CityDbo foundCity = cityRepository.findById(savedCity.getId())
            .block(Duration.ofSeconds(5));
        assertThat(foundCity).isNotNull();
    }

    @Test
    void deleteById_ShouldRemoveEntity() {
        // Given
        CityDbo city = createCityDbo();
        CityDbo savedCity = cityRepository.save(city)
            .block(Duration.ofSeconds(5));

        // When
        cityRepository.deleteById(savedCity.getId())
            .block(Duration.ofSeconds(5));

        // Then
        CityDbo foundCity = cityRepository.findById(savedCity.getId())
            .block(Duration.ofSeconds(5));
        assertThat(foundCity).isNull();
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        CityDbo city = createCityDbo();
        CityDbo savedCity = cityRepository.save(city)
            .block(Duration.ofSeconds(5));

        // When
        Boolean exists = cityRepository.existsById(savedCity.getId())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityDoesNotExist() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        
        // When
        Boolean exists = cityRepository.existsById(nonExistentId)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(exists).isFalse();
    }
}