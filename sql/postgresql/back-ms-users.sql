-- SQL DDL for back-ms-users
-- Database: POSTGRESQL
-- Generated automatically from OpenAPI specification
-- Do not edit manually

-- Table for users
CREATE TABLE "users" (
  "id" UUID DEFAULT gen_random_uuid() PRIMARY KEY -- Unique identifier,
  "user_id" UUID NOT NULL -- Unique identifier for the user account. Generated automatically upon creation.,
  "username" VARCHAR(255) NOT NULL UNIQUE -- User's unique username. Cannot be changed after account creation.,
  "email" VARCHAR(255) NOT NULL UNIQUE -- User's email address. Used for notifications and account recovery.,
  "firstName" VARCHAR(255) -- User's first name. May be null if not provided during registration.,
  "lastName" VARCHAR(255) -- User's last name. May be null if not provided during registration.,
  "status" VARCHAR(255) NOT NULL,
  "createdAt" TIMESTAMPTZ NOT NULL -- Timestamp when the user account was created. ISO 8601 format.,
  "updatedAt" TIMESTAMPTZ NOT NULL -- Timestamp when the user account was last updated. ISO 8601 format.
);

CREATE INDEX "idx_users_username" ON "users" ("username"); -- User's unique username. Cannot be changed after account creation.

CREATE INDEX "idx_users_email" ON "users" ("email"); -- User's email address. Used for notifications and account recovery.

CREATE INDEX "idx_users_firstName" ON "users" ("firstName"); -- User's first name. May be null if not provided during registration.

CREATE INDEX "idx_users_lastName" ON "users" ("lastName"); -- User's last name. May be null if not provided during registration.

CREATE INDEX "idx_users_status" ON "users" ("status"); -- Index for status field

-- Enumeration table for UserStatus
CREATE TABLE "userstatuss" (
  "id" UUID DEFAULT gen_random_uuid() PRIMARY KEY -- Unique identifier,
  "code" VARCHAR(50) NOT NULL UNIQUE -- Enum code value,
  "name" VARCHAR(100) NOT NULL -- Human readable name,
  "description" VARCHAR(255) -- Detailed description,
  "active" BOOLEAN NOT NULL DEFAULT TRUE -- Whether this enum value is active,
  "created_at" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP -- Record creation timestamp,
  "updated_at" TIMESTAMPTZ -- Record last update timestamp
);

-- Enum values
INSERT INTO "userstatuss" (code, name, description) VALUES ('ACTIVE', 'Active', 'UserStatus - Active');
INSERT INTO "userstatuss" (code, name, description) VALUES ('INACTIVE', 'Inactive', 'UserStatus - Inactive');
INSERT INTO "userstatuss" (code, name, description) VALUES ('SUSPENDED', 'Suspended', 'UserStatus - Suspended');