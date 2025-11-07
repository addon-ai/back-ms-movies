-- back-ms-users.sql
-- SQL DDL for back-ms-users
-- Database: POSTGRESQL
-- Generated automatically from OpenAPI specification
-- Do not edit manually

-- Table for users
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'users') THEN
        EXECUTE '
            CREATE TABLE public."users" (
              "id" UUID DEFAULT gen_random_uuid() PRIMARY KEY -- Unique identifier,
  "user_id" UUID NOT NULL -- Unique identifier for the user account. Generated automatically upon creation,
  "username" VARCHAR(255) NOT NULL UNIQUE -- Users unique username. Cannot be changed after account creation,
  "email" VARCHAR(255) NOT NULL UNIQUE -- Users email address. Used for notifications and account recovery,
  "firstName" VARCHAR(255) -- Users first name. May be null if not provided during registration,
  "lastName" VARCHAR(255) -- Users last name. May be null if not provided during registration,
  "status" VARCHAR(255) NOT NULL,
  "createdAt" TIMESTAMPTZ NOT NULL -- Timestamp when the user account was created. ISO 8601 format,
  "updatedAt" TIMESTAMPTZ NOT NULL -- Timestamp when the user account was last updated. ISO 8601 format
            )
        ';
    END IF;
END$$;

CREATE INDEX "idx_users_username" ON "users" ("username"); -- Users unique username. Cannot be changed after account creation

CREATE INDEX "idx_users_email" ON "users" ("email"); -- Users email address. Used for notifications and account recovery

CREATE INDEX "idx_users_firstName" ON "users" ("firstName"); -- Users first name. May be null if not provided during registration

CREATE INDEX "idx_users_lastName" ON "users" ("lastName"); -- Users last name. May be null if not provided during registration

CREATE INDEX "idx_users_status" ON "users" ("status"); -- Index for status field

-- Enumeration table for UserStatus
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'userstatuss') THEN
        EXECUTE '
            CREATE TABLE public."userstatuss" (
              "id" UUID DEFAULT gen_random_uuid() PRIMARY KEY -- Unique identifier,
  "code" VARCHAR(50) NOT NULL UNIQUE -- Enum code value,
  "name" VARCHAR(100) NOT NULL -- Human readable name,
  "description" VARCHAR(255) -- Detailed description,
  "active" BOOLEAN NOT NULL DEFAULT TRUE -- Whether this enum value is active,
  "created_at" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP -- Record creation timestamp,
  "updated_at" TIMESTAMPTZ -- Record last update timestamp
            )
        ';
    END IF;
END$$;

-- Enum values
INSERT INTO "userstatuss" (code, name, description) VALUES ('ACTIVE', 'Active', 'UserStatus - Active');
INSERT INTO "userstatuss" (code, name, description) VALUES ('INACTIVE', 'Inactive', 'UserStatus - Inactive');
INSERT INTO "userstatuss" (code, name, description) VALUES ('SUSPENDED', 'Suspended', 'UserStatus - Suspended');

-- back-ms-users-location.sql
-- SQL DDL for back-ms-users-location
-- Database: POSTGRESQL
-- Generated automatically from OpenAPI specification
-- Do not edit manually

-- Table for cities
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'cities') THEN
        EXECUTE '
            CREATE TABLE public."cities" (
              "id" UUID DEFAULT gen_random_uuid() PRIMARY KEY -- Unique identifier,
  "city_id" UUID NOT NULL,
  "name" VARCHAR(255) NOT NULL,
  "region_id" UUID NOT NULL,
  "status" VARCHAR(255) NOT NULL,
  "createdAt" TIMESTAMPTZ NOT NULL,
  "updatedAt" TIMESTAMPTZ NOT NULL
            )
        ';
    END IF;
END$$;

CREATE INDEX "idx_cities_name" ON "cities" ("name"); -- Index for name field

CREATE INDEX "idx_cities_status" ON "cities" ("status"); -- Index for status field

-- Table for countries
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'countries') THEN
        EXECUTE '
            CREATE TABLE public."countries" (
              "id" UUID DEFAULT gen_random_uuid() PRIMARY KEY -- Unique identifier,
  "country_id" UUID NOT NULL,
  "name" VARCHAR(255) NOT NULL,
  "code" VARCHAR(255) NOT NULL,
  "status" VARCHAR(255) NOT NULL,
  "createdAt" TIMESTAMPTZ NOT NULL,
  "updatedAt" TIMESTAMPTZ NOT NULL
            )
        ';
    END IF;
