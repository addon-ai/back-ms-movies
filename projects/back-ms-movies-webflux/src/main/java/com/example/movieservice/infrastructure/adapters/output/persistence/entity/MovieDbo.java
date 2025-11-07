package com.example.movieservice.infrastructure.adapters.output.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.example.movieservice.domain.model.EntityStatus;
import java.math.BigDecimal;

/**
 * R2DBC Entity representing Movie data in the database.
 * <p>
 * This class serves as the Data Base Object (DBO) in the Clean Architecture,
 * containing R2DBC annotations for reactive persistence mapping. It includes audit fields
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
@Table("movies")
public class MovieDbo {

    @Id
    @Column("MovieId")
    private String id;

    @Column("title")
    private String title;
    @Column("director")
    private String director;
    @Column("genre")
    private String genre;
    @Column("releaseYear")
    private BigDecimal releaseYear;
    @Column("duration")
    private BigDecimal duration;
    @Column("description")
    private String description;
    @Column("availableCopies")
    private BigDecimal availableCopies;
    @Column("rentalPrice")
    private Double rentalPrice;

    @Column("status")
    @Builder.Default
    private EntityStatus status = EntityStatus.ACTIVE;
    
    @Column("created_at")
    private String createdAt;

    @Column("updated_at")
    private String updatedAt;
}
