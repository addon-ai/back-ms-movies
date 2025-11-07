# PyGenerate SQL from OpenAPI

Generates SQL DDL scripts from OpenAPI JSON specifications for multiple database dialects.

## Features

- ✅ **Multi-Database Support**: PostgreSQL, MySQL, SQL Server, Oracle
- ✅ **Auto-Discovery**: Finds all `*.openapi.json` files in `build/smithy` subdirectories
- ✅ **Schema Filtering**: Only processes schemas ending with `Response`, `Status`, or `ResponseContent`
- ✅ **Smart Table Naming**: Converts schema names to proper table names (pluralized, snake_case)
- ✅ **Auto-Generated Fields**: Adds primary keys, timestamps, and audit fields
- ✅ **Index Generation**: Creates indexes for search fields (name, email, username, status)
- ✅ **Type Mapping**: Proper OpenAPI to SQL type conversion per dialect

## Usage

### Basic Usage
```bash
cd libs/pygenerate-sql-from-openapi
python main.py
```

### Custom Directories
```bash
python main.py /path/to/build/smithy /path/to/output
```

## Output

Generates SQL files in `schemas/` directory:
- `back-ms-users_postgresql.sql`
- `back-ms-users_mysql.sql`
- `back-ms-users_sqlserver.sql`
- `back-ms-users_oracle.sql`

## Example

Input OpenAPI schema:
```json
{
  "GetUserResponseContent": {
    "type": "object",
    "properties": {
      "userId": {"type": "string"},
      "username": {"type": "string"},
      "email": {"type": "string"},
      "firstName": {"type": "string"},
      "lastName": {"type": "string"}
    },
    "required": ["userId", "username", "email"]
  }
}
```

Output PostgreSQL:
```sql
CREATE TABLE "users" (
  "userId" VARCHAR(255) NOT NULL,
  "username" VARCHAR(255) NOT NULL UNIQUE,
  "email" VARCHAR(255) NOT NULL UNIQUE,
  "firstName" VARCHAR(255),
  "lastName" VARCHAR(255),
  "created_at" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" TIMESTAMPTZ
);

CREATE INDEX "idx_users_username" ON "users" ("username");
CREATE INDEX "idx_users_email" ON "users" ("email");
```