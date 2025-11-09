package com.example.userservice.infrastructure.adapters.output.persistence.repository;

import com.example.userservice.infrastructure.adapters.output.persistence.entity.CityDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaCityRepository;
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
 * Integration tests for JpaCityRepository.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JpaCityRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JpaCityRepository cityRepository;

    private CityDbo createCityDbo() {
        return CityDbo.builder()
            .name("test-name")
            .regionId("test-regionId")
            .status(EntityStatus.ACTIVE)
            .build();
    }

    @Test
    void findById_ShouldReturnEntity_WhenExists() {
        // Given
        CityDbo city = createCityDbo();
        CityDbo savedCity = entityManager.persistAndFlush(city);

        // When
        Optional<CityDbo> result = cityRepository.findById(savedCity.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedCity.getId());
    }

    @Test
    void save_ShouldPersistEntity() {
        // Given
        CityDbo city = createCityDbo();

        // When
        CityDbo savedCity = cityRepository.save(city);

        // Then
        assertThat(savedCity.getId()).isNotNull();
        
        CityDbo foundCity = entityManager.find(CityDbo.class, savedCity.getId());
        assertThat(foundCity).isNotNull();
    }

    @Test
    void deleteById_ShouldRemoveEntity() {
        // Given
        CityDbo city = createCityDbo();
        CityDbo savedCity = entityManager.persistAndFlush(city);

        // When
        cityRepository.deleteById(savedCity.getId());
        entityManager.flush();

        // Then
        CityDbo foundCity = entityManager.find(CityDbo.class, savedCity.getId());
        assertThat(foundCity).isNull();
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        CityDbo city = createCityDbo();
        CityDbo savedCity = entityManager.persistAndFlush(city);

        // When
        boolean exists = cityRepository.existsById(savedCity.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityDoesNotExist() {
        // When
        boolean exists = cityRepository.existsById("non-existent-id");

        // Then
        assertThat(exists).isFalse();
    }
}