package com.example.movieservice.infrastructure.adapters.output.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.example.movieservice.domain.model.EntityStatus;

/**
 * R2DBC Entity representing Rental data in the database.
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
@Table("rentals")
public class RentalDbo {

    @Id
    @Column("id")
    private String id;

    @Column("movieId")
    private String movieId;
    @Column("userId")
    private String userId;
    @Column("rentalDate")
    private String rentalDate;
    @Column("dueDate")
    private String dueDate;
    @Column("returnDate")
    private String returnDate;
    @Column("totalPrice")
    private Double totalPrice;
    @Column("lateFee")
    private Double lateFee;

    @Column("status")
    @Builder.Default
    private EntityStatus status = EntityStatus.ACTIVE;
    
    @Column("created_at")
    private String createdAt;

    @Column("updated_at")
    private String updatedAt;
}
