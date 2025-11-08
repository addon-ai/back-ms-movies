package com.example.userservice.infrastructure.adapters.output.persistence.repository;

import com.example.userservice.infrastructure.adapters.output.persistence.entity.CountryDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaCountryRepository;
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
 * Integration tests for JpaCountryRepository.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JpaCountryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JpaCountryRepository countryRepository;

    private CountryDbo createCountryDbo() {
        return CountryDbo.builder()
            .name("test-name")
            .code("test-code")
            .status(EntityStatus.ACTIVE)
            .build();
    }

    @Test
    void findById_ShouldReturnEntity_WhenExists() {
        // Given
        CountryDbo country = createCountryDbo();
        CountryDbo savedCountry = entityManager.persistAndFlush(country);

        // When
        Optional<CountryDbo> result = countryRepository.findById(savedCountry.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedCountry.getId());
    }

    @Test
    void save_ShouldPersistEntity() {
        // Given
        CountryDbo country = createCountryDbo();

        // When
        CountryDbo savedCountry = countryRepository.save(country);

        // Then
        assertThat(savedCountry.getId()).isNotNull();
        
        CountryDbo foundCountry = entityManager.find(CountryDbo.class, savedCountry.getId());
        assertThat(foundCountry).isNotNull();
    }

    @Test
    void deleteById_ShouldRemoveEntity() {
        // Given
        CountryDbo country = createCountryDbo();
        CountryDbo savedCountry = entityManager.persistAndFlush(country);

        // When
        countryRepository.deleteById(savedCountry.getId());
        entityManager.flush();

        // Then
        CountryDbo foundCountry = entityManager.find(CountryDbo.class, savedCountry.getId());
        assertThat(foundCountry).isNull();
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        CountryDbo country = createCountryDbo();
        CountryDbo savedCountry = entityManager.persistAndFlush(country);

        // When
        boolean exists = countryRepository.existsById(savedCountry.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityDoesNotExist() {
        // When
        boolean exists = countryRepository.existsById("non-existent-id");

        // Then
        assertThat(exists).isFalse();
    }
}