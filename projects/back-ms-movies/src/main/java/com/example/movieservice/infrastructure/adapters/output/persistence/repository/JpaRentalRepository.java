package com.example.movieservice.infrastructure.adapters.output.persistence.repository;

import com.example.movieservice.infrastructure.adapters.output.persistence.entity.RentalDbo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Rental entities.
 * <p>
 * This interface extends JpaRepository to provide standard CRUD operations
 * and includes custom query methods for specific business requirements.
 * It operates on RentalDbo entities for database persistence.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@Repository
public interface JpaRentalRepository extends JpaRepository<RentalDbo, String> {
    
    /**
     * Find entities with pagination and search functionality.
     * 
     * Searches in: status, 
     * 
     * 
     */
    @Query("SELECT e FROM RentalDbo e WHERE " +
           "(:search IS NULL OR " +
           "LOWER(e.status) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<RentalDbo> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    /**
     * Find all entities with pagination.
     */
    @Query("SELECT e FROM RentalDbo e")
    Page<RentalDbo> findAllPaged(Pageable pageable);
}

