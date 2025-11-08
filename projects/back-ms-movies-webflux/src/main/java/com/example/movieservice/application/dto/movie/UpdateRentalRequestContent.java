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
 * Data Transfer Object for UpdateRentalRequestContent.
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
@Schema(description = "Data Transfer Object for UpdateRentalRequestContent")
public class UpdateRentalRequestContent {

    @JsonProperty("returnDate")
    @Schema(description = "returnDate field")
    private String returnDate;

    @JsonProperty("lateFee")
    @Schema(description = "lateFee field")
    private Double lateFee;

    @JsonProperty("status")
    @Schema(description = "status field")
    private String status;
}
