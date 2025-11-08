# Duplicate Key Exception Handling - Spring WebFlux Microservice

## 🚨 Problem Summary

**Issue**: User creation fails with HTTP 500 Internal Server Error when attempting to create a user with duplicate username or email.

**Current Error Response**:
```json
{
  "timestamp": "2025-11-08T15:23:28.367+00:00",
  "path": "/users",
  "status": 500,
  "error": "Internal Server Error",
  "requestId": "255681df-15"
}
```

**Expected Response**:
```json
{
  "timestamp": "2025-11-08T15:23:28.367+00:00",
  "path": "/users", 
  "status": 409,
  "error": "Conflict",
  "message": "Username already exists"
}
```

**Root Cause**: `DuplicateKeyException` from database constraint violations are not properly handled and converted to appropriate HTTP 409 Conflict responses.

---

## 🔍 Technical Analysis

### 1. Database Constraint Violation

**Error Stack Trace**:
```
org.springframework.dao.DuplicateKeyException: executeMany; SQL [INSERT INTO users (username, email, first_name, last_name, status, created_at, updated_at) VALUES ($1, $2, $3, $4, $5, $6, $7)]; duplicate key value violates unique constraint "users_username_key"

Caused by: io.r2dbc.postgresql.ExceptionFactory$PostgresqlDataIntegrityViolationException: duplicate key value violates unique constraint "users_username_key"
```

**Database Schema**:
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
// ❌ PROBLEMATIC: No duplicate key exception handling
@Override
public Mono<CreateUserResponseContent> create(CreateUserRequestContent request) {
    logger.info("Executing CreateUser with request: {}", request);
    
    return Mono.fromCallable(() -> userMapper.fromCreateRequest(request))
            .flatMap(userRepositoryPort::save)  // ❌ DuplicateKeyException not handled
            .map(savedUser -> {
                logger.info("User created successfully with ID: {}", savedUser.getUserId());
                return userMapper.toCreateResponse(savedUser);
            })
            .doOnError(e -> logger.error("Error in CreateUser", e, request));
}
```

**Problems**:
- `DuplicateKeyException` propagates as HTTP 500 Internal Server Error
- No specific error message indicating which field conflicts
- Poor user experience with generic error responses
- Violates REST API best practices for conflict handling

### 3. Missing Exception Mapping

**Current Flow**:
1. ✅ User creation request received
2. ✅ Request mapped to domain object
3. ❌ **ISSUE**: Database constraint violation throws `DuplicateKeyException`
4. ❌ **ISSUE**: Exception not caught and mapped to appropriate HTTP status
5. ❌ **RESULT**: Generic HTTP 500 response returned

**Expected Flow**:
1. ✅ User creation request received
2. ✅ Request mapped to domain object
3. ✅ Database constraint violation throws `DuplicateKeyException`
4. ✅ **FIX**: Exception caught and mapped to `ConflictException`
5. ✅ **RESULT**: HTTP 409 Conflict response with specific message

---

## ✅ Complete Solution

### 1. Update Global Exception Handler

**File**: `GlobalExceptionHandler.java`

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // ... existing handlers ...
    
    /**
     * ✅ NEW: Handle DuplicateKeyException from database constraints
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateKeyException(
            DuplicateKeyException ex, WebRequest request) {
        logger.warn("Duplicate key constraint violation: {}", ex.getMessage());
        
        String message = "Resource already exists";
        if (ex.getMessage().contains("users_username_key")) {
            message = "Username already exists";
        } else if (ex.getMessage().contains("users_email_key")) {
            message = "Email already exists";
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", OffsetDateTime.now());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("error", "Conflict");
        response.put("message", message);
        response.put("path", request.getDescription(false).replace("uri=", ""));
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
```

### 2. Update Service Layer with Exception Mapping

**File**: `UserService.java`

```java
@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {
    
    // ... existing code ...
    
    /**
     * ✅ CORRECTED: Handle DuplicateKeyException and convert to ConflictException
     */
    @Override
    public Mono<CreateUserResponseContent> create(CreateUserRequestContent request) {
        logger.info("Executing CreateUser with request: {}", request);
        
        return Mono.fromCallable(() -> userMapper.fromCreateRequest(request))
                .flatMap(userRepositoryPort::save)
                .map(savedUser -> {
                    logger.info("User created successfully with ID: {}", savedUser.getUserId());
                    return userMapper.toCreateResponse(savedUser);
                })
                .onErrorMap(DuplicateKeyException.class, ex -> {
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
}
```

### 3. Enhanced Exception Handling with Multiple Constraints