END$$;

CREATE INDEX "idx_countries_name" ON "countries" ("name"); -- Index for name field

CREATE INDEX "idx_countries_status" ON "countries" ("status"); -- Index for status field

-- Table for locations
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'locations') THEN
        EXECUTE '
            CREATE TABLE public."locations" (
              "id" UUID DEFAULT gen_random_uuid() PRIMARY KEY -- Unique identifier,
  "location_id" UUID NOT NULL,
  "user_id" UUID NOT NULL,
  "country" VARCHAR(255) NOT NULL,
  "region" VARCHAR(255) NOT NULL,
  "city" VARCHAR(255) NOT NULL,
  "neighborhood" VARCHAR(255),
  "address" VARCHAR(255) NOT NULL,
  "postalCode" VARCHAR(255),
  "latitude" DOUBLE PRECISION,
  "longitude" DOUBLE PRECISION,
  "locationType" VARCHAR(255) NOT NULL,
  "status" VARCHAR(255) NOT NULL,
  "created_at" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP -- Record creation timestamp,
  "updated_at" TIMESTAMPTZ -- Record last update timestamp
            )
        ';
    END IF;
END$$;

CREATE INDEX "idx_locations_status" ON "locations" ("status"); -- Index for status field

-- Enumeration table for LocationType
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'locationtypes') THEN
        EXECUTE '
            CREATE TABLE public."locationtypes" (
              "id" UUID DEFAULT gen_random_uuid() PRIMARY KEY -- Unique identifier,
  "code" VARCHAR(50) NOT NULL UNIQUE -- Enum code value,
  "name" VARCHAR(100) NOT NULL -- Human readable name,
  "description" VARCHAR(255) -- Detailed description,
  "active" BOOLEAN NOT NULL DEFAULT TRUE -- Whether this enum value is active,
  "created_at" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP -- Record creation timestamp,
  "updated_at" TIMESTAMPTZ -- Record last update timestamp
            )
        ';
    END IF;
END$$;

-- Enum values
INSERT INTO "locationtypes" (code, name, description) VALUES ('HOME', 'Home', 'LocationType - Home');
INSERT INTO "locationtypes" (code, name, description) VALUES ('WORK', 'Work', 'LocationType - Work');
INSERT INTO "locationtypes" (code, name, description) VALUES ('BILLING', 'Billing', 'LocationType - Billing');
INSERT INTO "locationtypes" (code, name, description) VALUES ('SHIPPING', 'Shipping', 'LocationType - Shipping');
INSERT INTO "locationtypes" (code, name, description) VALUES ('OTHER', 'Other', 'LocationType - Other');

-- Table for neighborhoods
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'neighborhoods') THEN
        EXECUTE '
            CREATE TABLE public."neighborhoods" (
              "id" UUID DEFAULT gen_random_uuid() PRIMARY KEY -- Unique identifier,
  "neighborhood_id" UUID NOT NULL,
  "name" VARCHAR(255) NOT NULL,
  "city_id" UUID NOT NULL,
  "status" VARCHAR(255) NOT NULL,
  "createdAt" TIMESTAMPTZ NOT NULL,
  "updatedAt" TIMESTAMPTZ NOT NULL
            )
        ';
    END IF;
END$$;

CREATE INDEX "idx_neighborhoods_name" ON "neighborhoods" ("name"); -- Index for name field

CREATE INDEX "idx_neighborhoods_status" ON "neighborhoods" ("status"); -- Index for status field

-- Table for regions
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'regions') THEN
        EXECUTE '
            CREATE TABLE public."regions" (
              "id" UUID DEFAULT gen_random_uuid() PRIMARY KEY -- Unique identifier,
  "region_id" UUID NOT NULL,
  "name" VARCHAR(255) NOT NULL,
  "code" VARCHAR(255) NOT NULL,
  "country_id" UUID NOT NULL,
  "status" VARCHAR(255) NOT NULL,
  "createdAt" TIMESTAMPTZ NOT NULL,
  "updatedAt" TIMESTAMPTZ NOT NULL
            )
        ';
    END IF;
END$$;

CREATE INDEX "idx_regions_name" ON "regions" ("name"); -- Index for name field

CREATE INDEX "idx_regions_status" ON "regions" ("status"); -- Index for status field
