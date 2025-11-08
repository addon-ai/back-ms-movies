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
 * Data Transfer Object for UpdateLocationRequestContent.
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
@Schema(description = "Data Transfer Object for UpdateLocationRequestContent")
public class UpdateLocationRequestContent {

    @JsonProperty("countryId")
    @Schema(description = "countryId field")
    private String countryId;

    @JsonProperty("regionId")
    @Schema(description = "regionId field")
    private String regionId;

    @JsonProperty("cityId")
    @Schema(description = "cityId field")
    private String cityId;

    @JsonProperty("neighborhoodId")
    @Schema(description = "neighborhoodId field")
    private String neighborhoodId;

    @JsonProperty("address")
    @Schema(description = "address field")
    private String address;

    @JsonProperty("postalCode")
    @Schema(description = "postalCode field")
    private String postalCode;

    @JsonProperty("latitude")
    @Schema(description = "latitude field")
    private Double latitude;

    @JsonProperty("longitude")
    @Schema(description = "longitude field")
    private Double longitude;

    @JsonProperty("locationType")
    @Schema(description = "locationType field")
    private String locationType;
}
