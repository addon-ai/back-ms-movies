
package com.example.userservice.infrastructure.adapters.output.persistence.adapter;

import com.example.userservice.domain.ports.output.CityRepositoryPort;
import com.example.userservice.domain.model.City;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.CityDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaCityRepository;
import com.example.userservice.application.mapper.CityMapper;
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
 * Repository adapter implementing the City domain port.
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
public class CityRepositoryAdapter implements CityRepositoryPort {

    private final JpaCityRepository jpaRepository;
    private final CityMapper mapper;

    @Override
    public City save(City city) {
        log.debug("Saving City: {}", city);
        try {
            CityDbo dbo = mapper.toDbo(city);
            CityDbo savedDbo = jpaRepository.save(dbo);
            return mapper.toDomain(savedDbo);
        } catch (org.springframework.dao.DuplicateKeyException | org.springframework.dao.DataIntegrityViolationException e) {
            log.error("Database constraint violation while saving City: {}", e.getMessage(), e);
            throw e; // Propagate business exceptions to service layer
        } catch (Exception e) {
            log.error("Database error while saving City: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to save City", e);
        }
    }

    @Override
    public Optional<City> findById(String id) {
        log.debug("Finding City by id: {}", id);
        try {
            return jpaRepository.findById(UUID.fromString(id))
                    .map(mapper::toDomain);
        } catch (Exception e) {
            log.error("Database error while finding City by id {}: {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("Failed to find City by id", e);
        }
    }

    @Override
    public List<City> findAll() {
        log.debug("Finding all Cities");
        try {
            return mapper.toDomainList(jpaRepository.findAll());
        } catch (Exception e) {
            log.error("Database error while finding all Cities: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to find all Cities", e);
        }
    }

    @Override
    public void deleteById(String id) {
        log.debug("Deleting City by id: {}", id);
        try {
            jpaRepository.deleteById(UUID.fromString(id));
        } catch (Exception e) {
            log.error("Database error while deleting City by id {}: {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("Failed to delete City by id", e);
        }
    }

    @Override
    public boolean existsById(String id) {
        log.debug("Checking if City exists by id: {}", id);
        try {
            return jpaRepository.existsById(UUID.fromString(id));
        } catch (Exception e) {
            log.error("Database error while checking if City exists by id {}: {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("Failed to check if City exists by id", e);
        }
    }

    @Override
    public List<City> findBySearchTerm(String search, Integer page, Integer size) {
        log.debug("Searching Cities with term: {}, page: {}, size: {}", search, page, size);
        try {
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page != null && page > 0 ? page - 1 : 0, 
                size != null && size > 0 ? size : 20
            );
            
            return jpaRepository.findBySearchTerm(search != null ? search : "", pageable)
                    .map(mapper::toDomain)
                    .getContent();
        } catch (org.springframework.dao.DuplicateKeyException | org.springframework.dao.DataIntegrityViolationException e) {
            log.error("Database constraint violation while searching Cities: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Database error while searching Cities: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to search Cities", e);
        }
    }
    
    @Override
    public List<City> findByFilters(String search, String status, String dateFrom, String dateTo, Integer page, Integer size) {
        log.debug("Searching Cities with filters - search: {}, status: {}, dateFrom: {}, dateTo: {}, page: {}, size: {}", 
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
            log.error("Database constraint violation while filtering Cities: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Database error while filtering Cities: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to filter Cities", e);
        }
    }
    
    // Additional business methods with pagination
    public Page<City> findBySearchTermPaged(String search, org.springframework.data.domain.Pageable pageable) {
        log.debug("Searching Cities with term: {} and pagination: {}", search, pageable);
        return jpaRepository.findBySearchTerm(search, pageable)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<City> findAllPaged(Integer page, Integer size) {
        log.debug("Finding all Cities with pagination: page={}, size={}", page, size);
        try {
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page != null && page > 0 ? page - 1 : 0, 
                size != null && size > 0 ? size : 20
            );
            
            return jpaRepository.findAllPaged(pageable)
                    .map(mapper::toDomain)
                    .getContent();
        } catch (Exception e) {
            log.error("Database error while finding all Cities: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to find all Cities", e);
        }
    }
    
    public Page<City> findAllPagedInternal(org.springframework.data.domain.Pageable pageable) {
        log.debug("Finding all Cities with pagination: {}", pageable);
        return jpaRepository.findAllPaged(pageable)
                .map(mapper::toDomain);
    }
}
