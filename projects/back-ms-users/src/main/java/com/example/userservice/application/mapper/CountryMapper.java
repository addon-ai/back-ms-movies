package com.example.userservice.application.mapper;

import com.example.userservice.domain.model.Country;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.CountryDbo;
import com.example.userservice.application.dto.location.CountryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.IterableMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.math.BigDecimal;

/**
 * MapStruct mapper for Country transformations between layers.
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
public interface CountryMapper {

    CountryMapper INSTANCE = Mappers.getMapper(CountryMapper.class);

    // Domain to DBO mappings
    @Mapping(source = "countryId", target = "id")
    @org.mapstruct.Named("domainToDbo")
    CountryDbo toDbo(Country domain);
    
    @Mapping(source = "id", target = "countryId")
    @org.mapstruct.Named("dboToDomain")
    Country toDomain(CountryDbo dbo);
    
    @IterableMapping(qualifiedByName = "dboToDomain")
    List<Country> toDomainList(List<CountryDbo> dbos);
    
    @IterableMapping(qualifiedByName = "domainToDbo")
    List<CountryDbo> toDboList(List<Country> domains);

    // DTO to Domain mappings for Create/Update operations
    

    // Basic mapping methods - MapStruct will handle field mapping automatically
    @org.mapstruct.Named("domainToDto")
    CountryResponse toDto(Country domain);
    
    @IterableMapping(qualifiedByName = "domainToDto")
    List<CountryResponse> toDtoList(List<Country> domains);

    // Specific response mapping methods
    
}