**Advanced Service Implementation**:
```java
@Override
public Mono<CreateUserResponseContent> create(CreateUserRequestContent request) {
    logger.info("Executing CreateUser with request: {}", request);
    
    return Mono.fromCallable(() -> userMapper.fromCreateRequest(request))
            .flatMap(userRepositoryPort::save)
            .map(savedUser -> {
                logger.info("User created successfully with ID: {}", savedUser.getUserId());
                return userMapper.toCreateResponse(savedUser);
            })
            .onErrorMap(DuplicateKeyException.class, this::mapDuplicateKeyException)
            .doOnError(e -> logger.error("Error in CreateUser", e, request));
}

/**
 * ✅ NEW: Map DuplicateKeyException to specific ConflictException
 */
private ConflictException mapDuplicateKeyException(DuplicateKeyException ex) {
    String message = ex.getMessage().toLowerCase();
    
    if (message.contains("users_username_key")) {
        return new ConflictException("Username already exists");
    } else if (message.contains("users_email_key")) {
        return new ConflictException("Email already exists");
    } else if (message.contains("unique constraint")) {
        // Generic unique constraint violation
        return new ConflictException("Data conflicts with existing records");
    } else {
        // Fallback for other duplicate key scenarios
        return new ConflictException("Resource already exists");
    }
}
```

### 4. Update Required Imports

**File**: `UserService.java` - Add imports

```java
import com.example.userservice.infrastructure.config.exceptions.ConflictException;
import org.springframework.dao.DuplicateKeyException;
```

**File**: `GlobalExceptionHandler.java` - Add import

```java
import org.springframework.dao.DuplicateKeyException;
```

---

## 🎯 Code Generation Guidelines

### For Python/Mustache Template Authors

1. **Service Exception Mapping Template**:
```mustache
@Override
public Mono<Create{{entityName}}ResponseContent> create(Create{{entityName}}RequestContent request) {
    logger.info("Executing Create{{entityName}} with request: {}", request);
    
    return Mono.fromCallable(() -> {{entityName.toLowerCase}}Mapper.fromCreateRequest(request))
            .flatMap({{entityName.toLowerCase}}RepositoryPort::save)
            .map(saved{{entityName}} -> {
                logger.info("{{entityName}} created successfully with ID: {}", saved{{entityName}}.get{{entityName}}Id());
                return {{entityName.toLowerCase}}Mapper.toCreateResponse(saved{{entityName}});
            })
            .onErrorMap(DuplicateKeyException.class, ex -> {
                String message = ex.getMessage();
                {{#uniqueConstraints}}
                if (message.contains("{{constraintName}}")) {
                    return new ConflictException("{{fieldDisplayName}} already exists");
                }
                {{/uniqueConstraints}}
                return new ConflictException("{{entityName}} data conflicts with existing records");
            })
            .doOnError(e -> logger.error("Error in Create{{entityName}}", e, request));
}
```

2. **Global Exception Handler Template**:
```mustache
/**
 * Handle DuplicateKeyException from database constraints
 */
@ExceptionHandler(DuplicateKeyException.class)
public ResponseEntity<Map<String, Object>> handleDuplicateKeyException(
        DuplicateKeyException ex, WebRequest request) {
    logger.warn("Duplicate key constraint violation: {}", ex.getMessage());
    
    String message = "Resource already exists";
    {{#uniqueConstraints}}
    if (ex.getMessage().contains("{{constraintName}}")) {
        message = "{{fieldDisplayName}} already exists";
    }
    {{/uniqueConstraints}}
    
    Map<String, Object> response = new HashMap<>();
    response.put("timestamp", OffsetDateTime.now());
    response.put("status", HttpStatus.CONFLICT.value());
    response.put("error", "Conflict");
    response.put("message", message);
    response.put("path", request.getDescription(false).replace("uri=", ""));
    
    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
}
```

3. **Template Context Variables**:
```json
{
  "entityName": "User",
  "uniqueConstraints": [
    {
      "constraintName": "users_username_key",
      "fieldDisplayName": "Username"
    },
    {
      "constraintName": "users_email_key", 
      "fieldDisplayName": "Email"
    }
  ]
}
```

### Import Templates

**Service Imports**:
```mustache
import com.example.{{packageName}}.infrastructure.config.exceptions.ConflictException;
import org.springframework.dao.DuplicateKeyException;
```

**Exception Handler Imports**:
```mustache
import org.springframework.dao.DuplicateKeyException;
```

---

## 🧪 Testing Strategy

