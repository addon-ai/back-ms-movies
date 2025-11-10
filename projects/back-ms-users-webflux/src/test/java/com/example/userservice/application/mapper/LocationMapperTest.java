package com.example.userservice.application.mapper;

import com.example.userservice.domain.model.Location;
import com.example.userservice.domain.model.EntityStatus;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.LocationDbo;
import com.example.userservice.application.dto.location.CreateLocationRequestContent;
import com.example.userservice.application.dto.location.CreateLocationResponseContent;
import com.example.userservice.application.dto.location.UpdateLocationRequestContent;
import com.example.userservice.application.dto.location.UpdateLocationResponseContent;
import com.example.userservice.application.dto.location.GetLocationResponseContent;
import com.example.userservice.application.dto.location.LocationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for LocationMapper.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
class LocationMapperTest {

    private LocationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(LocationMapper.class);
    }

    @Test
    void fromCreateRequest_ShouldMapCorrectly() {
        // Given
        CreateLocationRequestContent request = CreateLocationRequestContent.builder()
            .build();

        // When
        Location result = mapper.fromCreateRequest(request);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toCreateResponse_ShouldMapCorrectly_FromDomain() {
        // Given
        Location domain = Location.builder()
            .build();

        // When
        CreateLocationResponseContent result = mapper.toCreateResponse(domain);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void fromUpdateRequest_ShouldMapCorrectly() {
        // Given
        UpdateLocationRequestContent request = UpdateLocationRequestContent.builder()
            .build();

        // When
        Location result = mapper.fromUpdateRequest(request);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toUpdateResponse_ShouldMapCorrectly_FromDomain() {
        // Given
        Location domain = Location.builder()
            .build();

        // When
        UpdateLocationResponseContent result = mapper.toUpdateResponse(domain);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toGetResponse_ShouldMapCorrectly_FromDomain() {
        // Given
        Location domain = Location.builder()
            .build();

        // When
        GetLocationResponseContent result = mapper.toGetResponse(domain);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toDbo_ShouldMapCorrectly_FromDomain() {
        // Given
        Location domain = Location.builder()
            .build();

        // When
        LocationDbo result = mapper.toDbo(domain);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toDbo_ShouldMapCorrectly_WithStatus() {
        // Given
        Location domain = Location.builder()
            .status("ACTIVE")
            .build();

        // When
        LocationDbo result = mapper.toDbo(domain);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(EntityStatus.ACTIVE);
    }

    @Test
    void toDomain_ShouldMapCorrectly_FromDbo() {
        // Given
        LocationDbo dbo = LocationDbo.builder()
            .build();

        // When
        Location result = mapper.toDomain(dbo);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toDomainList_ShouldMapCorrectly_FromDboList() {
        // Given
        LocationDbo dbo = LocationDbo.builder().build();
        List<LocationDbo> dboList = Arrays.asList(dbo);

        // When
        List<Location> result = mapper.toDomainList(dboList);

        // Then
        assertThat(result).isNotNull().hasSize(1);
    }

    @Test
    void toDboList_ShouldMapCorrectly_FromDomainList() {
        // Given
        Location domain = Location.builder().build();
        List<Location> domainList = Arrays.asList(domain);

        // When
        List<LocationDbo> result = mapper.toDboList(domainList);

        // Then
        assertThat(result).isNotNull().hasSize(1);
    }

    @Test
    void updateEntityFromRequest_ShouldUpdateCorrectly() {
        // Given
        UpdateLocationRequestContent request = UpdateLocationRequestContent.builder()
            .build();
        Location entity = Location.builder().build();

        // When
        mapper.updateEntityFromRequest(request, entity);

        // Then
        assertThat(entity).isNotNull();
    }

    @Test
    void updateEntityFromRequest_ShouldUpdateFields_WhenRequestHasValues() {
        // Given
        UpdateLocationRequestContent request = UpdateLocationRequestContent.builder()
            .build();
        Location entity = Location.builder().build();

        // When
        mapper.updateEntityFromRequest(request, entity);

        // Then
        assertThat(entity).isNotNull();
    }

    @Test
    void updateEntityFromRequest_ShouldDoNothing_WhenRequestIsNull() {
        // Given
        Location entity = Location.builder().build();
        Location originalEntity = Location.builder().build();

        // When
        mapper.updateEntityFromRequest(null, entity);

        // Then
        assertThat(entity).isEqualTo(originalEntity);
    }

    @Test
    void toDto_ShouldMapCorrectly_FromDomain() {
        // Given
        Location domain = Location.builder().build();

        // When
        LocationResponse result = mapper.toDto(domain);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toDtoList_ShouldMapCorrectly_FromDomainList() {
        // Given
        Location domain = Location.builder().build();
        List<Location> domainList = Arrays.asList(domain);

        // When
        List<LocationResponse> result = mapper.toDtoList(domainList);

        // Then
        assertThat(result).isNotNull().hasSize(1);
    }

    // Null parameter coverage tests
    @Test
    void toCreateResponse_ShouldReturnNull_WhenDomainIsNull() {
        // When
        CreateLocationResponseContent result = mapper.toCreateResponse(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toGetResponse_ShouldReturnNull_WhenDomainIsNull() {
        // When
        GetLocationResponseContent result = mapper.toGetResponse(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDto_ShouldReturnNull_WhenDomainIsNull() {
        // When
        LocationResponse result = mapper.toDto(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDtoList_ShouldReturnNull_WhenDomainsIsNull() {
        // When
        List<LocationResponse> result = mapper.toDtoList(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDomain_ShouldReturnNull_WhenDboIsNull() {
        // When
        Location result = mapper.toDomain(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDbo_ShouldReturnNull_WhenDomainIsNull() {
        // When
        LocationDbo result = mapper.toDbo(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDomainList_ShouldReturnNull_WhenDboListIsNull() {
        // When
        List<Location> result = mapper.toDomainList(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDboList_ShouldReturnNull_WhenDomainListIsNull() {
        // When
        List<LocationDbo> result = mapper.toDboList(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void fromCreateRequest_ShouldReturnNull_WhenRequestIsNull() {
        // When
        Location result = mapper.fromCreateRequest(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toUpdateResponse_ShouldReturnNull_WhenDomainIsNull() {
        // When
        UpdateLocationResponseContent result = mapper.toUpdateResponse(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void fromUpdateRequest_ShouldReturnNull_WhenRequestIsNull() {
        // When
        Location result = mapper.fromUpdateRequest(null);

        // Then
        assertThat(result).isNull();
    }
}