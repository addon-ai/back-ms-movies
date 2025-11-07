"""
File writing utilities for SQL output.
"""
from pathlib import Path
from typing import List

class FileWriter:
    """Handles writing SQL files to disk."""
    
    def __init__(self, output_dir: str = "sql", dialect: str = "postgresql"):
        self.output_dir = Path(output_dir) / dialect
        self.output_dir.mkdir(parents=True, exist_ok=True)
        self.dialect = dialect
    
    def write_sql_file(self, service_name: str, sql_statements: List[str]):
        """Write SQL statements to file."""
        filename = f"{service_name}.sql"
        file_path = self.output_dir / filename
        
        # Add header comment
        header = f"""-- SQL DDL for {service_name}
-- Database: {self.dialect.upper()}
-- Generated automatically from OpenAPI specification
-- Do not edit manually

"""
        
        content = header + '\n\n'.join(sql_statements)
        
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        
        return file_path