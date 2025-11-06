-- Insert sample countries
INSERT INTO countries (country_id, name, code, iso_code, phone_code, currency, status, created_at) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'Colombia', 'COL', 'CO', '+57', 'COP', 'ACTIVE', CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440002', 'United States', 'USA', 'US', '+1', 'USD', 'ACTIVE', CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440003', 'Mexico', 'MEX', 'MX', '+52', 'MXN', 'ACTIVE', CURRENT_TIMESTAMP)
ON CONFLICT (country_id) DO NOTHING;

-- Insert sample regions for Colombia
INSERT INTO regions (region_id, name, country_id, code, status, created_at) VALUES
('550e8400-e29b-41d4-a716-446655440011', 'Antioquia', '550e8400-e29b-41d4-a716-446655440001', 'ANT', 'ACTIVE', CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440012', 'Cundinamarca', '550e8400-e29b-41d4-a716-446655440001', 'CUN', 'ACTIVE', CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440013', 'Valle del Cauca', '550e8400-e29b-41d4-a716-446655440001', 'VAL', 'ACTIVE', CURRENT_TIMESTAMP)
ON CONFLICT (region_id) DO NOTHING;

-- Insert sample cities
INSERT INTO cities (city_id, name, region_id, code, population, status, created_at) VALUES
('550e8400-e29b-41d4-a716-446655440021', 'Medellín', '550e8400-e29b-41d4-a716-446655440011', 'MED', 2500000, 'ACTIVE', CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440022', 'Bogotá', '550e8400-e29b-41d4-a716-446655440012', 'BOG', 8000000, 'ACTIVE', CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440023', 'Cali', '550e8400-e29b-41d4-a716-446655440013', 'CAL', 2200000, 'ACTIVE', CURRENT_TIMESTAMP)
ON CONFLICT (city_id) DO NOTHING;

-- Insert sample neighborhoods
INSERT INTO neighborhoods (neighborhood_id, name, city_id, code, postal_code, status, created_at) VALUES
('550e8400-e29b-41d4-a716-446655440031', 'El Poblado', '550e8400-e29b-41d4-a716-446655440021', 'EPO', '050021', 'ACTIVE', CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440032', 'Laureles', '550e8400-e29b-41d4-a716-446655440021', 'LAU', '050034', 'ACTIVE', CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440033', 'Chapinero', '550e8400-e29b-41d4-a716-446655440022', 'CHA', '110221', 'ACTIVE', CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440034', 'Zona Rosa', '550e8400-e29b-41d4-a716-446655440022', 'ZRO', '110010', 'ACTIVE', CURRENT_TIMESTAMP)
ON CONFLICT (neighborhood_id) DO NOTHING;

-- Insert sample users
INSERT INTO users (user_id, username, email, first_name, last_name, phone, date_of_birth, status, created_at) VALUES
('550e8400-e29b-41d4-a716-446655440041', 'jsilgado', 'jiliar.silgado@gmail.com', 'Jiliar', 'Silgado', '+57300123456', '1990-01-15', 'ACTIVE', CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440042', 'admin', 'admin@example.com', 'Admin', 'User', '+57300654321', '1985-05-20', 'ACTIVE', CURRENT_TIMESTAMP)
ON CONFLICT (user_id) DO NOTHING;

-- Insert sample locations
INSERT INTO locations (location_id, country, region, city, neighborhood, address, postal_code, latitude, longitude, status, created_at) VALUES
('550e8400-e29b-41d4-a716-446655440051', 'Colombia', 'Antioquia', 'Medellín', 'El Poblado', 'Carrera 43A #5A-113', '050021', 6.2088, -75.5648, 'ACTIVE', CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440052', 'Colombia', 'Cundinamarca', 'Bogotá', 'Chapinero', 'Carrera 13 #85-32', '110221', 4.6097, -74.0817, 'ACTIVE', CURRENT_TIMESTAMP)
ON CONFLICT (location_id) DO NOTHING;