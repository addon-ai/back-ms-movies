package com.example.userservice.infrastructure.adapters.output.persistence.repository;

import com.example.userservice.infrastructure.adapters.output.persistence.entity.UserDbo;
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
 * Spring Data JPA Repository for User entities.
 * <p>
 * This interface extends JpaRepository to provide standard CRUD operations
 * and includes custom query methods for specific business requirements.
 * It operates on UserDbo entities for database persistence.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@Repository
public interface JpaUserRepository extends JpaRepository<UserDbo, UUID> {
    
    /**
     * Find entities with pagination and search functionality.
     */
    @Query("SELECT e FROM UserDbo e WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(e.username) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.first_name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.last_name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY e.createdAt DESC")
    Page<UserDbo> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    /**
     * Count entities matching search term.
     */
    @Query("SELECT COUNT(e) FROM UserDbo e WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(e.username) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.first_name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.last_name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Long countBySearchTerm(@Param("search") String search);
    
    /**
     * Find entities with combined filters including status and date range.
     */
    @Query("SELECT e FROM UserDbo e WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(e.username) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.first_name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.last_name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:status IS NULL OR :status = '' OR e.status = :status) " +
           "AND (:dateFrom IS NULL OR :dateFrom = '' OR e.createdAt >= CAST(:dateFrom AS TIMESTAMP)) " +
           "AND (:dateTo IS NULL OR :dateTo = '' OR e.createdAt <= CAST(:dateTo AS TIMESTAMP)) " +
           "ORDER BY e.createdAt DESC")
    Page<UserDbo> findByFilters(@Param("search") String search, 
                                          @Param("status") String status,
                                          @Param("dateFrom") String dateFrom,
                                          @Param("dateTo") String dateTo,
                                          Pageable pageable);
    
    /**
     * Find all entities with pagination.
     */
    @Query("SELECT e FROM UserDbo e ORDER BY e.createdAt DESC")
    Page<UserDbo> findAllPaged(Pageable pageable);
}

