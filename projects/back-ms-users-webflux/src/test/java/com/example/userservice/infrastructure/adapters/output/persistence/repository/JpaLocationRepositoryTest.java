package com.example.userservice.infrastructure.adapters.output.persistence.repository;

import com.example.userservice.infrastructure.adapters.output.persistence.entity.LocationDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaLocationRepository;
import com.example.userservice.domain.model.EntityStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for JpaLocationRepository.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JpaLocationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JpaLocationRepository locationRepository;

    private LocationDbo createLocationDbo() {
        return LocationDbo.builder()
            .userId("test-userId")
            .country("test-country")
            .region("test-region")
            .city("test-city")
            .address("test-address")
            .locationType("test-locationType")
            .status(EntityStatus.ACTIVE)
            .build();
    }

    @Test
    void findById_ShouldReturnEntity_WhenExists() {
        // Given
        LocationDbo location = createLocationDbo();
        LocationDbo savedLocation = entityManager.persistAndFlush(location);

        // When
        Optional<LocationDbo> result = locationRepository.findById(savedLocation.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedLocation.getId());
    }

    @Test
    void save_ShouldPersistEntity() {
        // Given
        LocationDbo location = createLocationDbo();

        // When
        LocationDbo savedLocation = locationRepository.save(location);

        // Then
        assertThat(savedLocation.getId()).isNotNull();
        
        LocationDbo foundLocation = entityManager.find(LocationDbo.class, savedLocation.getId());
        assertThat(foundLocation).isNotNull();
    }

    @Test
    void deleteById_ShouldRemoveEntity() {
        // Given
        LocationDbo location = createLocationDbo();
        LocationDbo savedLocation = entityManager.persistAndFlush(location);

        // When
        locationRepository.deleteById(savedLocation.getId());
        entityManager.flush();

        // Then
        LocationDbo foundLocation = entityManager.find(LocationDbo.class, savedLocation.getId());
        assertThat(foundLocation).isNull();
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        LocationDbo location = createLocationDbo();
        LocationDbo savedLocation = entityManager.persistAndFlush(location);

        // When
        boolean exists = locationRepository.existsById(savedLocation.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityDoesNotExist() {
        // When
        boolean exists = locationRepository.existsById("non-existent-id");

        // Then
        assertThat(exists).isFalse();
    }
}