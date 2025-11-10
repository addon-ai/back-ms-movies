#!/usr/bin/env python3
"""
Backstage Files Generator
Generates template.yaml and catalog-info.yaml for each project
"""
import json
from pathlib import Path
import pystache


class BackstageFilesGenerator:
    """Generates Backstage files for Java projects."""
    
    def __init__(self, config_path: str):
        """Initialize with configuration."""
        with open(config_path, 'r') as f:
            self.projects = json.load(f)
        self.templates_dir = Path(__file__).parent / 'templates'
    
    def generate_all(self, projects_dir: str):
        """Generate Backstage files for all projects."""
        projects_path = Path(projects_dir)
        
        for project_config in self.projects:
            project_name = project_config['project']['general']['name']
            project_path = projects_path / project_name
            
            if not project_path.exists():
                print(f"⚠️  Project {project_name} not found, skipping...")
                continue
            
            print(f"📦 Generating Backstage files for {project_name}...")
            self.generate_backstage_files(project_path, project_config)
    
    def generate_backstage_files(self, project_path: Path, project_config: dict):
        """Generate template.yaml and catalog-info.yaml in devops/ directory."""
        project_info = project_config['project']
        project_name = project_info['general']['name']
        stack_type = 'webflux' if 'webflux' in project_name.lower() else 'springboot'
        github_org = project_config.get('devops', {}).get('github', {}).get('organization', 'your-org')
        
        # Create devops directory
        devops_path = project_path / 'devops'
        devops_path.mkdir(exist_ok=True)
        
        # Generate template.yaml
        template_vars = {
            'template_id': f"{project_name}-template",
            'template_title': project_info['general']['description'],
            'template_description': project_info['general']['description'],
            'stack_type': stack_type,
            'default_owner': 'platform-team',
            'default_groupId': project_info['params']['groupId'],
            'default_javaVersion': project_config.get('devops', {}).get('ci', {}).get('javaVersion', '21'),
            'github_org': github_org
        }
        
        self._render_template('template.yaml.mustache', devops_path / 'template.yaml', template_vars)
        
        # Generate catalog-info.yaml
        catalog_vars = {
            'system_name': 'backend-services'
        }
        
        self._render_template('catalog-info.yaml.mustache', devops_path / 'catalog-info.yaml', catalog_vars)
        
        print(f"✅ Backstage files created in {devops_path}")
    
    def _render_template(self, template_name: str, output_path: Path, context: dict):
        """Render a Mustache template."""
        template_path = self.templates_dir / template_name
        template_content = template_path.read_text(encoding='utf-8')
        rendered = pystache.render(template_content, context)
        output_path.write_text(rendered, encoding='utf-8')


def main():
    """Main entry point."""
    import sys
    
    if len(sys.argv) < 3:
        print("Usage: python main.py <config_path> <projects_dir>")
        sys.exit(1)
    
    config_path = sys.argv[1]
    projects_dir = sys.argv[2]
    
    generator = BackstageFilesGenerator(config_path)
    generator.generate_all(projects_dir)
    
    print("\n✅ Backstage files generated in all projects!")
    print("\n📚 Files created:")
    print("   • template.yaml - Backstage template definition")
    print("   • catalog-info.yaml - Backstage catalog entry")


if __name__ == '__main__':
    main()
