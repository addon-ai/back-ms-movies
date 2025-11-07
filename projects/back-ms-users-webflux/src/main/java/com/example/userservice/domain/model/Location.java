package com.example.userservice.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object for Location.
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
@Schema(description = "Data Transfer Object for Location")
public class Location {

    @JsonProperty("locationId")
    @Schema(description = "locationId field")
    private String locationId;

    @JsonProperty("userId")
    @Schema(description = "userId field")
    private String userId;

    @JsonProperty("country")
    @Schema(description = "country field")
    private String country;

    @JsonProperty("region")
    @Schema(description = "region field")
    private String region;

    @JsonProperty("city")
    @Schema(description = "city field")
    private String city;

    @JsonProperty("neighborhood")
    @Schema(description = "neighborhood field")
    private String neighborhood;

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

    @JsonProperty("createdAt")
    @Schema(description = "createdAt field")
    private String createdAt;

    @JsonProperty("updatedAt")
    @Schema(description = "updatedAt field")
    private String updatedAt;

    @JsonProperty("status")
    @Schema(description = "status field")
    private String status;
}
