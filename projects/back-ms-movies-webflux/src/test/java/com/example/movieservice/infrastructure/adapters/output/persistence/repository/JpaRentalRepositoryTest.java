package com.example.movieservice.infrastructure.adapters.output.persistence.repository;

import com.example.movieservice.infrastructure.adapters.output.persistence.entity.RentalDbo;
import com.example.movieservice.infrastructure.adapters.output.persistence.repository.JpaRentalRepository;
import com.example.movieservice.domain.model.EntityStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for JpaRentalRepository.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@DataR2dbcTest
class JpaRentalRepositoryTest {

    @Autowired
    private JpaRentalRepository rentalRepository;

    private RentalDbo createRentalDbo() {
        UUID randomUUID = UUID.randomUUID();
        return RentalDbo.builder()
            .movieId("test-movieId-" + randomUUID)
            .userId("test-userId-" + randomUUID)
            .rentalDate("test-rentalDate-" + randomUUID)
            .dueDate("test-dueDate-" + randomUUID)
            .totalPrice(1.0)
            .status(EntityStatus.ACTIVE)
            .build();
    }

    @Test
    void findById_ShouldReturnEntity_WhenExists() {
        // Given
        RentalDbo rental = createRentalDbo();
        RentalDbo savedRental = rentalRepository.save(rental)
            .block(Duration.ofSeconds(5));

        // When
        RentalDbo result = rentalRepository.findById(savedRental.getId())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedRental.getId());
    }

    @Test
    void save_ShouldPersistEntity() {
        // Given
        RentalDbo rental = createRentalDbo();

        // When
        RentalDbo savedRental = rentalRepository.save(rental)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(savedRental.getId()).isNotNull();
        
        RentalDbo foundRental = rentalRepository.findById(savedRental.getId())
            .block(Duration.ofSeconds(5));
        assertThat(foundRental).isNotNull();
    }

    @Test
    void deleteById_ShouldRemoveEntity() {
        // Given
        RentalDbo rental = createRentalDbo();
        RentalDbo savedRental = rentalRepository.save(rental)
            .block(Duration.ofSeconds(5));

        // When
        rentalRepository.deleteById(savedRental.getId())
            .block(Duration.ofSeconds(5));

        // Then
        RentalDbo foundRental = rentalRepository.findById(savedRental.getId())
            .block(Duration.ofSeconds(5));
        assertThat(foundRental).isNull();
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        RentalDbo rental = createRentalDbo();
        RentalDbo savedRental = rentalRepository.save(rental)
            .block(Duration.ofSeconds(5));

        // When
        Boolean exists = rentalRepository.existsById(savedRental.getId())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityDoesNotExist() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        
        // When
        Boolean exists = rentalRepository.existsById(nonExistentId)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(exists).isFalse();
    }
}