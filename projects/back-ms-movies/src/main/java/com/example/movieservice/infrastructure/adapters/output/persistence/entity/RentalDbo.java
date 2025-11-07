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

/**
 * JPA Entity representing Rental data in the database.
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
@Table(name = "rentals")
public class RentalDbo {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "movieId", nullable = false)
    private String movieId;
    @Column(name = "userId", nullable = false)
    private String userId;
    @Column(name = "rentalDate", nullable = false)
    private String rentalDate;
    @Column(name = "dueDate", nullable = false)
    private String dueDate;
    @Column(name = "returnDate")
    private String returnDate;
    @Column(name = "totalPrice", nullable = false)
    private Double totalPrice;
    @Column(name = "lateFee")
    private Double lateFee;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private EntityStatus status = EntityStatus.ACTIVE;
    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;
}