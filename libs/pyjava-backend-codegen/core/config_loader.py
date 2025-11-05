"""
Configuration loader for project settings and parameters.
"""
import json
from pathlib import Path
from typing import Dict, List, Any


class ConfigLoader:
    """Handles loading and processing of project configuration files."""
    
    @staticmethod
    def load_projects_config(config_path: str) -> List[Dict[str, Any]]:
        """
        Load projects configuration from JSON file.
        
        Args:
            config_path: Path to the configuration JSON file
            
        Returns:
            List of project configurations
            
        Raises:
            FileNotFoundError: If configuration file doesn't exist
            json.JSONDecodeError: If configuration file is invalid JSON
        """
        with open(config_path, 'r') as f:
            return json.load(f)
    
    @staticmethod
    def build_package_structure(base_package: str) -> Dict[str, str]:
        """
        Define Hexagonal Architecture package structure.
        
        Args:
            base_package: Base package name (e.g., 'com.example.service')
            
        Returns:
            Dictionary mapping package types to their full package names
        """
        return {
            "root": base_package,
            "utils": f"{base_package}.utils",
            "domain_model": f"{base_package}.domain.model",
            "domain_ports_input": f"{base_package}.domain.ports.input",
            "domain_ports_output": f"{base_package}.domain.ports.output",
            "application_service": f"{base_package}.application.service",
            "application_dto": f"{base_package}.application.dto",
            "application_mapper": f"{base_package}.application.mapper",
            "infra_config": f"{base_package}.infrastructure.config",
            "infra_config_exceptions": f"{base_package}.infrastructure.config.exceptions",
            "infra_adapters_input_rest": f"{base_package}.infrastructure.adapters.input.rest",
            "infra_adapters_output_persistence": f"{base_package}.infrastructure.adapters.output.persistence",
            "infra_entity": f"{base_package}.infrastructure.adapters.output.persistence.entity",
            "infra_repository": f"{base_package}.infrastructure.adapters.output.persistence.repository",
            "infra_adapter": f"{base_package}.infrastructure.adapters.output.persistence.adapter",
        }
    
    @staticmethod
    def build_mustache_context(project_config: Dict[str, Any], target_packages: Dict[str, str]) -> Dict[str, Any]:
        """
        Build global Mustache context with all configuration options.
        
        Args:
            project_config: Single project configuration
            target_packages: Package structure mapping
            
        Returns:
            Dictionary containing all template variables and configuration
        """
        context = project_config.copy()
        context.update(project_config['project']['params']['configOptions'])
        context.update(target_packages)
        
        # Add project parameters
        if 'project' in project_config:
            context['author'] = project_config['project']['general'].get('author', 'Generator')
            context['version'] = project_config['project']['general'].get('version', '1.0.0')
            context['artifactVersion'] = project_config['project']['params'].get('artifactVersion', '1.0.0')
            context['project'] = project_config['project']
            context.update(project_config['project']['params'])
        
        # Add infrastructure configuration
        if 'infra' in project_config:
            context['infra'] = project_config['infra']
        
        # Set database type flags
        db_config = project_config.get('database', {})
        db_type = db_config.get('sgbd', 'h2').lower() if db_config else 'h2'
        context.update({
            'database': {
                **db_config,
                'postgresql': db_type == 'postgresql',
                'mysql': db_type == 'mysql', 
                'oracle': db_type == 'oracle',
                'sqlserver': db_type == 'sqlserver' or db_type == 'msserver',
                'h2': not db_type or db_type == 'h2'
            },
            'smithyModel': 'user-service.smithy',
            'generatorVersion': '1.0.0'
        })
        
        # Add maven configuration
        if 'maven' in project_config:
            context['maven'] = project_config['maven']
        
        # Add DevOps configuration
        if 'devops' in project_config:
            context['devops'] = project_config['devops']
        
        return context