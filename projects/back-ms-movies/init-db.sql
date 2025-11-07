-- Create database if it doesn't exist
SELECT 'CREATE DATABASE back_ms_movies_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'back_ms_movies_db')\gexec
