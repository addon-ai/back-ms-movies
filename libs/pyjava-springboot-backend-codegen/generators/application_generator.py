"""
Application layer generation functionality.
"""
from pathlib import Path
from typing import Dict, List, Any


class ApplicationGenerator:
    """Handles generation of application layer components."""
    
    def __init__(self, template_renderer, file_manager, target_packages, output_dir):
        self.template_renderer = template_renderer
        self.file_manager = file_manager
        self.target_packages = target_packages
        self.output_dir = output_dir
    
    def generate_mapper(self, entity: str, service: str, openapi_specs: List[Dict[str, Any]], mustache_context: Dict[str, Any]):
        """Generate mapper for entity transformations."""
        dto_base_path = self.output_dir / self.file_manager.get_package_path(self.target_packages['application_dto'])
        
        if not service:
            # Find service from openapi specs
            for spec_info in openapi_specs:
                if any(entity in schema_name for schema_name in spec_info['spec'].get('components', {}).get('schemas', {})):
                    service = spec_info['service_name']
                    break
        
        if not service:
            service = entity.lower()
        
        # Detect relation mappings from OpenAPI specs - validate target fields exist
        relation_mappings = []
        domain_fields = set()
        
        # First, collect all domain model fields from schemas
        for spec_info in openapi_specs:
            schemas = spec_info['spec'].get('components', {}).get('schemas', {})
            for schema_name, schema_data in schemas.items():
                if entity in schema_name and 'Response' in schema_name:
                    properties = schema_data.get('properties', {})
                    domain_fields.update(properties.keys())
                    break
        
        # Then create mappings only for fields that exist in domain
        for spec_info in openapi_specs:
            schemas = spec_info['spec'].get('components', {}).get('schemas', {})
            for schema_name, schema_data in schemas.items():
                if entity in schema_name and 'Request' in schema_name:
                    properties = schema_data.get('properties', {})
                    for prop_name, prop_data in properties.items():
                        if prop_name.endswith('Id') and prop_name != f'{entity.lower()}Id':
                            target_field = prop_name[:-2]  # Remove 'Id' suffix
                            # Only add mapping if target field exists in domain
                            if target_field in domain_fields:
                                relation_mappings.append({
                                    'sourceField': prop_name,
                                    'targetField': target_field
                                })
                    break
        
        # Check for specific DTOs
        create_dto_name = f"Create{entity}RequestContent"
        update_dto_name = f"Update{entity}RequestContent"
        response_dto_name = f"{entity}Response"
        list_response_dto_name = f"List{entity}sResponseContent"
        delete_dto_name = f"Delete{entity}ResponseContent"
        
        has_create_dto = False
        has_update_dto = False
        has_response_dto = False
        has_list_response_dto = False
        has_get_dto = False
        has_delete_dto = False
        
        # Check if DTOs exist in service folders
        if dto_base_path.exists():
            for service_folder in dto_base_path.iterdir():
                if service_folder.is_dir():
                    if (service_folder / f"{create_dto_name}.java").exists():
                        has_create_dto = True
                        service = service_folder.name
                    if (service_folder / f"{update_dto_name}.java").exists():
                        has_update_dto = True
                        service = service_folder.name
                    if (service_folder / f"{response_dto_name}.java").exists():
                        has_response_dto = True
                        service = service_folder.name
                    if (service_folder / f"{list_response_dto_name}.java").exists():
                        has_list_response_dto = True
                        service = service_folder.name
                    if (service_folder / f"Get{entity}ResponseContent.java").exists():
                        has_get_dto = True
                        service = service_folder.name
                    if (service_folder / f"{delete_dto_name}.java").exists():
                        has_delete_dto = True
                        service = service_folder.name
        
        # Build DTO imports
        dto_imports = []
        if has_create_dto:
            dto_imports.append(f'{self.target_packages["application_dto"]}.{service}.{create_dto_name}')
            dto_imports.append(f'{self.target_packages["application_dto"]}.{service}.Create{entity}ResponseContent')
        if has_update_dto:
            dto_imports.append(f'{self.target_packages["application_dto"]}.{service}.{update_dto_name}')
            dto_imports.append(f'{self.target_packages["application_dto"]}.{service}.Update{entity}ResponseContent')
        if has_response_dto:
            dto_imports.append(f'{self.target_packages["application_dto"]}.{service}.{response_dto_name}')
        if has_list_response_dto:
            dto_imports.append(f'{self.target_packages["application_dto"]}.{service}.{list_response_dto_name}')
        if has_get_dto:
            dto_imports.append(f'{self.target_packages["application_dto"]}.{service}.Get{entity}ResponseContent')
        
        # Add delete DTO import if it exists
        if dto_base_path.exists():
            for service_folder in dto_base_path.iterdir():
                if service_folder.is_dir() and (service_folder / f"{delete_dto_name}.java").exists():
                    dto_imports.append(f'{self.target_packages["application_dto"]}.{service_folder.name}.{delete_dto_name}')
                    break
        
        context = mustache_context.copy()
        context.update({
            'packageName': self.target_packages['application_mapper'],
            'classname': f"{entity}Mapper",
            'entityName': entity,
            'entityVarName': entity.lower(),
            'dboName': f"{entity}Dbo",
            'serviceName': service,
            'hasCreateDto': has_create_dto,
            'hasUpdateDto': has_update_dto,
            'hasResponseDto': has_response_dto,
            'hasListResponseDto': has_list_response_dto,
            'hasGetDto': has_get_dto,
            'hasDeleteDto': has_delete_dto,
            'createDtoName': create_dto_name,
            'updateDtoName': update_dto_name,
            'responseDtoName': response_dto_name,
            'listResponseDtoName': list_response_dto_name,
            'dtoImports': dto_imports,
            'relationMappings': relation_mappings,
            'isMapper': True
        })
        
        content = self.template_renderer.render_template('apiMapper.mustache', context)
        file_path = self.output_dir / self.file_manager.get_package_path(self.target_packages['application_mapper']) / f"{entity}Mapper.java"
        self.file_manager.write_file(file_path, content)
    
    def generate_consolidated_use_cases(self, entity: str, operations: List[Dict[str, Any]], complex_ops: List[str], mustache_context: Dict[str, Any]):
        """Generate consolidated use case interfaces for an entity."""
        service_name = operations[0]['service'] if operations else entity.lower()
        
        dto_base_path = self.output_dir / self.file_manager.get_package_path(self.target_packages['application_dto']) / service_name
        
        has_create = self.file_manager.check_dto_exists(dto_base_path, f'Create{entity}RequestContent') and self.file_manager.check_dto_exists(dto_base_path, f'Create{entity}ResponseContent')
        has_get = self.file_manager.check_dto_exists(dto_base_path, f'Get{entity}ResponseContent')
        has_update = self.file_manager.check_dto_exists(dto_base_path, f'Update{entity}RequestContent') and self.file_manager.check_dto_exists(dto_base_path, f'Update{entity}ResponseContent')
        has_delete = self.file_manager.check_dto_exists(dto_base_path, f'Delete{entity}ResponseContent')
        has_list = self.file_manager.check_dto_exists(dto_base_path, f'List{entity}sResponseContent')
        
        complex_ops_info = []
        if complex_ops:
            for op in complex_ops:
                if self.file_manager.check_dto_exists(dto_base_path, f'{op}ResponseContent'):
                    method_name = op[0].lower() + op[1:] if op else ''
                    complex_ops_info.append({
                        'operationId': op,
                        'methodName': method_name,
                        'responseType': f'{op}ResponseContent'
                    })
        
        context = mustache_context.copy()
        context.update({
            'packageName': self.target_packages['domain_ports_input'],
            'classname': f'{entity}UseCase',
            'entityName': entity,
            'entityVarName': entity.lower(),
            'serviceName': service_name,
            'hasCreate': has_create,
            'hasGet': has_get,
            'hasUpdate': has_update,
            'hasDelete': has_delete,
            'hasList': has_list,
            'hasComplexOperations': len(complex_ops_info) > 0,
            'complexOperations': complex_ops_info
        })
        
        content = self.template_renderer.render_template('consolidatedUseCase.mustache', context)
        file_path = self.output_dir / self.file_manager.get_package_path(self.target_packages['domain_ports_input']) / f'{entity}UseCase.java'
        self.file_manager.write_file(file_path, content)
    
    def generate_consolidated_service(self, entity: str, operations: List[Dict[str, Any]], complex_ops: List[str], mustache_context: Dict[str, Any]):
        """Generate consolidated application service."""
        entity_var_name = entity.lower()
        service_name = operations[0]['service'] if operations else entity_var_name
        
        has_create = any(op['id'].startswith('Create') and entity in op['id'] for op in operations)
        has_get = any(op['id'].startswith('Get') and entity in op['id'] for op in operations)
        has_update = any(op['id'].startswith('Update') and entity in op['id'] for op in operations)
        has_delete = any(op['id'].startswith('Delete') and entity in op['id'] for op in operations)
        has_list = any(op['id'] == f'List{entity}s' for op in operations)
        
        complex_ops_info = []
        if complex_ops:
            for op in complex_ops:
                method_name = op[0].lower() + op[1:] if op else ''
                repository_method = self._convert_operation_to_repository_method(op)
                default_param = self._get_default_parameter(op)
                complex_ops_info.append({
                    'operationId': op,
                    'methodName': method_name,
                    'responseType': f'{op}ResponseContent',
                    'repositoryMethod': repository_method,
                    'defaultParameter': default_param
                })
        
        context = mustache_context.copy()
        context.update({
            'packageName': self.target_packages['application_service'],
            'classname': f"{entity}Service",
            'entityName': entity,
            'entityVarName': entity_var_name,
            'serviceName': service_name,
            'hasCreate': has_create,
            'hasGet': has_get,
            'hasUpdate': has_update,
            'hasDelete': has_delete,
            'hasList': has_list,
            'hasComplexOperations': len(complex_ops_info) > 0,
            'complexOperations': complex_ops_info,
            'isApplicationService': True
        })
        
        content = self.template_renderer.render_template('consolidatedService.mustache', context)
        file_path = self.output_dir / self.file_manager.get_package_path(self.target_packages['application_service']) / f"{entity}Service.java"
        self.file_manager.write_file(file_path, content)
    
    def _convert_operation_to_repository_method(self, operation_id: str) -> str:
        """Convert operation ID to repository method name."""
        if operation_id.startswith('Get'):
            # GetNeighborhoodsByCity -> findNeighborhoodsByCity
            return 'find' + operation_id[3:]
        return 'findAll'
    
    def _get_default_parameter(self, operation_id: str) -> str:
        """Get default parameter for operation."""
        if 'ByCity' in operation_id:
            return '"defaultCityId"'
        elif 'ByCountry' in operation_id:
            return '"defaultCountryId"'
        elif 'ByRegion' in operation_id:
            return '"defaultRegionId"'
        return '"defaultId"'