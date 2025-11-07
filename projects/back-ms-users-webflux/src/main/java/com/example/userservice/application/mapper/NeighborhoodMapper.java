package com.example.userservice.application.mapper;

import com.example.userservice.domain.model.Neighborhood;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.NeighborhoodDbo;
import com.example.userservice.application.dto.location.NeighborhoodResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.IterableMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.math.BigDecimal;

/**
 * MapStruct mapper for Neighborhood transformations between layers.
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
public interface NeighborhoodMapper {

    NeighborhoodMapper INSTANCE = Mappers.getMapper(NeighborhoodMapper.class);

    // Domain to DBO mappings
    @Mapping(source = "neighborhoodId", target = "id")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "stringToInstant")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "stringToInstant")
    @org.mapstruct.Named("domainToDbo")
    NeighborhoodDbo toDbo(Neighborhood domain);
    
    @Mapping(source = "id", target = "neighborhoodId")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "instantToString")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "instantToString")
    @org.mapstruct.Named("dboToDomain")
    Neighborhood toDomain(NeighborhoodDbo dbo);
    
    @IterableMapping(qualifiedByName = "dboToDomain")
    List<Neighborhood> toDomainList(List<NeighborhoodDbo> dbos);
    
    @IterableMapping(qualifiedByName = "domainToDbo")
    List<NeighborhoodDbo> toDboList(List<Neighborhood> domains);

    // DTO to Domain mappings for Create/Update operations
    

    // Basic mapping methods - MapStruct will handle field mapping automatically
    @org.mapstruct.Named("domainToDto")
    NeighborhoodResponse toDto(Neighborhood domain);
    
    @IterableMapping(qualifiedByName = "domainToDto")
    List<NeighborhoodResponse> toDtoList(List<Neighborhood> domains);

    // Specific response mapping methods
    
    // Date conversion methods
    @org.mapstruct.Named("instantToString")
    default String instantToString(java.time.Instant instant) {
        return instant != null ? instant.toString() : null;
    }
    
    @org.mapstruct.Named("stringToInstant")
    default java.time.Instant stringToInstant(String dateString) {
        return dateString != null ? java.time.Instant.parse(dateString) : null;
    }
}