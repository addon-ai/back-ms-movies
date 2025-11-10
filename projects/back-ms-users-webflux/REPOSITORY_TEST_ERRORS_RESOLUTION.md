# Repository Test Errors Resolution

## 📋 Error Summary

**Date:** November 10, 2025  
**Author:** Jiliar Silgado <jiliar.silgado@gmail.com>  
**Status:** ✅ RESOLVED

### Error Description

After fixing the initial ApplicationContext loading errors, repository integration tests were failing with `BadSqlGrammarException` errors:

```
[ERROR] JpaCityRepositoryTest.save_ShouldPersistEntity » BadSqlGrammar 
executeMany; bad SQL grammar [INSERT INTO cities (name, region_id, status) VALUES ($1, $2, $3)]

[ERROR] JpaUserRepositoryTest.save_ShouldPersistEntity » BadSqlGrammar 
executeMany; bad SQL grammar [INSERT INTO users (username, email, status) VALUES ($1, $2, $3)]
```

**Total Errors:** 30 test failures across 6 repository test classes

### Affected Tests

- ✅ JpaUserRepositoryTest (5 tests)
- ✅ JpaCityRepositoryTest (5 tests)
- ✅ JpaCountryRepositoryTest (5 tests)
- ✅ JpaRegionRepositoryTest (5 tests)
- ✅ JpaNeighborhoodRepositoryTest (5 tests)
- ✅ JpaLocationRepositoryTest (5 tests)

---

## 🔍 Root Cause Analysis

### Problem 1: Missing Database Schema

**Issue:** Tests were trying to insert data into tables that didn't exist.

**Why:** 
- Flyway migrations were disabled in test configuration
- No test-specific schema was provided
- H2 in-memory database started empty

**Evidence:**
```
bad SQL grammar [INSERT INTO cities ...]
```

### Problem 2: Data Type Mismatch

**Issue:** After creating schema, tests failed with data conversion errors.

**Error Message:**
```
Data conversion error converting "'56ffe109-ebb2-49ef-8fb6-c2358cf9c8f9' 
(NEIGHBORHOODS: ""CITY_ID"" UUID NOT NULL)"
```

**Why:**
- Test schema defined foreign keys as `UUID` type
- Entity classes (DBOs) defined foreign keys as `String` type
- H2 database couldn't convert String to UUID automatically

**Entity Definition (CityDbo.java):**
```java
@Column("region_id")
private String regionId;  // ← String type
```

**Initial Schema:**
```sql
CREATE TABLE cities (
    city_id UUID PRIMARY KEY,
    region_id UUID NOT NULL  -- ← UUID type (mismatch!)
);
```

### Problem 3: Duplicate Key Violations

**Issue:** Tests were failing with unique constraint violations.

**Error Message:**
```
Unique index or primary key violation: "PUBLIC.CONSTRAINT_INDEX_4 
ON PUBLIC.USERS(USERNAME NULLS FIRST) VALUES ( /* 1 */ 'test-username' )"
```

**Why:**
- All tests used hardcoded test data (e.g., `"test-username"`)
- Tests ran in parallel or sequentially without cleanup
- H2 in-memory database persisted data across tests in same class

---

## ✅ Solution Applied

### Solution 1: Create Test Schema

**File Created:** `src/test/resources/schema.sql`

```sql
-- Test Schema for H2 Database
-- This schema is automatically loaded by Spring Boot for tests

-- Table for users
CREATE TABLE IF NOT EXISTS users (
    user_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table for countries
CREATE TABLE IF NOT EXISTS countries (
    country_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table for regions
CREATE TABLE IF NOT EXISTS regions (
    region_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(255) NOT NULL,
    country_id VARCHAR(255) NOT NULL,  -- VARCHAR to match entity
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table for cities
CREATE TABLE IF NOT EXISTS cities (
    city_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    region_id VARCHAR(255) NOT NULL,  -- VARCHAR to match entity
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table for neighborhoods
CREATE TABLE IF NOT EXISTS neighborhoods (
    neighborhood_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    city_id VARCHAR(255) NOT NULL,  -- VARCHAR to match entity
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table for locations
CREATE TABLE IF NOT EXISTS locations (
    location_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,  -- VARCHAR to match entity
    country VARCHAR(255) NOT NULL,
    region VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    neighborhood VARCHAR(255),
    address VARCHAR(255) NOT NULL,
    postal_code VARCHAR(255),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    location_type VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);
```

