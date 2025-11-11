package com.example.movieservice.application.dto.movie;

import java.math.BigDecimal;
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
 * Data Transfer Object for CreateMovieRequestContent.
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
@Schema(description = "Data Transfer Object for CreateMovieRequestContent")
public class CreateMovieRequestContent {

    @NotNull
    @JsonProperty("title")
    @Schema(description = "title field")
    private String title;

    @NotNull
    @JsonProperty("director")
    @Schema(description = "director field")
    private String director;

    @NotNull
    @JsonProperty("genre")
    @Schema(description = "genre field")
    private String genre;

    @NotNull
    @JsonProperty("releaseYear")
    @Schema(description = "releaseYear field")
    private BigDecimal releaseYear;

    @NotNull
    @JsonProperty("duration")
    @Schema(description = "duration field")
    private BigDecimal duration;

    @JsonProperty("description")
    @Schema(description = "description field")
    private String description;

    @NotNull
    @JsonProperty("availableCopies")
    @Schema(description = "availableCopies field")
    private BigDecimal availableCopies;

    @NotNull
    @JsonProperty("rentalPrice")
    @Schema(description = "rentalPrice field")
    private Double rentalPrice;
}
