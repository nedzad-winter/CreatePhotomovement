#!/bin/bash

# Sync current branch changes to all other development branches
# Usage: ./sync_branches.sh

set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"

# All development branches
ALL_BRANCHES=(
    "dev-1.20.1"
    "dev-1.21.1"
    "main"
)

cd "$ROOT_DIR"

# Get current branch
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
echo "Current branch: $CURRENT_BRANCH"

# Ensure working directory is clean
if [[ -n $(git status --porcelain) ]]; then
    echo "Working directory is not clean. Please commit or stash changes first."
    git status --short
    exit 1
fi

# Get branches to sync to (exclude current branch)
TARGET_BRANCHES=()
for branch in "${ALL_BRANCHES[@]}"; do
    if [[ "$branch" != "$CURRENT_BRANCH" ]]; then
        TARGET_BRANCHES+=("$branch")
    fi
done

if [[ ${#TARGET_BRANCHES[@]} -eq 0 ]]; then
    echo "No target branches to sync to."
    exit 0
fi

echo ""
echo "Will merge '$CURRENT_BRANCH' into:"
for branch in "${TARGET_BRANCHES[@]}"; do
    echo "  - $branch"
done
echo ""

# Confirm
read -p "Continue? (y/N) " confirm
if [[ "$confirm" != "y" && "$confirm" != "Y" ]]; then
    echo "Cancelled."
    exit 0
fi

FAILED_BRANCHES=()

for branch in "${TARGET_BRANCHES[@]}"; do
    echo ""
    echo "Merging into $branch..."
    
    # Check if branch exists
    if ! git show-ref --verify --quiet "refs/heads/$branch" 2>/dev/null; then
        echo "  Branch '$branch' does not exist locally, skipping."
        continue
    fi
    
    # Checkout target branch
    if ! git checkout "$branch"; then
        echo "  Failed to checkout $branch"
        FAILED_BRANCHES+=("$branch")
        git checkout "$CURRENT_BRANCH"
        continue
    fi
    
    # Merge current branch
    if ! git merge "$CURRENT_BRANCH" --no-edit; then
        echo "  Merge conflict in $branch! Aborting merge..."
        git merge --abort
        FAILED_BRANCHES+=("$branch")
        git checkout "$CURRENT_BRANCH"
        continue
    fi
    
    echo "  Successfully merged into $branch"
done

# Return to original branch
git checkout "$CURRENT_BRANCH"

echo ""
echo "Sync complete!"

if [[ ${#FAILED_BRANCHES[@]} -gt 0 ]]; then
    echo ""
    echo "Failed branches (need manual merge):"
    for branch in "${FAILED_BRANCHES[@]}"; do
        echo "  - $branch"
    done
fi

echo ""
echo "Don't forget to push all branches:"
echo "  git push --all"
