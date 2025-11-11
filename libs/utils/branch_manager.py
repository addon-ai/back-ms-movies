#!/usr/bin/env python3
"""
Branch Manager for Pipeline
Manages feature branches created by code-gen-pipeline.sh
"""

import os
import json
import subprocess
from datetime import datetime
from typing import List, Dict

class BranchManager:
    def __init__(self, config_path: str = None):
        self.config_path = config_path or os.path.join(os.path.dirname(__file__), 'pipeline-config.json')
        self.config = self._load_config()
        self.branch_prefix = self.config.get('branchManagement', {}).get('branchPrefix', 'feature/project_generation')
        self.max_branches = self.config.get('branchManagement', {}).get('maxBranches', 10)
        self.date_format = self.config.get('branchManagement', {}).get('dateFormat', '%Y%m%d_%H%M%S')
        
        # Prevent execution in project directories
        current_dir = os.getcwd()
        if '/projects/' in current_dir:
            raise ValueError(f"BranchManager should not be used in project directories: {current_dir}")
    
    def _load_config(self) -> Dict:
        """Load pipeline configuration"""
        try:
            with open(self.config_path, 'r') as f:
                return json.load(f)
        except FileNotFoundError:
            return {}
    
    def get_pipeline_branches(self) -> List[str]:
        """Get all pipeline-generated branches"""
        try:
            result = subprocess.run(['git', 'branch'], capture_output=True, text=True, check=True)
            branches = []
            for line in result.stdout.split('\n'):
                branch = line.strip().replace('* ', '')
                if branch.startswith(self.branch_prefix):
                    branches.append(branch)
            return sorted(branches)
        except subprocess.CalledProcessError:
            return []
    
    def create_new_branch(self) -> str:
        """Create new pipeline branch and manage branch limit"""
        timestamp = datetime.now().strftime(self.date_format)
        new_branch = f"{self.branch_prefix}_{timestamp}"
        
        # Get existing pipeline branches
        existing_branches = self.get_pipeline_branches()
        
        # If we're at the limit, delete the oldest branch
        if len(existing_branches) >= self.max_branches:
            oldest_branch = existing_branches[0]  # Already sorted
            self._delete_branch(oldest_branch)
            print(f"Deleted oldest branch: {oldest_branch}")
        
        # Create new branch
        try:
            subprocess.run(['git', 'checkout', '-b', new_branch], check=True)
            print(f"Created new branch: {new_branch}")
            return new_branch
        except subprocess.CalledProcessError as e:
            print(f"Failed to create branch {new_branch}: {e}")
            return ""
    
    def _delete_branch(self, branch_name: str):
        """Delete a branch locally and remotely"""
        try:
            # Switch to master if we're on the branch to be deleted
            current_branch = subprocess.run(['git', 'branch', '--show-current'], 
                                          capture_output=True, text=True, check=True).stdout.strip()
            if current_branch == branch_name:
                subprocess.run(['git', 'checkout', 'master'], check=True)
            
            # Delete local branch
            subprocess.run(['git', 'branch', '-D', branch_name], check=True)
            
            # Delete remote branch if it exists
            subprocess.run(['git', 'push', 'origin', '--delete', branch_name], 
                         check=False)  # Don't fail if remote doesn't exist
        except subprocess.CalledProcessError as e:
            print(f"Warning: Could not fully delete branch {branch_name}: {e}")
    
    def get_smithy_repos(self) -> List[str]:
        """Get list of Smithy repository names from build/smithy directory"""
        smithy_dir = "build/smithy"
        if not os.path.exists(smithy_dir):
            return []
        
        repos = []
        for item in os.listdir(smithy_dir):
            item_path = os.path.join(smithy_dir, item)
            if os.path.isdir(item_path):
                repos.append(item)
        return sorted(repos)
    
    def commit_pipeline_changes(self):
        """Add and commit all pipeline changes with Smithy repo names"""
        try:
            # Get Smithy repository names
            smithy_repos = self.get_smithy_repos()
            
            if smithy_repos:
                repo_list = ", ".join(smithy_repos)
                commit_message = f"Pipeline generation: {repo_list}"
            else:
                commit_message = "Pipeline generation: No Smithy repos found"
            
            # Add all changes
            subprocess.run(['git', 'add', '.'], check=True)
            
            # Check if there are changes to commit
            result = subprocess.run(['git', 'diff', '--cached', '--quiet'], capture_output=True)
            if result.returncode == 0:
                print("No changes to commit")
                return
            
            # Commit changes
            subprocess.run(['git', 'commit', '-m', commit_message], check=True)
            print(f"Committed changes: {commit_message}")
            
        except subprocess.CalledProcessError as e:
            print(f"Failed to commit changes: {e}")

def main():
    """Main entry point for branch management"""
    import sys
    
    branch_manager = BranchManager()
    
    if len(sys.argv) > 1 and sys.argv[1] == "--commit":
        # Commit mode: add and commit changes
        branch_manager.commit_pipeline_changes()
    else:
        # Branch creation mode
        new_branch = branch_manager.create_new_branch()
        if new_branch:
            print(f"Pipeline will run on branch: {new_branch}")
        else:
            print("Failed to create pipeline branch")

if __name__ == "__main__":
    main()