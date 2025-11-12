#!/usr/bin/env python3
import os
import pystache

class CatalogGenerator:
    """Generates catalog-info.yml files"""
    
    def __init__(self, templates_dir):
        self.templates_dir = templates_dir
    
    def generate_template_catalog(self, project_name, config, project_dir, provides_apis):
        """Generate catalog-info.yml for the template itself"""
        template_path = os.path.join(self.templates_dir, "template-catalog-info.yaml.mustache")
        with open(template_path, 'r') as f:
            template = f.read()
        
        stack_type = config['project']['general'].get('type', 'springBoot')
        stack_type_kebab = 'spring-webflux' if 'webflux' in stack_type.lower() else 'spring-boot'
        
        base_name = project_name.replace('-webflux', '').replace('back-ms-', '')
        system_name = f"{base_name}-system"
        
        # Get dependencies
        dependencies = self._get_dependencies_list(config, base_name)
        
        data = {
            'template_id': project_name,
            'template_description': config['project']['general']['description'],
            'stack_type_kebab': stack_type_kebab,
            'github_org': config['devops']['github']['organization'],
            'default_owner': 'platform-team',
            'system_name': system_name,
            'provides_apis': provides_apis,
            'dependencies': dependencies
        }
        
        output = pystache.render(template, data)
        
        with open(os.path.join(project_dir, "catalog-info.yml"), 'w') as f:
            f.write(output)
    
    def _get_dependencies_list(self, config, base_name):
        """Get list of dependency resource names"""
        deps = config.get('project', {}).get('dependencies', {})
        dep_list = []
        
        dep_mapping = {
            'java': lambda v: f"java-{v}-{base_name}",
            'springBoot': lambda v: f"spring-boot-{v.replace('.', '-')}-{base_name}",
            'mapstruct': lambda v: f"mapstruct-{v.replace('.', '-').replace('Final', '').strip('-')}-{base_name}",
            'lombok': lambda v: f"lombok-{v.replace('.', '-')}-{base_name}",
            'postgresql': lambda v: f"postgresql-{v.replace('.', '-')}-{base_name}",
            'h2': lambda v: f"h2-{v.replace('.', '-')}-{base_name}",
            'springdoc': lambda v: f"springdoc-{v.replace('.', '-')}-{base_name}",
            'mavenCompiler': lambda v: f"maven-compiler-{v.replace('.', '-')}-{base_name}",
            'mavenSurefire': lambda v: f"maven-surefire-{v.replace('.', '-')}-{base_name}",
            'lombokMapstructBinding': lambda v: f"lombok-mapstruct-binding-{v.replace('.', '-')}-{base_name}",
            'jacoco': lambda v: f"jacoco-{v.replace('.', '-')}-{base_name}",
            'flywayDatabasePostgresql': lambda v: f"flyway-{v.replace('.', '-')}-{base_name}"
        }
        
        for key, formatter in dep_mapping.items():
            if key in deps:
                dep_list.append(formatter(deps[key]))
        
        return dep_list
    
    def generate_skeleton_catalog(self, project_name, config, skeleton_dir):
        """Generate skeleton/catalog-info.yml"""
        template_path = os.path.join(self.templates_dir, "catalog-info.yaml.mustache")
        with open(template_path, 'r') as f:
            template = f.read()
        
        stack_type = config['project']['general'].get('type', 'springBoot')
        stack_type_kebab = 'spring-webflux' if 'webflux' in stack_type.lower() else 'spring-boot'
        
        base_name = project_name.replace('-webflux', '').replace('back-ms-', '')
        system_name = f"{base_name}-system"
        
        data = {
            'stack_type_kebab': stack_type_kebab,
            'is_webflux': 'webflux' in stack_type.lower(),
            'system_name': system_name
        }
        
        output = pystache.render(template, data)
        
        with open(os.path.join(skeleton_dir, "catalog-info.yml"), 'w') as f:
            f.write(output)
