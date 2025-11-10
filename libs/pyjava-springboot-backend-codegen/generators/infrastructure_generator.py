"""
Infrastructure layer generation functionality.
"""
from pathlib import Path
from typing import Dict, List, Any


class InfrastructureGenerator:
    """Handles generation of infrastructure layer components."""
    
    def __init__(self, template_renderer, file_manager, property_converter, target_packages, output_dir):
        self.template_renderer = template_renderer
        self.file_manager = file_manager
        self.property_converter = property_converter
        self.target_packages = target_packages
        self.output_dir = output_dir
    
    def generate_entity(self, entity: str, schema: Dict[str, Any], mustache_context: Dict[str, Any]):
        """Generate JPA entity (DBO) from OpenAPI schema."""
        context = mustache_context.copy()
        
        if schema:
            properties = schema.get('properties', {})
            required_fields = schema.get('required', [])
            
            vars_list = []
            imports = set()
            
            for prop_name, prop_data in properties.items():
                # Include relation fields (regionId, countryId, cityId) but exclude primary key fields
                is_primary_key = prop_name == f'{entity.lower()}Id'
                is_relation_field = prop_name.endswith('Id') and not is_primary_key
                is_regular_field = prop_name not in ['createdAt', 'updatedAt', 'status'] and not prop_name.endswith('Id')
                
                if is_regular_field or is_relation_field:
                    var_info = self.property_converter.convert_openapi_property(prop_name, prop_data, required_fields)
                    var_info['isIdField'] = is_primary_key
                    var_info['isTimestampField'] = prop_name in ['createdAt', 'updatedAt']
                    var_info['isStatusField'] = prop_name == 'status'
                    var_info['isRelationField'] = is_relation_field
                    vars_list.append(var_info)
                    if var_info.get('import'):
                        imports.add(var_info['import'])
            
            context.update({
                'vars': vars_list,
                'imports': [{'import': imp} for imp in sorted(imports)],
            })
        
        # Generate correct table name with proper pluralization
        table_name = self._pluralize_entity_name(entity.lower())
        
        context.update({
            'packageName': self.target_packages['infra_entity'],
            'classname': f"{entity}Dbo",
            'entityName': entity,
            'entityNameSnake': self._camel_to_snake(entity),
            'entityVarName': entity.lower(),
            'tableName': table_name,
            'isEntity': True,
            'useJPA': True,
            'useLombok': True
        })
        
        content = self.template_renderer.render_template('apiEntity.mustache', context)
        file_path = self.output_dir / self.file_manager.get_package_path(self.target_packages['infra_entity']) / f"{entity}Dbo.java"
        self.file_manager.write_file(file_path, content)
    
    def generate_jpa_repository(self, entity: str, schema: Dict[str, Any], mustache_context: Dict[str, Any]):
        """Generate Spring Data JPA repository."""
        search_fields = []
        if schema:
            properties = schema.get('properties', {})
            # Search in text fields that contain name, title, description, or status
            for prop_name, prop_data in properties.items():
                prop_type = prop_data.get('type', '')
                if (prop_type == 'string' and 
                    (any(keyword in prop_name.lower() for keyword in ['name', 'title', 'description', 'status']) or
                     prop_name in ['username', 'email'])):
                    search_fields.append(prop_name)
        
        search_conditions = []
        if search_fields:
            for field in search_fields:
                # Convert camelCase to snake_case for database column names
                db_field = self._camel_to_snake(field)
                search_conditions.append(f"LOWER(e.{db_field}) LIKE LOWER(CONCAT('%', :search, '%'))")
        else:
            search_conditions.append("LOWER(CAST(e.id AS string)) LIKE LOWER(CONCAT('%', :search, '%'))")
        
        search_query = " OR ".join(search_conditions)
        
        context = mustache_context.copy()
        context.update({
            'packageName': self.target_packages['infra_repository'],
            'classname': f"Jpa{entity}Repository",
            'entityName': entity,
            'tableName': self._pluralize_entity_name(entity.lower()),
            'searchFields': search_fields,
            'searchQuery': search_query,
            'hasSearchFields': len(search_fields) > 0,
            'isJpaRepository': True,
            'isAdapter': False
        })
        
        content = self.template_renderer.render_template('apiRepository.mustache', context)
        file_path = self.output_dir / self.file_manager.get_package_path(self.target_packages['infra_repository']) / f"Jpa{entity}Repository.java"
        self.file_manager.write_file(file_path, content)
    
    def generate_repository_adapter(self, entity: str, complex_operations: List[str], mustache_context: Dict[str, Any]):
        """Generate repository adapter."""
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
            'packageName': self.target_packages['infra_adapter'],
            'classname': f"{entity}RepositoryAdapter",
            'entityName': entity,
            'entityVarName': entity.lower(),
            'entityNamePlural': self._pluralize_entity_name(entity),
            'dboName': f"{entity}Dbo",
            'portName': f"{entity}RepositoryPort",
            'jpaRepositoryName': f"Jpa{entity}Repository",
            'isJpaRepository': False,
            'isAdapter': True,
            'hasComplexMethods': len(repository_methods) > 0,
            'repositoryMethods': repository_methods
        })
        
        content = self.template_renderer.render_template('apiRepository.mustache', context)
        file_path = self.output_dir / self.file_manager.get_package_path(self.target_packages['infra_adapter']) / f"{entity}RepositoryAdapter.java"
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
    
    def generate_rest_controller(self, entity: str, operations: List[str], service: str, mustache_context: Dict[str, Any]):
        """Generate REST controller."""
        crud_operations = [op for op in operations if any(op.startswith(prefix + entity) for prefix in ['Create', 'Get', 'Update', 'Delete']) or op == f'List{entity}s']
        complex_operations = [op for op in operations if op not in crud_operations]
        
        dto_imports = []
        if f'Create{entity}' in operations:
            dto_imports.extend([
                f'{self.target_packages["application_dto"]}.{service}.Create{entity}RequestContent',
                f'{self.target_packages["application_dto"]}.{service}.Create{entity}ResponseContent'
            ])
        if f'Get{entity}' in operations:
            dto_imports.append(f'{self.target_packages["application_dto"]}.{service}.Get{entity}ResponseContent')
        if f'Update{entity}' in operations:
            dto_imports.extend([
                f'{self.target_packages["application_dto"]}.{service}.Update{entity}RequestContent',
                f'{self.target_packages["application_dto"]}.{service}.Update{entity}ResponseContent'
            ])
        if f'Delete{entity}' in operations:
            dto_imports.append(f'{self.target_packages["application_dto"]}.{service}.Delete{entity}ResponseContent')
        if f'List{entity}s' in operations:
            dto_imports.append(f'{self.target_packages["application_dto"]}.{service}.List{entity}sResponseContent')
        
        for op in complex_operations:
            dto_imports.append(f'{self.target_packages["application_dto"]}.{service}.{op}ResponseContent')
        
        usecase_imports = [f'{self.target_packages["domain_ports_input"]}.{entity}UseCase']
        
        complex_ops_info = []
        for op in complex_operations:
            method_name = op[0].lower() + op[1:] if op else ''
            path_segment = op.lower().replace('get', '').replace('by', '-by-') if op.startswith('Get') else op.lower()
            complex_ops_info.append({
                'operationId': op,
                'methodName': method_name,
                'pathSegment': path_segment,
                'responseType': f'{op}ResponseContent'
            })
        
        context = mustache_context.copy()
        context.update({
            'packageName': self.target_packages['infra_adapters_input_rest'],
            'classname': f"{entity}Controller",
            'entityName': entity,
            'entityVarName': entity.lower(),
            'entityPath': entity.lower() + 's',
            'entityIdPath': f'{{{entity.lower()}Id}}',
            'hasCreate': f'Create{entity}' in operations,
            'hasGet': f'Get{entity}' in operations,
            'hasUpdate': f'Update{entity}' in operations,
            'hasDelete': f'Delete{entity}' in operations,
            'hasList': f'List{entity}s' in operations,
            'hasComplexOperations': len(complex_operations) > 0,
            'complexOperations': complex_ops_info,
            'serviceName': service,
            'dtoImports': dto_imports,
            'useCaseImports': usecase_imports,
            'isController': True,
            'useSpringWeb': True
        })
        
        content = self.template_renderer.render_template('apiController.mustache', context)
        file_path = self.output_dir / self.file_manager.get_package_path(self.target_packages['infra_adapters_input_rest']) / f"{entity}Controller.java"
        self.file_manager.write_file(file_path, content)
    
    def generate_conflict_exception(self, mustache_context: Dict[str, Any]):
        """Generate ConflictException class."""
        context = mustache_context.copy()
        context.update({
            'packageName': self.target_packages['infra_config_exceptions'],
            'classname': 'ConflictException'
        })
        
        content = self.template_renderer.render_template('ConflictException.mustache', context)
        file_path = self.output_dir / self.file_manager.get_package_path(self.target_packages['infra_config_exceptions']) / 'ConflictException.java'
        self.file_manager.write_file(file_path, content)
    
    def generate_internal_server_error_exception(self, mustache_context: Dict[str, Any]):
        """Generate InternalServerErrorException class."""
        context = mustache_context.copy()
        context.update({
            'packageName': self.target_packages['infra_config_exceptions'],
            'classname': 'InternalServerErrorException'
        })
        
        content = self.template_renderer.render_template('InternalServerErrorException.mustache', context)
        file_path = self.output_dir / self.file_manager.get_package_path(self.target_packages['infra_config_exceptions']) / 'InternalServerErrorException.java'
        self.file_manager.write_file(file_path, content)
    
    def _camel_to_snake(self, name: str) -> str:
        """
        Convert camelCase to snake_case.
        
        Args:
            name: camelCase string
            
        Returns:
            snake_case string
        """
        import re
        # Insert underscore before uppercase letters that follow lowercase letters
        s1 = re.sub('(.)([A-Z][a-z]+)', r'\1_\2', name)
        # Insert underscore before uppercase letters that follow lowercase letters or digits
        return re.sub('([a-z0-9])([A-Z])', r'\1_\2', s1).lower()
    
    def _pluralize_entity_name(self, entity_name: str) -> str:
        """Pluralize entity name with proper English rules."""
        # Handle capitalized entity names (e.g., "City" -> "Cities")
        if entity_name and entity_name[0].isupper():
            lower_name = entity_name.lower()
            if lower_name.endswith('y'):
                plural = lower_name[:-1] + 'ies'
            elif lower_name.endswith(('s', 'sh', 'ch', 'x', 'z')):
                plural = lower_name + 'es'
            else:
                plural = lower_name + 's'
            return plural.capitalize()
        else:
            # Handle lowercase entity names for table names
            if entity_name.endswith('y'):
                return entity_name[:-1] + 'ies'
            elif entity_name.endswith(('s', 'sh', 'ch', 'x', 'z')):
                return entity_name + 'es'
            else:
                return entity_name + 's'