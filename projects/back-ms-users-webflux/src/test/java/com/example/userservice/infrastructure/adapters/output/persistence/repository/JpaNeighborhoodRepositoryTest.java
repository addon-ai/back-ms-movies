package com.example.userservice.infrastructure.adapters.output.persistence.repository;

import com.example.userservice.infrastructure.adapters.output.persistence.entity.NeighborhoodDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaNeighborhoodRepository;
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
 * Integration tests for JpaNeighborhoodRepository.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@DataR2dbcTest
class JpaNeighborhoodRepositoryTest {

    @Autowired
    private JpaNeighborhoodRepository neighborhoodRepository;

    private NeighborhoodDbo createNeighborhoodDbo() {
        UUID randomUUID = UUID.randomUUID();
        return NeighborhoodDbo.builder()
            .name("test-name-" + randomUUID)
            .cityId("test-cityId-" + randomUUID)
            .status(EntityStatus.ACTIVE)
            .build();
    }

    @Test
    void findById_ShouldReturnEntity_WhenExists() {
        // Given
        NeighborhoodDbo neighborhood = createNeighborhoodDbo();
        NeighborhoodDbo savedNeighborhood = neighborhoodRepository.save(neighborhood)
            .block(Duration.ofSeconds(5));

        // When
        NeighborhoodDbo result = neighborhoodRepository.findById(savedNeighborhood.getId())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedNeighborhood.getId());
    }

    @Test
    void save_ShouldPersistEntity() {
        // Given
        NeighborhoodDbo neighborhood = createNeighborhoodDbo();

        // When
        NeighborhoodDbo savedNeighborhood = neighborhoodRepository.save(neighborhood)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(savedNeighborhood.getId()).isNotNull();
        
        NeighborhoodDbo foundNeighborhood = neighborhoodRepository.findById(savedNeighborhood.getId())
            .block(Duration.ofSeconds(5));
        assertThat(foundNeighborhood).isNotNull();
    }

    @Test
    void deleteById_ShouldRemoveEntity() {
        // Given
        NeighborhoodDbo neighborhood = createNeighborhoodDbo();
        NeighborhoodDbo savedNeighborhood = neighborhoodRepository.save(neighborhood)
            .block(Duration.ofSeconds(5));

        // When
        neighborhoodRepository.deleteById(savedNeighborhood.getId())
            .block(Duration.ofSeconds(5));

        // Then
        NeighborhoodDbo foundNeighborhood = neighborhoodRepository.findById(savedNeighborhood.getId())
            .block(Duration.ofSeconds(5));
        assertThat(foundNeighborhood).isNull();
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        NeighborhoodDbo neighborhood = createNeighborhoodDbo();
        NeighborhoodDbo savedNeighborhood = neighborhoodRepository.save(neighborhood)
            .block(Duration.ofSeconds(5));

        // When
        Boolean exists = neighborhoodRepository.existsById(savedNeighborhood.getId())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityDoesNotExist() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        
        // When
        Boolean exists = neighborhoodRepository.existsById(nonExistentId)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(exists).isFalse();
    }
}