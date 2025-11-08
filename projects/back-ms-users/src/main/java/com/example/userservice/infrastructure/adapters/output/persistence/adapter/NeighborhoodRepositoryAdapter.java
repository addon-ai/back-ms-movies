
package com.example.userservice.infrastructure.adapters.output.persistence.adapter;

import com.example.userservice.domain.ports.output.NeighborhoodRepositoryPort;
import com.example.userservice.domain.model.Neighborhood;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.NeighborhoodDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaNeighborhoodRepository;
import com.example.userservice.application.mapper.NeighborhoodMapper;
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
 * Repository adapter implementing the Neighborhood domain port.
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
public class NeighborhoodRepositoryAdapter implements NeighborhoodRepositoryPort {

    private final JpaNeighborhoodRepository jpaRepository;
    private final NeighborhoodMapper mapper;

    @Override
    public Neighborhood save(Neighborhood neighborhood) {
        log.debug("Saving Neighborhood: {}", neighborhood);
        try {
            NeighborhoodDbo dbo = mapper.toDbo(neighborhood);
            NeighborhoodDbo savedDbo = jpaRepository.save(dbo);
            return mapper.toDomain(savedDbo);
        } catch (Exception e) {
            log.error("Database error while saving Neighborhood: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to save Neighborhood", e);
        }
    }

    @Override
    public Optional<Neighborhood> findById(String id) {
        log.debug("Finding Neighborhood by id: {}", id);
        try {
            return jpaRepository.findById(UUID.fromString(id))
                    .map(mapper::toDomain);
        } catch (Exception e) {
            log.error("Database error while finding Neighborhood by id {}: {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("Failed to find Neighborhood by id", e);
        }
    }

    @Override
    public List<Neighborhood> findAll() {
        log.debug("Finding all Neighborhoods");
        try {
            return mapper.toDomainList(jpaRepository.findAll());
        } catch (Exception e) {
            log.error("Database error while finding all Neighborhoods: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to find all Neighborhoods", e);
        }
    }

    @Override
    public void deleteById(String id) {
        log.debug("Deleting Neighborhood by id: {}", id);
        try {
            jpaRepository.deleteById(UUID.fromString(id));
        } catch (Exception e) {
            log.error("Database error while deleting Neighborhood by id {}: {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("Failed to delete Neighborhood by id", e);
        }
    }

    @Override
    public boolean existsById(String id) {
        log.debug("Checking if Neighborhood exists by id: {}", id);
        try {
            return jpaRepository.existsById(UUID.fromString(id));
        } catch (Exception e) {
            log.error("Database error while checking if Neighborhood exists by id {}: {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("Failed to check if Neighborhood exists by id", e);
        }
    }

    @Override
    public List<Neighborhood> findBySearchTerm(String search, Integer page, Integer size) {
        log.debug("Searching Neighborhoods with term: {}, page: {}, size: {}", search, page, size);
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
            log.error("Database error while searching Neighborhoods: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to search Neighborhoods", e);
        }
    }
    
    // Additional business methods with pagination
    public Page<Neighborhood> findBySearchTermPaged(String search, org.springframework.data.domain.Pageable pageable) {
        log.debug("Searching Neighborhoods with term: {} and pagination: {}", search, pageable);
        return jpaRepository.findBySearchTerm(search, pageable)
                .map(mapper::toDomain);
    }
    
    public Page<Neighborhood> findAllPaged(org.springframework.data.domain.Pageable pageable) {
        log.debug("Finding all Neighborhoods with pagination: {}", pageable);
        return jpaRepository.findAllPaged(pageable)
                .map(mapper::toDomain);
    }
}
