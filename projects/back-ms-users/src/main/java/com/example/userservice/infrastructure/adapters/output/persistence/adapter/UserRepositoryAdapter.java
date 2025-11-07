
package com.example.userservice.infrastructure.adapters.output.persistence.adapter;

import com.example.userservice.domain.ports.output.UserRepositoryPort;
import com.example.userservice.domain.model.User;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.UserDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaUserRepository;
import com.example.userservice.application.mapper.UserMapper;
import com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

/**
 * Repository adapter implementing the User domain port.
 * <p>
 * This adapter serves as the output adapter in Clean Architecture,
 * implementing the domain repository interface and delegating to
 * Spring Data JPA repository. It handles the conversion between
 * domain objects and database entities using MapStruct.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final JpaUserRepository jpaRepository;
    private final UserMapper mapper;

    @Override
    public User save(User user) {
        log.debug("Saving User: {}", user);
        try {
            UserDbo dbo = mapper.toDbo(user);
            UserDbo savedDbo = jpaRepository.save(dbo);
            return mapper.toDomain(savedDbo);
        } catch (Exception e) {
            log.error("Database error while saving User: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to save User", e);
        }
    }

    @Override
    public Optional<User> findById(String id) {
        log.debug("Finding User by id: {}", id);
        try {
            return jpaRepository.findById(id)
                    .map(mapper::toDomain);
        } catch (Exception e) {
            log.error("Database error while finding User by id {}: {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("Failed to find User by id", e);
        }
    }

    @Override
    public List<User> findAll() {
        log.debug("Finding all Users");
        try {
            return mapper.toDomainList(jpaRepository.findAll());
        } catch (Exception e) {
            log.error("Database error while finding all Users: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to find all Users", e);
        }
    }

    @Override
    public void deleteById(String id) {
        log.debug("Deleting User by id: {}", id);
        try {
            jpaRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Database error while deleting User by id {}: {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("Failed to delete User by id", e);
        }
    }

    @Override
    public boolean existsById(String id) {
        log.debug("Checking if User exists by id: {}", id);
        try {
            return jpaRepository.existsById(id);
        } catch (Exception e) {
            log.error("Database error while checking if User exists by id {}: {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("Failed to check if User exists by id", e);
        }
    }

    @Override
    public List<User> findBySearchTerm(String search, Integer page, Integer size) {
        log.debug("Searching Users with term: {}, page: {}, size: {}", search, page, size);
        try {
            if (search == null || search.trim().isEmpty()) {
                return mapper.toDomainList(jpaRepository.findAll());
            }
            
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page != null && page > 0 ? page - 1 : 0, 
                size != null && size > 0 ? size : 20
            );
            
            return jpaRepository.findBySearchTerm(search, pageable)
                    .map(mapper::toDomain)
                    .getContent();
        } catch (Exception e) {
            log.error("Database error while searching Users: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to search Users", e);
        }
    }
    
    // Additional business methods with pagination
    public Page<User> findBySearchTermPaged(String search, org.springframework.data.domain.Pageable pageable) {
        log.debug("Searching Users with term: {} and pagination: {}", search, pageable);
        return jpaRepository.findBySearchTerm(search, pageable)
                .map(mapper::toDomain);
    }
    
    public Page<User> findAllPaged(org.springframework.data.domain.Pageable pageable) {
        log.debug("Finding all Users with pagination: {}", pageable);
        return jpaRepository.findAllPaged(pageable)
                .map(mapper::toDomain);
    }
}
