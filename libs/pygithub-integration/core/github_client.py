import os
import requests
import json
from typing import Dict, List, Optional

class GitHubClient:
    def __init__(self, token: str = None):
        self.token = token or os.getenv('GITHUB_TOKEN')
        self.config = self._load_github_config()
        self.base_url = self.config.get('github', {}).get('apiUrl', 'https://api.github.com')
        self.headers = {
            'Authorization': f'token {self.token}',
            'Accept': 'application/vnd.github.v3+json'
        }
    
    def _load_github_config(self) -> Dict:
        """Load GitHub configuration from params.json"""
        config_path = os.path.join(os.path.dirname(__file__), '..', '..', 'config', 'params.json')
        try:
            with open(config_path, 'r') as f:
                params = json.load(f)
                # Return first project's devops config as default
                if params and len(params) > 0:
                    return params[0].get('devops', {})
                return {}
        except FileNotFoundError:
            return {}
    
    def repository_exists(self, owner: str, repo_name: str) -> bool:
        """Check if repository exists"""
        # Use organization from config if owner matches, otherwise use provided owner
        organization = self.config.get('github', {}).get('organization')
        actual_owner = organization if organization else owner
        
        url = f"{self.base_url}/repos/{actual_owner}/{repo_name}"
        response = requests.get(url, headers=self.headers)
        return response.status_code == 200
    
    def create_repository(self, repo_name: str, description: str = "", private: bool = None) -> Dict:
        """Create a new repository"""
        organization = self.config.get('github', {}).get('organization')
        
        # Use private setting from config if not explicitly provided
        if private is None:
            private = self.config.get('github', {}).get('repositorySettings', {}).get('private', True)
        
        if organization:
            # Create repository in organization
            url = f"{self.base_url}/orgs/{organization}/repos"
        else:
            # Create repository for authenticated user
            url = f"{self.base_url}/user/repos"
        
        data = {
            'name': repo_name,
            'description': description,
            'private': private,
            'auto_init': False
        }
        response = requests.post(url, headers=self.headers, json=data)
        
        if response.status_code != 201:
            print(f"Repository creation failed: {response.status_code} - {response.text}")
            return {}
        
        return response.json()
    
    def get_user(self) -> Dict:
        """Get authenticated user info"""
        if not self.token:
            print("Error: GITHUB_TOKEN not found")
            return {}
        
        url = f"{self.base_url}/user"
        response = requests.get(url, headers=self.headers)
        
        if response.status_code != 200:
            print(f"GitHub API Error: {response.status_code} - {response.json().get('message', 'Unknown error')}")
            return {}
        
        return response.json()
    
    def setup_branch_protection(self, owner: str, repo_name: str, branch: str):
        """Setup branch protection rules"""
        url = f"{self.base_url}/repos/{owner}/{repo_name}/branches/{branch}/protection"
        
        protection_data = {
            "required_status_checks": {
                "strict": True,
                "contexts": ["build-and-test"]
            },
            "enforce_admins": False,
            "required_pull_request_reviews": {
                "required_approving_review_count": 1,
                "dismiss_stale_reviews": True,
                "require_code_owner_reviews": False
            },
            "restrictions": None,
            "allow_force_pushes": False,
            "allow_deletions": False
        }
        
        response = requests.put(url, headers=self.headers, json=protection_data)
        
        if response.status_code != 200:
            error_msg = response.json().get('message', 'Unknown error') if response.content else 'No response content'
            print(f"Branch protection failed for {branch}: {response.status_code} - {error_msg}")
        
        return response.status_code == 200
    
    def setup_repository_protection(self, owner: str, repo_name: str, protected_branches: List[str]):
        """Setup protection for multiple branches"""
        results = {}
        for branch in protected_branches:
            success = self.setup_branch_protection(owner, repo_name, branch)
            results[branch] = success
            if success:
                print(f"✅ Protected branch: {branch}")
            else:
                print(f"❌ Failed to protect branch: {branch}")
        return results
    
    def create_pr_template(self, owner: str, repo_name: str, template_content: str):
        """Create PR template in repository"""
        # First create .github directory if it doesn't exist
        github_dir_url = f"{self.base_url}/repos/{owner}/{repo_name}/contents/.github/.gitkeep"
        
        import base64
        
        # Create .github directory with .gitkeep file
        gitkeep_data = {
            "message": "Create .github directory",
            "content": base64.b64encode(b"").decode()
        }
        
        requests.put(github_dir_url, headers=self.headers, json=gitkeep_data)
        
        # Now create PR template
        url = f"{self.base_url}/repos/{owner}/{repo_name}/contents/.github/PULL_REQUEST_TEMPLATE.md"
        encoded_content = base64.b64encode(template_content.encode()).decode()
        
        data = {
            "message": "Add PR template",
            "content": encoded_content
        }
        
        response = requests.put(url, headers=self.headers, json=data)
        if response.status_code != 201:
            print(f"PR template creation failed: {response.status_code} - {response.text}")
        return response.status_code == 201