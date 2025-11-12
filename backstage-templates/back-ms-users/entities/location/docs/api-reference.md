# 📚 API Reference

## OpenAPI Specification

The complete API specification is available in the OpenAPI format. This specification includes:

- All available endpoints
- Request/response schemas
- Authentication requirements
- Error codes and responses

## 🔑 Key Endpoints


### Cities
- `GET /cities/{cityId}/neighborhoods` - Retrieves neighborhoods within a specific city. Returns all neighborhoods tha...

### Countries
- `GET /countries` - Retrieves list of available countries. Returns all countries in the system wi...
- `GET /countries/{countryId}/regions` - Retrieves regions within a specific country. Returns all regions (states, pro...

### Locations
- `GET /locations` - Lists locations with filtering and pagination. Returns a paginated list of lo...
- `POST /locations` - Creates a new location for a user. Registers a new location with complete add...
- `DELETE /locations/{locationId}` - Deletes a location. Permanently removes a location from the system. This oper...
- `GET /locations/{locationId}` - Retrieves a location by its unique identifier. Returns complete location info...
- `PUT /locations/{locationId}` - Updates an existing location's information. Allows partial updates to locatio...

### Regions
- `GET /regions/{regionId}/cities` - Retrieves cities within a specific region. Returns all cities that belong to ...


For complete details, refer to the OpenAPI specification file.
