"""
Project files generation functionality.
"""
from pathlib import Path
from typing import Dict, List, Any
import os


class ProjectGenerator:
    """Handles generation of project configuration and supporting files."""
    
    def __init__(self, template_renderer, file_manager, target_packages, output_dir, project_config):
        self.template_renderer = template_renderer
        self.file_manager = file_manager
        self.target_packages = target_packages
        self.output_dir = output_dir
        self.project_config = project_config
    
    def generate_main_application(self, mustache_context: Dict[str, Any]):
        """Generate Spring Boot main application class."""
        context = mustache_context.copy()
        main_class_name = self.project_config['project']['params']['configOptions']['mainClass']
        context.update({
            'packageName': self.target_packages['root'],
            'classname': main_class_name
        })
        
        content = self.template_renderer.render_template('Application.mustache', context)
        file_path = self.output_dir / self.file_manager.get_package_path(self.target_packages['root']) / f"{main_class_name}.java"
        self.file_manager.write_file(file_path, content)
    
    def generate_configuration(self, mustache_context: Dict[str, Any]):
        """Generate Spring configuration classes."""
        context = mustache_context.copy()
        
        # Only include entities that have ALL required generated files
        entities_context = []
        use_case_dir = self.output_dir / self.file_manager.get_package_path(self.target_packages['domain_ports_input'])
        service_dir = self.output_dir / self.file_manager.get_package_path(self.target_packages['application_service'])
        mapper_dir = self.output_dir / self.file_manager.get_package_path(self.target_packages['application_mapper'])
        repository_dir = self.output_dir / self.file_manager.get_package_path(self.target_packages['infra_repository'])
        adapter_dir = self.output_dir / self.file_manager.get_package_path(self.target_packages['infra_adapter'])
        
        if all(dir.exists() for dir in [use_case_dir, service_dir, mapper_dir, repository_dir, adapter_dir]):
            for use_case_file in use_case_dir.glob('*UseCase.java'):
                entity_name = use_case_file.stem.replace('UseCase', '')
                required_files = [
                    service_dir / f'{entity_name}Service.java',
                    mapper_dir / f'{entity_name}Mapper.java',
                    repository_dir / f'Jpa{entity_name}Repository.java',
                    adapter_dir / f'{entity_name}RepositoryAdapter.java'
                ]
                
                if all(file.exists() for file in required_files):
                    entities_context.append({
                        'entityName': entity_name,
                        'entityVarName': entity_name.lower()
                    })
        
        # Main application configuration
        context.update({
            'packageName': self.target_packages['infra_config'],
            'classname': 'ApplicationConfiguration',
            'entities': entities_context
        })
        content = self.template_renderer.render_template('Configuration.mustache', context)
        file_path = self.output_dir / self.file_manager.get_package_path(self.target_packages['infra_config']) / "ApplicationConfiguration.java"
        self.file_manager.write_file(file_path, content)
        
        # Security configuration with dynamic entity paths
        entities_with_paths = []
        for entity_info in entities_context:
            entity_path = entity_info['entityName'].lower() + 's'
            entities_with_paths.append({
                'entityName': entity_info['entityName'],
                'entityVarName': entity_info['entityVarName'],
                'entityPath': entity_path
            })
        
        context.update({
            'classname': 'SecurityConfiguration',
            'entities': entities_with_paths
        })
        content = self.template_renderer.render_template('SecurityConfiguration.mustache', context)
        file_path = self.output_dir / self.file_manager.get_package_path(self.target_packages['infra_config']) / "SecurityConfiguration.java"
        self.file_manager.write_file(file_path, content)
        
        # OpenAPI configuration
        context.update({'classname': 'OpenApiConfiguration', 'entityName': 'User'})
        content = self.template_renderer.render_template('OpenApiConfiguration.mustache', context)
        file_path = self.output_dir / self.file_manager.get_package_path(self.target_packages['infra_config']) / "OpenApiConfiguration.java"
        self.file_manager.write_file(file_path, content)
        
        # Global exception handler
        context.update({
            'packageName': self.target_packages['infra_config_exceptions'],
            'classname': 'GlobalExceptionHandler'
        })
        content = self.template_renderer.render_template('GlobalExceptionHandler.mustache', context)
        file_path = self.output_dir / self.file_manager.get_package_path(self.target_packages['infra_config_exceptions']) / "GlobalExceptionHandler.java"
        self.file_manager.write_file(file_path, content)
        
        # NotFoundException
        context.update({'classname': 'NotFoundException'})
        content = self.template_renderer.render_template('NotFoundException.mustache', context)
        file_path = self.output_dir / self.file_manager.get_package_path(self.target_packages['infra_config_exceptions']) / "NotFoundException.java"
        self.file_manager.write_file(file_path, content)
        
        # LoggingUtils
        context.update({
            'packageName': self.target_packages['utils'],
            'classname': 'LoggingUtils'
        })
        content = self.template_renderer.render_template('LoggingUtils.mustache', context)
        file_path = self.output_dir / self.file_manager.get_package_path(self.target_packages['utils']) / "LoggingUtils.java"
        self.file_manager.write_file(file_path, content)
    
    def generate_pom_xml(self, mustache_context: Dict[str, Any]):
        """Generate Maven POM file."""
        content = self.template_renderer.render_template('pom.xml.mustache', mustache_context)
        file_path = self.output_dir / "pom.xml"
        self.file_manager.write_file(file_path, content)
    
    def generate_application_properties(self, mustache_context: Dict[str, Any]):
        """Generate application.properties file."""
        content = self.template_renderer.render_template('application.properties.mustache', mustache_context)
        file_path = self.output_dir / "src/main/resources/application.properties"
        self.file_manager.write_file(file_path, content)
        
        # Generate test application.properties
        test_content = self.template_renderer.render_template('application-test.properties.mustache', mustache_context)
        test_file_path = self.output_dir / "src/test/resources/application-test.properties"
        self.file_manager.write_file(test_file_path, test_content)
    
    def generate_readme(self, mustache_context: Dict[str, Any]):
        """Generate project README file."""
        content = self.template_renderer.render_template('README.md.mustache', mustache_context)
        file_path = self.output_dir / "README.md"
        self.file_manager.write_file(file_path, content)
    
    def generate_docker_compose(self, mustache_context: Dict[str, Any]):
        """Generate docker-compose.yml file."""
        content = self.template_renderer.render_template('docker-compose.yml.mustache', mustache_context)
        file_path = self.output_dir / "docker-compose.yml"
        self.file_manager.write_file(file_path, content)
    
    def generate_dockerfile(self, mustache_context: Dict[str, Any]):
        """Generate Dockerfile."""
        content = self.template_renderer.render_template('Dockerfile.mustache', mustache_context)
        file_path = self.output_dir / "Dockerfile"
        self.file_manager.write_file(file_path, content)
    
    def generate_maven_wrapper(self, mustache_context: Dict[str, Any]):
        """Generate Maven wrapper scripts."""
        # Unix/Linux/macOS wrapper
        content = self.template_renderer.render_template('mvnw.mustache', mustache_context)
        file_path = self.output_dir / "mvnw"
        self.file_manager.write_file(file_path, content)
        os.chmod(file_path, 0o755)
        
        # Windows wrapper
        content = self.template_renderer.render_template('mvnw.cmd.mustache', mustache_context)
        file_path = self.output_dir / "mvnw.cmd"
        self.file_manager.write_file(file_path, content)
        
        # Maven wrapper properties
        wrapper_dir = self.output_dir / ".mvn" / "wrapper"
        self.file_manager.ensure_directory(wrapper_dir)
        content = self.template_renderer.render_template('maven-wrapper.properties.mustache', mustache_context)
        file_path = wrapper_dir / "maven-wrapper.properties"
        self.file_manager.write_file(file_path, content)
    
    def generate_ci_cd_workflow(self, mustache_context: Dict[str, Any]):
        """Generate GitHub Actions CI/CD workflow."""
        github_dir = self.output_dir / ".github" / "workflows"
        self.file_manager.ensure_directory(github_dir)
        
        content = self.template_renderer.render_template('project/ci-cd.yml.mustache', mustache_context)
        file_path = github_dir / "ci-cd.yml"
        self.file_manager.write_file(file_path, content)