package com.example.userservice.infrastructure.adapters.output.persistence.repository;

import com.example.userservice.infrastructure.adapters.output.persistence.entity.LocationDbo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Location entities.
 * <p>
 * This interface extends JpaRepository to provide standard CRUD operations
 * and includes custom query methods for specific business requirements.
 * It operates on LocationDbo entities for database persistence.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@Repository
public interface JpaLocationRepository extends JpaRepository<LocationDbo, String> {
    
    /**
     * Find entities with pagination and search functionality.
     * 
     * Searches in: status, 
     * 
     * 
     */
    @Query("SELECT e FROM LocationDbo e WHERE " +
           "(:search IS NULL OR " +
           "LOWER(e.status) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<LocationDbo> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    /**
     * Find all entities with pagination.
     */
    @Query("SELECT e FROM LocationDbo e")
    Page<LocationDbo> findAllPaged(Pageable pageable);
}

