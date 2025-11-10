package com.example.userservice.infrastructure.adapters.output.persistence.repository;

import com.example.userservice.infrastructure.adapters.output.persistence.entity.UserDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaUserRepository;
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
 * Integration tests for JpaUserRepository.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@DataR2dbcTest
class JpaUserRepositoryTest {

    @Autowired
    private JpaUserRepository userRepository;

    private UserDbo createUserDbo() {
        UUID randomUUID = UUID.randomUUID();
        return UserDbo.builder()
            .username("test-username-" + randomUUID)
            .email("test-" + randomUUID + "@example.com")
            .status(EntityStatus.ACTIVE)
            .build();
    }

    @Test
    void findById_ShouldReturnEntity_WhenExists() {
        // Given
        UserDbo user = createUserDbo();
        UserDbo savedUser = userRepository.save(user)
            .block(Duration.ofSeconds(5));

        // When
        UserDbo result = userRepository.findById(savedUser.getId())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedUser.getId());
    }

    @Test
    void save_ShouldPersistEntity() {
        // Given
        UserDbo user = createUserDbo();

        // When
        UserDbo savedUser = userRepository.save(user)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(savedUser.getId()).isNotNull();
        
        UserDbo foundUser = userRepository.findById(savedUser.getId())
            .block(Duration.ofSeconds(5));
        assertThat(foundUser).isNotNull();
    }

    @Test
    void deleteById_ShouldRemoveEntity() {
        // Given
        UserDbo user = createUserDbo();
        UserDbo savedUser = userRepository.save(user)
            .block(Duration.ofSeconds(5));

        // When
        userRepository.deleteById(savedUser.getId())
            .block(Duration.ofSeconds(5));

        // Then
        UserDbo foundUser = userRepository.findById(savedUser.getId())
            .block(Duration.ofSeconds(5));
        assertThat(foundUser).isNull();
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        UserDbo user = createUserDbo();
        UserDbo savedUser = userRepository.save(user)
            .block(Duration.ofSeconds(5));

        // When
        Boolean exists = userRepository.existsById(savedUser.getId())
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityDoesNotExist() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        
        // When
        Boolean exists = userRepository.existsById(nonExistentId)
            .block(Duration.ofSeconds(5));

        // Then
        assertThat(exists).isFalse();
    }
}