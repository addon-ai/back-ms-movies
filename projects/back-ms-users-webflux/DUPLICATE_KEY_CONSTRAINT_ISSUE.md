# Duplicate Key Constraint Issue - Spring WebFlux Microservice

## 🚨 Problem Summary

**Issue**: User creation fails with duplicate key constraint violation on username field.

**Error**: `duplicate key value violates unique constraint "users_username_key"`

**Root Cause**: Application attempts to insert a user with an existing username without proper validation or conflict handling.

---

## 🔍 Technical Analysis

### 1. Database Constraint

**Schema Definition**:
```sql
CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,  -- ❌ UNIQUE constraint
    email VARCHAR(255) NOT NULL UNIQUE,     -- ❌ UNIQUE constraint  
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Explicit unique constraints
ALTER TABLE users ADD CONSTRAINT users_username_key UNIQUE (username);
ALTER TABLE users ADD CONSTRAINT users_email_key UNIQUE (email);
```

### 2. Current Service Implementation

**File**: `UserService.java` - `create()` method

```java
// ❌ PROBLEMATIC: No duplicate validation
@Override
public Mono<CreateUserResponseContent> create(CreateUserRequestContent request) {
    logger.info("Executing CreateUser with request: {}", request);
    
    return Mono.fromCallable(() -> userMapper.fromCreateRequest(request))
            .flatMap(userRepositoryPort::save)  // ❌ Direct save without validation
            .map(savedUser -> {
                logger.info("User created successfully with ID: {}", savedUser.getUserId());
                return userMapper.toCreateResponse(savedUser);
            })
            .doOnError(e -> logger.error("Error in CreateUser", e, request));
}
```

**Problems**:
- No validation for existing username/email before creation
- Database constraint violation causes runtime exception
- Poor user experience with generic error messages
- No proper HTTP status codes for conflict scenarios

### 3. Missing Business Logic Validation

**Expected Flow**:
1. ✅ Validate request data
2. ❌ **MISSING**: Check if username already exists
3. ❌ **MISSING**: Check if email already exists  
4. ❌ **MISSING**: Handle conflicts gracefully
5. ✅ Create user if no conflicts

---

## ✅ Complete Solution

### 1. Add Validation Methods to Repository

```java
@Repository
public interface JpaUserRepository extends R2dbcRepository<UserDbo, UUID> {
    
    // ... existing methods ...
    
    /**
     * ✅ NEW: Check if username exists
     */
    @Query("SELECT COUNT(*) > 0 FROM users WHERE LOWER(username) = LOWER(:username)")
    Mono<Boolean> existsByUsername(@Param("username") String username);
    
    /**
     * ✅ NEW: Check if email exists
     */
    @Query("SELECT COUNT(*) > 0 FROM users WHERE LOWER(email) = LOWER(:email)")
    Mono<Boolean> existsByEmail(@Param("email") String email);
    
    /**
     * ✅ NEW: Find user by username
     */
    @Query("SELECT * FROM users WHERE LOWER(username) = LOWER(:username)")
    Mono<UserDbo> findByUsername(@Param("username") String username);
    
    /**
     * ✅ NEW: Find user by email
     */
    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email)")
    Mono<UserDbo> findByEmail(@Param("email") String email);
}
```

### 2. Update Repository Port Interface

```java
public interface UserRepositoryPort {
    
    // ... existing methods ...
    
    /**
     * ✅ NEW: Check if username exists
     */
    Mono<Boolean> existsByUsername(String username);
    
    /**
     * ✅ NEW: Check if email exists
     */
    Mono<Boolean> existsByEmail(String email);
    
    /**
     * ✅ NEW: Find user by username
     */
    Mono<User> findByUsername(String username);
    
    /**
     * ✅ NEW: Find user by email
     */
    Mono<User> findByEmail(String email);
}
```

### 3. Update Repository Adapter Implementation

```java
@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {
    
    // ... existing methods ...
    
    @Override
    public Mono<Boolean> existsByUsername(String username) {
        log.debug("Checking if username exists: {}", username);
        return r2dbcRepository.existsByUsername(username)
                .doOnError(e -> log.error("Database error while checking username: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to check username existence", e));
    }
    
    @Override
    public Mono<Boolean> existsByEmail(String email) {
        log.debug("Checking if email exists: {}", email);
        return r2dbcRepository.existsByEmail(email)
                .doOnError(e -> log.error("Database error while checking email: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to check email existence", e));
    }
    
    @Override
    public Mono<User> findByUsername(String username) {
        log.debug("Finding user by username: {}", username);
        return r2dbcRepository.findByUsername(username)
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while finding user by username: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find user by username", e));
    }
    
    @Override
    public Mono<User> findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return r2dbcRepository.findByEmail(email)
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Database error while finding user by email: {}", e.getMessage(), e))
                .onErrorMap(e -> new InternalServerErrorException("Failed to find user by email", e));
    }
}
```

