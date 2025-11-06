-- Create locations table
CREATE TABLE IF NOT EXISTS locations (
    location_id VARCHAR(36) PRIMARY KEY,
    country VARCHAR(100) NOT NULL,
    region VARCHAR(100),
    city VARCHAR(100),
    neighborhood VARCHAR(100),
    address VARCHAR(255),
    postal_code VARCHAR(20),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE
);

-- Create indexes for location searches
CREATE INDEX IF NOT EXISTS idx_locations_country ON locations(country);
CREATE INDEX IF NOT EXISTS idx_locations_region ON locations(region);
CREATE INDEX IF NOT EXISTS idx_locations_city ON locations(city);
CREATE INDEX IF NOT EXISTS idx_locations_neighborhood ON locations(neighborhood);
CREATE INDEX IF NOT EXISTS idx_locations_status ON locations(status);

-- Create composite index for hierarchical location searches
CREATE INDEX IF NOT EXISTS idx_locations_hierarchy ON locations(country, region, city, neighborhood);