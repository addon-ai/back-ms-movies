"""
Test schema generator for H2 database.
"""
from pathlib import Path
from typing import Dict, List, Any


class TestSchemaGenerator:
    """Generates schema.sql for H2 test database."""
    
    def __init__(self, output_dir: Path):
        self.output_dir = output_dir
    
    def generate_schema(self, entities: List[str], openapi_specs: List[Dict[str, Any]]) -> str:
        """Generate H2 schema from entities and OpenAPI specs."""
        schema_lines = [
            "-- Test Schema for H2 Database",
            "-- Automatically loaded by Spring Boot for tests",
            ""
        ]
        
        for entity in entities:
            table_name = self._to_table_name(entity)
            columns = self._extract_columns(entity, openapi_specs)
            
            schema_lines.append(f"-- Table: {table_name}")
            schema_lines.append(f"CREATE TABLE IF NOT EXISTS {table_name} (")
            schema_lines.append(f"    {entity.lower()}_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,")
            
            for i, col in enumerate(columns):
                is_last = i == len(columns) - 1
                comma = "" if is_last else ","
                schema_lines.append(f"    {col['name']} {col['type']}{col['constraints']}{comma}")
            
            schema_lines.append(");")
            schema_lines.append("")
        
        return "\n".join(schema_lines)
    
    def _to_table_name(self, entity: str) -> str:
        """Convert entity name to table name (plural, lowercase)."""
        # Simple pluralization
        if entity.endswith('y'):
            return entity[:-1].lower() + 'ies'
        elif entity.endswith('s'):
            return entity.lower() + 'es'
        else:
            return entity.lower() + 's'
    
    def _extract_columns(self, entity: str, openapi_specs: List[Dict[str, Any]]) -> List[Dict[str, str]]:
        """Extract columns from OpenAPI specs."""
        columns = []
        
        for spec_info in openapi_specs:
            schemas = spec_info['spec'].get('components', {}).get('schemas', {})
            
            # Look for entity schema
            for schema_name, schema_data in schemas.items():
                if entity in schema_name and 'Response' in schema_name:
                    properties = schema_data.get('properties', {})
                    required = schema_data.get('required', [])
                    
                    for prop_name, prop_data in properties.items():
                        # Skip ID field (already in primary key)
                        if prop_name == f'{entity.lower()}Id' or prop_name == 'id':
                            continue
                        
                        col_type = self._map_type_to_h2(prop_data.get('type'), prop_data.get('format'))
                        constraints = self._build_constraints(prop_name, prop_data, required)
                        
                        columns.append({
                            'name': self._to_snake_case(prop_name),
                            'type': col_type,
                            'constraints': constraints
                        })
                    break
        
        # Add default columns if not found
        if not columns:
            columns = [
                {'name': 'status', 'type': 'VARCHAR(255)', 'constraints': ' NOT NULL'},
                {'name': 'created_at', 'type': 'TIMESTAMP', 'constraints': ' NOT NULL DEFAULT CURRENT_TIMESTAMP'},
                {'name': 'updated_at', 'type': 'TIMESTAMP', 'constraints': ' NOT NULL DEFAULT CURRENT_TIMESTAMP'}
            ]
        
        return columns
    
    def _map_type_to_h2(self, json_type: str, json_format: str = None) -> str:
        """Map JSON schema type to H2 SQL type."""
        type_mapping = {
            'string': 'VARCHAR(255)',
            'integer': 'INTEGER',
            'number': 'DOUBLE PRECISION',
            'boolean': 'BOOLEAN',
            'array': 'VARCHAR(1000)',  # JSON as string
            'object': 'VARCHAR(2000)'  # JSON as string
        }
        
        if json_format == 'date-time':
            return 'TIMESTAMP'
        elif json_format == 'uuid':
            return 'UUID'
        elif json_format == 'int64':
            return 'BIGINT'
        
        return type_mapping.get(json_type, 'VARCHAR(255)')
    
    def _build_constraints(self, prop_name: str, prop_data: Dict, required: List[str]) -> str:
        """Build SQL constraints for column."""
        constraints = []
        
        if prop_name in required:
            constraints.append('NOT NULL')
        
        # Add defaults for common fields
        if prop_name in ['createdAt', 'created_at']:
            constraints.append('DEFAULT CURRENT_TIMESTAMP')
        elif prop_name in ['updatedAt', 'updated_at']:
            constraints.append('DEFAULT CURRENT_TIMESTAMP')
        
        return ' ' + ' '.join(constraints) if constraints else ''
    
    def _to_snake_case(self, camel_case: str) -> str:
        """Convert camelCase to snake_case."""
        result = []
        for i, char in enumerate(camel_case):
            if char.isupper() and i > 0:
                result.append('_')
            result.append(char.lower())
        return ''.join(result)
    
    def write_schema(self, entities: List[str], openapi_specs: List[Dict[str, Any]]):
        """Write schema.sql file."""
        schema_content = self.generate_schema(entities, openapi_specs)
        
        test_resources = self.output_dir / 'src' / 'test' / 'resources'
        test_resources.mkdir(parents=True, exist_ok=True)
        
        schema_file = test_resources / 'schema.sql'
        schema_file.write_text(schema_content, encoding='utf-8')
