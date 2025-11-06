-- Create neighborhoods table
CREATE TABLE IF NOT EXISTS neighborhoods (
    neighborhood_id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    city_id VARCHAR(36) NOT NULL,
    code VARCHAR(10),
    postal_code VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_neighborhoods_city FOREIGN KEY (city_id) REFERENCES cities(city_id)
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_neighborhoods_name ON neighborhoods(name);
CREATE INDEX IF NOT EXISTS idx_neighborhoods_city_id ON neighborhoods(city_id);
CREATE INDEX IF NOT EXISTS idx_neighborhoods_code ON neighborhoods(code);
CREATE INDEX IF NOT EXISTS idx_neighborhoods_postal_code ON neighborhoods(postal_code);
CREATE INDEX IF NOT EXISTS idx_neighborhoods_status ON neighborhoods(status);

-- Create unique constraint for name per city
CREATE UNIQUE INDEX IF NOT EXISTS idx_neighborhoods_name_city ON neighborhoods(name, city_id);