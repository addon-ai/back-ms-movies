package com.example.movieservice.application.dto.movie;

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
 * Data Transfer Object for GetRentalResponseContent.
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
@Schema(description = "Data Transfer Object for GetRentalResponseContent")
public class GetRentalResponseContent {

    @NotNull
    @JsonProperty("rentalId")
    @Schema(description = "rentalId field")
    private String rentalId;

    @NotNull
    @JsonProperty("movieId")
    @Schema(description = "movieId field")
    private String movieId;

    @NotNull
    @JsonProperty("userId")
    @Schema(description = "userId field")
    private String userId;

    @NotNull
    @JsonProperty("rentalDate")
    @Schema(description = "rentalDate field")
    private String rentalDate;

    @NotNull
    @JsonProperty("dueDate")
    @Schema(description = "dueDate field")
    private String dueDate;

    @JsonProperty("returnDate")
    @Schema(description = "returnDate field")
    private String returnDate;

    @NotNull
    @JsonProperty("totalPrice")
    @Schema(description = "totalPrice field")
    private Double totalPrice;

    @JsonProperty("lateFee")
    @Schema(description = "lateFee field")
    private Double lateFee;

    @NotNull
    @JsonProperty("createdAt")
    @Schema(description = "createdAt field")
    private String createdAt;

    @NotNull
    @JsonProperty("updatedAt")
    @Schema(description = "updatedAt field")
    private String updatedAt;

    @NotNull
    @JsonProperty("status")
    @Schema(description = "status field")
    private String status;
}
