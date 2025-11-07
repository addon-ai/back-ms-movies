package com.example.movieservice.application.dto.movie;

import java.math.BigDecimal;
import java.util.List;
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
 * Data Transfer Object for ListMoviesResponseContent.
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
@Schema(description = "Data Transfer Object for ListMoviesResponseContent")
public class ListMoviesResponseContent {

    @NotNull
    @JsonProperty("movies")
    @Schema(description = "movies field")
    private List<MovieResponse> movies;

    @NotNull
    @JsonProperty("page")
    @Schema(description = "page field")
    private BigDecimal page;

    @NotNull
    @JsonProperty("size")
    @Schema(description = "size field")
    private BigDecimal size;

    @NotNull
    @JsonProperty("total")
    @Schema(description = "total field")
    private BigDecimal total;

    @NotNull
    @JsonProperty("totalPages")
    @Schema(description = "totalPages field")
    private BigDecimal totalPages;
}
