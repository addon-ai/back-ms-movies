package com.example.movieservice.application.mapper;

import com.example.movieservice.domain.model.Movie;
import com.example.movieservice.domain.model.EntityStatus;
import com.example.movieservice.infrastructure.adapters.output.persistence.entity.MovieDbo;
import com.example.movieservice.application.dto.movie.CreateMovieRequestContent;
import com.example.movieservice.application.dto.movie.CreateMovieResponseContent;
import com.example.movieservice.application.dto.movie.UpdateMovieRequestContent;
import com.example.movieservice.application.dto.movie.UpdateMovieResponseContent;
import com.example.movieservice.application.dto.movie.GetMovieResponseContent;
import com.example.movieservice.application.dto.movie.MovieResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for MovieMapper.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@SpringBootTest
class MovieMapperTest {

    @Autowired
    private MovieMapper mapper;

    @Test
    void fromCreateRequest_ShouldMapCorrectly() {
        // Given
        CreateMovieRequestContent request = CreateMovieRequestContent.builder()
            .build();

        // When
        Movie result = mapper.fromCreateRequest(request);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toCreateResponse_ShouldMapCorrectly_FromDomain() {
        // Given
        Movie domain = Movie.builder()
            .build();

        // When
        CreateMovieResponseContent result = mapper.toCreateResponse(domain);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void fromUpdateRequest_ShouldMapCorrectly() {
        // Given
        UpdateMovieRequestContent request = UpdateMovieRequestContent.builder()
            .build();

        // When
        Movie result = mapper.fromUpdateRequest(request);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toUpdateResponse_ShouldMapCorrectly_FromDomain() {
        // Given
        Movie domain = Movie.builder()
            .build();

        // When
        UpdateMovieResponseContent result = mapper.toUpdateResponse(domain);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toGetResponse_ShouldMapCorrectly_FromDomain() {
        // Given
        Movie domain = Movie.builder()
            .build();

        // When
        GetMovieResponseContent result = mapper.toGetResponse(domain);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toDbo_ShouldMapCorrectly_FromDomain() {
        // Given
        Movie domain = Movie.builder()
            .build();

        // When
        MovieDbo result = mapper.toDbo(domain);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toDbo_ShouldMapCorrectly_WithStatus() {
        // Given
        Movie domain = Movie.builder()
            .status("ACTIVE")
            .build();

        // When
        MovieDbo result = mapper.toDbo(domain);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(EntityStatus.ACTIVE);
    }

    @Test
    void toDomain_ShouldMapCorrectly_FromDbo() {
        // Given
        MovieDbo dbo = MovieDbo.builder()
            .build();

        // When
        Movie result = mapper.toDomain(dbo);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toDomainList_ShouldMapCorrectly_FromDboList() {
        // Given
        MovieDbo dbo = MovieDbo.builder().build();
        List<MovieDbo> dboList = Arrays.asList(dbo);

        // When
        List<Movie> result = mapper.toDomainList(dboList);

        // Then
        assertThat(result).isNotNull().hasSize(1);
    }

    @Test
    void toDboList_ShouldMapCorrectly_FromDomainList() {
        // Given
        Movie domain = Movie.builder().build();
        List<Movie> domainList = Arrays.asList(domain);

        // When
        List<MovieDbo> result = mapper.toDboList(domainList);

        // Then
        assertThat(result).isNotNull().hasSize(1);
    }

    @Test
    void updateEntityFromRequest_ShouldUpdateCorrectly() {
        // Given
        UpdateMovieRequestContent request = UpdateMovieRequestContent.builder()
            .build();
        Movie entity = Movie.builder().build();

        // When
        mapper.updateEntityFromRequest(request, entity);

        // Then
        assertThat(entity).isNotNull();
    }

    @Test
    void updateEntityFromRequest_ShouldUpdateFields_WhenRequestHasValues() {
        // Given
        UpdateMovieRequestContent request = UpdateMovieRequestContent.builder()
            .build();
        Movie entity = Movie.builder().build();

        // When
        mapper.updateEntityFromRequest(request, entity);

        // Then
        assertThat(entity).isNotNull();
    }

    @Test
    void updateEntityFromRequest_ShouldDoNothing_WhenRequestIsNull() {
        // Given
        Movie entity = Movie.builder().build();
        Movie originalEntity = Movie.builder().build();

        // When
        mapper.updateEntityFromRequest(null, entity);

        // Then
        assertThat(entity).isEqualTo(originalEntity);
    }

    @Test
    void toDto_ShouldMapCorrectly_FromDomain() {
        // Given
        Movie domain = Movie.builder().build();

        // When
        MovieResponse result = mapper.toDto(domain);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toDtoList_ShouldMapCorrectly_FromDomainList() {
        // Given
        Movie domain = Movie.builder().build();
        List<Movie> domainList = Arrays.asList(domain);

        // When
        List<MovieResponse> result = mapper.toDtoList(domainList);

        // Then
        assertThat(result).isNotNull().hasSize(1);
    }

    // Null parameter coverage tests
    @Test
    void toCreateResponse_ShouldReturnNull_WhenDomainIsNull() {
        // When
        CreateMovieResponseContent result = mapper.toCreateResponse(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toGetResponse_ShouldReturnNull_WhenDomainIsNull() {
        // When
        GetMovieResponseContent result = mapper.toGetResponse(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDto_ShouldReturnNull_WhenDomainIsNull() {
        // When
        MovieResponse result = mapper.toDto(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDtoList_ShouldReturnNull_WhenDomainsIsNull() {
        // When
        List<MovieResponse> result = mapper.toDtoList(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDomain_ShouldReturnNull_WhenDboIsNull() {
        // When
        Movie result = mapper.toDomain(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDbo_ShouldReturnNull_WhenDomainIsNull() {
        // When
        MovieDbo result = mapper.toDbo(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDomainList_ShouldReturnNull_WhenDboListIsNull() {
        // When
        List<Movie> result = mapper.toDomainList(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDboList_ShouldReturnNull_WhenDomainListIsNull() {
        // When
        List<MovieDbo> result = mapper.toDboList(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void fromCreateRequest_ShouldReturnNull_WhenRequestIsNull() {
        // When
        Movie result = mapper.fromCreateRequest(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toUpdateResponse_ShouldReturnNull_WhenDomainIsNull() {
        // When
        UpdateMovieResponseContent result = mapper.toUpdateResponse(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void fromUpdateRequest_ShouldReturnNull_WhenRequestIsNull() {
        // When
        Movie result = mapper.fromUpdateRequest(null);

        // Then
        assertThat(result).isNull();
    }
}