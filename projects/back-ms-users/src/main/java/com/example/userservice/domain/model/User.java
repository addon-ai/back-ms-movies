package com.example.userservice.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object for User.
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
@Schema(description = "Data Transfer Object for User")
public class User {

    @JsonProperty("userId")
    @Schema(description = "userId field")
    private String userId;

    @JsonProperty("username")
    @Schema(description = "username field")
    private String username;

    @JsonProperty("email")
    @Schema(description = "email field")
    private String email;

    @JsonProperty("firstName")
    @Schema(description = "firstName field")
    private String firstName;

    @JsonProperty("lastName")
    @Schema(description = "lastName field")
    private String lastName;

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
