package com.example.userservice.application.mapper;

import com.example.userservice.domain.model.User;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.UserDbo;
import com.example.userservice.application.dto.user.CreateUserRequestContent;
import com.example.userservice.application.dto.user.CreateUserResponseContent;
import com.example.userservice.application.dto.user.UpdateUserRequestContent;
import com.example.userservice.application.dto.user.UpdateUserResponseContent;
import com.example.userservice.application.dto.user.UserResponse;
import com.example.userservice.application.dto.user.ListUsersResponseContent;
import com.example.userservice.application.dto.user.GetUserResponseContent;
import com.example.userservice.application.dto.user.DeleteUserResponseContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.IterableMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.math.BigDecimal;

/**
 * MapStruct mapper for User transformations between layers.
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
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // Domain to DBO mappings
    @Mapping(source = "userId", target = "id")
    @org.mapstruct.Named("domainToDbo")
    UserDbo toDbo(User domain);
    
    @Mapping(source = "id", target = "userId")
    @org.mapstruct.Named("dboToDomain")
    User toDomain(UserDbo dbo);
    
    @IterableMapping(qualifiedByName = "dboToDomain")
    List<User> toDomainList(List<UserDbo> dbos);
    
    @IterableMapping(qualifiedByName = "domainToDbo")
    List<UserDbo> toDboList(List<User> domains);

    // DTO to Domain mappings for Create/Update operations
    @Mapping(target = "userId", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now().toString())")
    @Mapping(target = "updatedAt", ignore = true)
    User fromCreateRequest(CreateUserRequestContent request);
    
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User fromUpdateRequest(UpdateUserRequestContent request);
    
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(UpdateUserRequestContent request, @org.mapstruct.MappingTarget User entity);

    // Basic mapping methods - MapStruct will handle field mapping automatically
    @org.mapstruct.Named("domainToDto")
    UserResponse toDto(User domain);
    
    @IterableMapping(qualifiedByName = "domainToDto")
    List<UserResponse> toDtoList(List<User> domains);

    // Specific response mapping methods
    CreateUserResponseContent toCreateResponse(User domain);
    GetUserResponseContent toGetResponse(User domain);
    UpdateUserResponseContent toUpdateResponse(User domain);
    
    // Delete response method - available when delete DTO exists
    default DeleteUserResponseContent toDeleteResponse(User domain) {
        return DeleteUserResponseContent.builder()
            .deleted(true)
            .message("User deleted successfully")
            .build();
    }
    
    // Pagination support for list responses
    default ListUsersResponseContent toListResponse(List<User> domains, int page, int size) {
        if (domains == null) return null;
        
        int total = domains.size();
        int totalPages = (int) Math.ceil((double) total / size);
        
        return ListUsersResponseContent.builder()
            .users(toDtoList(domains))
            .page(java.math.BigDecimal.valueOf(page))
            .size(java.math.BigDecimal.valueOf(size))
            .total(java.math.BigDecimal.valueOf(total))
            .totalPages(java.math.BigDecimal.valueOf(totalPages))
            .build();
    }
    
    // Overloaded method with default pagination
    default ListUsersResponseContent toListResponse(List<User> domains) {
        return toListResponse(domains, 1, domains != null ? domains.size() : 0);
    }
}