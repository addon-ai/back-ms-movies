"""
Domain layer generation functionality.
"""
from pathlib import Path
from typing import Dict, List, Any


class DomainGenerator:
    """Handles generation of domain layer components."""
    
    def __init__(self, template_renderer, file_manager, property_converter, target_packages, output_dir):
        self.template_renderer = template_renderer
        self.file_manager = file_manager
        self.property_converter = property_converter
        self.target_packages = target_packages
        self.output_dir = output_dir
    
    def generate_entity_status_enum(self, mustache_context: Dict[str, Any]):
        """Generate EntityStatus enum for domain layer."""
        context = mustache_context.copy()
        context.update({
            'packageName': self.target_packages['domain_model'],
            'classname': 'EntityStatus'
        })
        
        content = self.template_renderer.render_template('EntityStatus.mustache', context)
        file_path = self.output_dir / self.file_manager.get_package_path(self.target_packages['domain_model']) / "EntityStatus.java"
        self.file_manager.write_file(file_path, content)
    
    def generate_domain_model(self, entity: str, schema: Dict[str, Any], mustache_context: Dict[str, Any]):
        """Generate domain model (pure POJO) from OpenAPI schema."""
        context = mustache_context.copy()
        
        properties = schema.get('properties', {})
        required_fields = schema.get('required', [])
        
        vars_list = []
        imports = set()
        
        for prop_name, prop_data in properties.items():
            var_info = self.property_converter.convert_openapi_property(prop_name, prop_data, required_fields)
            var_info['hasValidation'] = False
            var_info['validationAnnotations'] = []
            vars_list.append(var_info)
            if var_info.get('import'):
                imports.add(var_info['import'])
        
        context.update({
            'packageName': self.target_packages['domain_model'],
            'classname': entity,
            'vars': vars_list,
            'imports': [{'import': imp} for imp in sorted(imports)],
            'models': [{'model': {'classname': entity, 'vars': vars_list}}],
            'isDomainModel': True,
            'useJPA': False,
            'useLombok': True
        })
        
        content = self.template_renderer.render_template('pojo.mustache', context)
        file_path = self.output_dir / self.file_manager.get_package_path(self.target_packages['domain_model']) / f"{entity}.java"
        self.file_manager.write_file(file_path, content)
    
    def generate_domain_port_output(self, entity: str, complex_operations: List[str], mustache_context: Dict[str, Any]):
        """Generate domain repository port (interface)."""
        # Extract repository methods from complex operations (only for relevant entity)
        repository_methods = []
        for op_id in complex_operations:
            if self._is_operation_for_entity(op_id, entity):
                method_name = self._convert_operation_to_repository_method(op_id)
                if method_name:
                    parameters = self._extract_parameters_from_operation(op_id)
                    parameter_name = self._extract_parameter_name_from_operation(op_id)
                    repository_methods.append({
                        'methodName': method_name,
                        'parameters': parameters,
                        'parameterName': parameter_name
                    })
        
        context = mustache_context.copy()
        context.update({
            'packageName': self.target_packages['domain_ports_output'],
            'classname': f"{entity}RepositoryPort",
            'entityName': entity,
            'entityVarName': entity.lower(),
            'interfaceOnly': True,
            'isDomainPort': True,
            'hasComplexMethods': len(repository_methods) > 0,
            'repositoryMethods': repository_methods
        })
        
        content = self.template_renderer.render_template('interface.mustache', context)
        file_path = self.output_dir / self.file_manager.get_package_path(self.target_packages['domain_ports_output']) / f"{entity}RepositoryPort.java"
        self.file_manager.write_file(file_path, content)
    
    def _convert_operation_to_repository_method(self, operation_id: str) -> str:
        """Convert operation ID to repository method name."""
        if operation_id.startswith('Get'):
            # GetNeighborhoodsByCity -> findNeighborhoodsByCity
            return 'find' + operation_id[3:]
        return None
    
    def _extract_parameters_from_operation(self, operation_id: str) -> str:
        """Extract parameters from operation ID."""
        if 'ByCity' in operation_id:
            return 'String cityId'
        elif 'ByCountry' in operation_id:
            return 'String countryId'
        elif 'ByRegion' in operation_id:
            return 'String regionId'
        return 'String id'
    
    def _is_operation_for_entity(self, operation_id: str, entity: str) -> bool:
        """Check if operation belongs to specific entity."""
        # Only Location entity should have these complex operations
        if entity == 'Location':
            return operation_id in ['GetNeighborhoodsByCity', 'GetRegionsByCountry', 'GetCitiesByRegion']
        return False
    
    def _extract_parameter_name_from_operation(self, operation_id: str) -> str:
        """Extract parameter name from operation ID."""
        if 'ByCity' in operation_id:
            return 'cityId'
        elif 'ByCountry' in operation_id:
            return 'countryId'
        elif 'ByRegion' in operation_id:
            return 'regionId'
        return 'id'