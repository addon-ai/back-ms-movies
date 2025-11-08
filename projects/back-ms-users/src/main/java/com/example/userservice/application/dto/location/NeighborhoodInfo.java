package com.example.userservice.application.dto.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object for NeighborhoodInfo.
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
@Schema(description = "Data Transfer Object for NeighborhoodInfo")
public class NeighborhoodInfo {

    @NotNull
    @JsonProperty("neighborhoodId")
    @Schema(description = "neighborhoodId field")
    private String neighborhoodId;

    @NotNull
    @JsonProperty("name")
    @Schema(description = "name field")
    private String name;

    @NotNull
    @JsonProperty("cityId")
    @Schema(description = "cityId field")
    private String cityId;

    @NotNull
    @JsonProperty("status")
    @Schema(description = "status field")
    private String status;

    @NotNull
    @JsonProperty("createdAt")
    @Schema(description = "createdAt field")
    private String createdAt;

    @NotNull
    @JsonProperty("updatedAt")
    @Schema(description = "updatedAt field")
    private String updatedAt;
}
