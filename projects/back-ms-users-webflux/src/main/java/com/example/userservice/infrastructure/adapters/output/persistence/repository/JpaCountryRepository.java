package com.example.userservice.infrastructure.adapters.output.persistence.repository;

import com.example.userservice.infrastructure.adapters.output.persistence.entity.CountryDbo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

/**
 * Spring Data R2DBC Repository for Country entities.
 * <p>
 * This interface extends R2dbcRepository to provide reactive CRUD operations
 * and includes custom query methods for specific business requirements.
 * It operates on CountryDbo entities for reactive database persistence.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@Repository
public interface JpaCountryRepository extends R2dbcRepository<CountryDbo, UUID> {
    
    /**
     * Find entities with search functionality.
     */
    @Query("SELECT * FROM countries u WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.first_name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.last_name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY u.created_at DESC " +
           "LIMIT :limit OFFSET :offset")
    Flux<CountryDbo> findBySearchTerm(@Param("search") String search, 
                                             @Param("limit") Long limit, 
                                             @Param("offset") Long offset);
    
    /**
     * Count entities matching search term.
     */
    @Query("SELECT COUNT(*) FROM countries u WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.first_name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.last_name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Mono<Long> countBySearchTerm(@Param("search") String search);
    
    /**
     * Find all entities with pagination.
     */
    @Query("SELECT * FROM countries u ORDER BY u.created_at DESC LIMIT :limit OFFSET :offset")
    Flux<CountryDbo> findAllPaged(@Param("limit") Long limit, @Param("offset") Long offset);
    
    /**
     * Count all entities.
     */
    @Query("SELECT COUNT(*) FROM countries")
    Mono<Long> countAll();
}

