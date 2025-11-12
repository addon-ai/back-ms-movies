#!/usr/bin/env python3
import os
from core.config_loader import ConfigLoader
from core.openapi_processor import OpenAPIProcessor
from generators.template_generator import TemplateGenerator
from generators.catalog_generator import CatalogGenerator
from generators.entity_generator import EntityGenerator
from generators.docs_generator import DocsGenerator
from generators.skeleton_generator import SkeletonGenerator
from generators.root_generator import RootGenerator
from generators.techdocs_generator import TechDocsGenerator
from generators.dependencies_generator import DependenciesGenerator

class BackstageGoldenPathGenerator:
    """Main generator orchestrator"""
    
    def __init__(self, projects_dir="projects", output_dir="backstage-templates", 
                 config_path="libs/config/params.json", smithy_build_path="smithy-build.json"):
        self.projects_dir = projects_dir
        self.output_dir = output_dir
        self.templates_dir = os.path.join(os.path.dirname(__file__), "templates")
        
        # Initialize components
        self.config_loader = ConfigLoader(config_path)
        self.openapi_processor = OpenAPIProcessor(smithy_build_path)
        self.template_generator = TemplateGenerator(self.templates_dir)
        self.catalog_generator = CatalogGenerator(self.templates_dir)
        self.entity_generator = EntityGenerator()
        self.docs_generator = DocsGenerator(self.templates_dir, projects_dir)
        self.skeleton_generator = SkeletonGenerator(self.templates_dir, projects_dir)
        self.root_generator = RootGenerator(self.templates_dir, output_dir)
        self.techdocs_generator = TechDocsGenerator()
        self.dependencies_generator = DependenciesGenerator()
    
    def generate_all(self):
        """Generate all Backstage templates"""
        print("🚀 Starting Backstage Collection Generation...")
        
        configs = self.config_loader.load()
        os.makedirs(self.output_dir, exist_ok=True)
        
        systems = set()
        
        for config in configs:
            project_name = config['project']['general']['name']
            base_name = project_name.replace('-webflux', '').replace('back-ms-', '')
            system_name = f"{base_name}-system"
            systems.add((system_name, base_name))
            self._generate_project(project_name, config)
        
        # Generate root files
        github_org = self.config_loader.get_github_org()
        self.root_generator.generate_readme(configs)
        self.root_generator.generate_collection_components(github_org)
        self.root_generator.generate_gitignore()
        self.root_generator.generate_org_yml()
        self.root_generator.generate_systems_yml(systems)
        self.root_generator.generate_entities_location(github_org)
        self.root_generator.generate_dependencies_location(github_org)
        
        print(f"✅ Backstage collection files generated in {self.output_dir}/")
    
    def _generate_project(self, project_name, config):
        """Generate all files for a single project"""
        print(f"  📦 Generating Backstage template for {project_name}...")
        
        project_dir = os.path.join(self.output_dir, project_name)
        skeleton_dir = os.path.join(project_dir, "skeleton")
        docs_dir = os.path.join(project_dir, "docs")
        os.makedirs(skeleton_dir, exist_ok=True)
        os.makedirs(docs_dir, exist_ok=True)
        
        # Generate template.yml
        self.template_generator.generate(project_name, config, project_dir)
        
        # Generate mkdocs.yml
        self.docs_generator.generate_mkdocs(project_name, config, project_dir)
        
        # Generate docs/index.md
        self.docs_generator.generate_docs_index(project_name, docs_dir)
        
        # Copy OpenAPI specs and generate entities
        openapi_files = self.openapi_processor.copy_specs(project_name, project_dir)
        self.entity_generator.generate(project_name, project_dir, openapi_files)
        
        # Generate TechDocs for each entity
        self.techdocs_generator.generate_for_entities(project_dir, openapi_files)
        
        # Generate dependencies.yml
        self.dependencies_generator.generate(project_name, config, project_dir)
        
        # Get provides APIs
        provides_apis = self.entity_generator.get_provides_apis(project_name, project_dir)
        
        # Generate catalog-info.yml
        self.catalog_generator.generate_template_catalog(project_name, config, project_dir, provides_apis)
        
        # Generate skeleton files
        self.catalog_generator.generate_skeleton_catalog(project_name, config, skeleton_dir)
        self.skeleton_generator.generate_readme(skeleton_dir)
        self.skeleton_generator.copy_project_files(project_name, skeleton_dir)

if __name__ == "__main__":
    generator = BackstageGoldenPathGenerator()
    generator.generate_all()
