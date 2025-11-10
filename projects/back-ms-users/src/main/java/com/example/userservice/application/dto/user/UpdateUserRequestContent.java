package com.example.userservice.application.dto.user;

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
 * Data Transfer Object for UpdateUserRequestContent.
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
@Schema(description = "Data Transfer Object for UpdateUserRequestContent")
public class UpdateUserRequestContent {

    @Size(min = 1, max = 100)
    @JsonProperty("firstName")
    @Schema(description = "firstName field")
    private String firstName;

    @Size(min = 1, max = 100)
    @JsonProperty("lastName")
    @Schema(description = "lastName field")
    private String lastName;

    @Pattern(regexp = "^[^@]+@[^@]+\\.[^@]+$")
    @JsonProperty("email")
    @Schema(description = "email field")
    private String email;
}
