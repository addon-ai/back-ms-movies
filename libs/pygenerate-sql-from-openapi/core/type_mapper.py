"""
Type mapping from OpenAPI to SQL dialects.
"""

TYPE_MAPPING = {
    'postgresql': {
        'integer': 'INTEGER',
        'integer_int64': 'BIGINT',
        'string': 'VARCHAR(255)',
        'string_date-time': 'TIMESTAMPTZ',
        'string_date': 'DATE',
        'string_uuid': 'VARCHAR(36)',
        'boolean': 'BOOLEAN',
        'number': 'DECIMAL(10,2)',
        'number_float': 'REAL',
        'number_double': 'DOUBLE PRECISION',
        'uuid_pk': 'UUID DEFAULT gen_random_uuid() PRIMARY KEY'
    },
    'mysql': {
        'integer': 'INT',
        'integer_int64': 'BIGINT',
        'string': 'VARCHAR(255)',
        'string_date-time': 'DATETIME',
        'string_date': 'DATE',
        'string_uuid': 'VARCHAR(36)',
        'boolean': 'BOOLEAN',
        'number': 'DECIMAL(10,2)',
        'number_float': 'FLOAT',
        'number_double': 'DOUBLE',
        'uuid_pk': 'CHAR(36) DEFAULT (UUID()) PRIMARY KEY'
    },
    'sqlserver': {
        'integer': 'INT',
        'integer_int64': 'BIGINT',
        'string': 'NVARCHAR(255)',
        'string_date-time': 'DATETIME2',
        'string_date': 'DATE',
        'string_uuid': 'UNIQUEIDENTIFIER',
        'boolean': 'BIT',
        'number': 'DECIMAL(10,2)',
        'number_float': 'REAL',
        'number_double': 'FLOAT',
        'uuid_pk': 'UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY'
    },
    'oracle': {
        'integer': 'NUMBER(10)',
        'integer_int64': 'NUMBER(19)',
        'string': 'VARCHAR2(255)',
        'string_date-time': 'TIMESTAMP',
        'string_date': 'DATE',
        'string_uuid': 'VARCHAR2(36)',
        'boolean': 'NUMBER(1)',
        'number': 'NUMBER(10,2)',
        'number_float': 'BINARY_FLOAT',
        'number_double': 'BINARY_DOUBLE',
        'uuid_pk': 'RAW(16) DEFAULT SYS_GUID() PRIMARY KEY'
    }
}

def get_sql_type(prop, dialect):
    """Get SQL type mapped from OpenAPI property."""
    dialect_map = TYPE_MAPPING[dialect]
    
    # Handle UUID primary key
    if prop.get('type') == 'uuid_pk':
        return dialect_map['uuid_pk']
    
    # Type and format mapping
    json_type = prop.get('type')
    json_format = prop.get('format', '')
    
    # Mapping key (e.g. 'string_date-time' or 'integer')
    type_key = f"{json_type}_{json_format}" if json_format else json_type
    
    if type_key in dialect_map:
        return dialect_map[type_key]
    elif json_type in dialect_map:
        return dialect_map[json_type]
    else:
        return 'VARCHAR(255)'  # Default type