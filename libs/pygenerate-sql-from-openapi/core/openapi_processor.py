"""
OpenAPI JSON file processor.
"""
import json
import os
from pathlib import Path
from typing import Dict, List, Any

class OpenApiProcessor:
    """Processes OpenAPI JSON files from build/smithy directory."""
    
    def __init__(self, build_dir: str = "build/smithy"):
        self.build_dir = Path(build_dir)
    
    def find_openapi_files(self) -> List[Dict[str, Any]]:
        """Find all openapi.json files in subdirectories."""
        openapi_files = []
        
        if not self.build_dir.exists():
            return openapi_files
        
        for subdir in self.build_dir.iterdir():
            if subdir.is_dir():
                openapi_dir = subdir / "openapi"
                if openapi_dir.exists():
                    # Find any .openapi.json file in the openapi directory
                    for openapi_file in openapi_dir.glob("*.openapi.json"):
                        openapi_files.append({
                            'service_name': subdir.name,
                            'file_path': openapi_file
                        })
        
        return openapi_files
    
    def load_openapi_spec(self, file_path: Path) -> Dict[str, Any]:
        """Load OpenAPI specification from JSON file."""
        with open(file_path, 'r', encoding='utf-8') as f:
            return json.load(f)
    
    def extract_response_schemas(self, openapi_spec: Dict[str, Any]) -> Dict[str, Any]:
        """Extract schemas that end with 'Response' or 'Status', excluding CRUD operations."""
        schemas = openapi_spec.get('components', {}).get('schemas', {})
        response_schemas = {}
        
        # Exclude CRUD operation schemas
        excluded_prefixes = ['Create', 'Delete', 'List', 'Update', 'Get']
        excluded_keywords = ['Error', 'Validation', 'Conflict', 'NotFound']
        
        for schema_name, schema_def in schemas.items():
            # Process enums as tables
            if schema_def.get('type') == 'string' and 'enum' in schema_def:
                table_name = schema_name.lower() + 's'
                response_schemas[table_name] = {
                    'type': 'enum_table',
                    'enum_values': schema_def['enum'],
                    'original_name': schema_name
                }
                continue
            
            if (schema_name.endswith('Response') or 
                schema_name.endswith('Status') or
                schema_name.endswith('ResponseContent')):
                
                # Skip CRUD operations and error schemas
                if (any(schema_name.startswith(prefix) for prefix in excluded_prefixes) or
                    any(keyword in schema_name for keyword in excluded_keywords)):
                    continue
                
                # Convert schema name to table name
                table_name = self._schema_to_table_name(schema_name)
                response_schemas[table_name] = schema_def
        
        return response_schemas
    
    def _schema_to_table_name(self, schema_name: str) -> str:
        """Convert schema name to table name."""
        # Remove suffixes and convert to snake_case
        name = schema_name.replace('Response', '').replace('Status', '').replace('Content', '')
        
        # Handle GetUser -> User
        if name.startswith('Get'):
            name = name[3:]
        
        # Convert CamelCase to snake_case
        import re
        name = re.sub('(.)([A-Z][a-z]+)', r'\1_\2', name)
        name = re.sub('([a-z0-9])([A-Z])', r'\1_\2', name).lower()
        
        # Pluralize table names
        if name.endswith('y'):
            name = name[:-1] + 'ies'
        elif name.endswith(('s', 'sh', 'ch', 'x', 'z')):
            name = name + 'es'
        else:
            name = name + 's'
        
        return name