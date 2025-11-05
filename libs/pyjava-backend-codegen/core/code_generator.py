"""
Main code generator orchestrating the entire generation process.
"""
from pathlib import Path
from typing import Dict, List, Any

from utils.template_renderer import TemplateRenderer
from utils.file_manager import FileManager
from utils.property_converter import PropertyConverter
from config_loader import ConfigLoader
from openapi_processor import OpenApiProcessor

from generators.dto_generator import DtoGenerator
from generators.domain_generator import DomainGenerator
from generators.application_generator import ApplicationGenerator
from generators.infrastructure_generator import InfrastructureGenerator
from generators.test_generator import TestGenerator
from generators.project_generator import ProjectGenerator


class CodeGenerator:
    """Main code generator for Hexagonal Architecture Spring Boot projects."""
    
    def __init__(self, config_path: str, templates_dir: str, project_config: Dict[str, Any]):
        self.config_path = config_path
        self.templates_dir = Path(templates_dir)
        self.project_config = project_config
        self.output_dir = Path("projects") / project_config['project']['general']['name']
        self.base_package = project_config['project']['params']['configOptions']['basePackage']
        
        # Initialize components
        self.config_loader = ConfigLoader()
        self.openapi_processor = OpenApiProcessor(project_config['project']['general']['folder'])
        self.template_renderer = TemplateRenderer(templates_dir)
        self.file_manager = FileManager(self.output_dir)
        self.property_converter = PropertyConverter()
        
        # Build context
        self.openapi_specs = self.openapi_processor.load_openapi_specs()
        self.target_packages = self.config_loader.build_package_structure(self.base_package)
        self.mustache_context = self.config_loader.build_mustache_context(project_config, self.target_packages)
        
        # Initialize generators
        self.dto_generator = DtoGenerator(
            self.template_renderer, self.file_manager, self.property_converter, 
            self.target_packages, self.output_dir
        )
        self.domain_generator = DomainGenerator(
            self.template_renderer, self.file_manager, self.property_converter,
            self.target_packages, self.output_dir
        )
        self.application_generator = ApplicationGenerator(
            self.template_renderer, self.file_manager, self.target_packages, self.output_dir
        )
        self.infrastructure_generator = InfrastructureGenerator(
            self.template_renderer, self.file_manager, self.property_converter,
            self.target_packages, self.output_dir
        )
        self.test_generator = TestGenerator(
            self.template_renderer, self.file_manager, self.target_packages, 
            self.output_dir, self.openapi_specs
        )
        self.project_generator = ProjectGenerator(
            self.template_renderer, self.file_manager, self.target_packages,
            self.output_dir, self.project_config
        )
    
    def generate_complete_project(self):
        """Generate complete Hexagonal Architecture project from OpenAPI specs."""
        print("Generating Hexagonal Architecture Spring Boot project from OpenAPI specs...")
        
        all_schemas, all_operations, all_entities = self._extract_openapi_data()
        
        # Generate DTOs from all schemas (excluding Error DTOs)
        self._generate_dtos(all_schemas)
        
        # Generate composite Request DTOs
        self.dto_generator.generate_composite_request_dtos(self.openapi_specs, self.mustache_context)
        
        # Generate domain layer
        self._generate_domain_layer(all_entities, all_schemas)
        
        # Generate application layer
        entity_operations = self._group_operations_by_entity(all_operations, all_entities)
        self._generate_application_layer(entity_operations, all_entities)
        
        # Generate infrastructure layer
        self._generate_infrastructure_layer(all_entities, all_schemas, entity_operations)
        
        # Create test directory structure and generate tests
        self.file_manager.create_test_directories()
        self.test_generator.generate_tests_for_existing_components(self.mustache_context)
        
        # Generate supporting files
        self._generate_supporting_files()
        
        # Generate test utilities
        self.test_generator.generate_logging_utils_test(self.mustache_context)
        self.test_generator.generate_logback_test_config(self.mustache_context)
        
        self._print_generation_summary(all_schemas, all_operations, all_entities)
    
    def _extract_openapi_data(self):
        """Extract schemas, operations, and entities from OpenAPI specs."""
        all_schemas = {}
        all_operations = []
        all_entities = set()
        
        # Process each OpenAPI spec
        for spec_info in self.openapi_specs:
            openapi_spec = spec_info['spec']
            service_name = spec_info['service_name']
            
            print(f"Processing {service_name} service...")
            
            # Extract data from OpenAPI spec
            schemas = openapi_spec.get('components', {}).get('schemas', {})
            paths = openapi_spec.get('paths', {})
            
            # Store schemas with service context
            for schema_name, schema_data in schemas.items():
                all_schemas[f"{service_name}_{schema_name}"] = {
                    'data': schema_data,
                    'service': service_name,
                    'original_name': schema_name
                }
            
            # Extract operations
            for path, methods in paths.items():
                for method, operation_data in methods.items():
                    if 'operationId' in operation_data:
                        all_operations.append({
                            'id': operation_data['operationId'],
                            'service': service_name
                        })
            
            # Extract entities from schemas (look for Response schemas)
            for schema_name in schemas.keys():
                if schema_name.endswith('Response') or schema_name.endswith('ResponseContent'):
                    entity_name = schema_name.replace('Response', '').replace('ResponseContent', '')
                    if entity_name.startswith('Get'):
                        entity_name = entity_name[3:]  # Remove 'Get' prefix
                    
                    if entity_name:
                        all_entities.add(entity_name)
        
        return all_schemas, all_operations, all_entities
    
    def _generate_dtos(self, all_schemas: Dict[str, Any]):
        """Generate DTOs from all schemas."""
        for schema_key, schema_info in all_schemas.items():
            if schema_info['data'].get('type') == 'object' and 'Error' not in schema_info['original_name']:
                self.dto_generator.generate_dto(
                    schema_info['original_name'], 
                    schema_info['data'], 
                    schema_info['service'],
                    self.mustache_context
                )
    
    def _generate_domain_layer(self, all_entities: set, all_schemas: Dict[str, Any]):
        """Generate domain layer components."""
        # Generate EntityStatus enum
        self.domain_generator.generate_entity_status_enum(self.mustache_context)
        
        # Generate domain models and ports
        for entity in all_entities:
            entity_schema = self._find_entity_schema(entity, all_schemas)
            if entity_schema:
                self.domain_generator.generate_domain_model(entity, entity_schema, self.mustache_context)
                self.domain_generator.generate_domain_port_output(entity, self.mustache_context)
    
    def _generate_application_layer(self, entity_operations: Dict[str, List[Dict[str, Any]]], all_entities: set):
        """Generate application layer components."""
        # Generate mappers for all domain entities
        domain_entities = self._filter_domain_entities(all_entities)
        
        for entity in domain_entities:
            entity_service = self._find_entity_service(entity)
            self.application_generator.generate_mapper(entity, entity_service, self.openapi_specs, self.mustache_context)
        
        # Generate consolidated services and use cases
        for entity_name, operations in entity_operations.items():
            complex_operations = self._find_complex_operations_for_entity(entity_name)
            self.application_generator.generate_consolidated_use_cases(entity_name, operations, complex_operations, self.mustache_context)
            self.application_generator.generate_consolidated_service(entity_name, operations, complex_operations, self.mustache_context)
    
    def _generate_infrastructure_layer(self, all_entities: set, all_schemas: Dict[str, Any], entity_operations: Dict[str, List[Dict[str, Any]]]):
        """Generate infrastructure layer components."""
        # Generate exception classes
        self.infrastructure_generator.generate_conflict_exception(self.mustache_context)
        self.infrastructure_generator.generate_internal_server_error_exception(self.mustache_context)
        
        # Generate persistence layer
        for entity in all_entities:
            entity_schema = self._find_entity_schema(entity, all_schemas)
            if entity_schema:
                self.infrastructure_generator.generate_entity(entity, entity_schema, self.mustache_context)
                self.infrastructure_generator.generate_jpa_repository(entity, entity_schema, self.mustache_context)
                self.infrastructure_generator.generate_repository_adapter(entity, self.mustache_context)
        
        # Generate REST controllers
        for entity_name in entity_operations.keys():
            if self._is_domain_entity(entity_name):
                crud_operations = [op['id'] for op in entity_operations[entity_name]]
                complex_operations = self._find_complex_operations_for_entity(entity_name)
                all_available_operations = crud_operations + complex_operations
                service_name = entity_operations[entity_name][0]['service'] if entity_operations[entity_name] else entity_name.lower()
                self.infrastructure_generator.generate_rest_controller(entity_name, all_available_operations, service_name, self.mustache_context)
    
    def _generate_supporting_files(self):
        """Generate supporting files like main class, configuration, etc."""
        self.project_generator.generate_main_application(self.mustache_context)
        self.project_generator.generate_configuration(self.mustache_context)
        self.project_generator.generate_pom_xml(self.mustache_context)
        self.project_generator.generate_application_properties(self.mustache_context)
        self.project_generator.generate_readme(self.mustache_context)
        self.project_generator.generate_docker_compose(self.mustache_context)
        self.project_generator.generate_dockerfile(self.mustache_context)
        self.project_generator.generate_maven_wrapper(self.mustache_context)
        self.project_generator.generate_ci_cd_workflow(self.mustache_context)
    
    def _group_operations_by_entity(self, all_operations: List[Dict[str, Any]], all_entities: set) -> Dict[str, List[Dict[str, Any]]]:
        """Group operations by entity for consolidated services."""
        entity_operations = {}
        for operation_info in all_operations:
            entity_name = None
            op_id = operation_info['id']
            
            # Only process basic CRUD operations that match domain entities
            for entity in all_entities:
                if (op_id == f'Create{entity}' or op_id == f'Get{entity}' or 
                    op_id == f'Update{entity}' or op_id == f'Delete{entity}' or 
                    op_id == f'List{entity}s'):
                    entity_name = entity
                    break
            
            if entity_name:
                if entity_name not in entity_operations:
                    entity_operations[entity_name] = []
                entity_operations[entity_name].append(operation_info)
        
        return entity_operations
    
    def _find_entity_schema(self, entity: str, all_schemas: Dict[str, Any]) -> Dict[str, Any]:
        """Find the main response schema for an entity."""
        for schema_key, schema_info in all_schemas.items():
            original_name = schema_info['original_name']
            if original_name == f'{entity}Response' or original_name == f'Get{entity}ResponseContent':
                return schema_info['data']
        return None
    
    def _filter_domain_entities(self, entities) -> List[str]:
        """Filter entities to only include domain entities."""
        return [entity for entity in entities 
                if ('Error' not in entity and 'Content' not in entity and 
                    not entity.startswith(('Create', 'Get', 'Update', 'Delete', 'List')))]
    
    def _find_entity_service(self, entity: str) -> str:
        """Find service name for an entity."""
        for spec_info in self.openapi_specs:
            if any(entity in schema_name for schema_name in spec_info['spec'].get('components', {}).get('schemas', {})):
                return spec_info['service_name']
        return entity.lower()
    
    def _is_domain_entity(self, entity_name: str) -> bool:
        """Check if entity is a domain entity."""
        return ('Error' not in entity_name and 'Content' not in entity_name and 
                not entity_name.startswith(('Create', 'Get', 'Update', 'Delete', 'List')))
    
    def _find_complex_operations_for_entity(self, entity_name: str) -> List[str]:
        """Find complex operations for this entity."""
        complex_operations = []
        for op in self.openapi_specs:
            paths = op['spec'].get('paths', {})
            for path, methods in paths.items():
                for method, operation_data in methods.items():
                    if 'operationId' in operation_data:
                        op_id = operation_data['operationId']
                        if (op_id.startswith('Get') and 'By' in op_id and 
                            (entity_name.lower() in op_id.lower() or 
                             any(related in op_id for related in ['Cities', 'Countries', 'Regions', 'Neighborhoods']) and entity_name == 'Location')):
                            complex_operations.append(op_id)
        return complex_operations
    
    def _print_generation_summary(self, all_schemas: Dict[str, Any], all_operations: List[Dict[str, Any]], all_entities: set):
        """Print generation summary."""
        project_info = self.project_config.get('project', {}).get('general', {})
        print(f"\nHexagonal Architecture project generated successfully in: {self.output_dir}")
        print(f"Project: {project_info.get('name', 'generated-project')} v{project_info.get('version', '1.0.0')}")
        print(f"Description: {project_info.get('description', 'Generated project')}")
        print(f"Generated {len(all_schemas)} DTOs from {len(self.openapi_specs)} OpenAPI specs")
        print(f"Generated {len(all_operations)} use cases from operations")
        print(f"Generated {len(all_entities)} entities: {', '.join(sorted(all_entities))}")
        print("\nProject structure follows Hexagonal Architecture principles:")
        print("- Domain: Pure business logic and ports")
        print("- Application: Use case implementations and DTOs")
        print("- Infrastructure: External adapters (REST, JPA, Config)")