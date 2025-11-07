-- SQL DDL for back-ms-users-location
-- Database: POSTGRESQL
-- Generated automatically from OpenAPI specification
-- Do not edit manually

-- Table for cities
CREATE TABLE "cities" (
  "id" UUID DEFAULT gen_random_uuid() PRIMARY KEY -- Unique identifier,
  "city_id" UUID NOT NULL,
  "name" VARCHAR(255) NOT NULL,
  "region_id" UUID NOT NULL,
  "status" VARCHAR(255) NOT NULL,
  "createdAt" TIMESTAMPTZ NOT NULL,
  "updatedAt" TIMESTAMPTZ NOT NULL
);

CREATE INDEX "idx_cities_name" ON "cities" ("name"); -- Index for name field

CREATE INDEX "idx_cities_status" ON "cities" ("status"); -- Index for status field

-- Table for countries
CREATE TABLE "countries" (
  "id" UUID DEFAULT gen_random_uuid() PRIMARY KEY -- Unique identifier,
  "country_id" UUID NOT NULL,
  "name" VARCHAR(255) NOT NULL,
  "code" VARCHAR(255) NOT NULL,
  "status" VARCHAR(255) NOT NULL,
  "createdAt" TIMESTAMPTZ NOT NULL,
  "updatedAt" TIMESTAMPTZ NOT NULL
);

CREATE INDEX "idx_countries_name" ON "countries" ("name"); -- Index for name field

CREATE INDEX "idx_countries_status" ON "countries" ("status"); -- Index for status field

-- Table for locations
CREATE TABLE "locations" (
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
);

CREATE INDEX "idx_locations_status" ON "locations" ("status"); -- Index for status field

-- Enumeration table for LocationType
CREATE TABLE "locationtypes" (
  "id" UUID DEFAULT gen_random_uuid() PRIMARY KEY -- Unique identifier,
  "code" VARCHAR(50) NOT NULL UNIQUE -- Enum code value,
  "name" VARCHAR(100) NOT NULL -- Human readable name,
  "description" VARCHAR(255) -- Detailed description,
  "active" BOOLEAN NOT NULL DEFAULT TRUE -- Whether this enum value is active,
  "created_at" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP -- Record creation timestamp,
  "updated_at" TIMESTAMPTZ -- Record last update timestamp
);

-- Enum values
INSERT INTO "locationtypes" (code, name, description) VALUES ('HOME', 'Home', 'LocationType - Home');
INSERT INTO "locationtypes" (code, name, description) VALUES ('WORK', 'Work', 'LocationType - Work');
INSERT INTO "locationtypes" (code, name, description) VALUES ('BILLING', 'Billing', 'LocationType - Billing');
INSERT INTO "locationtypes" (code, name, description) VALUES ('SHIPPING', 'Shipping', 'LocationType - Shipping');
INSERT INTO "locationtypes" (code, name, description) VALUES ('OTHER', 'Other', 'LocationType - Other');

-- Table for neighborhoods
CREATE TABLE "neighborhoods" (
  "id" UUID DEFAULT gen_random_uuid() PRIMARY KEY -- Unique identifier,
  "neighborhood_id" UUID NOT NULL,
  "name" VARCHAR(255) NOT NULL,
  "city_id" UUID NOT NULL,
  "status" VARCHAR(255) NOT NULL,
  "createdAt" TIMESTAMPTZ NOT NULL,
  "updatedAt" TIMESTAMPTZ NOT NULL
);

CREATE INDEX "idx_neighborhoods_name" ON "neighborhoods" ("name"); -- Index for name field

CREATE INDEX "idx_neighborhoods_status" ON "neighborhoods" ("status"); -- Index for status field

-- Table for regions
CREATE TABLE "regions" (
  "id" UUID DEFAULT gen_random_uuid() PRIMARY KEY -- Unique identifier,
  "region_id" UUID NOT NULL,
  "name" VARCHAR(255) NOT NULL,
  "code" VARCHAR(255) NOT NULL,
  "country_id" UUID NOT NULL,
  "status" VARCHAR(255) NOT NULL,
  "createdAt" TIMESTAMPTZ NOT NULL,
  "updatedAt" TIMESTAMPTZ NOT NULL
);

CREATE INDEX "idx_regions_name" ON "regions" ("name"); -- Index for name field

CREATE INDEX "idx_regions_status" ON "regions" ("status"); -- Index for status field