### 4. Create Custom Exception for Conflicts

```java
package com.example.userservice.infrastructure.config.exceptions;

/**
 * ✅ NEW: Exception for resource conflicts (HTTP 409)
 */
public class ConflictException extends RuntimeException {
    
    public ConflictException(String message) {
        super(message);
    }
    
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### 5. Update Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // ... existing handlers ...
    
    /**
     * ✅ NEW: Handle conflict exceptions (HTTP 409)
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now().toString())
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .message(ex.getMessage())
                .path(getCurrentPath())
                .build();
                
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
```

### 6. Fix Service Layer with Proper Validation

```java
@Override
public Mono<CreateUserResponseContent> create(CreateUserRequestContent request) {
    logger.info("Executing CreateUser with request: {}", request);
    
    return validateUserUniqueness(request.getUsername(), request.getEmail())
            .then(Mono.fromCallable(() -> userMapper.fromCreateRequest(request)))
            .flatMap(userRepositoryPort::save)
            .map(savedUser -> {
                logger.info("User created successfully with ID: {}", savedUser.getUserId());
                return userMapper.toCreateResponse(savedUser);
            })
            .doOnError(e -> logger.error("Error in CreateUser", e, request));
}

/**
 * ✅ NEW: Validate username and email uniqueness
 */
private Mono<Void> validateUserUniqueness(String username, String email) {
    return Mono.zip(
        userRepositoryPort.existsByUsername(username),
        userRepositoryPort.existsByEmail(email)
    ).flatMap(tuple -> {
        boolean usernameExists = tuple.getT1();
        boolean emailExists = tuple.getT2();
        
        if (usernameExists && emailExists) {
            return Mono.error(new ConflictException("Username and email already exist"));
        } else if (usernameExists) {
            return Mono.error(new ConflictException("Username already exists"));
        } else if (emailExists) {
            return Mono.error(new ConflictException("Email already exists"));
        }
        
        return Mono.empty();
    });
}
```

### 7. Enhanced Service with Better Error Handling

```java
@Override
public Mono<CreateUserResponseContent> create(CreateUserRequestContent request) {
    logger.info("Executing CreateUser with request: {}", request);
    
    return validateUserUniqueness(request.getUsername(), request.getEmail())
            .then(Mono.fromCallable(() -> userMapper.fromCreateRequest(request)))
            .flatMap(userRepositoryPort::save)
            .map(savedUser -> {
                logger.info("User created successfully with ID: {}", savedUser.getUserId());
                return userMapper.toCreateResponse(savedUser);
            })
            .onErrorMap(DuplicateKeyException.class, ex -> {
                // ✅ Fallback: Handle database-level constraint violations
                String message = ex.getMessage();
                if (message.contains("users_username_key")) {
                    return new ConflictException("Username already exists");
                } else if (message.contains("users_email_key")) {
                    return new ConflictException("Email already exists");
                } else {
                    return new ConflictException("User data conflicts with existing records");
                }
            })
            .doOnError(e -> logger.error("Error in CreateUser", e, request));
}
```

---

## 🎯 Code Generation Guidelines

### For Python/Mustache Template Authors

1. **Repository Validation Methods Template**:
```mustache
/**
 * Check if {{fieldName}} exists
 */
@Query("SELECT COUNT(*) > 0 FROM {{tableName}} WHERE LOWER({{dbColumnName}}) = LOWER(:{{fieldName}})")
Mono<Boolean> existsBy{{fieldName.capitalize}}(@Param("{{fieldName}}") String {{fieldName}});

/**
 * Find entity by {{fieldName}}
 */
@Query("SELECT * FROM {{tableName}} WHERE LOWER({{dbColumnName}}) = LOWER(:{{fieldName}})")
Mono<{{entityName}}Dbo> findBy{{fieldName.capitalize}}(@Param("{{fieldName}}") String {{fieldName}});
```

2. **Service Validation Template**:
```mustache
/**
 * Validate {{entityName.toLowerCase}} uniqueness
 */
private Mono<Void> validate{{entityName}}Uniqueness({{#uniqueFields}}String {{fieldName}}{{#hasNext}}, {{/hasNext}}{{/uniqueFields}}) {
    return Mono.zip(
        {{#uniqueFields}}
        {{entityName.toLowerCase}}RepositoryPort.existsBy{{fieldName.capitalize}}({{fieldName}}){{#hasNext}},{{/hasNext}}
        {{/uniqueFields}}
    ).flatMap(tuple -> {
        {{#uniqueFields}}
        boolean {{fieldName}}Exists = tuple.getT{{index}}();
        {{/uniqueFields}}
        
        {{#uniqueFields}}
        if ({{fieldName}}Exists) {
            return Mono.error(new ConflictException("{{fieldName.capitalize}} already exists"));
        }
        {{/uniqueFields}}
        
        return Mono.empty();
    });
}
```