**Key Points:**
- ✅ All tables match entity definitions exactly
- ✅ Foreign keys use `VARCHAR(255)` to match entity String fields
- ✅ Primary keys use `UUID` with auto-generation
- ✅ Timestamps have default values for convenience

### Solution 2: Enable Schema Loading

**File Modified:** `src/test/resources/application-test.yml`

```yaml
spring:
  r2dbc:
    url: r2dbc:h2:mem:///testdb?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
  flyway:
    enabled: false
  sql:
    init:
      mode: always  # ← Enable SQL initialization
      schema-locations: classpath:schema.sql  # ← Load schema.sql

logging:
  level:
    root: INFO
    org.springframework: WARN
```

**Why This Works:**
- `spring.sql.init.mode: always` tells Spring Boot to run SQL scripts
- `schema-locations` points to our test schema file
- Runs before any tests execute
- Creates all tables in H2 in-memory database

### Solution 3: Make Test Data Unique

**Problem:** Hardcoded test data caused duplicate key violations.

**Before (JpaUserRepositoryTest.java):**
```java
private UserDbo createUserDbo() {
    return UserDbo.builder()
        .username("test-username")  // ← Always the same!
        .email("test@example.com")  // ← Always the same!
        .status(EntityStatus.ACTIVE)
        .build();
}
```

**After (JpaUserRepositoryTest.java):**
```java
private UserDbo createUserDbo() {
    return UserDbo.builder()
        .username("test-username-" + UUID.randomUUID())  // ← Unique!
        .email("test-" + UUID.randomUUID() + "@example.com")  // ← Unique!
        .status(EntityStatus.ACTIVE)
        .build();
}
```

**Applied to All Repository Tests:**
- ✅ JpaUserRepositoryTest
- ✅ JpaCityRepositoryTest
- ✅ JpaCountryRepositoryTest
- ✅ JpaRegionRepositoryTest
- ✅ JpaNeighborhoodRepositoryTest
- ✅ JpaLocationRepositoryTest

**Example for CityDbo:**
```java
private CityDbo createCityDbo() {
    UUID randomUUID = UUID.randomUUID();
    return CityDbo.builder()
        .name("test-name-" + randomUUID)
        .regionId(randomUUID.toString())  // String type
        .status(EntityStatus.ACTIVE)
        .build();
}
```

---

## 📊 Results

### Before Fix
```
[ERROR] Tests run: 251, Failures: 0, Errors: 30, Skipped: 0
[INFO] BUILD FAILURE
```

### After Fix
```
[INFO] Tests run: 251, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**Success Rate:** 100% ✅  
**Time:** ~75 seconds for full test suite

---

## 🔧 Technical Details

### Why Foreign Keys Are Strings

The entity classes use `String` for foreign key references instead of `UUID`:

```java
@Column("region_id")
private String regionId;  // Not UUID!
```

**Reasons:**
1. **Flexibility:** Can store UUID as string or other identifier formats
2. **R2DBC Compatibility:** Simpler type conversion in reactive streams
3. **JSON Serialization:** Easier to serialize/deserialize in REST APIs
4. **Domain Separation:** Infrastructure layer doesn't enforce UUID at entity level

### H2 vs PostgreSQL Differences

| Aspect | PostgreSQL (Production) | H2 (Tests) |
|--------|------------------------|------------|
| UUID Generation | `gen_random_uuid()` | `RANDOM_UUID()` |
| Timestamp Type | `TIMESTAMPTZ` | `TIMESTAMP` |
| Foreign Key Type | Can be `UUID` | Must match entity (`VARCHAR`) |
| Schema Location | Flyway migrations | `schema.sql` in test resources |

### Test Isolation Strategy

**Approach Used:** Unique test data per test execution

**Alternatives Considered:**
1. ❌ `@DirtiesContext` - Too slow, recreates entire Spring context
2. ❌ `@Transactional` with rollback - Doesn't work well with R2DBC
3. ❌ Manual cleanup in `@AfterEach` - Adds complexity and boilerplate
4. ✅ **Unique data with UUID** - Simple, fast, no cleanup needed

---

## 📝 Lessons Learned

### 1. Schema Consistency is Critical

**Lesson:** Test database schema must exactly match entity definitions.

**Best Practice:**
- Keep test schema in sync with production Flyway migrations
- Use same data types in tests as in entities
- Document any intentional differences

### 2. Test Data Must Be Unique

**Lesson:** Hardcoded test data causes flaky tests and failures.

**Best Practice:**
```java
// ❌ Bad: Hardcoded values
.username("test-user")

