#!/usr/bin/env python3
import os
import json
import shutil
import pystache

class BackstageGoldenPathGenerator:
    def __init__(self, projects_dir="projects", output_dir="backstage-templates", config_path="libs/config/params.json"):
        self.projects_dir = projects_dir
        self.output_dir = output_dir
        self.config_path = config_path
        self.templates_dir = os.path.join(os.path.dirname(__file__), "templates")
        
    def generate_all(self):
        """Generate Backstage templates for all projects"""
        print("🚀 Starting Backstage Golden Path Generation...")
        
        with open(self.config_path, 'r') as f:
            configs = json.load(f)
        
        os.makedirs(self.output_dir, exist_ok=True)
        
        for config in configs:
            project_name = config['project']['general']['name']
            self.generate_template(project_name, config)
        
        self._generate_root_catalog()
        self._generate_type_docs(configs)
        
        print(f"✅ Backstage templates generated in {self.output_dir}/")
    
    def generate_template(self, project_name, config):
        """Generate template for a single project"""
        print(f"  📦 Generating template for {project_name}...")
        
        template_dir = os.path.join(self.output_dir, project_name)
        os.makedirs(template_dir, exist_ok=True)
        
        # Generate template.yaml
        self._generate_template_yaml(template_dir, project_name, config)
        
        # Generate skeleton
        self._generate_skeleton(project_name, template_dir, config)
        
        # Generate project-level catalog
        self._generate_project_catalog(template_dir, project_name, config)
    
    def _generate_template_yaml(self, template_dir, project_name, config):
        """Generate template.yaml file"""
        template_path = os.path.join(self.templates_dir, "template.yaml.mustache")
        with open(template_path, 'r') as f:
            template = f.read()
        
        stack_type = config['project']['general'].get('type', 'springBoot')
        is_webflux = stack_type == 'springWebflux'
        
        data = {
            'project_name': project_name,
            'project_description': config['project']['general']['description'],
            'stack_type': stack_type,
            'is_webflux': is_webflux,
            'github_org': config['devops']['github']['organization']
        }
        
        output = pystache.render(template, data)
        
        with open(os.path.join(template_dir, "template.yaml"), 'w') as f:
            f.write(output)
    
    def _generate_skeleton(self, project_name, template_dir, config):
        """Generate skeleton directory"""
        skeleton_dir = os.path.join(template_dir, "skeleton")
        source_dir = os.path.join(self.projects_dir, project_name)
        
        if os.path.exists(skeleton_dir):
            shutil.rmtree(skeleton_dir)
        
        def ignore_files(dir, files):
            ignored = []
            for f in files:
                if f.endswith('.java') or f.endswith('.sql') or f == 'devops' or f == 'target' or f == '.git':
                    ignored.append(f)
            return ignored
        
        shutil.copytree(source_dir, skeleton_dir, ignore=ignore_files)
        
        # Generate catalog-info.yaml in skeleton
        catalog_template_path = os.path.join(self.templates_dir, "catalog-info.yaml.mustache")
        with open(catalog_template_path, 'r') as f:
            catalog_template = f.read()
        
        stack_type = config['project']['general'].get('type', 'springBoot')
        is_webflux = stack_type == 'springWebflux'
        
        data = {
            'project_description': config['project']['general']['description'],
            'stack_type': stack_type,
            'is_webflux': is_webflux
        }
        
        catalog_output = pystache.render(catalog_template, data)
        
        with open(os.path.join(skeleton_dir, "catalog-info.yaml"), 'w') as f:
            f.write(catalog_output)
    
    def _generate_project_catalog(self, template_dir, project_name, config):
        """Generate project-level catalog-info.yaml"""
        template_path = os.path.join(self.templates_dir, "template-catalog-info.yaml.mustache")
        with open(template_path, 'r') as f:
            template = f.read()
        
        stack_type = config['project']['general'].get('type', 'springBoot')
        
        data = {
            'project_name': project_name,
            'project_description': config['project']['general']['description'],
            'stack_type': stack_type,
            'github_org': config['devops']['github']['organization']
        }
        
        output = pystache.render(template, data)
        
        with open(os.path.join(template_dir, "catalog-info.yaml"), 'w') as f:
            f.write(output)
    
    def _generate_root_catalog(self):
        """Generate root catalog-info.yaml"""
        template_path = os.path.join(self.templates_dir, "README.md.mustache")
        with open(template_path, 'r') as f:
            readme_template = f.read()
        
        readme_output = pystache.render(readme_template, {})
        
        with open(os.path.join(self.output_dir, "README.md"), 'w') as f:
            f.write(readme_output)
        
        catalog_path = os.path.join(self.templates_dir, "catalog-components.yaml.mustache")
        with open(catalog_path, 'r') as f:
            catalog_template = f.read()
        
        catalog_output = pystache.render(catalog_template, {})
        
        with open(os.path.join(self.output_dir, "catalog-info.yaml"), 'w') as f:
            f.write(catalog_output)
    
    def _generate_type_docs(self, configs):
        """Generate type-level documentation folders"""
        types = {}
        for config in configs:
            stack_type = config['project']['general'].get('type', 'springBoot')
            if stack_type not in types:
                types[stack_type] = []
            types[stack_type].append(config)
        
        for stack_type, type_configs in types.items():
            type_name = "springboot-service" if stack_type == "springBoot" else "webflux-service"
            type_dir = os.path.join(self.output_dir, type_name)
            os.makedirs(type_dir, exist_ok=True)
            
            # Generate type-level catalog
            self._generate_type_catalog(type_dir, type_name, stack_type)
            
            # Generate docs
            self._generate_type_documentation(type_dir, type_name, stack_type, type_configs[0])
            
            # Generate subcomponents
            self._generate_subcomponents(type_dir, type_name, stack_type)
    
    def _generate_type_catalog(self, type_dir, type_name, stack_type):
        """Generate type-level catalog-info.yaml"""
        template_path = os.path.join(self.templates_dir, "type-catalog-info.yaml.mustache")
        with open(template_path, 'r') as f:
            template = f.read()
        
        is_webflux = stack_type == 'springWebflux'
        
        data = {
            'type_name': type_name,
            'stack_type': stack_type,
            'is_webflux': is_webflux
        }
        
        output = pystache.render(template, data)
        
        with open(os.path.join(type_dir, "catalog-info.yaml"), 'w') as f:
            f.write(output)
    
    def _generate_type_documentation(self, type_dir, type_name, stack_type, sample_config):
        """Generate documentation for service type"""
        docs_dir = os.path.join(type_dir, "docs")
        os.makedirs(docs_dir, exist_ok=True)
        
        # Generate index
        index_template_path = os.path.join(self.templates_dir, "type-docs-index.md.mustache")
        with open(index_template_path, 'r') as f:
            index_template = f.read()
        
        dependencies = sample_config['project']['dependencies']
        is_webflux = stack_type == 'springWebflux'
        
        data = {
            'type_name': type_name,
            'stack_type': stack_type,
            'is_webflux': is_webflux,
            'spring_boot_version': dependencies['springBoot'],
            'java_version': dependencies['java'],
            'mapstruct_version': dependencies['mapstruct'],
            'lombok_version': dependencies['lombok']
        }
        
        index_output = pystache.render(index_template, data)
        
        with open(os.path.join(docs_dir, "index.md"), 'w') as f:
            f.write(index_output)
        
        # Generate layer docs
        for layer in ['domain', 'application', 'infrastructure']:
            layer_template_path = os.path.join(self.templates_dir, f"{layer}-layer.md.mustache")
            with open(layer_template_path, 'r') as f:
                layer_template = f.read()
            
            layer_output = pystache.render(layer_template, data)
            
            with open(os.path.join(docs_dir, f"{layer}-layer.md"), 'w') as f:
                f.write(layer_output)
        
        # Generate mkdocs.yml
        mkdocs_template_path = os.path.join(self.templates_dir, "mkdocs.yml.mustache")
        with open(mkdocs_template_path, 'r') as f:
            mkdocs_template = f.read()
        
        mkdocs_output = pystache.render(mkdocs_template, data)
        
        with open(os.path.join(type_dir, "mkdocs.yml"), 'w') as f:
            f.write(mkdocs_output)
    
    def _generate_subcomponents(self, type_dir, type_name, stack_type):
        """Generate individual YAML files for hexagonal architecture subcomponents"""
        subcomponents_path = os.path.join(self.templates_dir, "subcomponents.json")
        with open(subcomponents_path, 'r') as f:
            subcomponents_data = json.load(f)
        
        subcomponent_template_path = os.path.join(self.templates_dir, "subcomponent.yml.mustache")
        with open(subcomponent_template_path, 'r') as f:
            subcomponent_template = f.read()
        
        is_webflux = stack_type == 'springWebflux'
        
        for layer, components in subcomponents_data['layers'].items():
            for component in components:
                data = {
                    'component_name': component['name'],
                    'component_title': component['title'],
                    'component_description': component['description'],
                    'layer_name': layer,
                    'type_name': type_name,
                    'is_webflux': is_webflux
                }
                
                output = pystache.render(subcomponent_template, data)
                
                filename = f"{layer}-{component['name']}.yml"
                with open(os.path.join(type_dir, filename), 'w') as f:
                    f.write(output)

if __name__ == "__main__":
    generator = BackstageGoldenPathGenerator()
    generator.generate_all()
