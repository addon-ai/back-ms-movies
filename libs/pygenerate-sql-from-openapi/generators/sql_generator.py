"""
SQL DDL generator from OpenAPI schemas.
"""
from typing import Dict, List, Any
from core.type_mapper import get_sql_type

class SqlGenerator:
    """Generates SQL DDL statements from OpenAPI schemas."""
    
    def __init__(self, dialect: str):
        self.dialect = dialect
        self.supported_dialects = ['postgresql', 'mysql', 'sqlserver', 'oracle']
        
        if dialect not in self.supported_dialects:
            raise ValueError(f"Unsupported dialect: {dialect}. Supported: {self.supported_dialects}")
    
    def generate_create_table(self, table_name: str, schema_def: Dict[str, Any]) -> str:
        """Generate CREATE TABLE statement from schema definition."""
        # Handle enum tables
        if schema_def.get('type') == 'enum_table':
            return self._generate_enum_table(table_name, schema_def)
        
        if schema_def.get('type') != 'object':
            return ""
        
        columns = []
        required_fields = schema_def.get('required', [])
        properties = schema_def.get('properties', {})
        table_description = self._clean_sql_comment(schema_def.get('description', f'Table for {table_name}'))
        
        # Add table comment
        result = f"-- {table_description}\n"
        
        # Process properties to find primary key
        primary_key_field = None
        for prop_name in properties.keys():
            if prop_name.endswith('Id') or prop_name == 'id':
                primary_key_field = prop_name
                break
        
        # Process properties
        for prop_name, prop_def in properties.items():
            field_description = self._clean_sql_comment(prop_def.get('description', ''))
            comment = f' -- {field_description}' if field_description else ''
            
            # Handle ID fields
            if prop_name.endswith('Id') or prop_name == 'id':
                if prop_name == primary_key_field:
                    # This is the primary key
                    uuid_pk_type = get_sql_type({'type': 'uuid_pk'}, self.dialect)
                    columns.append(f'{prop_name} {uuid_pk_type} -- Primary key identifier{comment}')
                else:
                    # This is a foreign key
                    sql_type = 'UUID'
                    if prop_name in required_fields:
                        sql_type += ' NOT NULL'
                    columns.append(f'{prop_name} {sql_type}{comment}')
                continue
            
            # Handle timestamp fields
            if prop_name in ['createdAt', 'updatedAt'] or prop_name.endswith('Date'):
                sql_type = self._get_timestamp_type()
            else:
                sql_type = get_sql_type(prop_def, self.dialect)
            
            # Handle constraints (NOT NULL)
            if prop_name in required_fields:
                sql_type += ' NOT NULL'
            
            # Handle unique constraints
            if prop_name in ['username', 'email', 'identification']:
                sql_type += ' UNIQUE'
            
            columns.append(f'{prop_name} {sql_type}{comment}')
        
        # Add audit fields if not present
        if 'created_at' not in properties and 'createdAt' not in properties:
            default_timestamp = self._get_default_timestamp()
            columns.append(f'created_at {self._get_timestamp_type()} NOT NULL DEFAULT {default_timestamp} -- Record creation timestamp')
        
        if 'updated_at' not in properties and 'updatedAt' not in properties:
            columns.append(f'updated_at {self._get_timestamp_type()} -- Record last update timestamp')
        
        # Create the CREATE TABLE statement with IF NOT EXISTS logic
        result += self._get_create_table_if_not_exists(table_name, columns)
        
        return result
    
    def _get_timestamp_type(self) -> str:
        """Get timestamp type for the dialect."""
        timestamp_types = {
            'postgresql': 'TIMESTAMPTZ',
            'mysql': 'DATETIME(6)',
            'sqlserver': 'DATETIME2',
            'oracle': 'TIMESTAMP'
        }
        return timestamp_types[self.dialect]
    
    def _get_default_timestamp(self) -> str:
        """Get default timestamp value for the dialect."""
        defaults = {
            'postgresql': 'CURRENT_TIMESTAMP',
            'mysql': 'CURRENT_TIMESTAMP',
            'sqlserver': 'GETDATE()',
            'oracle': 'CURRENT_TIMESTAMP'
        }
        return defaults[self.dialect]
    
    def generate_indexes(self, table_name: str, schema_def: Dict[str, Any]) -> List[str]:
        """Generate index statements for common search fields."""
        indexes = []
        properties = schema_def.get('properties', {})
        
        # Create indexes for search fields
        search_fields = []
        for prop_name in properties.keys():
            # Skip ID fields as they become foreign keys
            if prop_name.endswith('Id'):
                continue
            if any(keyword in prop_name.lower() for keyword in ['name', 'title', 'email', 'username', 'status']):
                search_fields.append(prop_name)
        
        for field in search_fields:
            index_name = f"idx_{table_name}_{field}"
            field_description = self._clean_sql_comment(properties.get(field, {}).get('description', f'Index for {field} field'))
            indexes.append(f'CREATE INDEX IF NOT EXISTS {index_name} ON {table_name} ({field}); -- {field_description}')
        
        return indexes
    
    def _generate_enum_table(self, table_name: str, schema_def: Dict[str, Any]) -> str:
        """Generate enum table with INSERT statements."""
        enum_values = schema_def.get('enum_values', [])
        original_name = schema_def.get('original_name', table_name)
        enum_description = self._clean_sql_comment(schema_def.get('description', f'Enumeration table for {original_name}'))
        
        # Create table structure with documentation
        uuid_pk_type = get_sql_type({'type': 'uuid_pk'}, self.dialect)
        default_timestamp = self._get_default_timestamp()
        
        result = f"-- {enum_description}\n"
        
        # Define columns for enum table
        columns = [
            f'id {uuid_pk_type} -- Unique identifier',
            'code VARCHAR(50) NOT NULL UNIQUE -- Enum code value',
            'name VARCHAR(100) NOT NULL -- Human readable name',
            'description VARCHAR(255) -- Detailed description',
            'active BOOLEAN NOT NULL DEFAULT TRUE -- Whether this enum value is active',
            f'created_at {self._get_timestamp_type()} NOT NULL DEFAULT {default_timestamp} -- Record creation timestamp',
            f'updated_at {self._get_timestamp_type()} -- Record last update timestamp'
        ]
        
        result += self._get_create_table_if_not_exists(table_name, columns)
        
        # Generate INSERT statements
        inserts = []
        for value in enum_values:
            # Convert enum value to readable name
            name = value.replace('_', ' ').title()
            description = self._clean_sql_comment(f"{original_name} - {name}")
            
            insert_stmt = f"INSERT INTO {table_name} (code, name, description) VALUES ('{value}', '{name}', '{description}') ON CONFLICT (code) DO NOTHING;"
            inserts.append(insert_stmt)
        
        # Combine CREATE TABLE and INSERTs
        if inserts:
            result += "\n\n-- Enum values\n" + "\n".join(inserts)
        
        return result
    
    def generate_enum_inserts(self, table_name: str, schema_def: Dict[str, Any]) -> List[str]:
        """Generate INSERT statements for enum values."""
        if schema_def.get('type') != 'enum_table':
            return []
        
        enum_values = schema_def.get('enum_values', [])
        original_name = schema_def.get('original_name', table_name)
        inserts = []
        
        for value in enum_values:
            name = value.replace('_', ' ').title()
            description = self._clean_sql_comment(f"{original_name} - {name}")
            insert_stmt = f"INSERT INTO {table_name} (code, name, description) VALUES ('{value}', '{name}', '{description}') ON CONFLICT (code) DO NOTHING;"
            inserts.append(insert_stmt)
        
        return inserts
    
    def _get_create_table_if_not_exists(self, table_name: str, columns: List[str]) -> str:
        """Generate CREATE TABLE IF NOT EXISTS for each dialect."""
        # Clean column definitions and add commas
        clean_columns = []
        for i, col in enumerate(columns):
            # Remove quotes from column names
            col_clean = col.replace('"', '')
            # Add comma to all columns except the last one
            if i < len(columns) - 1:
                # Find where the comment starts
                if ' -- ' in col_clean:
                    parts = col_clean.split(' -- ', 1)
                    col_clean = parts[0].rstrip() + ', -- ' + parts[1]
                else:
                    col_clean = col_clean.rstrip() + ','
            clean_columns.append(col_clean)
        
        columns_str = '\n    '.join(clean_columns)
        
        if self.dialect == 'postgresql':
            return f'''CREATE TABLE IF NOT EXISTS {table_name} (
    {columns_str}
);'''
        
        elif self.dialect == 'mysql':
            columns_str = '\n    '.join(clean_columns)
            return f'''CREATE TABLE IF NOT EXISTS `{table_name}` (
    {columns_str}
);'''
        
        elif self.dialect == 'sqlserver':
            columns_str = '\n        '.join(clean_columns)
            return f'''IF NOT EXISTS (SELECT * FROM sysobjects WHERE name = '{table_name}' AND xtype = 'U')
BEGIN
    CREATE TABLE dbo.[{table_name}] (
        {columns_str}
    );
END
GO'''
        
        elif self.dialect == 'oracle':
            columns_str = '\n                '.join(clean_columns)
            return f'''DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count FROM all_tables WHERE table_name = '{table_name.upper()}';
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE '
            CREATE TABLE {table_name} (
                {columns_str}
            )
        ';
    END IF;
END;
/'''
        
        else:
            columns_str = '\n    '.join(clean_columns)
            return f'CREATE TABLE {table_name} (\n    {columns_str}\n);'
    
    def _clean_sql_comment(self, text: str) -> str:
        """Clean text for use in SQL comments to prevent syntax errors."""
        if not text:
            return text
        
        # Replace HTML entities
        text = text.replace('&#39;', "")
        text = text.replace('&quot;', "")
        text = text.replace('&amp;', "and")
        text = text.replace('&lt;', "<")
        text = text.replace('&gt;', ">")
        
        # Remove problematic characters for SQL comments
        text = text.replace("'", "")
        text = text.replace('"', "")
        text = text.replace('`', "")
        text = text.replace('\n', " ")
        text = text.replace('\r', " ")
        text = text.replace('\t', " ")
        
        # Remove trailing punctuation that might cause issues
        text = text.rstrip('.,;:')
        
        # Clean up multiple spaces
        import re
        text = re.sub(r'\s+', ' ', text).strip()
        
        return text