package com.example.userservice.infrastructure.adapters.output.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.example.userservice.domain.model.EntityStatus;
import java.util.UUID;
import java.time.Instant;

/**
 * R2DBC Entity representing Region data in the database.
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
@Table("regions")
public class RegionDbo {

    @Id
    @Column("region_id")
    private UUID id;

    @Column("name")
    private String name;
    @Column("code")
    private String code;
    @Column("country_id")
    private String countryId;

    @Column("status")
    @Builder.Default
    private EntityStatus status = EntityStatus.ACTIVE;
    
    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}
