package com.example.movieservice.application.mapper;

import com.example.movieservice.domain.model.Movie;
import com.example.movieservice.infrastructure.adapters.output.persistence.entity.MovieDbo;
import com.example.movieservice.application.dto.movie.CreateMovieRequestContent;
import com.example.movieservice.application.dto.movie.CreateMovieResponseContent;
import com.example.movieservice.application.dto.movie.UpdateMovieRequestContent;
import com.example.movieservice.application.dto.movie.UpdateMovieResponseContent;
import com.example.movieservice.application.dto.movie.MovieResponse;
import com.example.movieservice.application.dto.movie.ListMoviesResponseContent;
import com.example.movieservice.application.dto.movie.GetMovieResponseContent;
import com.example.movieservice.application.dto.movie.DeleteMovieResponseContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.IterableMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.math.BigDecimal;

/**
 * MapStruct mapper for Movie transformations between layers.
 * <p>
 * This mapper handles conversions between:
 * - Domain models (pure business objects)
 * - DTOs (data transfer objects for API communication)
 * - DBOs (database objects for persistence)
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
public interface MovieMapper {

    MovieMapper INSTANCE = Mappers.getMapper(MovieMapper.class);

    // Domain to DBO mappings
    @Mapping(source = "movieId", target = "id")
    @org.mapstruct.Named("domainToDbo")
    MovieDbo toDbo(Movie domain);
    
    @Mapping(source = "id", target = "movieId")
    @org.mapstruct.Named("dboToDomain")
    Movie toDomain(MovieDbo dbo);
    
    @IterableMapping(qualifiedByName = "dboToDomain")
    List<Movie> toDomainList(List<MovieDbo> dbos);
    
    @IterableMapping(qualifiedByName = "domainToDbo")
    List<MovieDbo> toDboList(List<Movie> domains);

    // DTO to Domain mappings for Create/Update operations
    @Mapping(target = "movieId", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now().toString())")
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now().toString())")
    Movie fromCreateRequest(CreateMovieRequestContent request);
    
    @Mapping(target = "movieId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Movie fromUpdateRequest(UpdateMovieRequestContent request);
    
    @Mapping(target = "movieId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(UpdateMovieRequestContent request, @org.mapstruct.MappingTarget Movie entity);

    // Basic mapping methods - MapStruct will handle field mapping automatically
    @org.mapstruct.Named("domainToDto")
    MovieResponse toDto(Movie domain);
    
    @IterableMapping(qualifiedByName = "domainToDto")
    List<MovieResponse> toDtoList(List<Movie> domains);

    // Specific response mapping methods
    CreateMovieResponseContent toCreateResponse(Movie domain);
    GetMovieResponseContent toGetResponse(Movie domain);
    UpdateMovieResponseContent toUpdateResponse(Movie domain);
    
    // Delete response method - available when delete DTO exists
    default DeleteMovieResponseContent toDeleteResponse(Movie domain) {
        DeleteMovieResponseContent response = new DeleteMovieResponseContent();
        response.setDeleted(true);
        response.setMessage("Movie deleted successfully");
        return response;
    }
    
    // Pagination support for list responses with proper total count
    default ListMoviesResponseContent toListResponse(List<Movie> domains, int page, int size, int totalCount) {
        if (domains == null) return null;
        
        int totalPages = (int) Math.ceil((double) totalCount / size);
        
        ListMoviesResponseContent response = new ListMoviesResponseContent();
        response.setMovies(toDtoList(domains));
        response.setPage(java.math.BigDecimal.valueOf(page));
        response.setSize(java.math.BigDecimal.valueOf(size));
        response.setTotal(java.math.BigDecimal.valueOf(totalCount));
        response.setTotalPages(java.math.BigDecimal.valueOf(totalPages));
        return response;
    }
    
    // Overloaded method for backward compatibility
    default ListMoviesResponseContent toListResponse(List<Movie> domains, int page, int size) {
        return toListResponse(domains, page, size, domains != null ? domains.size() : 0);
    }
    
    // Overloaded method with default pagination
    default ListMoviesResponseContent toListResponse(List<Movie> domains) {
        return toListResponse(domains, 1, domains != null ? domains.size() : 0);
    }
}