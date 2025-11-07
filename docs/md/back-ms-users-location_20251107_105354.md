# back-ms-users-location API Documentation

**Generated on**: 2025-11-07 10:53:54

---

**Total Schemas Found**: 26

---

## Component Schema: `CityInfo`

- **Type**: `object`

### Properties:

- **`cityId`**
  - **Type**: `string`
- **`name`**
  - **Type**: `string`
- **`regionId`**
  - **Type**: `string`
- **`status`**
  - **Type**: `string`
- **`createdAt`**
  - **Type**: `string`
- **`updatedAt`**
  - **Type**: `string`

### Required properties:

`cityId, createdAt, name, regionId, status, updatedAt`

---

## Component Schema: `CityResponse`

- **Type**: `object`

### Properties:

- **`cityId`**
  - **Type**: `string`
- **`name`**
  - **Type**: `string`
- **`regionId`**
  - **Type**: `string`
- **`status`**
  - **Type**: `string`
- **`createdAt`**
  - **Type**: `string`
- **`updatedAt`**
  - **Type**: `string`

### Required properties:

`cityId, createdAt, name, regionId, status, updatedAt`

---

## Component Schema: `ConflictError`

- **Type**: `object`

### Properties:

- **`message`**
  - **Type**: `string`

### Required properties:

`message`

---

## Component Schema: `CountryInfo`

- **Type**: `object`

### Properties:

- **`countryId`**
  - **Type**: `string`
- **`name`**
  - **Type**: `string`
- **`code`**
  - **Type**: `string`
- **`status`**
  - **Type**: `string`
- **`createdAt`**
  - **Type**: `string`
- **`updatedAt`**
  - **Type**: `string`

### Required properties:

`code, countryId, createdAt, name, status, updatedAt`

---

## Component Schema: `CountryResponse`

- **Type**: `object`

### Properties:

- **`countryId`**
  - **Type**: `string`
- **`name`**
  - **Type**: `string`
- **`code`**
  - **Type**: `string`
- **`status`**
  - **Type**: `string`
- **`createdAt`**
  - **Type**: `string`
- **`updatedAt`**
  - **Type**: `string`

### Required properties:

`code, countryId, createdAt, name, status, updatedAt`

---

## Component Schema: `CreateLocationRequestContent`

- **Type**: `object`

### Properties:

- **`userId`**
  - **Type**: `string`
- **`countryId`**
  - **Type**: `string`
- **`regionId`**
  - **Type**: `string`
- **`cityId`**
  - **Type**: `string`
- **`neighborhoodId`**
  - **Type**: `string`
- **`address`**
  - **Type**: `string`
- **`postalCode`**
  - **Type**: `string`
- **`latitude`**
  - **Type**: `number`
- **`longitude`**
  - **Type**: `number`
- **`locationType`**
  - `$ref`: #/components/schemas/LocationType

### Required properties:

`address, cityId, countryId, locationType, regionId, userId`

---

## Component Schema: `CreateLocation`

- **Type**: `object`

### Properties:

- **`locationId`**
  - **Type**: `string`
- **`userId`**
  - **Type**: `string`
- **`countryId`**
  - **Type**: `string`
- **`regionId`**
  - **Type**: `string`
- **`cityId`**
  - **Type**: `string`
- **`neighborhoodId`**
  - **Type**: `string`
- **`address`**
  - **Type**: `string`
- **`postalCode`**
  - **Type**: `string`
- **`latitude`**
  - **Type**: `number`
- **`longitude`**
  - **Type**: `number`
- **`locationType`**
  - `$ref`: #/components/schemas/LocationType
- **`createdAt`**
  - **Type**: `string`
- **`status`**
  - **Type**: `string`

### Required properties:

`address, cityId, countryId, createdAt, locationId, locationType, regionId, status, userId`

---

## Component Schema: `DeleteLocation`

- **Type**: `object`

### Properties:

- **`deleted`**
  - **Type**: `boolean`
- **`message`**
  - **Type**: `string`

### Required properties:

`deleted, message`

