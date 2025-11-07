"""
Main SQL generator from OpenAPI specifications.
"""
import sys
import os
from pathlib import Path

# Add current directory to Python path
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from core.openapi_processor import OpenApiProcessor
from generators.sql_generator import SqlGenerator
from utils.file_writer import FileWriter

class OpenApiSqlGenerator:
    """Main generator class that orchestrates the SQL generation process."""
    
    def __init__(self, build_dir: str = "build/smithy", output_dir: str = "sql", dialect: str = "postgresql"):
        self.processor = OpenApiProcessor(build_dir)
        self.file_writer = FileWriter(output_dir, dialect)
        self.dialect = dialect
        self.supported_dialects = ['postgresql', 'mysql', 'sqlserver', 'oracle']
        
        if dialect not in self.supported_dialects:
            raise ValueError(f"Unsupported dialect: {dialect}. Supported: {self.supported_dialects}")
    
    def generate_all(self):
        """Generate SQL files for all services and dialects."""
        openapi_files = self.processor.find_openapi_files()
        
        if not openapi_files:
            print("No OpenAPI files found in build/smithy directory")
            return
        
        print(f"Found {len(openapi_files)} OpenAPI files")
        
        for file_info in openapi_files:
            service_name = file_info['service_name']
            file_path = file_info['file_path']
            
            print(f"Processing {service_name}...")
            
            try:
                # Load OpenAPI spec
                openapi_spec = self.processor.load_openapi_spec(file_path)
                
                # Extract response schemas
                schemas = self.processor.extract_response_schemas(openapi_spec)
                
                if not schemas:
                    print(f"  No Response/Status schemas found in {service_name}")
                    continue
                
                print(f"  Found {len(schemas)} schemas: {list(schemas.keys())}")
                
                # Generate SQL for specified dialect
                sql_statements = []
                generator = SqlGenerator(self.dialect)
                
                for table_name, schema_def in schemas.items():
                    # Generate CREATE TABLE
                    create_table = generator.generate_create_table(table_name, schema_def)
                    if create_table:
                        sql_statements.append(create_table)
                    
                    # Generate indexes
                    indexes = generator.generate_indexes(table_name, schema_def)
                    sql_statements.extend(indexes)
                
                if sql_statements:
                    output_file = self.file_writer.write_sql_file(service_name, sql_statements)
                    print(f"  Generated: {output_file}")
                else:
                    print(f"  No valid schemas found for {service_name}")
                
            except Exception as e:
                print(f"  Error processing {service_name}: {e}")
        
        print("SQL generation completed!")

def main():
    """Main entry point."""
    if len(sys.argv) < 2:
        print("Usage: python main.py <dialect> [build_dir] [output_dir]")
        print("Supported dialects: postgresql, mysql, sqlserver, oracle")
        return
    
    dialect = sys.argv[1]
    current_dir = Path.cwd()
    build_dir = current_dir / "build" / "smithy" if len(sys.argv) <= 2 else sys.argv[2]
    output_dir = current_dir / "sql" if len(sys.argv) <= 3 else sys.argv[3]
    
    generator = OpenApiSqlGenerator(str(build_dir), str(output_dir), dialect)
    generator.generate_all()

if __name__ == "__main__":
    main()