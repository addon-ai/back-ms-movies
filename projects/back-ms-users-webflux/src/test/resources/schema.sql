-- Test Schema for H2 Database
-- Automatically loaded by Spring Boot for tests

-- Table: neighborhoods
CREATE TABLE IF NOT EXISTS neighborhoods (
    neighborhood_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    city_id VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Table: createusercontents
CREATE TABLE IF NOT EXISTS createusercontents (
    createusercontent_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Table: locations
CREATE TABLE IF NOT EXISTS locations (
    location_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    region VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    neighborhood VARCHAR(255),
    address VARCHAR(255) NOT NULL,
    postal_code VARCHAR(255),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    location_type VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Table: citiesbyregioncontents
CREATE TABLE IF NOT EXISTS citiesbyregioncontents (
    citiesbyregioncontent_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Table: countriescontents
CREATE TABLE IF NOT EXISTS countriescontents (
    countriescontent_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Table: listlocationscontents
CREATE TABLE IF NOT EXISTS listlocationscontents (
    listlocationscontent_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Table: updatelocationcontents
CREATE TABLE IF NOT EXISTS updatelocationcontents (
    updatelocationcontent_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Table: cities
CREATE TABLE IF NOT EXISTS cities (
    city_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    region_id VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Table: neighborhoodsbycitycontents
CREATE TABLE IF NOT EXISTS neighborhoodsbycitycontents (
    neighborhoodsbycitycontent_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Table: listuserscontents
CREATE TABLE IF NOT EXISTS listuserscontents (
    listuserscontent_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Table: createlocationcontents
CREATE TABLE IF NOT EXISTS createlocationcontents (
    createlocationcontent_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Table: locationcontents
CREATE TABLE IF NOT EXISTS locationcontents (
    locationcontent_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Table: countries
CREATE TABLE IF NOT EXISTS countries (
    country_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Table: conflicterrorcontents
CREATE TABLE IF NOT EXISTS conflicterrorcontents (
    conflicterrorcontent_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Table: updateusercontents
CREATE TABLE IF NOT EXISTS updateusercontents (
    updateusercontent_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Table: users
CREATE TABLE IF NOT EXISTS users (
    user_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Table: validationerrorcontents
CREATE TABLE IF NOT EXISTS validationerrorcontents (
    validationerrorcontent_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Table: regionsbycountrycontents
CREATE TABLE IF NOT EXISTS regionsbycountrycontents (
    regionsbycountrycontent_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Table: notfounderrorcontents
CREATE TABLE IF NOT EXISTS notfounderrorcontents (
    notfounderrorcontent_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Table: deletelocationcontents
CREATE TABLE IF NOT EXISTS deletelocationcontents (
    deletelocationcontent_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Table: regions
CREATE TABLE IF NOT EXISTS regions (
    region_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(255) NOT NULL,
    country_id VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Table: deleteusercontents
CREATE TABLE IF NOT EXISTS deleteusercontents (
    deleteusercontent_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Table: usercontents
CREATE TABLE IF NOT EXISTS usercontents (
    usercontent_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
