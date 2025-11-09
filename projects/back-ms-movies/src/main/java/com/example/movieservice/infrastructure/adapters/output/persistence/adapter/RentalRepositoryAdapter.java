
package com.example.movieservice.infrastructure.adapters.output.persistence.adapter;

import com.example.movieservice.domain.ports.output.RentalRepositoryPort;
import com.example.movieservice.domain.model.Rental;
import com.example.movieservice.infrastructure.adapters.output.persistence.entity.RentalDbo;
import com.example.movieservice.infrastructure.adapters.output.persistence.repository.JpaRentalRepository;
import com.example.movieservice.application.mapper.RentalMapper;
import com.example.movieservice.infrastructure.config.exceptions.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository adapter implementing the Rental domain port.
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
public class RentalRepositoryAdapter implements RentalRepositoryPort {

    private final JpaRentalRepository jpaRepository;
    private final RentalMapper mapper;

    @Override
    public Rental save(Rental rental) {
        log.debug("Saving Rental: {}", rental);
        try {
            RentalDbo dbo = mapper.toDbo(rental);
            RentalDbo savedDbo = jpaRepository.save(dbo);
            return mapper.toDomain(savedDbo);
        } catch (org.springframework.dao.DuplicateKeyException | org.springframework.dao.DataIntegrityViolationException e) {
            log.error("Database constraint violation while saving Rental: {}", e.getMessage(), e);
            throw e; // Propagate business exceptions to service layer
        } catch (Exception e) {
            log.error("Database error while saving Rental: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to save Rental", e);
        }
    }

    @Override
    public Optional<Rental> findById(String id) {
        log.debug("Finding Rental by id: {}", id);
        try {
            return jpaRepository.findById(UUID.fromString(id))
                    .map(mapper::toDomain);
        } catch (Exception e) {
            log.error("Database error while finding Rental by id {}: {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("Failed to find Rental by id", e);
        }
    }

    @Override
    public List<Rental> findAll() {
        log.debug("Finding all Rentals");
        try {
            return mapper.toDomainList(jpaRepository.findAll());
        } catch (Exception e) {
            log.error("Database error while finding all Rentals: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to find all Rentals", e);
        }
    }

    @Override
    public void deleteById(String id) {
        log.debug("Deleting Rental by id: {}", id);
        try {
            jpaRepository.deleteById(UUID.fromString(id));
        } catch (Exception e) {
            log.error("Database error while deleting Rental by id {}: {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("Failed to delete Rental by id", e);
        }
    }

    @Override
    public boolean existsById(String id) {
        log.debug("Checking if Rental exists by id: {}", id);
        try {
            return jpaRepository.existsById(UUID.fromString(id));
        } catch (Exception e) {
            log.error("Database error while checking if Rental exists by id {}: {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("Failed to check if Rental exists by id", e);
        }
    }

    @Override
    public List<Rental> findBySearchTerm(String search, Integer page, Integer size) {
        log.debug("Searching Rentals with term: {}, page: {}, size: {}", search, page, size);
        try {
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page != null && page > 0 ? page - 1 : 0, 
                size != null && size > 0 ? size : 20
            );
            
            return jpaRepository.findBySearchTerm(search != null ? search : "", pageable)
                    .map(mapper::toDomain)
                    .getContent();
        } catch (org.springframework.dao.DuplicateKeyException | org.springframework.dao.DataIntegrityViolationException e) {
            log.error("Database constraint violation while searching Rentals: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Database error while searching Rentals: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to search Rentals", e);
        }
    }
    
    // Additional business methods with pagination
    public Page<Rental> findBySearchTermPaged(String search, org.springframework.data.domain.Pageable pageable) {
        log.debug("Searching Rentals with term: {} and pagination: {}", search, pageable);
        return jpaRepository.findBySearchTerm(search, pageable)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<Rental> findAllPaged(Integer page, Integer size) {
        log.debug("Finding all Rentals with pagination: page={}, size={}", page, size);
        try {
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page != null && page > 0 ? page - 1 : 0, 
                size != null && size > 0 ? size : 20
            );
            
            return jpaRepository.findAllPaged(pageable)
                    .map(mapper::toDomain)
                    .getContent();
        } catch (Exception e) {
            log.error("Database error while finding all Rentals: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to find all Rentals", e);
        }
    }
    
    public Page<Rental> findAllPagedInternal(org.springframework.data.domain.Pageable pageable) {
        log.debug("Finding all Rentals with pagination: {}", pageable);
        return jpaRepository.findAllPaged(pageable)
                .map(mapper::toDomain);
    }
}
