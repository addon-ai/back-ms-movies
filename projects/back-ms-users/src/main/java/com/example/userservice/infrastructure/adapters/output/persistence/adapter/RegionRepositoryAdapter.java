
package com.example.userservice.infrastructure.adapters.output.persistence.adapter;

import com.example.userservice.domain.ports.output.RegionRepositoryPort;
import com.example.userservice.domain.model.Region;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.RegionDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaRegionRepository;
import com.example.userservice.application.mapper.RegionMapper;
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
 * Repository adapter implementing the Region domain port.
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
public class RegionRepositoryAdapter implements RegionRepositoryPort {

    private final JpaRegionRepository jpaRepository;
    private final RegionMapper mapper;

    @Override
    public Region save(Region region) {
        log.debug("Saving Region: {}", region);
        try {
            RegionDbo dbo = mapper.toDbo(region);
            RegionDbo savedDbo = jpaRepository.save(dbo);
            return mapper.toDomain(savedDbo);
        } catch (org.springframework.dao.DuplicateKeyException | org.springframework.dao.DataIntegrityViolationException e) {
            log.error("Database constraint violation while saving Region: {}", e.getMessage(), e);
            throw e; // Propagate business exceptions to service layer
        } catch (Exception e) {
            log.error("Database error while saving Region: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to save Region", e);
        }
    }

    @Override
    public Optional<Region> findById(String id) {
        log.debug("Finding Region by id: {}", id);
        try {
            return jpaRepository.findById(UUID.fromString(id))
                    .map(mapper::toDomain);
        } catch (Exception e) {
            log.error("Database error while finding Region by id {}: {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("Failed to find Region by id", e);
        }
    }

    @Override
    public List<Region> findAll() {
        log.debug("Finding all Regions");
        try {
            return mapper.toDomainList(jpaRepository.findAll());
        } catch (Exception e) {
            log.error("Database error while finding all Regions: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to find all Regions", e);
        }
    }

    @Override
    public void deleteById(String id) {
        log.debug("Deleting Region by id: {}", id);
        try {
            jpaRepository.deleteById(UUID.fromString(id));
        } catch (Exception e) {
            log.error("Database error while deleting Region by id {}: {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("Failed to delete Region by id", e);
        }
    }

    @Override
    public boolean existsById(String id) {
        log.debug("Checking if Region exists by id: {}", id);
        try {
            return jpaRepository.existsById(UUID.fromString(id));
        } catch (Exception e) {
            log.error("Database error while checking if Region exists by id {}: {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("Failed to check if Region exists by id", e);
        }
    }

    @Override
    public List<Region> findBySearchTerm(String search, Integer page, Integer size) {
        log.debug("Searching Regions with term: {}, page: {}, size: {}", search, page, size);
        try {
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page != null && page > 0 ? page - 1 : 0, 
                size != null && size > 0 ? size : 20
            );
            
            return jpaRepository.findBySearchTerm(search != null ? search : "", pageable)
                    .map(mapper::toDomain)
                    .getContent();
        } catch (org.springframework.dao.DuplicateKeyException | org.springframework.dao.DataIntegrityViolationException e) {
            log.error("Database constraint violation while searching Regions: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Database error while searching Regions: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to search Regions", e);
        }
    }
    
    // Additional business methods with pagination
    public Page<Region> findBySearchTermPaged(String search, org.springframework.data.domain.Pageable pageable) {
        log.debug("Searching Regions with term: {} and pagination: {}", search, pageable);
        return jpaRepository.findBySearchTerm(search, pageable)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<Region> findAllPaged(Integer page, Integer size) {
        log.debug("Finding all Regions with pagination: page={}, size={}", page, size);
        try {
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page != null && page > 0 ? page - 1 : 0, 
                size != null && size > 0 ? size : 20
            );
            
            return jpaRepository.findAllPaged(pageable)
                    .map(mapper::toDomain)
                    .getContent();
        } catch (Exception e) {
            log.error("Database error while finding all Regions: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to find all Regions", e);
        }
    }
    
    public Page<Region> findAllPagedInternal(org.springframework.data.domain.Pageable pageable) {
        log.debug("Finding all Regions with pagination: {}", pageable);
        return jpaRepository.findAllPaged(pageable)
                .map(mapper::toDomain);
    }
}
