package com.example.movieservice.infrastructure.adapters.output.persistence.repository;

import com.example.movieservice.infrastructure.adapters.output.persistence.entity.RentalDbo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

/**
 * Spring Data R2DBC Repository for Rental entities.
 * <p>
 * This interface extends R2dbcRepository to provide reactive CRUD operations
 * and includes custom query methods for specific business requirements.
 * It operates on RentalDbo entities for reactive database persistence.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@Repository
public interface JpaRentalRepository extends R2dbcRepository<RentalDbo, UUID> {
    
    /**
     * Find entities with search functionality.
     * 
     * Searches in: status, 
     * 
     * 
     */
    @Query("SELECT * FROM rentals WHERE " +
           "(:search IS NULL OR " +
           "LOWER(e.status) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "LIMIT :limit OFFSET :offset")
    Flux<RentalDbo> findBySearchTerm(@Param("search") String search, 
                                             @Param("limit") Long limit, 
                                             @Param("offset") Long offset);
    
    /**
     * Count entities matching search term.
     */
    @Query("SELECT COUNT(*) FROM rentals WHERE " +
           "(:search IS NULL OR " +
           "LOWER(e.status) LIKE LOWER(CONCAT('%', :search, '%')))")
    Mono<Long> countBySearchTerm(@Param("search") String search);
    
    /**
     * Find all entities with pagination.
     */
    @Query("SELECT * FROM rentals LIMIT :limit OFFSET :offset")
    Flux<RentalDbo> findAllPaged(@Param("limit") Long limit, @Param("offset") Long offset);
}