// ✅ Good: Unique values
.username("test-user-" + UUID.randomUUID())
```

### 3. R2DBC Requires Different Approach

**Lesson:** R2DBC is not JPA - different configuration and testing strategies.

**Key Differences:**
- No `@Transactional` rollback support
- Different type conversions
- Reactive streams require `.block()` in tests
- Schema initialization works differently

### 4. H2 Compatibility

**Lesson:** H2 syntax differs from PostgreSQL in subtle ways.

**Watch Out For:**
- UUID generation functions
- Timestamp types and timezones
- Type conversion strictness
- Function names (e.g., `gen_random_uuid()` vs `RANDOM_UUID()`)

---

## 🚀 Verification Steps

To verify the fix works:

```bash
# Run all tests
mvn clean test

# Run specific repository test
mvn test -Dtest=JpaUserRepositoryTest

# Run all repository tests
mvn test -Dtest="Jpa*RepositoryTest"

# Run with coverage
mvn clean verify
```

Expected output:
```
[INFO] Tests run: 251, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 📚 Files Modified

### Created Files
1. `src/test/resources/schema.sql` - H2 test database schema

### Modified Files
1. `src/test/resources/application-test.yml` - Enabled SQL initialization
2. `src/test/java/.../JpaUserRepositoryTest.java` - Unique test data
3. `src/test/java/.../JpaCityRepositoryTest.java` - Unique test data
4. `src/test/java/.../JpaCountryRepositoryTest.java` - Unique test data
5. `src/test/java/.../JpaRegionRepositoryTest.java` - Unique test data
6. `src/test/java/.../JpaNeighborhoodRepositoryTest.java` - Unique test data
7. `src/test/java/.../JpaLocationRepositoryTest.java` - Unique test data

---

## 🔗 Related Documentation

- [TEST_ERRORS_RESOLUTION.md](./TEST_ERRORS_RESOLUTION.md) - Initial ApplicationContext errors
- [Spring Boot Testing Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Spring Data R2DBC Reference](https://docs.spring.io/spring-data/r2dbc/docs/current/reference/html/)
- [H2 Database Documentation](https://www.h2database.com/)

---

## ✅ Checklist for Future Test Development

When creating new repository tests:

- [ ] Create test schema in `schema.sql` matching entity definitions
- [ ] Use unique test data with `UUID.randomUUID()`
- [ ] Match foreign key types (String vs UUID) with entity definitions
- [ ] Use `.block()` for reactive operations in tests
- [ ] Verify H2 compatibility for SQL functions
- [ ] Test both save and retrieve operations
- [ ] Include null/edge case tests
- [ ] Ensure tests can run in any order

---

## 🎯 Summary

**Problem:** Repository tests failing due to missing schema and data type mismatches  
**Root Cause:** No test database schema + UUID/String type conflicts + duplicate test data  
**Solution:** Created H2 schema matching entities + enabled SQL init + unique test data  
**Result:** All 251 tests passing ✅  
**Time to Fix:** ~30 minutes  
**Impact:** Zero test failures, full test coverage maintained
