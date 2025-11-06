package com.example.movieservice.infrastructure.adapters.output.persistence.repository;

import com.example.movieservice.infrastructure.adapters.output.persistence.entity.RentalDbo;
import com.example.movieservice.infrastructure.adapters.output.persistence.repository.JpaRentalRepository;
import com.example.movieservice.domain.model.EntityStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for JpaRentalRepository.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JpaRentalRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JpaRentalRepository rentalRepository;

    private RentalDbo createRentalDbo() {
        return RentalDbo.builder()
            .movieId("test-movieId")
            .userId("test-userId")
            .rentalDate("test-rentalDate")
            .dueDate("test-dueDate")
            .totalPrice(1.0)
            .status(EntityStatus.ACTIVE)
            .build();
    }

    @Test
    void findById_ShouldReturnEntity_WhenExists() {
        // Given
        RentalDbo rental = createRentalDbo();
        RentalDbo savedRental = entityManager.persistAndFlush(rental);

        // When
        Optional<RentalDbo> result = rentalRepository.findById(savedRental.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedRental.getId());
    }

    @Test
    void save_ShouldPersistEntity() {
        // Given
        RentalDbo rental = createRentalDbo();

        // When
        RentalDbo savedRental = rentalRepository.save(rental);

        // Then
        assertThat(savedRental.getId()).isNotNull();
        
        RentalDbo foundRental = entityManager.find(RentalDbo.class, savedRental.getId());
        assertThat(foundRental).isNotNull();
    }

    @Test
    void deleteById_ShouldRemoveEntity() {
        // Given
        RentalDbo rental = createRentalDbo();
        RentalDbo savedRental = entityManager.persistAndFlush(rental);

        // When
        rentalRepository.deleteById(savedRental.getId());
        entityManager.flush();

        // Then
        RentalDbo foundRental = entityManager.find(RentalDbo.class, savedRental.getId());
        assertThat(foundRental).isNull();
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        RentalDbo rental = createRentalDbo();
        RentalDbo savedRental = entityManager.persistAndFlush(rental);

        // When
        boolean exists = rentalRepository.existsById(savedRental.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityDoesNotExist() {
        // When
        boolean exists = rentalRepository.existsById("non-existent-id");

        // Then
        assertThat(exists).isFalse();
    }
}