3. **Template Context Variables**:
```json
{
  "entityName": "User",
  "tableName": "users",
  "uniqueFields": [
    {"fieldName": "username", "dbColumnName": "username", "index": "1", "hasNext": true},
    {"fieldName": "email", "dbColumnName": "email", "index": "2", "hasNext": false}
  ]
}
```

### Exception Handler Template

```mustache
/**
 * Handle conflict exceptions (HTTP 409)
 */
@ExceptionHandler(ConflictException.class)
public ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex) {
    ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now().toString())
            .status(HttpStatus.CONFLICT.value())
            .error("Conflict")
            .message(ex.getMessage())
            .path(getCurrentPath())
            .build();
            
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
}
```

---

## 🧪 Testing Strategy

### Unit Tests for Validation

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Test
    void shouldThrowConflictWhenUsernameExists() {
        // Given
        when(userRepositoryPort.existsByUsername("existinguser"))
            .thenReturn(Mono.just(true));
        when(userRepositoryPort.existsByEmail("new@email.com"))
            .thenReturn(Mono.just(false));
            
        CreateUserRequestContent request = CreateUserRequestContent.builder()
            .username("existinguser")
            .email("new@email.com")
            .build();
            
        // When & Then
        StepVerifier.create(userService.create(request))
            .expectError(ConflictException.class)
            .verify();
    }
    
    @Test
    void shouldCreateUserWhenNoConflicts() {
        // Given
        when(userRepositoryPort.existsByUsername("newuser"))
            .thenReturn(Mono.just(false));
        when(userRepositoryPort.existsByEmail("new@email.com"))
            .thenReturn(Mono.just(false));
        when(userRepositoryPort.save(any()))
            .thenReturn(Mono.just(mockUser));
            
        CreateUserRequestContent request = CreateUserRequestContent.builder()
            .username("newuser")
            .email("new@email.com")
            .build();
            
        // When & Then
        StepVerifier.create(userService.create(request))
            .expectNextMatches(response -> response.getUsername().equals("newuser"))
            .verifyComplete();
    }
}
```

### Integration Tests

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {
    
    @Test
    void shouldReturn409WhenUsernameExists() {
        // Given: Create initial user
        CreateUserRequestContent initialUser = CreateUserRequestContent.builder()
            .username("testuser")
            .email("test@example.com")
            .build();
            
        webTestClient.post()
            .uri("/users")
            .header("X-Request-ID", "test1")
            .bodyValue(initialUser)
            .exchange()
            .expectStatus().isCreated();
            
        // When: Try to create user with same username
        CreateUserRequestContent duplicateUser = CreateUserRequestContent.builder()
            .username("testuser")  // Same username
            .email("different@example.com")
            .build();
            
        // Then: Should return 409 Conflict
        webTestClient.post()
            .uri("/users")
            .header("X-Request-ID", "test2")
            .bodyValue(duplicateUser)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Username already exists");
    }
}
```

---

## 📋 Implementation Checklist

- [ ] **Repository Methods**: Add `existsByUsername`, `existsByEmail`, `findByUsername`, `findByEmail`
- [ ] **Port Interface**: Update with new validation methods
- [ ] **Repository Adapter**: Implement validation methods with error handling
- [ ] **Custom Exception**: Create `ConflictException` for HTTP 409 responses
- [ ] **Exception Handler**: Add global handler for conflict exceptions
- [ ] **Service Validation**: Implement `validateUserUniqueness` method
- [ ] **Service Enhancement**: Update create method with validation flow
- [ ] **Fallback Handling**: Add database constraint violation mapping
- [ ] **Unit Tests**: Test validation logic and conflict scenarios
- [ ] **Integration Tests**: Test HTTP 409 responses and error messages

---

## 🎖️ Best Practices

1. **Proactive Validation**: Check uniqueness before attempting database operations
2. **Graceful Degradation**: Handle both application-level and database-level constraint violations
3. **Clear Error Messages**: Provide specific feedback about which field conflicts
4. **Proper HTTP Status**: Use 409 Conflict for resource conflicts, not 400 Bad Request
5. **Case Insensitive**: Use LOWER() in SQL queries for username/email uniqueness
6. **Atomic Operations**: Use reactive streams to chain validation and creation
7. **Comprehensive Testing**: Test both success and conflict scenarios

---

**Priority**: High - Affects user experience and data integrity  
**Effort**: Medium - Requires validation logic and error handling  
**Risk**: Low - Well-established patterns with clear implementation path

---

**Author**: Senior Software Architect  
**Date**: 2024-11-08  
**Version**: 1.0.0