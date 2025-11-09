
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
import java.util.UUID;

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
        } catch (org.springframework.dao.DuplicateKeyException | org.springframework.dao.DataIntegrityViolationException e) {
            log.error("Database constraint violation while saving User: {}", e.getMessage(), e);
            throw e; // Propagate business exceptions to service layer
        } catch (Exception e) {
            log.error("Database error while saving User: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to save User", e);
        }
    }

    @Override
    public Optional<User> findById(String id) {
        log.debug("Finding User by id: {}", id);
        try {
            return jpaRepository.findById(UUID.fromString(id))
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
            jpaRepository.deleteById(UUID.fromString(id));
        } catch (Exception e) {
            log.error("Database error while deleting User by id {}: {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("Failed to delete User by id", e);
        }
    }

    @Override
    public boolean existsById(String id) {
        log.debug("Checking if User exists by id: {}", id);
        try {
            return jpaRepository.existsById(UUID.fromString(id));
        } catch (Exception e) {
            log.error("Database error while checking if User exists by id {}: {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("Failed to check if User exists by id", e);
        }
    }

    @Override
    public List<User> findBySearchTerm(String search, Integer page, Integer size) {
        log.debug("Searching Users with term: {}, page: {}, size: {}", search, page, size);
        try {
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page != null && page > 0 ? page - 1 : 0, 
                size != null && size > 0 ? size : 20
            );
            
            return jpaRepository.findBySearchTerm(search != null ? search : "", pageable)
                    .map(mapper::toDomain)
                    .getContent();
        } catch (org.springframework.dao.DuplicateKeyException | org.springframework.dao.DataIntegrityViolationException e) {
            log.error("Database constraint violation while searching Users: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Database error while searching Users: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to search Users", e);
        }
    }
    
    @Override
    public List<User> findByFilters(String search, String status, String dateFrom, String dateTo, Integer page, Integer size) {
        log.debug("Searching Users with filters - search: {}, status: {}, dateFrom: {}, dateTo: {}, page: {}, size: {}", 
                 search, status, dateFrom, dateTo, page, size);
        try {
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page != null && page > 0 ? page - 1 : 0, 
                size != null && size > 0 ? size : 20
            );
            
            return jpaRepository.findByFilters(
                search != null ? search : "", 
                status != null ? status : "",
                dateFrom != null ? dateFrom : "",
                dateTo != null ? dateTo : "",
                pageable
            ).map(mapper::toDomain).getContent();
        } catch (org.springframework.dao.DuplicateKeyException | org.springframework.dao.DataIntegrityViolationException e) {
            log.error("Database constraint violation while filtering Users: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Database error while filtering Users: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to filter Users", e);
        }
    }
    
    // Additional business methods with pagination
    public Page<User> findBySearchTermPaged(String search, org.springframework.data.domain.Pageable pageable) {
        log.debug("Searching Users with term: {} and pagination: {}", search, pageable);
        return jpaRepository.findBySearchTerm(search, pageable)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<User> findAllPaged(Integer page, Integer size) {
        log.debug("Finding all Users with pagination: page={}, size={}", page, size);
        try {
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page != null && page > 0 ? page - 1 : 0, 
                size != null && size > 0 ? size : 20
            );
            
            return jpaRepository.findAllPaged(pageable)
                    .map(mapper::toDomain)
                    .getContent();
        } catch (Exception e) {
            log.error("Database error while finding all Users: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to find all Users", e);
        }
    }
    
    public Page<User> findAllPagedInternal(org.springframework.data.domain.Pageable pageable) {
        log.debug("Finding all Users with pagination: {}", pageable);
        return jpaRepository.findAllPaged(pageable)
                .map(mapper::toDomain);
    }
}
