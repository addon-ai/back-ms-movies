
package com.example.userservice.infrastructure.adapters.output.persistence.adapter;

import com.example.userservice.domain.ports.output.CountryRepositoryPort;
import com.example.userservice.domain.model.Country;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.CountryDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaCountryRepository;
import com.example.userservice.application.mapper.CountryMapper;
import com.example.userservice.infrastructure.config.exceptions.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

/**
 * Repository adapter implementing the Country domain port.
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
public class CountryRepositoryAdapter implements CountryRepositoryPort {

    private final JpaCountryRepository jpaRepository;
    private final CountryMapper mapper;

    @Override
    public Country save(Country country) {
        log.debug("Saving Country: {}", country);
        try {
            CountryDbo dbo = mapper.toDbo(country);
            CountryDbo savedDbo = jpaRepository.save(dbo);
            return mapper.toDomain(savedDbo);
        } catch (Exception e) {
            log.error("Database error while saving Country: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to save Country", e);
        }
    }

    @Override
    public Optional<Country> findById(String id) {
        log.debug("Finding Country by id: {}", id);
        try {
            return jpaRepository.findById(id)
                    .map(mapper::toDomain);
        } catch (Exception e) {
            log.error("Database error while finding Country by id {}: {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("Failed to find Country by id", e);
        }
    }

    @Override
    public List<Country> findAll() {
        log.debug("Finding all Countries");
        try {
            return mapper.toDomainList(jpaRepository.findAll());
        } catch (Exception e) {
            log.error("Database error while finding all Countries: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to find all Countries", e);
        }
    }

    @Override
    public void deleteById(String id) {
        log.debug("Deleting Country by id: {}", id);
        try {
            jpaRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Database error while deleting Country by id {}: {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("Failed to delete Country by id", e);
        }
    }

    @Override
    public boolean existsById(String id) {
        log.debug("Checking if Country exists by id: {}", id);
        try {
            return jpaRepository.existsById(id);
        } catch (Exception e) {
            log.error("Database error while checking if Country exists by id {}: {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("Failed to check if Country exists by id", e);
        }
    }

    @Override
    public List<Country> findBySearchTerm(String search, Integer page, Integer size) {
        log.debug("Searching Countries with term: {}, page: {}, size: {}", search, page, size);
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
            log.error("Database error while searching Countries: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to search Countries", e);
        }
    }
    
    // Additional business methods with pagination
    public Page<Country> findBySearchTermPaged(String search, org.springframework.data.domain.Pageable pageable) {
        log.debug("Searching Countries with term: {} and pagination: {}", search, pageable);
        return jpaRepository.findBySearchTerm(search, pageable)
                .map(mapper::toDomain);
    }
    
    public Page<Country> findAllPaged(org.springframework.data.domain.Pageable pageable) {
        log.debug("Finding all Countries with pagination: {}", pageable);
        return jpaRepository.findAllPaged(pageable)
                .map(mapper::toDomain);
    }
}
