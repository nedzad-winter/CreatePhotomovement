# Sync current branch changes to all other development branches
# Usage: .\sync_branches.ps1

$ErrorActionPreference = "Stop"
$RootDir = Resolve-Path "$PSScriptRoot/.."

# All development branches
$AllBranches = @(
    "dev-1.20.1",
    "dev-1.21.1",
    "main"
)

Push-Location $RootDir

try {
    # Get current branch
    $CurrentBranch = git rev-parse --abbrev-ref HEAD
    Write-Host "Current branch: $CurrentBranch" -ForegroundColor Cyan
    
    # Ensure working directory is clean
    $Status = git status --porcelain
    if ($Status) {
        Write-Host "Working directory is not clean. Please commit or stash changes first." -ForegroundColor Red
        Write-Host $Status
        exit 1
    }
    
    # Get branches to sync to (exclude current branch)
    $TargetBranches = $AllBranches | Where-Object { $_ -ne $CurrentBranch }
    
    if ($TargetBranches.Count -eq 0) {
        Write-Host "No target branches to sync to." -ForegroundColor Yellow
        exit 0
    }
    
    Write-Host ""
    Write-Host "Will merge '$CurrentBranch' into:" -ForegroundColor Green
    $TargetBranches | ForEach-Object { Write-Host "  - $_" -ForegroundColor Gray }
    Write-Host ""
    
    # Confirm
    $Confirm = Read-Host "Continue? (y/N)"
    if ($Confirm -ne "y" -and $Confirm -ne "Y") {
        Write-Host "Cancelled." -ForegroundColor Yellow
        exit 0
    }
    
    $FailedBranches = @()
    
    foreach ($Branch in $TargetBranches) {
        Write-Host ""
        Write-Host "Merging into $Branch..." -ForegroundColor Green
        
        # Check if branch exists
        $BranchExists = git show-ref --verify --quiet "refs/heads/$Branch" 2>$null
        if ($LASTEXITCODE -ne 0) {
            Write-Host "  Branch '$Branch' does not exist locally, skipping." -ForegroundColor Yellow
            continue
        }
        
        # Checkout target branch
        git checkout $Branch
        if ($LASTEXITCODE -ne 0) {
            Write-Host "  Failed to checkout $Branch" -ForegroundColor Red
            $FailedBranches += $Branch
            git checkout $CurrentBranch
            continue
        }
        
        # Merge current branch
        git merge $CurrentBranch --no-edit
        if ($LASTEXITCODE -ne 0) {
            Write-Host "  Merge conflict in $Branch! Aborting merge..." -ForegroundColor Red
            git merge --abort
            $FailedBranches += $Branch
            git checkout $CurrentBranch
            continue
        }
        
        Write-Host "  Successfully merged into $Branch" -ForegroundColor Gray
    }
    
    # Return to original branch
    git checkout $CurrentBranch
    
    Write-Host ""
    Write-Host "Sync complete!" -ForegroundColor Cyan
    
    if ($FailedBranches.Count -gt 0) {
        Write-Host ""
        Write-Host "Failed branches (need manual merge):" -ForegroundColor Red
        $FailedBranches | ForEach-Object { Write-Host "  - $_" -ForegroundColor Red }
    }
    
    Write-Host ""
    Write-Host "Don't forget to push all branches:" -ForegroundColor Yellow
    Write-Host "  git push --all" -ForegroundColor Gray
}
finally {
    Pop-Location
}
