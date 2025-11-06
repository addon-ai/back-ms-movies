-- PostgreSQL initialization script for back-ms-movies
-- This script runs automatically when the container starts for the first time

-- Create extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE back-ms-movies_db TO postgres;

-- Log initialization
\echo 'Database back-ms-movies_db initialized successfully'