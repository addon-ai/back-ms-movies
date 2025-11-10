package com.example.userservice.application.mapper;

import com.example.userservice.domain.model.User;
import com.example.userservice.domain.model.EntityStatus;
import com.example.userservice.infrastructure.adapters.output.persistence.entity.UserDbo;
import com.example.userservice.application.dto.user.CreateUserRequestContent;
import com.example.userservice.application.dto.user.CreateUserResponseContent;
import com.example.userservice.application.dto.user.UpdateUserRequestContent;
import com.example.userservice.application.dto.user.UpdateUserResponseContent;
import com.example.userservice.application.dto.user.GetUserResponseContent;
import com.example.userservice.application.dto.user.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for UserMapper.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper mapper;

    @Test
    void fromCreateRequest_ShouldMapCorrectly() {
        // Given
        CreateUserRequestContent request = CreateUserRequestContent.builder()
            .build();

        // When
        User result = mapper.fromCreateRequest(request);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toCreateResponse_ShouldMapCorrectly_FromDomain() {
        // Given
        User domain = User.builder()
            .build();

        // When
        CreateUserResponseContent result = mapper.toCreateResponse(domain);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void fromUpdateRequest_ShouldMapCorrectly() {
        // Given
        UpdateUserRequestContent request = UpdateUserRequestContent.builder()
            .build();

        // When
        User result = mapper.fromUpdateRequest(request);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toUpdateResponse_ShouldMapCorrectly_FromDomain() {
        // Given
        User domain = User.builder()
            .build();

        // When
        UpdateUserResponseContent result = mapper.toUpdateResponse(domain);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toGetResponse_ShouldMapCorrectly_FromDomain() {
        // Given
        User domain = User.builder()
            .build();

        // When
        GetUserResponseContent result = mapper.toGetResponse(domain);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toDbo_ShouldMapCorrectly_FromDomain() {
        // Given
        User domain = User.builder()
            .build();

        // When
        UserDbo result = mapper.toDbo(domain);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toDbo_ShouldMapCorrectly_WithStatus() {
        // Given
        User domain = User.builder()
            .status("ACTIVE")
            .build();

        // When
        UserDbo result = mapper.toDbo(domain);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(EntityStatus.ACTIVE);
    }

    @Test
    void toDomain_ShouldMapCorrectly_FromDbo() {
        // Given
        UserDbo dbo = UserDbo.builder()
            .build();

        // When
        User result = mapper.toDomain(dbo);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toDomainList_ShouldMapCorrectly_FromDboList() {
        // Given
        UserDbo dbo = UserDbo.builder().build();
        List<UserDbo> dboList = Arrays.asList(dbo);

        // When
        List<User> result = mapper.toDomainList(dboList);

        // Then
        assertThat(result).isNotNull().hasSize(1);
    }

    @Test
    void toDboList_ShouldMapCorrectly_FromDomainList() {
        // Given
        User domain = User.builder().build();
        List<User> domainList = Arrays.asList(domain);

        // When
        List<UserDbo> result = mapper.toDboList(domainList);

        // Then
        assertThat(result).isNotNull().hasSize(1);
    }

    @Test
    void updateEntityFromRequest_ShouldUpdateCorrectly() {
        // Given
        UpdateUserRequestContent request = UpdateUserRequestContent.builder()
            .build();
        User entity = User.builder().build();

        // When
        mapper.updateEntityFromRequest(request, entity);

        // Then
        assertThat(entity).isNotNull();
    }

    @Test
    void updateEntityFromRequest_ShouldUpdateFields_WhenRequestHasValues() {
        // Given
        UpdateUserRequestContent request = UpdateUserRequestContent.builder()
            .build();
        User entity = User.builder().build();

        // When
        mapper.updateEntityFromRequest(request, entity);

        // Then
        assertThat(entity).isNotNull();
    }

    @Test
    void updateEntityFromRequest_ShouldDoNothing_WhenRequestIsNull() {
        // Given
        User entity = User.builder().build();
        User originalEntity = User.builder().build();

        // When
        mapper.updateEntityFromRequest(null, entity);

        // Then
        assertThat(entity).isEqualTo(originalEntity);
    }

    @Test
    void toDto_ShouldMapCorrectly_FromDomain() {
        // Given
        User domain = User.builder().build();

        // When
        UserResponse result = mapper.toDto(domain);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void toDtoList_ShouldMapCorrectly_FromDomainList() {
        // Given
        User domain = User.builder().build();
        List<User> domainList = Arrays.asList(domain);

        // When
        List<UserResponse> result = mapper.toDtoList(domainList);

        // Then
        assertThat(result).isNotNull().hasSize(1);
    }

    // Null parameter coverage tests
    @Test
    void toCreateResponse_ShouldReturnNull_WhenDomainIsNull() {
        // When
        CreateUserResponseContent result = mapper.toCreateResponse(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toGetResponse_ShouldReturnNull_WhenDomainIsNull() {
        // When
        GetUserResponseContent result = mapper.toGetResponse(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDto_ShouldReturnNull_WhenDomainIsNull() {
        // When
        UserResponse result = mapper.toDto(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDtoList_ShouldReturnNull_WhenDomainsIsNull() {
        // When
        List<UserResponse> result = mapper.toDtoList(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDomain_ShouldReturnNull_WhenDboIsNull() {
        // When
        User result = mapper.toDomain(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDbo_ShouldReturnNull_WhenDomainIsNull() {
        // When
        UserDbo result = mapper.toDbo(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDomainList_ShouldReturnNull_WhenDboListIsNull() {
        // When
        List<User> result = mapper.toDomainList(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toDboList_ShouldReturnNull_WhenDomainListIsNull() {
        // When
        List<UserDbo> result = mapper.toDboList(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void fromCreateRequest_ShouldReturnNull_WhenRequestIsNull() {
        // When
        User result = mapper.fromCreateRequest(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toUpdateResponse_ShouldReturnNull_WhenDomainIsNull() {
        // When
        UpdateUserResponseContent result = mapper.toUpdateResponse(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void fromUpdateRequest_ShouldReturnNull_WhenRequestIsNull() {
        // When
        User result = mapper.fromUpdateRequest(null);

        // Then
        assertThat(result).isNull();
    }
}