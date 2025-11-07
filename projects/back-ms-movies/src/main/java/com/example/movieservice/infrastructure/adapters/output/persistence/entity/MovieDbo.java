package com.example.movieservice.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.GenericGenerator;
import com.example.movieservice.domain.model.EntityStatus;
import java.math.BigDecimal;

/**
 * JPA Entity representing Movie data in the database.
 * <p>
 * This class serves as the Data Base Object (DBO) in the Clean Architecture,
 * containing JPA annotations for persistence mapping. It includes audit fields
 * for tracking creation and modification timestamps.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movies")
public class MovieDbo {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "movie_id", updatable = false, nullable = false)
    private String id;

    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "director", nullable = false)
    private String director;
    @Column(name = "genre", nullable = false)
    private String genre;
    @Column(name = "release_year", nullable = false)
    private BigDecimal releaseYear;
    @Column(name = "duration", nullable = false)
    private BigDecimal duration;
    @Column(name = "description")
    private String description;
    @Column(name = "available_copies", nullable = false)
    private BigDecimal availableCopies;
    @Column(name = "rental_price", nullable = false)
    private Double rentalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private EntityStatus status = EntityStatus.ACTIVE;
    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;
}