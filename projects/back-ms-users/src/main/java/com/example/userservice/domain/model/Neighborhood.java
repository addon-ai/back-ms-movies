package com.example.userservice.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object for Neighborhood.
 * <p>
 * This class represents data transferred between the application layers,
 * containing validation annotations and JSON serialization configuration.
 * It serves as the contract for API communication in Clean Architecture.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for Neighborhood")
public class Neighborhood {

    @JsonProperty("neighborhoodId")
    @Schema(description = "neighborhoodId field")
    private String neighborhoodId;

    @JsonProperty("name")
    @Schema(description = "name field")
    private String name;

    @JsonProperty("cityId")
    @Schema(description = "cityId field")
    private String cityId;

    @JsonProperty("status")
    @Schema(description = "status field")
    private String status;

    @JsonProperty("createdAt")
    @Schema(description = "createdAt field")
    private String createdAt;

    @JsonProperty("updatedAt")
    @Schema(description = "updatedAt field")
    private String updatedAt;
}
