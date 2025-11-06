package com.example.userservice.infrastructure.adapters.output.persistence.repository;

import com.example.userservice.infrastructure.adapters.output.persistence.entity.RegionDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaRegionRepository;
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
 * Integration tests for JpaRegionRepository.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JpaRegionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JpaRegionRepository regionRepository;

    private RegionDbo createRegionDbo() {
        return RegionDbo.builder()
            .name("test-name")
            .code("test-code")
            .countryId("test-countryId")
            .status(EntityStatus.ACTIVE)
            .build();
    }

    @Test
    void findById_ShouldReturnEntity_WhenExists() {
        // Given
        RegionDbo region = createRegionDbo();
        RegionDbo savedRegion = entityManager.persistAndFlush(region);

        // When
        Optional<RegionDbo> result = regionRepository.findById(savedRegion.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedRegion.getId());
    }

    @Test
    void save_ShouldPersistEntity() {
        // Given
        RegionDbo region = createRegionDbo();

        // When
        RegionDbo savedRegion = regionRepository.save(region);

        // Then
        assertThat(savedRegion.getId()).isNotNull();
        
        RegionDbo foundRegion = entityManager.find(RegionDbo.class, savedRegion.getId());
        assertThat(foundRegion).isNotNull();
    }

    @Test
    void deleteById_ShouldRemoveEntity() {
        // Given
        RegionDbo region = createRegionDbo();
        RegionDbo savedRegion = entityManager.persistAndFlush(region);

        // When
        regionRepository.deleteById(savedRegion.getId());
        entityManager.flush();

        // Then
        RegionDbo foundRegion = entityManager.find(RegionDbo.class, savedRegion.getId());
        assertThat(foundRegion).isNull();
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        RegionDbo region = createRegionDbo();
        RegionDbo savedRegion = entityManager.persistAndFlush(region);

        // When
        boolean exists = regionRepository.existsById(savedRegion.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityDoesNotExist() {
        // When
        boolean exists = regionRepository.existsById("non-existent-id");

        // Then
        assertThat(exists).isFalse();
    }
}