# Mock Data Issue Solution - Spring WebFlux R2DBC

## 🚨 Problem Summary

**Issue**: GET `/users?page=1&size=20` endpoint returns mock data with "string" values instead of real database data.

**Current Response**:
```json
{
  "users": [
    {
      "userId": "string",
      "username": "string", 
      "email": "string",
      "firstName": "string",
      "lastName": "string",
      "status": "string",
      "createdAt": "string",
      "updatedAt": "string"
    }
  ],
  "page": 0,
  "size": 0,
  "total": 0,
  "totalPages": 0
}
```

**Error on Startup**:
```
Failed to configure a ConnectionFactory: 'url' attribute is not specified and no embedded database could be configured.
Reason: Failed to determine a suitable R2DBC Connection URL
```

---

## 🔍 Root Cause Analysis

### 1. Missing Database Configuration Profile
**Problem**: Application uses `local` profile but no `application-local.yml` exists.
**Impact**: R2DBC cannot connect to database, causing startup failure.

### 2. Database Schema Mismatch
**Problem**: SQL schema uses `user_id` but entity mapping expects `id`.
**Impact**: Even if connected, queries would fail due to column name mismatch.

### 3. Empty Database
**Problem**: No test data exists in database.
**Impact**: Even with correct connection, endpoint returns empty results.

### 4. Swagger Mock Response
**Problem**: When database connection fails, Swagger may return example/mock data.
**Impact**: API appears to work but returns placeholder values.

---

## ✅ Complete Solution Implementation

### 1. Create Local Profile Configuration

**File**: `application-local.yml`

```yaml
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/back_ms_users_webflux_db
    username: postgres
    password: password123
  flyway:
    url: jdbc:postgresql://localhost:5432/back_ms_users_webflux_db
    user: postgres
    password: password123
    locations: classpath:db/migration
    baseline-on-migrate: true
    validate-on-migrate: false
    enabled: true

server:
  port: 8080

logging:
  level:
    root: DEBUG
    org.springframework.r2dbc: DEBUG
    com.example.userservice: DEBUG

springdoc:
  api-docs:
    enabled: true
  swagger-ui:
    enabled: true
```

### 2. Fix Database Schema Column Names

**File**: `V1__initial_schema.sql`

**Problem**:
```sql
-- Wrong column name
CREATE TABLE users (
    user_id UUID PRIMARY KEY,  -- ❌ Entity expects 'id'
    username VARCHAR(255),
    -- ...
);
```

**Solution**:
```sql
-- Correct column name
CREATE TABLE users (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,  -- ✅ Matches entity mapping
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);
```

### 3. Add Test Data Migration

**File**: `V2__insert_test_data.sql`

```sql
-- Insert test users for endpoint verification
INSERT INTO users (id, username, email, first_name, last_name, status, created_at, updated_at) VALUES 
(gen_random_uuid(), 'john_doe', 'john@example.com', 'John', 'Doe', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'jane_smith', 'jane@example.com', 'Jane', 'Smith', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'bob_wilson', 'bob@example.com', 'Bob', 'Wilson', 'INACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;
```

### 4. Verify Entity Mapping Consistency

**File**: `UserDbo.java`

```java
@Table("users")
public class UserDbo {
    @Id
    @Column("id")  // ✅ Matches database column
    private UUID id;
    
    @Column("username")
    private String username;
    
    @Column("email") 
    private String email;
    
    @Column("first_name")  // ✅ Snake case matches database
    private String firstName;
    
    @Column("last_name")   // ✅ Snake case matches database
    private String lastName;
    
    @Column("status")
    private EntityStatus status;
    
    @Column("created_at")  // ✅ Snake case matches database
    private Instant createdAt;
    
    @Column("updated_at")  // ✅ Snake case matches database
    private Instant updatedAt;
}
```

---

## 🎯 Python/Mustache Code Generation Templates

### 1. Profile Configuration Template

**File**: `application_profile.mustache`

