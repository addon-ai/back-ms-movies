package com.example.userservice.infrastructure.adapters.output.persistence.repository;

import com.example.userservice.infrastructure.adapters.output.persistence.entity.NeighborhoodDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaNeighborhoodRepository;
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
 * Integration tests for JpaNeighborhoodRepository.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JpaNeighborhoodRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JpaNeighborhoodRepository neighborhoodRepository;

    private NeighborhoodDbo createNeighborhoodDbo() {
        return NeighborhoodDbo.builder()
            .name("test-name")
            .cityId("test-cityId")
            .status(EntityStatus.ACTIVE)
            .build();
    }

    @Test
    void findById_ShouldReturnEntity_WhenExists() {
        // Given
        NeighborhoodDbo neighborhood = createNeighborhoodDbo();
        NeighborhoodDbo savedNeighborhood = entityManager.persistAndFlush(neighborhood);

        // When
        Optional<NeighborhoodDbo> result = neighborhoodRepository.findById(savedNeighborhood.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedNeighborhood.getId());
    }

    @Test
    void save_ShouldPersistEntity() {
        // Given
        NeighborhoodDbo neighborhood = createNeighborhoodDbo();

        // When
        NeighborhoodDbo savedNeighborhood = neighborhoodRepository.save(neighborhood);

        // Then
        assertThat(savedNeighborhood.getId()).isNotNull();
        
        NeighborhoodDbo foundNeighborhood = entityManager.find(NeighborhoodDbo.class, savedNeighborhood.getId());
        assertThat(foundNeighborhood).isNotNull();
    }

    @Test
    void deleteById_ShouldRemoveEntity() {
        // Given
        NeighborhoodDbo neighborhood = createNeighborhoodDbo();
        NeighborhoodDbo savedNeighborhood = entityManager.persistAndFlush(neighborhood);

        // When
        neighborhoodRepository.deleteById(savedNeighborhood.getId());
        entityManager.flush();

        // Then
        NeighborhoodDbo foundNeighborhood = entityManager.find(NeighborhoodDbo.class, savedNeighborhood.getId());
        assertThat(foundNeighborhood).isNull();
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        NeighborhoodDbo neighborhood = createNeighborhoodDbo();
        NeighborhoodDbo savedNeighborhood = entityManager.persistAndFlush(neighborhood);

        // When
        boolean exists = neighborhoodRepository.existsById(savedNeighborhood.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityDoesNotExist() {
        // When
        boolean exists = neighborhoodRepository.existsById("non-existent-id");

        // Then
        assertThat(exists).isFalse();
    }
}