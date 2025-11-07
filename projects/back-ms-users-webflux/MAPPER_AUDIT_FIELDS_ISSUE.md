# MapStruct Audit Fields Mapping Issue in Hexagonal Architecture

## 🎯 Executive Summary

**Issue**: Database constraint violations due to improper handling of audit fields (`created_at`, `updated_at`) in MapStruct mappers within a Spring WebFlux reactive microservice following Hexagonal Architecture.

**Root Cause**: Type mismatch and incomplete mapping between Domain layer (`String`) and Infrastructure layer (`java.time.Instant`) for audit fields.

**Impact**: Runtime `DataIntegrityViolationException` preventing entity persistence operations.

---

## 🏗️ Architecture Context

### Layer Structure
```
Domain Layer (Business Logic)
├── User.java (createdAt: String, updatedAt: String)
└── Other domain models...

Infrastructure Layer (Persistence)
├── UserDbo.java (createdAt: Instant, updatedAt: Instant)
└── Other DBO entities...

Application Layer (Orchestration)
├── UserMapper.java (MapStruct interface)
└── Other mappers...
```

### Technology Stack
- **Framework**: Spring Boot 3.2.5 + WebFlux (Reactive)
- **Database**: R2DBC + PostgreSQL
- **Mapping**: MapStruct 1.5.5.Final
- **Architecture**: Hexagonal (Ports & Adapters)

---

## 🚨 Problem Analysis

### 1. Type Mismatch Issue

**Domain Model** (`User.java`):
```java
@Data
@Builder
public class User {
    private String userId;
    private String username;
    private String email;
    // Audit fields as String (ISO-8601 format)
    private String createdAt;  // ❌ String type
    private String updatedAt;  // ❌ String type
}
```

**Database Entity** (`UserDbo.java`):
```java
@Data
@Builder
@Table("users")
public class UserDbo {
    @Id
    @Column("user_id")
    private UUID id;
    
    @Column("username")
    private String username;
    
    // Audit fields as Instant (database native)
    @Column("created_at")
    private Instant createdAt;  // ✅ Instant type
    
    @Column("updated_at") 
    private Instant updatedAt;  // ✅ Instant type (NOT NULL constraint)
}
```

### 2. Database Schema Constraints

```sql
CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,    -- ❌ NOT NULL constraint
    updated_at TIMESTAMP NOT NULL     -- ❌ NOT NULL constraint
);
```

### 3. Problematic Mapper Implementation

**Original Broken Mapper**:
```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    
    // ❌ No type conversion for audit fields
    @Mapping(source = "userId", target = "id")
    UserDbo toDbo(User domain);
    
    // ❌ Missing updatedAt in create operations
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now().toString())")
    @Mapping(target = "updatedAt", ignore = true)  // ❌ IGNORED = NULL
    User fromCreateRequest(CreateUserRequestContent request);
}
```

### 4. Runtime Error Manifestation

```
org.springframework.dao.DataIntegrityViolationException: 
executeMany; SQL [INSERT INTO users (username, email, first_name, last_name, status, created_at) 
VALUES ($1, $2, $3, $4, $5, $6)]; 
null value in column "updated_at" of relation "users" violates not-null constraint
```

**Error Flow**:
1. `fromCreateRequest()` ignores `updatedAt` → `null` value
2. `toDbo()` maps `null` String to `null` Instant
3. R2DBC attempts INSERT with `updated_at = NULL`
4. PostgreSQL rejects due to NOT NULL constraint

---

## ✅ Solution Architecture

### 1. Type Conversion Strategy

Implement bidirectional conversion between `String` ↔ `Instant`:

```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    
    // Conversion utility methods
    @Named("instantToString")
    default String instantToString(Instant instant) {
        return instant != null ? instant.toString() : null;
    }
    
    @Named("stringToInstant") 
    default Instant stringToInstant(String dateString) {
        return dateString != null ? Instant.parse(dateString) : null;
    }
}
```

### 2. Domain ↔ DBO Mapping with Type Conversion

```java
// Domain to DBO (String → Instant)
@Mapping(source = "userId", target = "id")
@Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "stringToInstant")
@Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "stringToInstant")
@Named("domainToDbo")
UserDbo toDbo(User domain);

// DBO to Domain (Instant → String)
@Mapping(source = "id", target = "userId")
@Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "instantToString")
@Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "instantToString")
@Named("dboToDomain")
User toDomain(UserDbo dbo);
```

### 3. Create Operation Audit Fields

```java
// ✅ Both audit fields set for CREATE operations
@Mapping(target = "userId", ignore = true)
@Mapping(target = "status", constant = "ACTIVE")
@Mapping(target = "createdAt", expression = "java(java.time.Instant.now().toString())")
@Mapping(target = "updatedAt", expression = "java(java.time.Instant.now().toString())")  // ✅ SET
User fromCreateRequest(CreateUserRequestContent request);
```

### 4. Update Operation Audit Fields

```java
// ✅ Only updatedAt modified for UPDATE operations
@Mapping(target = "userId", ignore = true)
@Mapping(target = "status", ignore = true)
@Mapping(target = "createdAt", ignore = true)  // Preserve original
@Mapping(target = "updatedAt", expression = "java(java.time.Instant.now().toString())")  // ✅ UPDATE
User fromUpdateRequest(UpdateUserRequestContent request);
```

