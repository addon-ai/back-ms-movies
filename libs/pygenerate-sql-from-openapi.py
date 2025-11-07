#!/usr/bin/env python3
"""
SQL Generator from OpenAPI Specifications
Generates SQL DDL scripts from OpenAPI JSON schemas for multiple database dialects.
"""

import sys
import os
import json
from pathlib import Path

# Add the pygenerate-sql-from-openapi library to the path
sys.path.insert(0, os.path.join(os.path.dirname(__file__), 'pygenerate-sql-from-openapi'))

from main import OpenApiSqlGenerator

def load_config(config_path: str):
    """Load configuration from params.json"""
    with open(config_path, 'r') as f:
        return json.load(f)

def get_database_dialect(projects_config):
    """Extract database dialect from first project configuration"""
    if not projects_config:
        return 'postgresql'  # Default
    
    first_project = projects_config[0]
    database_config = first_project.get('database', {})
    return database_config.get('sgbd', 'postgresql')

def main():
    """Main entry point for SQL generation"""
    # Get project root directory
    script_dir = Path(__file__).parent
    project_root = script_dir.parent
    
    # Load configuration
    config_path = project_root / "libs" / "config" / "params.json"
    
    if not config_path.exists():
        print(f"❌ Configuration file not found: {config_path}")
        return
    
    try:
        projects_config = load_config(config_path)
        dialect = get_database_dialect(projects_config)
        
        print(f"🗄️  Generating SQL DDL scripts for {dialect.upper()}...")
        
        # Set up paths
        build_dir = project_root / "build" / "smithy"
        output_dir = project_root / "sql"
        
        # Generate SQL files
        generator = OpenApiSqlGenerator(str(build_dir), str(output_dir), dialect)
        generator.generate_all()
        
        print(f"✅ SQL generation complete for {dialect.upper()}")
        
    except Exception as e:
        print(f"❌ Error generating SQL: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()