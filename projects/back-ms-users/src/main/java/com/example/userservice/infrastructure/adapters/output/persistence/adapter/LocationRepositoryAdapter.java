
package com.example.userservice.infrastructure.adapters.output.persistence.adapter;

import com.example.userservice.domain.ports.output.LocationRepositoryPort;
import com.example.userservice.domain.model.Location;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.LocationDbo;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaLocationRepository;
import com.example.userservice.application.mapper.LocationMapper;
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
 * Repository adapter implementing the Location domain port.
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
public class LocationRepositoryAdapter implements LocationRepositoryPort {

    private final JpaLocationRepository jpaRepository;
    private final LocationMapper mapper;

    @Override
    public Location save(Location location) {
        log.debug("Saving Location: {}", location);
        try {
            LocationDbo dbo = mapper.toDbo(location);
            LocationDbo savedDbo = jpaRepository.save(dbo);
            return mapper.toDomain(savedDbo);
        } catch (org.springframework.dao.DuplicateKeyException | org.springframework.dao.DataIntegrityViolationException e) {
            log.error("Database constraint violation while saving Location: {}", e.getMessage(), e);
            throw e; // Propagate business exceptions to service layer
        } catch (Exception e) {
            log.error("Database error while saving Location: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to save Location", e);
        }
    }

    @Override
    public Optional<Location> findById(String id) {
        log.debug("Finding Location by id: {}", id);
        try {
            return jpaRepository.findById(UUID.fromString(id))
                    .map(mapper::toDomain);
        } catch (Exception e) {
            log.error("Database error while finding Location by id {}: {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("Failed to find Location by id", e);
        }
    }

    @Override
    public List<Location> findAll() {
        log.debug("Finding all Locations");
        try {
            return mapper.toDomainList(jpaRepository.findAll());
        } catch (Exception e) {
            log.error("Database error while finding all Locations: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to find all Locations", e);
        }
    }

    @Override
    public void deleteById(String id) {
        log.debug("Deleting Location by id: {}", id);
        try {
            jpaRepository.deleteById(UUID.fromString(id));
        } catch (Exception e) {
            log.error("Database error while deleting Location by id {}: {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("Failed to delete Location by id", e);
        }
    }

    @Override
    public boolean existsById(String id) {
        log.debug("Checking if Location exists by id: {}", id);
        try {
            return jpaRepository.existsById(UUID.fromString(id));
        } catch (Exception e) {
            log.error("Database error while checking if Location exists by id {}: {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("Failed to check if Location exists by id", e);
        }
    }

    @Override
    public List<Location> findBySearchTerm(String search, Integer page, Integer size) {
        log.debug("Searching Locations with term: {}, page: {}, size: {}", search, page, size);
        try {
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page != null && page > 0 ? page - 1 : 0, 
                size != null && size > 0 ? size : 20
            );
            
            return jpaRepository.findBySearchTerm(search != null ? search : "", pageable)
                    .map(mapper::toDomain)
                    .getContent();
        } catch (org.springframework.dao.DuplicateKeyException | org.springframework.dao.DataIntegrityViolationException e) {
            log.error("Database constraint violation while searching Locations: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Database error while searching Locations: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to search Locations", e);
        }
    }
    
    @Override
    public List<Location> findByFilters(String search, String status, String dateFrom, String dateTo, Integer page, Integer size) {
        log.debug("Searching Locations with filters - search: {}, status: {}, dateFrom: {}, dateTo: {}, page: {}, size: {}", 
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
            log.error("Database constraint violation while filtering Locations: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Database error while filtering Locations: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to filter Locations", e);
        }
    }
    
    // Additional business methods with pagination
    public Page<Location> findBySearchTermPaged(String search, org.springframework.data.domain.Pageable pageable) {
        log.debug("Searching Locations with term: {} and pagination: {}", search, pageable);
        return jpaRepository.findBySearchTerm(search, pageable)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<Location> findAllPaged(Integer page, Integer size) {
        log.debug("Finding all Locations with pagination: page={}, size={}", page, size);
        try {
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page != null && page > 0 ? page - 1 : 0, 
                size != null && size > 0 ? size : 20
            );
            
            return jpaRepository.findAllPaged(pageable)
                    .map(mapper::toDomain)
                    .getContent();
        } catch (Exception e) {
            log.error("Database error while finding all Locations: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to find all Locations", e);
        }
    }
    
    public Page<Location> findAllPagedInternal(org.springframework.data.domain.Pageable pageable) {
        log.debug("Finding all Locations with pagination: {}", pageable);
        return jpaRepository.findAllPaged(pageable)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<Location> findNeighborhoodsByCity(String cityId) {
        log.debug("Executing findNeighborhoodsByCity with parameters: {}", cityId);
        try {
            // TODO: Implement custom query for findNeighborhoodsByCity
            return mapper.toDomainList(jpaRepository.findAll());
        } catch (Exception e) {
            log.error("Database error in findNeighborhoodsByCity: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to execute findNeighborhoodsByCity", e);
        }
    }
    
    @Override
    public List<Location> findRegionsByCountry(String countryId) {
        log.debug("Executing findRegionsByCountry with parameters: {}", countryId);
        try {
            // TODO: Implement custom query for findRegionsByCountry
            return mapper.toDomainList(jpaRepository.findAll());
        } catch (Exception e) {
            log.error("Database error in findRegionsByCountry: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to execute findRegionsByCountry", e);
        }
    }
    
    @Override
    public List<Location> findCitiesByRegion(String regionId) {
        log.debug("Executing findCitiesByRegion with parameters: {}", regionId);
        try {
            // TODO: Implement custom query for findCitiesByRegion
            return mapper.toDomainList(jpaRepository.findAll());
        } catch (Exception e) {
            log.error("Database error in findCitiesByRegion: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to execute findCitiesByRegion", e);
        }
    }
    
}
