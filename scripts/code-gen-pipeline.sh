#!/bin/bash

# Code Generation Pipeline
# Complete pipeline for generating Java backends and JSON schemas from Smithy definitions

set -e

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# Define paths
CONFIG_PATH="$PROJECT_ROOT/libs/config/params.json"
TEMPLATES_DIR="$PROJECT_ROOT/libs/pyjava-springboot-backend-codegen/templates"

# Get first project name from params.json array
if [ -f "$PROJECT_ROOT/libs/config/params.json" ]; then
    PROJECT_NAME=$(python3 -c "import json; config=json.load(open('$PROJECT_ROOT/libs/config/params.json')); print(config[0]['project']['general']['name'] if config else 'generated-project')")
else
    PROJECT_NAME="generated-project"
fi

OUTPUT_DIR="$PROJECT_ROOT"

echo "🚀 Starting Code Generation Pipeline"
echo "📋 Projects: Multiple projects from config array"
echo "⚙️  Config: $CONFIG_PATH"
echo "📁 Templates: $TEMPLATES_DIR"
echo "📂 Output: $OUTPUT_DIR"
echo ""

# Create pipeline branch
echo "🌿 Creating pipeline branch..."
python3 "$PROJECT_ROOT/scripts/branch_manager.py"
echo ""

# Check if Python 3 is available
echo "🔍 Checking Python 3 availability..."
if ! command -v python3 &> /dev/null; then
    echo "❌ Error: Python 3 is required but not installed."
    exit 1
fi
echo "✅ Python 3 found"

# Install dependencies if not available
echo "📦 Checking dependencies..."
DEPS_MISSING=false

if ! python3 -c "import pystache" 2>/dev/null; then
    echo "📥 Installing pystache..."
    pip3 install pystache
    DEPS_MISSING=true
fi

if ! python3 -c "import requests" 2>/dev/null; then
    echo "📥 Installing requests..."
    pip3 install requests
    DEPS_MISSING=true
fi

if [ "$DEPS_MISSING" = false ]; then
    echo "✅ Dependencies satisfied"
else
    echo "✅ Dependencies installed"
fi

# Smart cleanup: preserve Git history for existing GitHub repositories
echo "🔍 Checking existing projects and GitHub repositories..."
if [ -n "$GITHUB_TOKEN" ] && [ -d "$PROJECT_ROOT/projects" ]; then
    python3 "$PROJECT_ROOT/libs/py-github-integration.py" --backup-only
else
    echo "🗑️  Cleaning up existing projects..."
    if [ -d "$PROJECT_ROOT/projects" ]; then
        rm -rf "$PROJECT_ROOT/projects"
    fi
    echo "✅ Cleanup complete"
fi

echo ""
echo "🏗️  Step 1: Generating Java Backend projects with hexagonal architecture..."
echo ""

# Process each project based on its type
python3 "$PROJECT_ROOT/scripts/process_projects.py" "$CONFIG_PATH" "$PROJECT_ROOT" "$TEMPLATES_DIR"

echo ""
echo "📋 Step 2: Generating JSON Schemas from OpenAPI specifications..."
echo ""

# Run the JSON schema generator
python3 "$PROJECT_ROOT/libs/jsonschema-generator.py"

echo ""
echo "🎲 Step 3: Generating fake data for unit testing..."
echo ""

# Run the fake data generator
python3 "$PROJECT_ROOT/libs/fake-data-generator.py"

echo ""
echo "📚 Step 4: Generating OpenAPI documentation..."
echo ""

# Run the OpenAPI documentation generator
python3 "$PROJECT_ROOT/libs/pyopenapi-docs-generator.py"

echo ""
echo "🏗️  Step 5: Generating architectural diagrams (components & sequences)..."
echo ""

# Run the architectural documentation generator
python3 "$PROJECT_ROOT/libs/pyarchitect-docs-generator.py"

echo ""
echo "🗄️  Step 6: Generating SQL DDL scripts from OpenAPI specifications..."
echo ""

# Run the SQL generator
python3 "$PROJECT_ROOT/libs/pygenerate-sql-from-openapi.py"

echo ""
echo "🐙 Step 7: Synchronizing projects with GitHub repositories..."
echo ""

# Check if any project has GitHub integration enabled
GITHUB_ENABLED=$(python3 -c "
import json
with open('$CONFIG_PATH', 'r') as f:
    projects = json.load(f)
for project in projects:
    if project.get('devops', {}).get('github', {}).get('on', True):
        print('true')
        break
else:
    print('false')
")

# Run the GitHub integration
if [ "$GITHUB_ENABLED" = "true" ]; then
    if [ -n "$GITHUB_TOKEN" ]; then
        python3 "$PROJECT_ROOT/libs/py-github-integration.py"
        echo "✅ GitHub synchronization complete"
        
        echo "🔒 Applying branch protection rules..."
        python3 "$PROJECT_ROOT/libs/pygithub-integration/apply_branch_protection.py"
        echo "✅ Branch protection applied"
    else
        echo "⚠️  GITHUB_TOKEN not set. Skipping GitHub synchronization."
        echo "   Set GITHUB_TOKEN environment variable to enable GitHub integration."
    fi
else
    echo "⚠️  GitHub integration disabled for all projects (devops.github.on = false)"
fi

echo ""
echo ""
echo "💾 Committing pipeline changes..."
echo ""

# Commit all pipeline changes
python3 "$PROJECT_ROOT/scripts/branch_manager.py" --commit

echo ""
echo "🎉 Code Generation Pipeline complete!"
echo "📁 Generated outputs:"
echo "   • projects/ → Java Spring Boot applications with hexagonal architecture"
echo "   • schemas/ → JSON Schema files from OpenAPI specs"
echo "   • schemas/*/fake-data/ → Fake data"
echo "   • docs/puml/open-api/ → OpenAPI documentation (PlantUML, Markdown, TXT)"
echo "   • docs/puml/components/ → Architectural component diagrams (PlantUML)"
echo "   • docs/puml/sequences/ → CRUD sequence diagrams by service (PlantUML)"
echo "   • sql/ → SQL DDL scripts for database creation"
echo "   • GitHub repositories → Synchronized with generated projects (if GITHUB_TOKEN set)"
echo "🌿 Pipeline branch: $(git branch --show-current)"
echo "🚀 Ready to run:"
echo "   • Spring Boot: cd projects/[project-name] && mvn spring-boot:run"
echo "   • Spring WebFlux: cd projects/[project-name] && mvn spring-boot:run"