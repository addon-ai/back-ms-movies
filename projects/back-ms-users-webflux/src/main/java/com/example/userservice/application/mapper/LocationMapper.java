package com.example.userservice.application.mapper;

import com.example.userservice.domain.model.Location;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.LocationDbo;
import com.example.userservice.application.dto.location.CreateLocationRequestContent;
import com.example.userservice.application.dto.location.CreateLocationResponseContent;
import com.example.userservice.application.dto.location.UpdateLocationRequestContent;
import com.example.userservice.application.dto.location.UpdateLocationResponseContent;
import com.example.userservice.application.dto.location.LocationResponse;
import com.example.userservice.application.dto.location.ListLocationsResponseContent;
import com.example.userservice.application.dto.location.GetLocationResponseContent;
import com.example.userservice.application.dto.location.DeleteLocationResponseContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.IterableMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.math.BigDecimal;

/**
 * MapStruct mapper for Location transformations between layers.
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
public interface LocationMapper {

    LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);

    // Domain to DBO mappings
    @Mapping(source = "locationId", target = "id")
    @org.mapstruct.Named("domainToDbo")
    LocationDbo toDbo(Location domain);
    
    @Mapping(source = "id", target = "locationId")
    @org.mapstruct.Named("dboToDomain")
    Location toDomain(LocationDbo dbo);
    
    @IterableMapping(qualifiedByName = "dboToDomain")
    List<Location> toDomainList(List<LocationDbo> dbos);
    
    @IterableMapping(qualifiedByName = "domainToDbo")
    List<LocationDbo> toDboList(List<Location> domains);

    // DTO to Domain mappings for Create/Update operations
    @Mapping(target = "locationId", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now().toString())")
    @Mapping(target = "updatedAt", ignore = true)
    Location fromCreateRequest(CreateLocationRequestContent request);
    
    @Mapping(target = "locationId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Location fromUpdateRequest(UpdateLocationRequestContent request);
    
    @Mapping(target = "locationId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(UpdateLocationRequestContent request, @org.mapstruct.MappingTarget Location entity);

    // Basic mapping methods - MapStruct will handle field mapping automatically
    @org.mapstruct.Named("domainToDto")
    LocationResponse toDto(Location domain);
    
    @IterableMapping(qualifiedByName = "domainToDto")
    List<LocationResponse> toDtoList(List<Location> domains);

    // Specific response mapping methods
    CreateLocationResponseContent toCreateResponse(Location domain);
    GetLocationResponseContent toGetResponse(Location domain);
    UpdateLocationResponseContent toUpdateResponse(Location domain);
    
    // Delete response method - available when delete DTO exists
    default DeleteLocationResponseContent toDeleteResponse(Location domain) {
        return DeleteLocationResponseContent.builder()
            .deleted(true)
            .message("Location deleted successfully")
            .build();
    }
    
    // Pagination support for list responses
    default ListLocationsResponseContent toListResponse(List<Location> domains, int page, int size) {
        if (domains == null) return null;
        
        int total = domains.size();
        int totalPages = (int) Math.ceil((double) total / size);
        
        return ListLocationsResponseContent.builder()
            .locations(toDtoList(domains))
            .page(java.math.BigDecimal.valueOf(page))
            .size(java.math.BigDecimal.valueOf(size))
            .total(java.math.BigDecimal.valueOf(total))
            .totalPages(java.math.BigDecimal.valueOf(totalPages))
            .build();
    }
    
    // Overloaded method with default pagination
    default ListLocationsResponseContent toListResponse(List<Location> domains) {
        return toListResponse(domains, 1, domains != null ? domains.size() : 0);
    }
}