---

## Component Schema: `CitiesByRegion`

- **Type**: `object`

### Properties:

- **`cities`**
  - **Type**: `array`

### Required properties:

`cities`

---

## Component Schema: `Countries`

- **Type**: `object`

### Properties:

- **`countries`**
  - **Type**: `array`

### Required properties:

`countries`

---

## Component Schema: `Location`

- **Type**: `object`

### Properties:

- **`locationId`**
  - **Type**: `string`
- **`userId`**
  - **Type**: `string`
- **`country`**
  - `$ref`: #/components/schemas/CountryInfo
- **`region`**
  - `$ref`: #/components/schemas/RegionInfo
- **`city`**
  - `$ref`: #/components/schemas/CityInfo
- **`neighborhood`**
  - `$ref`: #/components/schemas/NeighborhoodInfo
- **`address`**
  - **Type**: `string`
- **`postalCode`**
  - **Type**: `string`
- **`latitude`**
  - **Type**: `number`
- **`longitude`**
  - **Type**: `number`
- **`locationType`**
  - `$ref`: #/components/schemas/LocationType
- **`createdAt`**
  - **Type**: `string`
- **`updatedAt`**
  - **Type**: `string`
- **`status`**
  - **Type**: `string`

### Required properties:

`address, city, country, createdAt, locationId, locationType, region, status, updatedAt, userId`

---

## Component Schema: `NeighborhoodsByCity`

- **Type**: `object`

### Properties:

- **`neighborhoods`**
  - **Type**: `array`

### Required properties:

`neighborhoods`

---

## Component Schema: `RegionsByCountry`

- **Type**: `object`

### Properties:

- **`regions`**
  - **Type**: `array`

### Required properties:

`regions`

---

## Component Schema: `ListLocations`

- **Type**: `object`

### Properties:

- **`locations`**
  - **Type**: `array`
- **`page`**
  - **Type**: `number`
- **`size`**
  - **Type**: `number`
- **`total`**
  - **Type**: `number`
- **`totalPages`**
  - **Type**: `number`

### Required properties:

`locations, page, size, total, totalPages`

---

## Component Schema: `LocationResponse`

- **Type**: `object`

### Properties:

- **`locationId`**
  - **Type**: `string`
- **`userId`**
  - **Type**: `string`
- **`country`**
  - `$ref`: #/components/schemas/CountryInfo
- **`region`**
  - `$ref`: #/components/schemas/RegionInfo
- **`city`**
  - `$ref`: #/components/schemas/CityInfo
- **`neighborhood`**
  - `$ref`: #/components/schemas/NeighborhoodInfo
- **`address`**
  - **Type**: `string`
- **`postalCode`**
  - **Type**: `string`
- **`latitude`**
  - **Type**: `number`
- **`longitude`**
  - **Type**: `number`
- **`locationType`**
  - `$ref`: #/components/schemas/LocationType
- **`status`**
  - **Type**: `string`

### Required properties:

`address, city, country, locationId, locationType, region, status, userId`

---

## Component Schema: `LocationType`

- **Type**: `string`

### Enum values:

`HOME, WORK, BILLING, SHIPPING, OTHER`

---

## Component Schema: `NeighborhoodInfo`

- **Type**: `object`

### Properties:

- **`neighborhoodId`**
  - **Type**: `string`
- **`name`**
  - **Type**: `string`
- **`cityId`**
  - **Type**: `string`
- **`status`**
  - **Type**: `string`
- **`createdAt`**
  - **Type**: `string`
- **`updatedAt`**
  - **Type**: `string`

### Required properties:

`cityId, createdAt, name, neighborhoodId, status, updatedAt`

---

## Component Schema: `NeighborhoodResponse`

- **Type**: `object`

### Properties:

- **`neighborhoodId`**
  - **Type**: `string`
- **`name`**
  - **Type**: `string`
- **`cityId`**
  - **Type**: `string`
- **`status`**
  - **Type**: `string`
- **`createdAt`**
  - **Type**: `string`
