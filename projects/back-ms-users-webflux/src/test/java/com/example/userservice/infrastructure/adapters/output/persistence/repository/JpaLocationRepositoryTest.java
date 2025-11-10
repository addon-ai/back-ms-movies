package com.example.userservice.infrastructure.adapters.output.persistence.repository;

import com.example.userservice.infrastructure.adapters.output.persistence.entity.LocationDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaLocationRepository;
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
 * Integration tests for JpaLocationRepository.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@DataR2dbcTest
class JpaLocationRepositoryTest {

    @Autowired
    private JpaLocationRepository locationRepository;

    private LocationDbo createLocationDbo() {
        UUID randomUUID = UUID.randomUUID();
        return LocationDbo.builder()
            .userId("test-userId-" + randomUUID)
            .country("test-country-" + randomUUID)
            .region("test-region-" + randomUUID)
            .city("test-city-" + randomUUID)
            .address("test-address-" + randomUUID)
            .locationType("test-locationType-" + randomUUID)
            .status(EntityStatus.ACTIVE)
            .build();
    }

    @Test
    void findById_ShouldReturnEntity_WhenExists() {
        // Given
        LocationDbo location = createLocationDbo();
        LocationDbo savedLocation = locationRepository.save(location)
            .block(Duration.ofSeconds(5));

        // When
        LocationDbo result = locationRepository.findById(savedLocation.getId())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedLocation.getId());
    }

    @Test
    void save_ShouldPersistEntity() {
        // Given
        LocationDbo location = createLocationDbo();

        // When
        LocationDbo savedLocation = locationRepository.save(location)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(savedLocation.getId()).isNotNull();
        
        LocationDbo foundLocation = locationRepository.findById(savedLocation.getId())
            .block(Duration.ofSeconds(5));
        assertThat(foundLocation).isNotNull();
    }

    @Test
    void deleteById_ShouldRemoveEntity() {
        // Given
        LocationDbo location = createLocationDbo();
        LocationDbo savedLocation = locationRepository.save(location)
            .block(Duration.ofSeconds(5));

        // When
        locationRepository.deleteById(savedLocation.getId())
            .block(Duration.ofSeconds(5));

        // Then
        LocationDbo foundLocation = locationRepository.findById(savedLocation.getId())
            .block(Duration.ofSeconds(5));
        assertThat(foundLocation).isNull();
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        LocationDbo location = createLocationDbo();
        LocationDbo savedLocation = locationRepository.save(location)
            .block(Duration.ofSeconds(5));

        // When
        Boolean exists = locationRepository.existsById(savedLocation.getId())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityDoesNotExist() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        
        // When
        Boolean exists = locationRepository.existsById(nonExistentId)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(exists).isFalse();
    }
}