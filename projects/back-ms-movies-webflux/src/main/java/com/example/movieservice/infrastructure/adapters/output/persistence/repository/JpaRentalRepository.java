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
     */
    @Query("SELECT * FROM rentals e WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(e.status) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY e.created_at DESC " +
           "LIMIT :limit OFFSET :offset")
    Flux<RentalDbo> findBySearchTerm(@Param("search") String search, 
                                             @Param("limit") Long limit, 
                                             @Param("offset") Long offset);
    
    /**
     * Count entities matching search term.
     */
    @Query("SELECT COUNT(*) FROM rentals e WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(e.status) LIKE LOWER(CONCAT('%', :search, '%')))")
    Mono<Long> countBySearchTerm(@Param("search") String search);
    
    /**
     * Find entities with comprehensive filtering.
     */
    @Query("SELECT * FROM rentals e WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(e.status) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:status IS NULL OR :status = '' OR e.status = :status) " +
           "AND (:dateFrom IS NULL OR :dateFrom = '' OR e.created_at >= CAST(:dateFrom AS TIMESTAMP)) " +
           "AND (:dateTo IS NULL OR :dateTo = '' OR e.created_at <= CAST(:dateTo AS TIMESTAMP)) " +
           "ORDER BY e.created_at DESC " +
           "LIMIT :limit OFFSET :offset")
    Flux<RentalDbo> findByFilters(@Param("search") String search,
                                          @Param("status") String status,
                                          @Param("dateFrom") String dateFrom,
                                          @Param("dateTo") String dateTo,
                                          @Param("limit") Long limit,
                                          @Param("offset") Long offset);
    
    /**
     * Find all entities with pagination.
     */
    @Query("SELECT * FROM rentals e ORDER BY e.created_at DESC LIMIT :limit OFFSET :offset")
    Flux<RentalDbo> findAllPaged(@Param("limit") Long limit, @Param("offset") Long offset);
    
    /**
     * Count all entities.
     */
    @Query("SELECT COUNT(*) FROM rentals")
    Mono<Long> countAll();
}

