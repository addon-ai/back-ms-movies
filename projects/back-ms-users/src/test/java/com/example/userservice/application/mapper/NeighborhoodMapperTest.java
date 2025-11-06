package com.example.userservice.application.mapper;

import com.example.userservice.domain.model.Neighborhood;
import com.example.userservice.domain.model.EntityStatus;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.NeighborhoodDbo;
import com.example.userservice.application.dto.location.NeighborhoodResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for NeighborhoodMapper.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@SpringBootTest
class NeighborhoodMapperTest {

    @Autowired
    private NeighborhoodMapper mapper;




    @Test
    void toDbo_ShouldMapCorrectly_FromDomain() {
        // Given
        Neighborhood domain = Neighborhood.builder()
            .build();

        // When
        NeighborhoodDbo result = mapper.toDbo(domain);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toDbo_ShouldMapCorrectly_WithStatus() {
        // Given
        Neighborhood domain = Neighborhood.builder()
            .status("ACTIVE")
            .build();

        // When
        NeighborhoodDbo result = mapper.toDbo(domain);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(EntityStatus.ACTIVE);
    }

    @Test
    void toDomain_ShouldMapCorrectly_FromDbo() {
        // Given
        NeighborhoodDbo dbo = NeighborhoodDbo.builder()
            .build();

        // When
        Neighborhood result = mapper.toDomain(dbo);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toDomainList_ShouldMapCorrectly_FromDboList() {
        // Given
        NeighborhoodDbo dbo = NeighborhoodDbo.builder().build();
        List<NeighborhoodDbo> dboList = Arrays.asList(dbo);

        // When
        List<Neighborhood> result = mapper.toDomainList(dboList);

        // Then
        assertThat(result).isNotNull().hasSize(1);
    }

    @Test
    void toDboList_ShouldMapCorrectly_FromDomainList() {
        // Given
        Neighborhood domain = Neighborhood.builder().build();
        List<Neighborhood> domainList = Arrays.asList(domain);

        // When
        List<NeighborhoodDbo> result = mapper.toDboList(domainList);

        // Then
        assertThat(result).isNotNull().hasSize(1);
    }


    @Test
    void toDto_ShouldMapCorrectly_FromDomain() {
        // Given
        Neighborhood domain = Neighborhood.builder().build();

        // When
        NeighborhoodResponse result = mapper.toDto(domain);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toDtoList_ShouldMapCorrectly_FromDomainList() {
        // Given
        Neighborhood domain = Neighborhood.builder().build();
        List<Neighborhood> domainList = Arrays.asList(domain);

        // When
        List<NeighborhoodResponse> result = mapper.toDtoList(domainList);

        // Then
        assertThat(result).isNotNull().hasSize(1);
    }

    // Null parameter coverage tests


    @Test
    void toDto_ShouldReturnNull_WhenDomainIsNull() {
        // When
        NeighborhoodResponse result = mapper.toDto(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDtoList_ShouldReturnNull_WhenDomainsIsNull() {
        // When
        List<NeighborhoodResponse> result = mapper.toDtoList(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDomain_ShouldReturnNull_WhenDboIsNull() {
        // When
        Neighborhood result = mapper.toDomain(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDbo_ShouldReturnNull_WhenDomainIsNull() {
        // When
        NeighborhoodDbo result = mapper.toDbo(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDomainList_ShouldReturnNull_WhenDboListIsNull() {
        // When
        List<Neighborhood> result = mapper.toDomainList(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDboList_ShouldReturnNull_WhenDomainListIsNull() {
        // When
        List<NeighborhoodDbo> result = mapper.toDboList(null);

        // Then
        assertThat(result).isNull();
    }


}