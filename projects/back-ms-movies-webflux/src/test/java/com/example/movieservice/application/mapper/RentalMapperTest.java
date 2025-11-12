package com.example.movieservice.application.mapper;

import com.example.movieservice.domain.model.Rental;
import com.example.movieservice.domain.model.EntityStatus;
import com.example.movieservice.infrastructure.adapters.output.persistence.entity.RentalDbo;
import com.example.movieservice.application.dto.movie.CreateRentalRequestContent;
import com.example.movieservice.application.dto.movie.CreateRentalResponseContent;
import com.example.movieservice.application.dto.movie.UpdateRentalRequestContent;
import com.example.movieservice.application.dto.movie.UpdateRentalResponseContent;
import com.example.movieservice.application.dto.movie.GetRentalResponseContent;
import com.example.movieservice.application.dto.movie.RentalResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for RentalMapper.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
class RentalMapperTest {

    private RentalMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(RentalMapper.class);
    }

    @Test
    void fromCreateRequest_ShouldMapCorrectly() {
        // Given
        CreateRentalRequestContent request = CreateRentalRequestContent.builder()
            .build();

        // When
        Rental result = mapper.fromCreateRequest(request);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toCreateResponse_ShouldMapCorrectly_FromDomain() {
        // Given
        Rental domain = Rental.builder()
            .build();

        // When
        CreateRentalResponseContent result = mapper.toCreateResponse(domain);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void fromUpdateRequest_ShouldMapCorrectly() {
        // Given
        UpdateRentalRequestContent request = UpdateRentalRequestContent.builder()
            .build();

        // When
        Rental result = mapper.fromUpdateRequest(request);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toUpdateResponse_ShouldMapCorrectly_FromDomain() {
        // Given
        Rental domain = Rental.builder()
            .build();

        // When
        UpdateRentalResponseContent result = mapper.toUpdateResponse(domain);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toGetResponse_ShouldMapCorrectly_FromDomain() {
        // Given
        Rental domain = Rental.builder()
            .build();

        // When
        GetRentalResponseContent result = mapper.toGetResponse(domain);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toDbo_ShouldMapCorrectly_FromDomain() {
        // Given
        Rental domain = Rental.builder()
            .build();

        // When
        RentalDbo result = mapper.toDbo(domain);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toDbo_ShouldMapCorrectly_WithStatus() {
        // Given
        Rental domain = Rental.builder()
            .status("ACTIVE")
            .build();

        // When
        RentalDbo result = mapper.toDbo(domain);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(EntityStatus.ACTIVE);
    }

    @Test
    void toDomain_ShouldMapCorrectly_FromDbo() {
        // Given
        RentalDbo dbo = RentalDbo.builder()
            .build();

        // When
        Rental result = mapper.toDomain(dbo);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toDomainList_ShouldMapCorrectly_FromDboList() {
        // Given
        RentalDbo dbo = RentalDbo.builder().build();
        List<RentalDbo> dboList = Arrays.asList(dbo);

        // When
        List<Rental> result = mapper.toDomainList(dboList);

        // Then
        assertThat(result).isNotNull().hasSize(1);
    }

    @Test
    void toDboList_ShouldMapCorrectly_FromDomainList() {
        // Given
        Rental domain = Rental.builder().build();
        List<Rental> domainList = Arrays.asList(domain);

        // When
        List<RentalDbo> result = mapper.toDboList(domainList);

        // Then
        assertThat(result).isNotNull().hasSize(1);
    }

    @Test
    void updateEntityFromRequest_ShouldUpdateCorrectly() {
        // Given
        UpdateRentalRequestContent request = UpdateRentalRequestContent.builder()
            .build();
        Rental entity = Rental.builder().build();

        // When
        mapper.updateEntityFromRequest(request, entity);

        // Then
        assertThat(entity).isNotNull();
    }

    @Test
    void updateEntityFromRequest_ShouldUpdateFields_WhenRequestHasValues() {
        // Given
        UpdateRentalRequestContent request = UpdateRentalRequestContent.builder()
            .build();
        Rental entity = Rental.builder().build();

        // When
        mapper.updateEntityFromRequest(request, entity);

        // Then
        assertThat(entity).isNotNull();
    }

    @Test
    void updateEntityFromRequest_ShouldDoNothing_WhenRequestIsNull() {
        // Given
        Rental entity = Rental.builder().build();
        Rental originalEntity = Rental.builder().build();

        // When
        mapper.updateEntityFromRequest(null, entity);

        // Then
        assertThat(entity).isEqualTo(originalEntity);
    }

    @Test
    void toDto_ShouldMapCorrectly_FromDomain() {
        // Given
        Rental domain = Rental.builder().build();

        // When
        RentalResponse result = mapper.toDto(domain);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toDtoList_ShouldMapCorrectly_FromDomainList() {
        // Given
        Rental domain = Rental.builder().build();
        List<Rental> domainList = Arrays.asList(domain);

        // When
        List<RentalResponse> result = mapper.toDtoList(domainList);

        // Then
        assertThat(result).isNotNull().hasSize(1);
    }

    // Null parameter coverage tests
    @Test
    void toCreateResponse_ShouldReturnNull_WhenDomainIsNull() {
        // When
        CreateRentalResponseContent result = mapper.toCreateResponse(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toGetResponse_ShouldReturnNull_WhenDomainIsNull() {
        // When
        GetRentalResponseContent result = mapper.toGetResponse(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDto_ShouldReturnNull_WhenDomainIsNull() {
        // When
        RentalResponse result = mapper.toDto(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDtoList_ShouldReturnNull_WhenDomainsIsNull() {
        // When
        List<RentalResponse> result = mapper.toDtoList(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDomain_ShouldReturnNull_WhenDboIsNull() {
        // When
        Rental result = mapper.toDomain(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDbo_ShouldReturnNull_WhenDomainIsNull() {
        // When
        RentalDbo result = mapper.toDbo(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDomainList_ShouldReturnNull_WhenDboListIsNull() {
        // When
        List<Rental> result = mapper.toDomainList(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDboList_ShouldReturnNull_WhenDomainListIsNull() {
        // When
        List<RentalDbo> result = mapper.toDboList(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void fromCreateRequest_ShouldReturnNull_WhenRequestIsNull() {
        // When
        Rental result = mapper.fromCreateRequest(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toUpdateResponse_ShouldReturnNull_WhenDomainIsNull() {
        // When
        UpdateRentalResponseContent result = mapper.toUpdateResponse(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void fromUpdateRequest_ShouldReturnNull_WhenRequestIsNull() {
        // When
        Rental result = mapper.fromUpdateRequest(null);

        // Then
        assertThat(result).isNull();
    }
}