package com.example.userservice.infrastructure.adapters.output.persistence.repository;

import com.example.userservice.infrastructure.adapters.output.persistence.entity.RegionDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaRegionRepository;
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
 * Integration tests for JpaRegionRepository.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@DataR2dbcTest
class JpaRegionRepositoryTest {

    @Autowired
    private JpaRegionRepository regionRepository;

    private RegionDbo createRegionDbo() {
        UUID randomUUID = UUID.randomUUID();
        return RegionDbo.builder()
            .name("test-name-" + randomUUID)
            .code("test-code-" + randomUUID)
            .countryId("test-countryId-" + randomUUID)
            .status(EntityStatus.ACTIVE)
            .build();
    }

    @Test
    void findById_ShouldReturnEntity_WhenExists() {
        // Given
        RegionDbo region = createRegionDbo();
        RegionDbo savedRegion = regionRepository.save(region)
            .block(Duration.ofSeconds(5));

        // When
        RegionDbo result = regionRepository.findById(savedRegion.getId())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedRegion.getId());
    }

    @Test
    void save_ShouldPersistEntity() {
        // Given
        RegionDbo region = createRegionDbo();

        // When
        RegionDbo savedRegion = regionRepository.save(region)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(savedRegion.getId()).isNotNull();
        
        RegionDbo foundRegion = regionRepository.findById(savedRegion.getId())
            .block(Duration.ofSeconds(5));
        assertThat(foundRegion).isNotNull();
    }

    @Test
    void deleteById_ShouldRemoveEntity() {
        // Given
        RegionDbo region = createRegionDbo();
        RegionDbo savedRegion = regionRepository.save(region)
            .block(Duration.ofSeconds(5));

        // When
        regionRepository.deleteById(savedRegion.getId())
            .block(Duration.ofSeconds(5));

        // Then
        RegionDbo foundRegion = regionRepository.findById(savedRegion.getId())
            .block(Duration.ofSeconds(5));
        assertThat(foundRegion).isNull();
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        RegionDbo region = createRegionDbo();
        RegionDbo savedRegion = regionRepository.save(region)
            .block(Duration.ofSeconds(5));

        // When
        Boolean exists = regionRepository.existsById(savedRegion.getId())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityDoesNotExist() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        
        // When
        Boolean exists = regionRepository.existsById(nonExistentId)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(exists).isFalse();
    }
}