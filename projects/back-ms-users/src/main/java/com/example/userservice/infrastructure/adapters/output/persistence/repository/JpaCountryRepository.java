package com.example.userservice.infrastructure.adapters.output.persistence.repository;

import com.example.userservice.infrastructure.adapters.output.persistence.entity.CountryDbo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Country entities.
 * <p>
 * This interface extends JpaRepository to provide standard CRUD operations
 * and includes custom query methods for specific business requirements.
 * It operates on CountryDbo entities for database persistence.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@Repository
public interface JpaCountryRepository extends JpaRepository<CountryDbo, String> {
    
    /**
     * Find entities with pagination and search functionality.
     * 
     * Searches in: name, status, 
     * 
     * 
     */
    @Query("SELECT e FROM CountryDbo e WHERE " +
           "(:search IS NULL OR " +
           "LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.status) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CountryDbo> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    /**
     * Find all entities with pagination.
     */
    @Query("SELECT e FROM CountryDbo e")
    Page<CountryDbo> findAllPaged(Pageable pageable);
}

