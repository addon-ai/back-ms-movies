package com.example.userservice.infrastructure.adapters.output.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.example.userservice.domain.model.EntityStatus;

/**
 * R2DBC Entity representing Location data in the database.
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
@Table("locations")
public class LocationDbo {

    @Id
    @Column("LocationId")
    private String id;

    @Column("userId")
    private String userId;
    @Column("country")
    private String country;
    @Column("region")
    private String region;
    @Column("city")
    private String city;
    @Column("neighborhood")
    private String neighborhood;
    @Column("address")
    private String address;
    @Column("postalCode")
    private String postalCode;
    @Column("latitude")
    private Double latitude;
    @Column("longitude")
    private Double longitude;
    @Column("locationType")
    private String locationType;

    @Column("status")
    @Builder.Default
    private EntityStatus status = EntityStatus.ACTIVE;
    
    @Column("created_at")
    private String createdAt;

    @Column("updated_at")
    private String updatedAt;
}
