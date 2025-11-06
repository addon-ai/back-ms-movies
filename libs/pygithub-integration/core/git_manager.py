import os
import subprocess
import shutil
import json
from datetime import datetime
from typing import List, Dict

class GitManager:
    def __init__(self, project_path: str):
        self.project_path = project_path
        self.project_name = os.path.basename(project_path)
        self.config = self._load_github_config()
        
        # Prevent execution in boiler-plate-code-gen root directory only
        if os.path.basename(self.project_path) == 'boiler-plate-code-gen' and os.path.exists(os.path.join(self.project_path, 'scripts')):
            raise ValueError(f"GitManager should not be used in boiler-plate-code-gen repository: {self.project_path}")
    
    def _load_github_config(self) -> Dict:
        """Load GitHub configuration for specific project from params.json"""
        config_path = os.path.join(os.path.dirname(__file__), '..', '..', 'config', 'params.json')
        try:
            with open(config_path, 'r') as f:
                params = json.load(f)
                # Find config for this specific project
                for project_config in params:
                    project_name = project_config.get('project', {}).get('general', {}).get('name', '')
                    if project_name == self.project_name:
                        return project_config.get('devops', {})
                # Fallback to first project if not found
                if params and len(params) > 0:
                    return params[0].get('devops', {})
                return {}
        except FileNotFoundError:
            return {}
    
    def has_git_repo(self) -> bool:
        """Check if project has .git directory"""
        return os.path.exists(os.path.join(self.project_path, '.git'))
    
    def backup_git_history(self, backup_path: str):
        """Backup .git directory"""
        git_path = os.path.join(self.project_path, '.git')
        if os.path.exists(git_path):
            # Remove existing backup if it exists
            if os.path.exists(backup_path):
                shutil.rmtree(backup_path)
            shutil.copytree(git_path, backup_path)
    
    def restore_git_history(self, backup_path: str):
        """Restore .git directory"""
        git_path = os.path.join(self.project_path, '.git')
        if os.path.exists(backup_path):
            if os.path.exists(git_path):
                shutil.rmtree(git_path)
            shutil.copytree(backup_path, git_path)
    
    def init_repository(self, repo_name: str):
        """Initialize git repository with config and SSH remote"""
        os.chdir(self.project_path)
        
        # Set default branch to main globally to avoid master warning
        subprocess.run(['git', 'config', '--global', 'init.defaultBranch', 'main'], check=False)
        
        subprocess.run(['git', 'init'], check=True)
        
        # Configure git user from config file
        git_config = self.config.get('github', {}).get('gitConfig', {})
        user_name = git_config.get('user.name', 'Code Generator')
        user_email = git_config.get('user.email', 'codegen@addon-ai.com')
        
        subprocess.run(['git', 'config', 'user.name', user_name], check=True)
        subprocess.run(['git', 'config', 'user.email', user_email], check=True)
        
        # Create SSH remote URL
        ssh_base = self.config.get('github', {}).get('sshUrl', 'git@github.com:addon-ai')
        ssh_remote_url = f"{ssh_base}/{repo_name}.git"
        
        # Add remote origin with SSH URL
        subprocess.run(['git', 'remote', 'add', 'origin', ssh_remote_url], check=True)
        
        # Set default branch to main
        subprocess.run(['git', 'branch', '-M', 'main'], check=True)
    
    def initial_commit_and_push(self, commit_message: str = "Initial commit: Generated Spring Boot project"):
        """Create initial commit with generated README.md using template"""
        os.chdir(self.project_path)
        
        # Generate README.md using the template if it doesn't exist
        if not os.path.exists('README.md'):
            self._generate_readme_from_template()
        
        # Add both README.md and PR template
        subprocess.run(['git', 'add', 'README.md'], check=True)
        if os.path.exists('.github/PULL_REQUEST_TEMPLATE.md'):
            subprocess.run(['git', 'add', '.github/PULL_REQUEST_TEMPLATE.md'], check=True)
        subprocess.run(['git', 'commit', '-m', commit_message], check=True)
        subprocess.run(['git', 'push', '-u', 'origin', 'main'], check=True)
    
    def create_branches(self, branches: List[str]):
        """Create and push empty branches with only README.md"""
        os.chdir(self.project_path)
        for branch in branches:
            subprocess.run(['git', 'checkout', '-b', branch], check=True)
            subprocess.run(['git', 'push', '-u', 'origin', branch], check=True)
        subprocess.run(['git', 'checkout', 'main'], check=True)
    
    def commit_and_push(self, branch_name: str, commit_message: str):
        """Add, commit and push all project files to feature branch"""
        os.chdir(self.project_path)
        subprocess.run(['git', 'checkout', '-b', branch_name], check=True)
        subprocess.run(['git', 'add', '.', '--force'], check=True)
        
        # Check if there are changes to commit
        result = subprocess.run(['git', 'diff', '--cached', '--quiet'], capture_output=True)
        if result.returncode == 0:
            print(f"No changes to commit for {self.project_name}")
            return
        
        subprocess.run(['git', 'commit', '-m', commit_message], check=True)
        subprocess.run(['git', 'push', '-u', 'origin', branch_name], check=True)
    
    def get_feature_branch_name(self) -> str:
        """Generate feature branch name with timestamp"""
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        return f"feature/push_automatic_{timestamp}"
    
    def commit_project_code(self):
        """Create feature branch with all project code"""
        os.chdir(self.project_path)
        
        # Create feature branch from main
        feature_branch = self.get_feature_branch_name()
        subprocess.run(['git', 'checkout', 'main'], check=True)
        subprocess.run(['git', 'checkout', '-b', feature_branch], check=True)
        
        # Add all project files
        subprocess.run(['git', 'add', '.', '--force'], check=True)
        
        # Check if there are changes to commit
        result = subprocess.run(['git', 'diff', '--cached', '--quiet'], capture_output=True)
        if result.returncode == 0:
            print(f"No changes to commit for {self.project_name}")
            return feature_branch
        
        # Commit and push
        commit_message = f"Auto-generated update: {feature_branch}"
        subprocess.run(['git', 'commit', '-m', commit_message], check=True)
        subprocess.run(['git', 'push', '-u', 'origin', feature_branch], check=True)
        
        return feature_branch