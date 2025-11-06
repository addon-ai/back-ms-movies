#!/usr/bin/env python3

import os
import sys
import json

# Add the parent directory to the path to import other libraries
sys.path.append(os.path.join(os.path.dirname(__file__), '..'))

from core.github_client import GitHubClient

def apply_branch_protection():
    """Apply branch protection to existing repositories"""
    
    # Load config
    config_path = os.path.join(os.path.dirname(__file__), '..', 'config', 'params.json')
    with open(config_path, 'r') as f:
        params = json.load(f)
    
    github_client = GitHubClient()
    
    # Get organization from config
    organization = params[0].get('devops', {}).get('github', {}).get('organization', 'addon-ai')
    
    # Apply protection to each project
    for project_config in params:
        project_name = project_config.get('project', {}).get('general', {}).get('name')
        
        if not project_name:
            continue
            
        print(f"Applying branch protection to {project_name}...")
        
        # Check if repository exists
        if github_client.repository_exists(organization, project_name):
            # Apply protection to all default branches
            default_branches = project_config.get('devops', {}).get('github', {}).get('defaultBranches', ['develop', 'test', 'staging', 'main'])
            github_client.setup_repository_protection(organization, project_name, default_branches)
        else:
            print(f"⚠️  Repository {project_name} not found")

if __name__ == "__main__":
    apply_branch_protection()