package com.example.movieservice.application.dto.movie;

import com.example.movieservice.application.dto.movie.UpdateRentalRequestContent;
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
 * Data Transfer Object for UpdateRentalRequest.
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
@Schema(description = "Data Transfer Object for UpdateRentalRequest")
public class UpdateRentalRequest {

    @NotNull
    @JsonProperty("")
    @Schema(description = "rentalId field")
    private String rentalId;

    @NotNull
    @Valid
    @JsonProperty("")
    @Schema(description = "body field")
    private UpdateRentalRequestContent body;
}
