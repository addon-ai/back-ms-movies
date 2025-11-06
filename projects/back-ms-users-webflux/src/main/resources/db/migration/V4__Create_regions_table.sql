-- Create regions table
CREATE TABLE IF NOT EXISTS regions (
    region_id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    country_id VARCHAR(36) NOT NULL,
    code VARCHAR(10),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_regions_country FOREIGN KEY (country_id) REFERENCES countries(country_id)
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_regions_name ON regions(name);
CREATE INDEX IF NOT EXISTS idx_regions_country_id ON regions(country_id);
CREATE INDEX IF NOT EXISTS idx_regions_code ON regions(code);
CREATE INDEX IF NOT EXISTS idx_regions_status ON regions(status);

-- Create unique constraint for name per country
CREATE UNIQUE INDEX IF NOT EXISTS idx_regions_name_country ON regions(name, country_id);