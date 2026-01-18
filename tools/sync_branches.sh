#!/bin/bash
# Sync current branch changes to all other local branches
# Usage: ./sync_branches.sh
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"
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
# Get all local branches (exclude current branch)
mapfile -t ALL_BRANCHES < <(git branch --format="%(refname:short)" | grep -v "^${CURRENT_BRANCH}$")
if [[ ${#ALL_BRANCHES[@]} -eq 0 ]]; then
    echo "No other local branches to sync to."
    exit 0
fi
echo ""
echo "Will merge '$CURRENT_BRANCH' into:"
for branch in "${ALL_BRANCHES[@]}"; do
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
SUCCESS_BRANCHES=()
for branch in "${ALL_BRANCHES[@]}"; do
    [[ -z "$branch" ]] && continue
    
    echo ""
    echo "Merging into $branch..."
    
    # Checkout target branch
    if ! git checkout "$branch" >/dev/null 2>&1; then
        echo "  Failed to checkout $branch"
        FAILED_BRANCHES+=("$branch")
        git checkout "$CURRENT_BRANCH" >/dev/null 2>&1
        continue
    fi
    
    # Merge current branch
    if ! git merge "$CURRENT_BRANCH" --no-edit >/dev/null 2>&1; then
        echo "  Merge conflict in $branch! Aborting merge..."
        git merge --abort >/dev/null 2>&1
        FAILED_BRANCHES+=("$branch")
        git checkout "$CURRENT_BRANCH" >/dev/null 2>&1
        continue
    fi
    
    echo "  Successfully merged into $branch"
    SUCCESS_BRANCHES+=("$branch")
done
# Return to original branch
git checkout "$CURRENT_BRANCH" >/dev/null 2>&1
echo ""
echo "Sync complete!"
if [[ ${#SUCCESS_BRANCHES[@]} -gt 0 ]]; then
    echo ""
    echo "Successfully merged into:"
    for branch in "${SUCCESS_BRANCHES[@]}"; do
        echo "  - $branch"
    done
fi
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
