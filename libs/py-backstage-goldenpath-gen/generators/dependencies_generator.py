#!/usr/bin/env python3
import os

class DependenciesGenerator:
    """Generates dependencies.yml for each project"""
    
    def generate(self, project_name, config, project_dir):
        """Generate dependencies.yml in entities directory"""
        dependencies = config.get('project', {}).get('dependencies', {})
        if not dependencies:
            return
        
        base_name = project_name.replace('-webflux', '').replace('back-ms-', '')
        system_name = f"{base_name}-system"
        
        entities_dir = os.path.join(project_dir, 'entities')
        os.makedirs(entities_dir, exist_ok=True)
        
        resources = []
        
        # Java runtime
        if 'java' in dependencies:
            resources.append(self._create_resource(
                f"java-{dependencies['java']}-{base_name}",
                f"Java Runtime Environment version {dependencies['java']}",
                ['java', 'runtime'],
                'runtime',
                system_name
            ))
        
        # Spring Boot
        if 'springBoot' in dependencies:
            version = dependencies['springBoot'].replace('.', '-')
            resources.append(self._create_resource(
                f"spring-boot-{version}-{base_name}",
                f"Spring Boot Framework version {dependencies['springBoot']}",
                ['spring-boot', 'framework'],
                'library',
                system_name
            ))
        
        # MapStruct
        if 'mapstruct' in dependencies:
            version = dependencies['mapstruct'].replace('.', '-').replace('Final', '').strip('-')
            resources.append(self._create_resource(
                f"mapstruct-{version}-{base_name}",
                f"MapStruct mapping library version {dependencies['mapstruct']}",
                ['mapstruct', 'mapping'],
                'library',
                system_name
            ))
        
        # Lombok
        if 'lombok' in dependencies:
            version = dependencies['lombok'].replace('.', '-')
            resources.append(self._create_resource(
                f"lombok-{version}-{base_name}",
                f"Lombok code generation library version {dependencies['lombok']}",
                ['lombok', 'codegen'],
                'library',
                system_name
            ))
        
        # PostgreSQL
        if 'postgresql' in dependencies:
            version = dependencies['postgresql'].replace('.', '-')
            resources.append(self._create_resource(
                f"postgresql-{version}-{base_name}",
                f"PostgreSQL JDBC Driver version {dependencies['postgresql']}",
                ['postgresql', 'database'],
                'database-driver',
                system_name
            ))
        
        # H2
        if 'h2' in dependencies:
            version = dependencies['h2'].replace('.', '-')
            resources.append(self._create_resource(
                f"h2-{version}-{base_name}",
                f"H2 Database Engine version {dependencies['h2']}",
                ['h2', 'database'],
                'database-driver',
                system_name
            ))
        
        # Springdoc
        if 'springdoc' in dependencies:
            version = dependencies['springdoc'].replace('.', '-')
            resources.append(self._create_resource(
                f"springdoc-{version}-{base_name}",
                f"SpringDoc OpenAPI library version {dependencies['springdoc']}",
                ['springdoc', 'openapi'],
                'library',
                system_name
            ))
        
        # Maven Compiler
        if 'mavenCompiler' in dependencies:
            version = dependencies['mavenCompiler'].replace('.', '-')
            resources.append(self._create_resource(
                f"maven-compiler-{version}-{base_name}",
                f"Maven Compiler Plugin version {dependencies['mavenCompiler']}",
                ['maven', 'compiler'],
                'library',
                system_name
            ))
        
        # Maven Surefire
        if 'mavenSurefire' in dependencies:
            version = dependencies['mavenSurefire'].replace('.', '-')
            resources.append(self._create_resource(
                f"maven-surefire-{version}-{base_name}",
                f"Maven Surefire Plugin version {dependencies['mavenSurefire']}",
                ['maven', 'testing'],
                'library',
                system_name
            ))
        
        # Lombok MapStruct Binding
        if 'lombokMapstructBinding' in dependencies:
            version = dependencies['lombokMapstructBinding'].replace('.', '-')
            resources.append(self._create_resource(
                f"lombok-mapstruct-binding-{version}-{base_name}",
                f"Lombok MapStruct Binding version {dependencies['lombokMapstructBinding']}",
                ['lombok', 'mapstruct'],
                'library',
                system_name
            ))
        
        # Jacoco
        if 'jacoco' in dependencies:
            version = dependencies['jacoco'].replace('.', '-')
            resources.append(self._create_resource(
                f"jacoco-{version}-{base_name}",
                f"JaCoCo Code Coverage version {dependencies['jacoco']}",
                ['jacoco', 'testing'],
                'library',
                system_name
            ))
        
        # Flyway
        if 'flywayDatabasePostgresql' in dependencies:
            version = dependencies['flywayDatabasePostgresql'].replace('.', '-')
            resources.append(self._create_resource(
                f"flyway-{version}-{base_name}",
                f"Flyway Database Migration version {dependencies['flywayDatabasePostgresql']}",
                ['flyway', 'database'],
                'library',
                system_name
            ))
        
        # Write dependencies.yml
        content = '\n---\n'.join(resources) + '\n'
        with open(os.path.join(entities_dir, 'dependencies.yml'), 'w') as f:
            f.write(content)
    
    def _create_resource(self, name, description, tags, resource_type, system):
        """Create a resource YAML block"""
        tags_yaml = '\n'.join([f"    - {tag}" for tag in tags])
        return f"""apiVersion: backstage.io/v1alpha1
kind: Resource
metadata:
  name: {name}
  description: {description}
  tags:
{tags_yaml}
spec:
  type: {resource_type}
  owner: platform-team
  system: {system}"""