```mustache
spring:
  r2dbc:
    url: {{#isLocal}}r2dbc:postgresql://localhost:5432/{{databaseName}}{{/isLocal}}{{^isLocal}}${DB_URL}{{/isLocal}}
    username: {{#isLocal}}{{defaultUsername}}{{/isLocal}}{{^isLocal}}${DB_USERNAME}{{/isLocal}}
    password: {{#isLocal}}{{defaultPassword}}{{/isLocal}}{{^isLocal}}${DB_PASSWORD}{{/isLocal}}
  flyway:
    url: {{#isLocal}}jdbc:postgresql://localhost:5432/{{databaseName}}{{/isLocal}}{{^isLocal}}${FLYWAY_URL}{{/isLocal}}
    user: {{#isLocal}}{{defaultUsername}}{{/isLocal}}{{^isLocal}}${DB_USERNAME}{{/isLocal}}
    password: {{#isLocal}}{{defaultPassword}}{{/isLocal}}{{^isLocal}}${DB_PASSWORD}{{/isLocal}}
    locations: classpath:db/migration
    baseline-on-migrate: true
    validate-on-migrate: false
    enabled: true

server:
  port: {{serverPort}}

logging:
  level:
    root: {{#isLocal}}DEBUG{{/isLocal}}{{^isLocal}}INFO{{/isLocal}}
    org.springframework.r2dbc: {{#isLocal}}DEBUG{{/isLocal}}{{^isLocal}}WARN{{/isLocal}}
    {{packageName}}: {{#isLocal}}DEBUG{{/isLocal}}{{^isLocal}}INFO{{/isLocal}}

springdoc:
  api-docs:
    enabled: {{#isLocal}}true{{/isLocal}}{{^isLocal}}false{{/isLocal}}
  swagger-ui:
    enabled: {{#isLocal}}true{{/isLocal}}{{^isLocal}}false{{/isLocal}}
```

### 2. Database Schema Template

**File**: `schema.mustache`

```mustache
-- Table for {{tableName}}
CREATE TABLE IF NOT EXISTS {{tableName}} (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY, -- ✅ CRITICAL: Always use 'id' as PK
    {{#fields}}
    {{dbColumnName}} {{dbType}}{{#isNotNull}} NOT NULL{{/isNotNull}}{{#isUnique}} UNIQUE{{/isUnique}},{{#hasComment}} -- {{comment}}{{/hasComment}}
    {{/fields}}
    {{#auditFields}}
    {{fieldName}} TIMESTAMPTZ NOT NULL{{#hasDefault}} DEFAULT {{defaultValue}}{{/hasDefault}},{{#hasComment}} -- {{comment}}{{/hasComment}}
    {{/auditFields}}
);

{{#indexes}}
CREATE INDEX IF NOT EXISTS idx_{{tableName}}_{{columnName}} ON {{tableName}} ({{columnName}});{{#hasComment}} -- {{comment}}{{/hasComment}}
{{/indexes}}
```

### 3. Test Data Template

**File**: `test_data.mustache`

```mustache
-- Insert test data for {{entityName}}
INSERT INTO {{tableName}} ({{#fields}}{{dbColumnName}}{{#hasNext}}, {{/hasNext}}{{/fields}}) VALUES 
{{#testRecords}}
({{#fieldValues}}'{{value}}'{{#hasNext}}, {{/hasNext}}{{/fieldValues}}){{#hasNext}},{{/hasNext}}
{{/testRecords}}
ON CONFLICT ({{uniqueField}}) DO NOTHING;
```

### 4. Python Generation Script

**File**: `generate_database_config.py`

