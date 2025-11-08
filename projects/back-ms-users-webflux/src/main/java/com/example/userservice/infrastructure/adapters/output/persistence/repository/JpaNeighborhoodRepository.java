package com.example.userservice.infrastructure.adapters.output.persistence.repository;

import com.example.userservice.infrastructure.adapters.output.persistence.entity.NeighborhoodDbo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

/**
 * Spring Data R2DBC Repository for Neighborhood entities.
 * <p>
 * This interface extends R2dbcRepository to provide reactive CRUD operations
 * and includes custom query methods for specific business requirements.
 * It operates on NeighborhoodDbo entities for reactive database persistence.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@Repository
public interface JpaNeighborhoodRepository extends R2dbcRepository<NeighborhoodDbo, UUID> {
    
    /**
     * Find entities with search functionality.
     * 
     * Searches in: name, status, 
     * 
     * 
     */
    @Query("SELECT * FROM neighborhoods WHERE " +
           "(:search IS NULL OR " +
           "LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.status) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "LIMIT :limit OFFSET :offset")
    Flux<NeighborhoodDbo> findBySearchTerm(@Param("search") String search, 
                                             @Param("limit") Long limit, 
                                             @Param("offset") Long offset);
    
    /**
     * Count entities matching search term.
     */
    @Query("SELECT COUNT(*) FROM neighborhoods WHERE " +
           "(:search IS NULL OR " +
           "LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.status) LIKE LOWER(CONCAT('%', :search, '%')))")
    Mono<Long> countBySearchTerm(@Param("search") String search);
    
    /**
     * Find all entities with pagination.
     */
    @Query("SELECT * FROM neighborhoods LIMIT :limit OFFSET :offset")
    Flux<NeighborhoodDbo> findAllPaged(@Param("limit") Long limit, @Param("offset") Long offset);
}