---

## 🔧 Implementation Template

### Complete Corrected Mapper Pattern

```java
@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EntityMapper {

    // === TYPE CONVERSION UTILITIES ===
    @Named("instantToString")
    default String instantToString(Instant instant) {
        return instant != null ? instant.toString() : null;
    }
    
    @Named("stringToInstant")
    default Instant stringToInstant(String dateString) {
        return dateString != null ? Instant.parse(dateString) : null;
    }

    // === DOMAIN ↔ DBO MAPPINGS ===
    @Mapping(source = "entityId", target = "id")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "stringToInstant")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "stringToInstant")
    @Named("domainToDbo")
    EntityDbo toDbo(Entity domain);
    
    @Mapping(source = "id", target = "entityId")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "instantToString")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "instantToString")
    @Named("dboToDomain")
    Entity toDomain(EntityDbo dbo);

    // === CREATE OPERATIONS ===
    @Mapping(target = "entityId", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now().toString())")
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now().toString())")
    Entity fromCreateRequest(CreateEntityRequestContent request);

    // === UPDATE OPERATIONS ===
    @Mapping(target = "entityId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now().toString())")
    Entity fromUpdateRequest(UpdateEntityRequestContent request);
}
```

---

## 🎯 Code Generation Guidelines

### For Mustache Template Authors

1. **Audit Fields Pattern Detection**:
   ```mustache
   {{#hasAuditFields}}
   // Date conversion methods
   @Named("instantToString")
   default String instantToString(Instant instant) {
       return instant != null ? instant.toString() : null;
   }
   
   @Named("stringToInstant")
   default Instant stringToInstant(String dateString) {
       return dateString != null ? Instant.parse(dateString) : null;
   }
   {{/hasAuditFields}}
   ```

2. **Domain-DBO Mapping Template**:
   ```mustache
   @Mapping(source = "{{domainIdField}}", target = "id")
   {{#auditFields}}
   @Mapping(source = "{{fieldName}}", target = "{{fieldName}}", qualifiedByName = "stringToInstant")
   {{/auditFields}}
   @Named("domainToDbo")
   {{entityName}}Dbo toDbo({{entityName}} domain);
   ```

3. **Create Request Template**:
   ```mustache
   @Mapping(target = "{{domainIdField}}", ignore = true)
   @Mapping(target = "status", constant = "ACTIVE")
   {{#auditFields}}
   @Mapping(target = "{{fieldName}}", expression = "java(java.time.Instant.now().toString())")
   {{/auditFields}}
   {{entityName}} fromCreateRequest(Create{{entityName}}RequestContent request);
   ```

### Template Context Variables

```json
{
  "hasAuditFields": true,
  "auditFields": [
    {"fieldName": "createdAt"},
    {"fieldName": "updatedAt"}
  ],
  "domainIdField": "userId",
  "entityName": "User"
}
```

---

## 🧪 Validation Strategy

### Unit Test Pattern

```java
@ExtendWith(MockitoExtension.class)
class UserMapperTest {
    
    @Test
    void shouldMapDomainToDboWithAuditFields() {
        // Given
        User domain = User.builder()
            .userId("123")
            .createdAt("2024-01-01T10:00:00Z")
            .updatedAt("2024-01-01T11:00:00Z")
            .build();
            
        // When
        UserDbo dbo = mapper.toDbo(domain);
        
        // Then
        assertThat(dbo.getCreatedAt()).isEqualTo(Instant.parse("2024-01-01T10:00:00Z"));
        assertThat(dbo.getUpdatedAt()).isEqualTo(Instant.parse("2024-01-01T11:00:00Z"));
    }
    
    @Test
    void shouldSetBothAuditFieldsOnCreate() {
        // Given
        CreateUserRequestContent request = CreateUserRequestContent.builder()
            .username("testuser")
            .build();
            
        // When
        User domain = mapper.fromCreateRequest(request);
        
        // Then
        assertThat(domain.getCreatedAt()).isNotNull();
        assertThat(domain.getUpdatedAt()).isNotNull();
        assertThat(domain.getCreatedAt()).isEqualTo(domain.getUpdatedAt());
    }
}
```

---

## 📋 Checklist for Code Generation

- [ ] **Audit Fields Detection**: Template identifies `createdAt`/`updatedAt` fields
- [ ] **Type Conversion**: Include `instantToString`/`stringToInstant` methods
- [ ] **Domain-DBO Mapping**: Apply `qualifiedByName` for audit fields
- [ ] **Create Operations**: Set both audit fields to `Instant.now().toString()`
- [ ] **Update Operations**: Only set `updatedAt`, ignore `createdAt`
- [ ] **Null Safety**: Handle null values in conversion methods
- [ ] **Consistent Naming**: Use `@Named` annotations for method references

---

## 🎖️ Best Practices

1. **Separation of Concerns**: Domain uses business-friendly `String`, Infrastructure uses database-native `Instant`
2. **Immutable Audit Trail**: Never modify `createdAt` after initial creation
3. **Automatic Timestamping**: Use expressions for consistent timestamp generation
4. **Type Safety**: Explicit conversion methods prevent runtime errors
5. **Template Reusability**: Generic pattern applicable to all entities with audit fields

---

**Author**: Senior Software Architect  
**Date**: 2024-11-07  
**Version**: 1.0.0