```python
def generate_profile_config(profile_name, config):
    """Generate profile-specific configuration"""
    
    context = {
        "isLocal": profile_name == "local",
        "databaseName": config['database_name'],
        "defaultUsername": config.get('default_username', 'postgres'),
        "defaultPassword": config.get('default_password', 'password123'),
        "serverPort": config.get('server_port', 8080),
        "packageName": config['package_name']
    }
    
    return render_template('application_profile.mustache', context)

def generate_schema_sql(entities):
    """Generate database schema with correct column names"""
    
    for entity in entities:
        # Ensure primary key is always 'id'
        entity['primary_key'] = 'id'
        
        # Convert camelCase to snake_case for database columns
        for field in entity['fields']:
            field['dbColumnName'] = camel_to_snake(field['fieldName'])
            
        # Add audit fields
        entity['auditFields'] = [
            {
                "fieldName": "created_at",
                "dbType": "TIMESTAMPTZ",
                "hasDefault": True,
                "defaultValue": "CURRENT_TIMESTAMP",
                "comment": "Record creation timestamp"
            },
            {
                "fieldName": "updated_at", 
                "dbType": "TIMESTAMPTZ",
                "comment": "Record last update timestamp"
            }
        ]
    
    return render_template('schema.mustache', {"entities": entities})

def generate_test_data(entity_config):
    """Generate test data for entities"""
    
    test_records = []
    for i in range(3):  # Generate 3 test records
        record = {
            "fieldValues": []
        }
        
        for field in entity_config['fields']:
            if field['fieldName'] == 'id':
                record['fieldValues'].append({"value": "gen_random_uuid()"})
            elif field['fieldName'] in ['createdAt', 'updatedAt']:
                record['fieldValues'].append({"value": "CURRENT_TIMESTAMP"})
            else:
                record['fieldValues'].append({
                    "value": generate_test_value(field, i)
                })
        
        test_records.append(record)
    
    context = {
        "entityName": entity_config['entity_name'],
        "tableName": entity_config['table_name'],
        "testRecords": test_records,
        "uniqueField": entity_config.get('unique_field', 'username')
    }
    
    return render_template('test_data.mustache', context)

def camel_to_snake(name):
    """Convert camelCase to snake_case"""
    import re
    s1 = re.sub('(.)([A-Z][a-z]+)', r'\1_\2', name)
    return re.sub('([a-z0-9])([A-Z])', r'\1_\2', s1).lower()

# Example usage
user_config = {
    "entity_name": "User",
    "table_name": "users", 
    "database_name": "back_ms_users_webflux_db",
    "package_name": "com.example.userservice",
    "fields": [
        {"fieldName": "username", "dbType": "VARCHAR(255)", "isNotNull": True, "isUnique": True},
        {"fieldName": "email", "dbType": "VARCHAR(255)", "isNotNull": True, "isUnique": True},
        {"fieldName": "firstName", "dbType": "VARCHAR(255)"},
        {"fieldName": "lastName", "dbType": "VARCHAR(255)"},
        {"fieldName": "status", "dbType": "VARCHAR(255)", "isNotNull": True}
    ]
}
```

---

## 📋 Implementation Checklist

### ✅ Database Configuration
- [x] Create `application-local.yml` with hardcoded database values
- [x] Fix R2DBC connection URL format
- [x] Enable Flyway migrations for local profile
- [x] Add debug logging for database operations

### ✅ Database Schema Fixes
- [x] Change `user_id` to `id` in all tables
- [x] Ensure column names match entity `@Column` annotations
- [x] Use snake_case for database columns (first_name, last_name, etc.)
- [x] Add proper constraints and indexes

### ✅ Test Data
- [x] Create migration script with sample users
- [x] Use proper UUID generation
- [x] Include all required fields with realistic values
- [x] Handle conflicts with ON CONFLICT DO NOTHING

### ✅ Code Generation Updates
- [x] Update schema templates to use 'id' as primary key
- [x] Add camelCase to snake_case conversion
- [x] Include test data generation in pipeline
- [x] Add profile-specific configuration templates

---

## 🚀 Expected Result After Fix

**Correct API Response**:
```json
{
  "users": [
    {
      "userId": "123e4567-e89b-12d3-a456-426614174000",
      "username": "john_doe",
      "email": "john@example.com", 
      "firstName": "John",
      "lastName": "Doe",
      "status": "ACTIVE",
      "createdAt": "2024-11-08T16:30:00Z",
      "updatedAt": "2024-11-08T16:30:00Z"
    },
    {
      "userId": "456e7890-e12b-34c5-d678-901234567890",
      "username": "jane_smith",
      "email": "jane@example.com",
      "firstName": "Jane", 
      "lastName": "Smith",
      "status": "ACTIVE",
      "createdAt": "2024-11-08T16:30:00Z",
      "updatedAt": "2024-11-08T16:30:00Z"
    }
  ],
  "page": 1,
  "size": 20,
  "total": 3,
  "totalPages": 1
}
```

**Key Improvements**:
- ✅ Real database data instead of mock values
- ✅ Correct pagination metadata
- ✅ Proper UUID values
- ✅ Actual timestamps
- ✅ Working database connection
- ✅ Successful application startup

This solution ensures that Spring WebFlux microservices generated via Python/Mustache templates will connect to the database properly and return real data instead of mock responses.