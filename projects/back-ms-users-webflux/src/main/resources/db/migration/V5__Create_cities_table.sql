-- Create cities table
CREATE TABLE IF NOT EXISTS cities (
    city_id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    region_id VARCHAR(36) NOT NULL,
    code VARCHAR(10),
    population INTEGER,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_cities_region FOREIGN KEY (region_id) REFERENCES regions(region_id)
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_cities_name ON cities(name);
CREATE INDEX IF NOT EXISTS idx_cities_region_id ON cities(region_id);
CREATE INDEX IF NOT EXISTS idx_cities_code ON cities(code);
CREATE INDEX IF NOT EXISTS idx_cities_status ON cities(status);
CREATE INDEX IF NOT EXISTS idx_cities_population ON cities(population);

-- Create unique constraint for name per region
CREATE UNIQUE INDEX IF NOT EXISTS idx_cities_name_region ON cities(name, region_id);