package com.example.userservice.application.mapper;

import com.example.userservice.domain.model.Country;
import com.example.userservice.domain.model.EntityStatus;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.CountryDbo;
import com.example.userservice.application.dto.location.CountryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for CountryMapper.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
class CountryMapperTest {

    private CountryMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(CountryMapper.class);
    }




    @Test
    void toDbo_ShouldMapCorrectly_FromDomain() {
        // Given
        Country domain = Country.builder()
            .build();

        // When
        CountryDbo result = mapper.toDbo(domain);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toDbo_ShouldMapCorrectly_WithStatus() {
        // Given
        Country domain = Country.builder()
            .status("ACTIVE")
            .build();

        // When
        CountryDbo result = mapper.toDbo(domain);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(EntityStatus.ACTIVE);
    }

    @Test
    void toDomain_ShouldMapCorrectly_FromDbo() {
        // Given
        CountryDbo dbo = CountryDbo.builder()
            .build();

        // When
        Country result = mapper.toDomain(dbo);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toDomainList_ShouldMapCorrectly_FromDboList() {
        // Given
        CountryDbo dbo = CountryDbo.builder().build();
        List<CountryDbo> dboList = Arrays.asList(dbo);

        // When
        List<Country> result = mapper.toDomainList(dboList);

        // Then
        assertThat(result).isNotNull().hasSize(1);
    }

    @Test
    void toDboList_ShouldMapCorrectly_FromDomainList() {
        // Given
        Country domain = Country.builder().build();
        List<Country> domainList = Arrays.asList(domain);

        // When
        List<CountryDbo> result = mapper.toDboList(domainList);

        // Then
        assertThat(result).isNotNull().hasSize(1);
    }


    @Test
    void toDto_ShouldMapCorrectly_FromDomain() {
        // Given
        Country domain = Country.builder().build();

        // When
        CountryResponse result = mapper.toDto(domain);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toDtoList_ShouldMapCorrectly_FromDomainList() {
        // Given
        Country domain = Country.builder().build();
        List<Country> domainList = Arrays.asList(domain);

        // When
        List<CountryResponse> result = mapper.toDtoList(domainList);

        // Then
        assertThat(result).isNotNull().hasSize(1);
    }

    // Null parameter coverage tests


    @Test
    void toDto_ShouldReturnNull_WhenDomainIsNull() {
        // When
        CountryResponse result = mapper.toDto(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDtoList_ShouldReturnNull_WhenDomainsIsNull() {
        // When
        List<CountryResponse> result = mapper.toDtoList(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDomain_ShouldReturnNull_WhenDboIsNull() {
        // When
        Country result = mapper.toDomain(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDbo_ShouldReturnNull_WhenDomainIsNull() {
        // When
        CountryDbo result = mapper.toDbo(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDomainList_ShouldReturnNull_WhenDboListIsNull() {
        // When
        List<Country> result = mapper.toDomainList(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDboList_ShouldReturnNull_WhenDomainListIsNull() {
        // When
        List<CountryDbo> result = mapper.toDboList(null);

        // Then
        assertThat(result).isNull();
    }


}