package com.example.movieservice.application.mapper;

import com.example.movieservice.domain.model.Rental;
import com.example.movieservice.infrastructure.adapters.output.persistence.entity.RentalDbo;
import com.example.movieservice.application.dto.movie.CreateRentalRequestContent;
import com.example.movieservice.application.dto.movie.CreateRentalResponseContent;
import com.example.movieservice.application.dto.movie.UpdateRentalRequestContent;
import com.example.movieservice.application.dto.movie.UpdateRentalResponseContent;
import com.example.movieservice.application.dto.movie.RentalResponse;
import com.example.movieservice.application.dto.movie.ListRentalsResponseContent;
import com.example.movieservice.application.dto.movie.GetRentalResponseContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.IterableMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.math.BigDecimal;

/**
 * MapStruct mapper for Rental transformations between layers.
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
public interface RentalMapper {

    RentalMapper INSTANCE = Mappers.getMapper(RentalMapper.class);

    // Domain to DBO mappings
    @Mapping(source = "rentalId", target = "id")
    @org.mapstruct.Named("domainToDbo")
    RentalDbo toDbo(Rental domain);
    
    @Mapping(source = "id", target = "rentalId")
    @org.mapstruct.Named("dboToDomain")
    Rental toDomain(RentalDbo dbo);
    
    @IterableMapping(qualifiedByName = "dboToDomain")
    List<Rental> toDomainList(List<RentalDbo> dbos);
    
    @IterableMapping(qualifiedByName = "domainToDbo")
    List<RentalDbo> toDboList(List<Rental> domains);

    // DTO to Domain mappings for Create/Update operations
    @Mapping(target = "rentalId", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now().toString())")
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now().toString())")
    Rental fromCreateRequest(CreateRentalRequestContent request);
    
    @Mapping(target = "rentalId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Rental fromUpdateRequest(UpdateRentalRequestContent request);
    
    @Mapping(target = "rentalId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(UpdateRentalRequestContent request, @org.mapstruct.MappingTarget Rental entity);

    // Basic mapping methods - MapStruct will handle field mapping automatically
    @org.mapstruct.Named("domainToDto")
    RentalResponse toDto(Rental domain);
    
    @IterableMapping(qualifiedByName = "domainToDto")
    List<RentalResponse> toDtoList(List<Rental> domains);

    // Specific response mapping methods
    CreateRentalResponseContent toCreateResponse(Rental domain);
    GetRentalResponseContent toGetResponse(Rental domain);
    UpdateRentalResponseContent toUpdateResponse(Rental domain);
    
    // Pagination support for list responses with proper total count
    default ListRentalsResponseContent toListResponse(List<Rental> domains, int page, int size, int totalCount) {
        if (domains == null) return null;
        
        int totalPages = (int) Math.ceil((double) totalCount / size);
        
        ListRentalsResponseContent response = new ListRentalsResponseContent();
        response.setRentals(toDtoList(domains));
        response.setPage(java.math.BigDecimal.valueOf(page));
        response.setSize(java.math.BigDecimal.valueOf(size));
        response.setTotal(java.math.BigDecimal.valueOf(totalCount));
        response.setTotalPages(java.math.BigDecimal.valueOf(totalPages));
        return response;
    }
    
    // Overloaded method for backward compatibility
    default ListRentalsResponseContent toListResponse(List<Rental> domains, int page, int size) {
        return toListResponse(domains, page, size, domains != null ? domains.size() : 0);
    }
    
    // Overloaded method with default pagination
    default ListRentalsResponseContent toListResponse(List<Rental> domains) {
        return toListResponse(domains, 1, domains != null ? domains.size() : 0);
    }
}