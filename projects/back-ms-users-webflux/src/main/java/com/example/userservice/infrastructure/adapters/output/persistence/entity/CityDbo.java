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
 * R2DBC Entity representing City data in the database.
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
@Table("cities")
public class CityDbo {

    @Id
    @Column("id")
    private String id;

    @Column("name")
    private String name;
    @Column("regionId")
    private String regionId;

    @Column("status")
    @Builder.Default
    private EntityStatus status = EntityStatus.ACTIVE;
    
    @Column("created_at")
    private String createdAt;

    @Column("updated_at")
    private String updatedAt;
}
