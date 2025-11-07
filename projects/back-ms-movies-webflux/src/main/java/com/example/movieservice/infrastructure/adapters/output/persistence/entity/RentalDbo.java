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
    @Column("rental_id")
    private String id;

    @Column("movie_id")
    private String movieId;
    @Column("user_id")
    private String userId;
    @Column("rental_date")
    private String rentalDate;
    @Column("due_date")
    private String dueDate;
    @Column("return_date")
    private String returnDate;
    @Column("total_price")
    private Double totalPrice;
    @Column("late_fee")
    private Double lateFee;

    @Column("status")
    @Builder.Default
    private EntityStatus status = EntityStatus.ACTIVE;
    
    @Column("created_at")
    private String createdAt;

    @Column("updated_at")
    private String updatedAt;
}
