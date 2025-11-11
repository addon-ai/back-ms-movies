#!/usr/bin/env python3
import os
import json
import shutil
import pystache

class BackstageGoldenPathGenerator:
    def __init__(self, projects_dir="projects", output_dir="backstage-templates", config_path="libs/config/params.json", smithy_build_path="smithy-build.json"):
        self.projects_dir = projects_dir
        self.output_dir = output_dir
        self.config_path = config_path
        self.smithy_build_path = smithy_build_path
        self.templates_dir = os.path.join(os.path.dirname(__file__), "templates")
        
    def generate_all(self):
        """Generate Backstage collection files for all projects"""
        print("🚀 Starting Backstage Collection Generation...")
        
        with open(self.config_path, 'r') as f:
            configs = json.load(f)
        
        os.makedirs(self.output_dir, exist_ok=True)
        
        # Generate catalog-info.yaml for each project
        for config in configs:
            project_name = config['project']['general']['name']
            self._generate_project_catalog(project_name, config)
        
        # Generate collection files
        self._generate_collection_components(configs)
        self._generate_root_mkdocs()
        
        print(f"✅ Backstage collection files generated in {self.output_dir}/")
    
    def _generate_project_catalog(self, project_name, config):
        """Generate template.yaml and skeleton/catalog-info.yaml for a project"""
        print(f"  📦 Generating Backstage template for {project_name}...")
        
        project_dir = os.path.join(self.output_dir, project_name)
        skeleton_dir = os.path.join(project_dir, "skeleton")
        docs_dir = os.path.join(project_dir, "docs")
        os.makedirs(skeleton_dir, exist_ok=True)
        os.makedirs(docs_dir, exist_ok=True)
        
        # Generate template.yaml
        self._generate_template_yaml(project_name, config, project_dir)
        
        # Generate catalog-info.yaml (for the template itself)
        self._generate_template_catalog_info(project_name, config, project_dir)
        
        # Generate mkdocs.yml
        self._generate_mkdocs(project_name, config, project_dir)
        
        # Generate docs/index.md
        self._generate_docs_index(project_name, docs_dir)
        
        # Copy OpenAPI specs from Smithy build
        self._copy_openapi_specs(project_name, project_dir)
        
        # Generate entities.yml
        self._generate_entities_yml(project_name, config, project_dir)
        
        # Generate skeleton/catalog-info.yaml (for generated projects)
        self._generate_skeleton_catalog(project_name, config, skeleton_dir)
    
    def _generate_template_yaml(self, project_name, config, project_dir):
        """Generate template.yaml"""
        template_path = os.path.join(self.templates_dir, "template.yaml.mustache")
        with open(template_path, 'r') as f:
            template = f.read()
        
        stack_type = config['project']['general'].get('type', 'springBoot')
        
        data = {
            'template_id': project_name,
            'template_title': config['project']['general']['description'],
            'template_description': config['project']['general']['description'],
            'stack_type': stack_type,
            'default_owner': 'platform-team',
            'default_groupId': config['project']['params']['configOptions']['basePackage'].rsplit('.', 1)[0],
            'default_artifactId': project_name,
            'default_javaVersion': config['devops']['ci'].get('javaVersion', '21'),
            'default_springBootVersion': '3.2.5',
            'default_coverageThreshold': config['devops']['ci'].get('coverageThreshold', '85'),
            'github_org': config['devops']['github']['organization']
        }
        
        output = pystache.render(template, data)
        
        with open(os.path.join(project_dir, "template.yml"), 'w') as f:
            f.write(output)
    
    def _generate_template_catalog_info(self, project_name, config, project_dir):
        """Generate catalog-info.yaml for the template itself"""
        template_path = os.path.join(self.templates_dir, "template-catalog-info.yaml.mustache")
        with open(template_path, 'r') as f:
            template = f.read()
        
        stack_type = config['project']['general'].get('type', 'springBoot')
        stack_type_kebab = 'spring-webflux' if 'webflux' in stack_type.lower() else 'spring-boot'
        
        data = {
            'template_id': project_name,
            'template_description': config['project']['general']['description'],
            'stack_type_kebab': stack_type_kebab,
            'github_org': config['devops']['github']['organization'],
            'default_owner': 'platform-team'
        }
        
        output = pystache.render(template, data)
        
        with open(os.path.join(project_dir, "catalog-info.yml"), 'w') as f:
            f.write(output)
    
    def _generate_skeleton_catalog(self, project_name, config, skeleton_dir):
        """Generate skeleton/catalog-info.yaml"""
        template_path = os.path.join(self.templates_dir, "catalog-info.yaml.mustache")
        with open(template_path, 'r') as f:
            template = f.read()
        
        stack_type = config['project']['general'].get('type', 'springBoot')
        stack_type_kebab = 'spring-webflux' if 'webflux' in stack_type.lower() else 'spring-boot'
        
        data = {
            'stack_type_kebab': stack_type_kebab,
            'is_webflux': 'webflux' in stack_type.lower(),
            'system_name': 'default-system'
        }
        
        output = pystache.render(template, data)
        
        with open(os.path.join(skeleton_dir, "catalog-info.yml"), 'w') as f:
            f.write(output)
        
        # Generate README.md
        self._generate_skeleton_readme(skeleton_dir)
        
        # Copy project files to skeleton
        self._copy_project_to_skeleton(project_name, skeleton_dir)
    
    def _generate_skeleton_readme(self, skeleton_dir):
        """Generate skeleton/README.md"""
        template_path = os.path.join(self.templates_dir, "skeleton-README.md.mustache")
        with open(template_path, 'r') as f:
            template = f.read()
        
        with open(os.path.join(skeleton_dir, "README.md"), 'w') as f:
            f.write(template)
    

    def _generate_mkdocs(self, project_name, config, project_dir):
        """Generate mkdocs.yml"""
        template_path = os.path.join(self.templates_dir, "project-mkdocs.yml.mustache")
        with open(template_path, 'r') as f:
            template = f.read()
        
        data = {
            'project_name': project_name,
            'project_description': config['project']['general']['description']
        }
        
        output = pystache.render(template, data)
        
        with open(os.path.join(project_dir, "mkdocs.yml"), 'w') as f:
            f.write(output)
    
    def _generate_docs_index(self, project_name, docs_dir):
        """Generate docs/index.md from project README.md"""
        readme_path = os.path.join(self.projects_dir, project_name, "README.md")
        
        if os.path.exists(readme_path):
            with open(readme_path, 'r') as f:
                readme_content = f.read()
            
            with open(os.path.join(docs_dir, "index.md"), 'w') as f:
                f.write(readme_content)
        else:
            # Fallback content if README doesn't exist
            with open(os.path.join(docs_dir, "index.md"), 'w') as f:
                f.write(f"# {project_name}\n\nDocumentation coming soon...\n")
    
    def _copy_openapi_specs(self, project_name, project_dir):
        """Copy OpenAPI specs from Smithy build output"""
        if not os.path.exists(self.smithy_build_path):
            return
        
        with open(self.smithy_build_path, 'r') as f:
            smithy_build = json.load(f)
        
        projections = smithy_build.get('projections', {})
        openapi_dir = os.path.join(project_dir, 'openapi')
        os.makedirs(openapi_dir, exist_ok=True)
        
        # Find projections that match this project
        for projection_name in projections.keys():
            # Check if projection name starts with project name prefix
            # e.g., back-ms-users matches back-ms-users and back-ms-users-location
            if projection_name.startswith(project_name.rsplit('-webflux', 1)[0]):
                source_path = os.path.join('build', 'smithy', projection_name, 'openapi')
                
                if os.path.exists(source_path):
                    # Copy all files from source to destination
                    for file in os.listdir(source_path):
                        src_file = os.path.join(source_path, file)
                        if os.path.isfile(src_file):
                            dest_file = os.path.join(openapi_dir, file)
                            shutil.copy2(src_file, dest_file)
    
    def _generate_entities_yml(self, project_name, config, project_dir):
        """Generate entities.yml for each project"""
        template_path = os.path.join(self.templates_dir, "entities.yml.mustache")
        with open(template_path, 'r') as f:
            template = f.read()
        
        output = pystache.render(template, {})
        
        with open(os.path.join(project_dir, "entities.yml"), 'w') as f:
            f.write(output)
    
    def _generate_collection_components(self, configs):
        """Generate collection-components.yml"""
        template_path = os.path.join(self.templates_dir, "collection-components.yml.mustache")
        with open(template_path, 'r') as f:
            template = f.read()
        
        targets = []
        for config in configs:
            project_name = config['project']['general']['name']
            targets.append(f"./{project_name}/catalog-info.yml")
        
        data = {'targets': targets}
        output = pystache.render(template, data)
        
        with open(os.path.join(self.output_dir, "collection-components.yml"), 'w') as f:
            f.write(output)
        
        # Generate .gitignore in root
        self._generate_root_gitignore()
        
        # Generate org.yml in root
        self._generate_org_yml()
    
    def _generate_root_gitignore(self):
        """Generate .gitignore in backstage-templates root"""
        template_path = os.path.join(self.templates_dir, "skeleton-gitignore.mustache")
        with open(template_path, 'r') as f:
            template = f.read()
        
        with open(os.path.join(self.output_dir, ".gitignore"), 'w') as f:
            f.write(template)
    
    def _generate_org_yml(self):
        """Generate org.yml in backstage-templates root"""
        org_content = """---
# https://backstage.io/docs/features/software-catalog/descriptor-format#kind-user
apiVersion: backstage.io/v1alpha1
kind: User
metadata:
  name: admin
spec:
  memberOf: [guests, platform-team]
---
# https://backstage.io/docs/features/software-catalog/descriptor-format#kind-group
apiVersion: backstage.io/v1alpha1
kind: Group
metadata:
  name: platform-team
spec:
  type: team
  children: []
"""
        with open(os.path.join(self.output_dir, "org.yml"), 'w') as f:
            f.write(org_content)
    
    def _copy_project_to_skeleton(self, project_name, skeleton_dir):
        """Copy project files to skeleton excluding .java, .sql, .class, build, target"""
        import shutil
        
        project_path = os.path.join(self.projects_dir, project_name)
        if not os.path.exists(project_path):
            return
        
        exclude_extensions = {'.java', '.sql', '.class'}
        exclude_dirs = {'build', 'target', '.git', '.idea', '__pycache__'}
        
        for root, dirs, files in os.walk(project_path):
            # Filter out excluded directories
            dirs[:] = [d for d in dirs if d not in exclude_dirs]
            
            # Calculate relative path
            rel_path = os.path.relpath(root, project_path)
            dest_dir = skeleton_dir if rel_path == '.' else os.path.join(skeleton_dir, rel_path)
            
            # Create destination directory
            os.makedirs(dest_dir, exist_ok=True)
            
            # Copy files
            for file in files:
                file_ext = os.path.splitext(file)[1]
                if file_ext not in exclude_extensions:
                    src_file = os.path.join(root, file)
                    dest_file = os.path.join(dest_dir, file)
                    shutil.copy2(src_file, dest_file)
    
    def _generate_root_mkdocs(self):
        """Generate root mkdocs.yml"""
        template_path = os.path.join(self.templates_dir, "root-mkdocs.yml.mustache")
        if not os.path.exists(template_path):
            return
        
        with open(template_path, 'r') as f:
            template = f.read()
        
        output = pystache.render(template, {})
        
        with open(os.path.join(self.output_dir, "mkdocs.yml"), 'w') as f:
            f.write(output)

if __name__ == "__main__":
    generator = BackstageGoldenPathGenerator()
    generator.generate_all()
