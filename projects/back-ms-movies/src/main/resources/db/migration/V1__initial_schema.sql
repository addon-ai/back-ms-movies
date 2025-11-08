-- back-ms-movies.sql
-- SQL DDL for back-ms-movies
-- Database: POSTGRESQL
-- Generated automatically from OpenAPI specification
-- Do not edit manually

-- Table for movies
CREATE TABLE IF NOT EXISTS movies (
    movie_id UUID DEFAULT gen_random_uuid() PRIMARY KEY, -- Primary key identifier
    title VARCHAR(255) NOT NULL,
    director VARCHAR(255) NOT NULL,
    genre VARCHAR(255) NOT NULL,
    release_year DECIMAL(10,2) NOT NULL,
    duration DECIMAL(10,2) NOT NULL,
    description VARCHAR(255),
    available_copies DECIMAL(10,2) NOT NULL,
    rental_price DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    status VARCHAR(255) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_movies_title ON movies (title); -- Index for title field

CREATE INDEX IF NOT EXISTS idx_movies_status ON movies (status); -- Index for status field

-- Table for rentals
CREATE TABLE IF NOT EXISTS rentals (
    rental_id UUID DEFAULT gen_random_uuid() PRIMARY KEY, -- Primary key identifier
    movie_id UUID NOT NULL,
    user_id UUID NOT NULL,
    rental_date TIMESTAMPTZ NOT NULL,
    due_date TIMESTAMPTZ NOT NULL,
    return_date TIMESTAMPTZ,
    total_price DOUBLE PRECISION NOT NULL,
    late_fee DOUBLE PRECISION,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    status VARCHAR(255) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_rentals_status ON rentals (status); -- Index for status field
