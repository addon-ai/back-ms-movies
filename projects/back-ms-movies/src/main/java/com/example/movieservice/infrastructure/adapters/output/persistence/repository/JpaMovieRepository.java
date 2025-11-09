package com.example.movieservice.infrastructure.adapters.output.persistence.repository;

import com.example.movieservice.infrastructure.adapters.output.persistence.entity.MovieDbo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for Movie entities.
 * <p>
 * This interface extends JpaRepository to provide standard CRUD operations
 * and includes custom query methods for specific business requirements.
 * It operates on MovieDbo entities for database persistence.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@Repository
public interface JpaMovieRepository extends JpaRepository<MovieDbo, UUID> {
    
    /**
     * Find entities with pagination and search functionality.
     */
    @Query("SELECT e FROM MovieDbo e WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(e.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY e.createdAt DESC")
    Page<MovieDbo> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    /**
     * Count entities matching search term.
     */
    @Query("SELECT COUNT(e) FROM MovieDbo e WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(e.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Long countBySearchTerm(@Param("search") String search);
    
    /**
     * Find all entities with pagination.
     */
    @Query("SELECT e FROM MovieDbo e ORDER BY e.createdAt DESC")
    Page<MovieDbo> findAllPaged(Pageable pageable);
}

