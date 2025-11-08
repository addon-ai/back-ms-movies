package com.example.userservice.application.mapper;

import com.example.userservice.domain.model.City;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.CityDbo;
import com.example.userservice.application.dto.location.CityResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.IterableMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.math.BigDecimal;

/**
 * MapStruct mapper for City transformations between layers.
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
public interface CityMapper {

    CityMapper INSTANCE = Mappers.getMapper(CityMapper.class);

    // Domain to DBO mappings
    @Mapping(source = "cityId", target = "id")
    @org.mapstruct.Named("domainToDbo")
    CityDbo toDbo(City domain);
    
    @Mapping(source = "id", target = "cityId")
    @org.mapstruct.Named("dboToDomain")
    City toDomain(CityDbo dbo);
    
    @IterableMapping(qualifiedByName = "dboToDomain")
    List<City> toDomainList(List<CityDbo> dbos);
    
    @IterableMapping(qualifiedByName = "domainToDbo")
    List<CityDbo> toDboList(List<City> domains);

    // DTO to Domain mappings for Create/Update operations
    

    // Basic mapping methods - MapStruct will handle field mapping automatically
    @org.mapstruct.Named("domainToDto")
    CityResponse toDto(City domain);
    
    @IterableMapping(qualifiedByName = "domainToDto")
    List<CityResponse> toDtoList(List<City> domains);

    // Specific response mapping methods
    
}