### Unit Tests for Exception Mapping

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Test
    void shouldMapUsernameConstraintViolationToConflict() {
        // Given
        DuplicateKeyException dbException = new DuplicateKeyException(
            "duplicate key value violates unique constraint \"users_username_key\""
        );
        when(userRepositoryPort.save(any())).thenReturn(Mono.error(dbException));
        
        CreateUserRequestContent request = CreateUserRequestContent.builder()
            .username("existinguser")
            .email("new@email.com")
            .build();
            
        // When & Then
        StepVerifier.create(userService.create(request))
            .expectErrorMatches(ex -> 
                ex instanceof ConflictException && 
                ex.getMessage().equals("Username already exists")
            )
            .verify();
    }
    
    @Test
    void shouldMapEmailConstraintViolationToConflict() {
        // Given
        DuplicateKeyException dbException = new DuplicateKeyException(
            "duplicate key value violates unique constraint \"users_email_key\""
        );
        when(userRepositoryPort.save(any())).thenReturn(Mono.error(dbException));
        
        CreateUserRequestContent request = CreateUserRequestContent.builder()
            .username("newuser")
            .email("existing@email.com")
            .build();
            
        // When & Then
        StepVerifier.create(userService.create(request))
            .expectErrorMatches(ex -> 
                ex instanceof ConflictException && 
                ex.getMessage().equals("Email already exists")
            )
            .verify();
    }
}
```

### Integration Tests for HTTP Responses

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {
    
    @Test
    void shouldReturn409WhenUsernameExists() {
        // Given: Create initial user
        CreateUserRequestContent initialUser = CreateUserRequestContent.builder()
            .username("testuser")
            .email("test@example.com")
            .password("Test123!")
            .firstName("Test")
            .lastName("User")
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
            .password("Test123!")
            .firstName("Different")
            .lastName("User")
            .build();
            
        // Then: Should return 409 Conflict
        webTestClient.post()
            .uri("/users")
            .header("X-Request-ID", "test2")
            .bodyValue(duplicateUser)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody()
            .jsonPath("$.status").isEqualTo(409)
            .jsonPath("$.error").isEqualTo("Conflict")
            .jsonPath("$.message").isEqualTo("Username already exists")
            .jsonPath("$.path").isEqualTo("/users");
    }
    
    @Test
    void shouldReturn409WhenEmailExists() {
        // Given: Create initial user
        CreateUserRequestContent initialUser = CreateUserRequestContent.builder()
            .username("testuser1")
            .email("test@example.com")
            .password("Test123!")
            .firstName("Test")
            .lastName("User")
            .build();
            
        webTestClient.post()
            .uri("/users")
            .header("X-Request-ID", "test3")
            .bodyValue(initialUser)
            .exchange()
            .expectStatus().isCreated();
            
        // When: Try to create user with same email
        CreateUserRequestContent duplicateUser = CreateUserRequestContent.builder()
            .username("testuser2")  // Different username
            .email("test@example.com")  // Same email
            .password("Test123!")
            .firstName("Different")
            .lastName("User")
            .build();
            
        // Then: Should return 409 Conflict
        webTestClient.post()
            .uri("/users")
            .header("X-Request-ID", "test4")
            .bodyValue(duplicateUser)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody()
            .jsonPath("$.status").isEqualTo(409)
            .jsonPath("$.error").isEqualTo("Conflict")
            .jsonPath("$.message").isEqualTo("Email already exists");
    }
}
```

---

## 📋 Implementation Checklist

- [x] **Global Exception Handler**: Add `DuplicateKeyException` handler
- [x] **Service Layer**: Add exception mapping with `onErrorMap`
- [x] **Import Statements**: Add required imports for exception classes
- [x] **Error Messages**: Specific messages for username/email conflicts
- [x] **HTTP Status Codes**: Return 409 Conflict instead of 500 Internal Server Error
- [ ] **Unit Tests**: Test exception mapping logic
- [ ] **Integration Tests**: Test HTTP 409 responses
- [ ] **Documentation**: Update API documentation with conflict responses

---

## 🎖️ Best Practices

1. **Specific Error Messages**: Provide clear feedback about which field conflicts
2. **Proper HTTP Status**: Use 409 Conflict for resource conflicts, not 500 Internal Server Error
3. **Consistent Response Format**: Maintain same error response structure across all endpoints
4. **Exception Mapping**: Convert database exceptions to domain-appropriate exceptions
5. **Logging**: Log constraint violations at WARN level, not ERROR level
6. **Fallback Handling**: Handle both known and unknown constraint violations gracefully
7. **Case Insensitive**: Handle constraint names in case-insensitive manner

---

## 🔄 Response Comparison

### Before (❌ Incorrect):
```json
{
  "timestamp": "2025-11-08T15:23:28.367+00:00",
  "path": "/users",
  "status": 500,
  "error": "Internal Server Error",
  "requestId": "255681df-15"
}
```

### After (✅ Correct):
```json
{
  "timestamp": "2025-11-08T15:23:28.367+00:00",
  "path": "/users",
  "status": 409,
  "error": "Conflict", 
  "message": "Username already exists"
}
```

---

**Priority**: High - Affects API contract and user experience  
**Effort**: Low - Simple exception mapping changes  
**Risk**: Very Low - Non-breaking change with improved error handling

---

**Author**: Senior Software Architect  
**Date**: 2024-11-08  
**Version**: 1.0.0