param (
    [Parameter(Mandatory=$true)]
    [string]$Version
)

$ErrorActionPreference = "Stop"
$RootDir = Resolve-Path "$PSScriptRoot/.."
$BuildsDir = Join-Path $PSScriptRoot "builds"

# Ensure builds directory exists
if (-not (Test-Path $BuildsDir)) {
    New-Item -ItemType Directory -Force -Path $BuildsDir | Out-Null
}

Write-Host "Starting build for version: $Version" -ForegroundColor Cyan

# Function to run gradle build and copy artifacts
function Build-Project {
    param (
        [string]$ProjectName,
        [string]$ProjectPath,
        [string]$GradleTask
    )

    Write-Host "Building $ProjectName..." -ForegroundColor Green
    Push-Location $RootDir
    
    try {
        $GradlePath = Join-Path $RootDir "gradlew.bat"
        & $GradlePath $GradleTask "-Pmod_version=$Version"
        if ($LASTEXITCODE -ne 0) {
            Write-Error "Build failed for $ProjectName"
        }
    }
    finally {
        Pop-Location
    }

    $LibsDir = Join-Path $RootDir "$ProjectPath/build/libs"
    if (Test-Path $LibsDir) {
        Get-ChildItem -Path $LibsDir -Filter "*$Version*.jar" | ForEach-Object {
             Copy-Item -Path $_.FullName -Destination $BuildsDir -Force
             Write-Host "Copied: $($_.Name)" -ForegroundColor Gray
        }
    } else {
        Write-Warning "No libs directory found for $ProjectName at $LibsDir"
    }
}

# Build Forge 1.20.1
Build-Project -ProjectName "Forge 1.20.1" -ProjectPath "forge/1201" -GradleTask ":forge:1201:build"

# Build NeoForge 1.20.1
Build-Project -ProjectName "NeoForge 1.20.1" -ProjectPath "neoforge/1201" -GradleTask ":neoforge:1201:build"

# Build NeoForge 1.21.1
Build-Project -ProjectName "NeoForge 1.21.1" -ProjectPath "neoforge/1211" -GradleTask ":neoforge:1211:build"

Write-Host "All builds completed! Artifacts are in: $BuildsDir" -ForegroundColor Cyan
