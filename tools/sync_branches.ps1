# Sync current branch changes to all other local branches
# Usage: .\sync_branches.ps1

$ErrorActionPreference = "Stop"
$RootDir = Resolve-Path "$PSScriptRoot/.."

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
    
    # Get all local branches (exclude current branch)
    $AllBranches = git branch --format="%(refname:short)" | Where-Object { $_.Trim() -ne "" -and $_.Trim() -ne $CurrentBranch }
    
    if ($AllBranches.Count -eq 0) {
        Write-Host "No other local branches to sync to." -ForegroundColor Yellow
        exit 0
    }
    
    Write-Host ""
    Write-Host "Will merge '$CurrentBranch' into:" -ForegroundColor Green
    $AllBranches | ForEach-Object { Write-Host "  - $_" -ForegroundColor Gray }
    Write-Host ""
    
    # Confirm
    $Confirm = Read-Host "Continue? (y/N)"
    if ($Confirm -ne "y" -and $Confirm -ne "Y") {
        Write-Host "Cancelled." -ForegroundColor Yellow
        exit 0
    }
    
    $FailedBranches = @()
    $SuccessBranches = @()
    
    foreach ($Branch in $AllBranches) {
        $Branch = $Branch.Trim()
        if (-not $Branch) { continue }
        
        Write-Host ""
        Write-Host "Merging into $Branch..." -ForegroundColor Green
        
        # Checkout target branch
        git checkout $Branch 2>$null
        if ($LASTEXITCODE -ne 0) {
            Write-Host "  Failed to checkout $Branch" -ForegroundColor Red
            $FailedBranches += $Branch
            git checkout $CurrentBranch 2>$null
            continue
        }
        
        # Merge current branch
        git merge $CurrentBranch --no-edit 2>$null
        if ($LASTEXITCODE -ne 0) {
            Write-Host "  Merge conflict in $Branch! Aborting merge..." -ForegroundColor Red
            git merge --abort 2>$null
            $FailedBranches += $Branch
            git checkout $CurrentBranch 2>$null
            continue
        }
        
        Write-Host "  Successfully merged into $Branch" -ForegroundColor Gray
        $SuccessBranches += $Branch
    }
    
    # Return to original branch
    git checkout $CurrentBranch 2>$null
    
    Write-Host ""
    Write-Host "Sync complete!" -ForegroundColor Cyan
    
    if ($SuccessBranches.Count -gt 0) {
        Write-Host ""
        Write-Host "Successfully merged into:" -ForegroundColor Green
        $SuccessBranches | ForEach-Object { Write-Host "  - $_" -ForegroundColor Green }
    }
    
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
