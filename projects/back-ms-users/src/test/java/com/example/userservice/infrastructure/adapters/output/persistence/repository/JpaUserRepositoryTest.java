package com.example.userservice.infrastructure.adapters.output.persistence.repository;

import com.example.userservice.infrastructure.adapters.output.persistence.entity.UserDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaUserRepository;
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
 * Integration tests for JpaUserRepository.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JpaUserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JpaUserRepository userRepository;

    private UserDbo createUserDbo() {
        return UserDbo.builder()
            .username("test-username")
            .email("test@example.com")
            .status(EntityStatus.ACTIVE)
            .build();
    }

    @Test
    void findById_ShouldReturnEntity_WhenExists() {
        // Given
        UserDbo user = createUserDbo();
        UserDbo savedUser = entityManager.persistAndFlush(user);

        // When
        Optional<UserDbo> result = userRepository.findById(savedUser.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    void save_ShouldPersistEntity() {
        // Given
        UserDbo user = createUserDbo();

        // When
        UserDbo savedUser = userRepository.save(user);

        // Then
        assertThat(savedUser.getId()).isNotNull();
        
        UserDbo foundUser = entityManager.find(UserDbo.class, savedUser.getId());
        assertThat(foundUser).isNotNull();
    }

    @Test
    void deleteById_ShouldRemoveEntity() {
        // Given
        UserDbo user = createUserDbo();
        UserDbo savedUser = entityManager.persistAndFlush(user);

        // When
        userRepository.deleteById(savedUser.getId());
        entityManager.flush();

        // Then
        UserDbo foundUser = entityManager.find(UserDbo.class, savedUser.getId());
        assertThat(foundUser).isNull();
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEntityExists() {
        // Given
        UserDbo user = createUserDbo();
        UserDbo savedUser = entityManager.persistAndFlush(user);

        // When
        boolean exists = userRepository.existsById(savedUser.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_ShouldReturnFalse_WhenEntityDoesNotExist() {
        // When
        boolean exists = userRepository.existsById("non-existent-id");

        // Then
        assertThat(exists).isFalse();
    }
}