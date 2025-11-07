-- back-ms-movies.sql
-- SQL DDL for back-ms-movies
-- Database: POSTGRESQL
-- Generated automatically from OpenAPI specification
-- Do not edit manually

-- Table for movies
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'movies') THEN
        EXECUTE '
            CREATE TABLE public."movies" (
              "id" UUID DEFAULT gen_random_uuid() PRIMARY KEY -- Unique identifier,
  "movie_id" UUID NOT NULL,
  "title" VARCHAR(255) NOT NULL,
  "director" VARCHAR(255) NOT NULL,
  "genre" VARCHAR(255) NOT NULL,
  "releaseYear" DECIMAL(10,2) NOT NULL,
  "duration" DECIMAL(10,2) NOT NULL,
  "description" VARCHAR(255),
  "availableCopies" DECIMAL(10,2) NOT NULL,
  "rentalPrice" DOUBLE PRECISION NOT NULL,
  "createdAt" TIMESTAMPTZ NOT NULL,
  "updatedAt" TIMESTAMPTZ NOT NULL,
  "status" VARCHAR(255) NOT NULL
            )
        ';
    END IF;
END$$;

CREATE INDEX "idx_movies_title" ON "movies" ("title"); -- Index for title field

CREATE INDEX "idx_movies_status" ON "movies" ("status"); -- Index for status field

-- Table for rentals
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'rentals') THEN
        EXECUTE '
            CREATE TABLE public."rentals" (
              "id" UUID DEFAULT gen_random_uuid() PRIMARY KEY -- Unique identifier,
  "rental_id" UUID NOT NULL,
  "movie_id" UUID NOT NULL,
  "user_id" UUID NOT NULL,
  "rentalDate" TIMESTAMPTZ NOT NULL,
  "dueDate" TIMESTAMPTZ NOT NULL,
  "returnDate" TIMESTAMPTZ,
  "totalPrice" DOUBLE PRECISION NOT NULL,
  "lateFee" DOUBLE PRECISION,
  "createdAt" TIMESTAMPTZ NOT NULL,
  "updatedAt" TIMESTAMPTZ NOT NULL,
  "status" VARCHAR(255) NOT NULL
            )
        ';
    END IF;
END$$;

CREATE INDEX "idx_rentals_status" ON "rentals" ("status"); -- Index for status field