- **`updatedAt`**
  - **Type**: `string`

### Required properties:

`cityId, createdAt, name, neighborhoodId, status, updatedAt`

---

## Component Schema: `NotFoundError`

- **Type**: `object`

### Properties:

- **`message`**
  - **Type**: `string`

### Required properties:

`message`

---

## Component Schema: `RegionInfo`

- **Type**: `object`

### Properties:

- **`regionId`**
  - **Type**: `string`
- **`name`**
  - **Type**: `string`
- **`code`**
  - **Type**: `string`
- **`countryId`**
  - **Type**: `string`
- **`status`**
  - **Type**: `string`
- **`createdAt`**
  - **Type**: `string`
- **`updatedAt`**
  - **Type**: `string`

### Required properties:

`code, countryId, createdAt, name, regionId, status, updatedAt`

---

## Component Schema: `RegionResponse`

- **Type**: `object`

### Properties:

- **`regionId`**
  - **Type**: `string`
- **`name`**
  - **Type**: `string`
- **`code`**
  - **Type**: `string`
- **`countryId`**
  - **Type**: `string`
- **`status`**
  - **Type**: `string`
- **`createdAt`**
  - **Type**: `string`
- **`updatedAt`**
  - **Type**: `string`

### Required properties:

`code, countryId, createdAt, name, regionId, status, updatedAt`

---

## Component Schema: `UpdateLocationRequestContent`

- **Type**: `object`

### Properties:

- **`countryId`**
  - **Type**: `string`
- **`regionId`**
  - **Type**: `string`
- **`cityId`**
  - **Type**: `string`
- **`neighborhoodId`**
  - **Type**: `string`
- **`address`**
  - **Type**: `string`
- **`postalCode`**
  - **Type**: `string`
- **`latitude`**
  - **Type**: `number`
- **`longitude`**
  - **Type**: `number`
- **`locationType`**
  - `$ref`: #/components/schemas/LocationType

---

## Component Schema: `UpdateLocation`

- **Type**: `object`

### Properties:

- **`locationId`**
  - **Type**: `string`
- **`userId`**
  - **Type**: `string`
- **`country`**
  - `$ref`: #/components/schemas/CountryInfo
- **`region`**
  - `$ref`: #/components/schemas/RegionInfo
- **`city`**
  - `$ref`: #/components/schemas/CityInfo
- **`neighborhood`**
  - `$ref`: #/components/schemas/NeighborhoodInfo
- **`address`**
  - **Type**: `string`
- **`postalCode`**
  - **Type**: `string`
- **`latitude`**
  - **Type**: `number`
- **`longitude`**
  - **Type**: `number`
- **`locationType`**
  - `$ref`: #/components/schemas/LocationType
- **`updatedAt`**
  - **Type**: `string`
- **`status`**
  - **Type**: `string`

### Required properties:

`address, city, country, locationId, locationType, region, status, updatedAt, userId`

---

## Component Schema: `ValidationError`

- **Type**: `object`

### Properties:

- **`message`**
  - **Type**: `string`
- **`field`**
  - **Type**: `string`

### Required properties:

`field, message`

---

## Component Schema: `CreateLocationRequest`

- **Type**: `object`

### Properties:

- **`userId`**
  - **Type**: `string`
- **`countryId`**
  - **Type**: `string`
- **`regionId`**
  - **Type**: `string`
- **`cityId`**
  - **Type**: `string`
- **`neighborhoodId`**
  - **Type**: `string`
- **`address`**
  - **Type**: `string`
- **`postalCode`**
  - **Type**: `string`
- **`latitude`**
  - **Type**: `number`
- **`longitude`**
  - **Type**: `number`
- **`locationType`**
  - `$ref`: #/components/schemas/LocationType

### Required properties:

`address, cityId, countryId, locationType, regionId, userId`

---

## Component Schema: `UpdateLocationRequest`

- **Type**: `object`

### Properties:

- **`locationId`**
  - **Type**: `string`
- **`body`**
  - **Type**: `object`

### Required properties:

`locationId, body`

---

