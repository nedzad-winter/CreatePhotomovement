param (
    [string]$Version
)

$ErrorActionPreference = "Stop"
$RootDir = Resolve-Path "$PSScriptRoot/.."
$BuildsDir = Join-Path $PSScriptRoot "builds"
$GameProfilesDir = "D:\Minecraft\profiles"

# If no version provided, read from gradle.properties
if (-not $Version) {
    $GradlePropsFile = Join-Path $RootDir "gradle.properties"
    if (Test-Path $GradlePropsFile) {
        $GradleProps = Get-Content $GradlePropsFile
        $VersionLine = $GradleProps | Where-Object { $_ -match "^\s*mod_version\s*=" }
        if ($VersionLine) {
            $Version = ($VersionLine -split "=")[1].Trim()
        }
    }
    if (-not $Version) {
        Write-Error "Could not determine version. Please provide -Version parameter or ensure mod_version is set in gradle.properties"
        exit 1
    }
}

# Mapping of project to game profile folder
$GameProfileMapping = @{
    "neoforge/1201" = "Dev-CreatePM-NeoForge-1201"
    "neoforge/1211" = "Dev-CreatePM-NeoForge-1211"
    "fabric/1201" = "Dev-CreatePM-Fabric-1201"
}

# Ensure builds directory exists
if (-not (Test-Path $BuildsDir)) {
    New-Item -ItemType Directory -Force -Path $BuildsDir | Out-Null
}

Write-Host "Starting build for version: $Version" -ForegroundColor Cyan
Write-Host ""

# Function to copy jar to destinations
function Copy-JarToDestinations {
    param (
        [string]$JarPath,
        [string]$ProjectPath
    )
    
    $JarName = Split-Path $JarPath -Leaf
    
    # Copy to builds directory
    Copy-Item -Path $JarPath -Destination $BuildsDir -Force
    Write-Host "  Copied to builds: $JarName" -ForegroundColor Gray
    
    # Copy to game profile if exists
    $ProfileName = $GameProfileMapping[$ProjectPath]
    if ($ProfileName) {
        $GameModsDir = Join-Path $GameProfilesDir "$ProfileName\mods"
        if (Test-Path $GameModsDir) {
            # Remove old versions of this mod from the mods folder
            Get-ChildItem -Path $GameModsDir -Filter "createphotomovement-*.jar" | ForEach-Object {
                Remove-Item $_.FullName -Force
                Write-Host "  Removed old: $($_.Name)" -ForegroundColor DarkGray
            }
            # Copy new version
            Copy-Item -Path $JarPath -Destination $GameModsDir -Force
            Write-Host "  Copied to game: $GameModsDir" -ForegroundColor Yellow
        } else {
            Write-Host "  Game folder not found: $GameModsDir (skipped)" -ForegroundColor DarkGray
        }
    }
}

# Function to run gradle build for root project modules
function Build-RootProject {
    param (
        [string]$ProjectName,
        [string]$ProjectPath,
        [string]$GradleTask
    )

    Write-Host "Building $ProjectName..." -ForegroundColor Green
    Push-Location $RootDir
    
    try {
        $GradlePath = Join-Path $RootDir "gradlew.bat"
        & $GradlePath $GradleTask
        if ($LASTEXITCODE -ne 0) {
            Write-Error "Build failed for $ProjectName"
            return
        }
    }
    finally {
        Pop-Location
    }

    $LibsDir = Join-Path $RootDir "$ProjectPath/build/libs"
    if (Test-Path $LibsDir) {
        Get-ChildItem -Path $LibsDir -Filter "*$Version*.jar" | Where-Object { $_.Name -notlike "*-sources*" } | ForEach-Object {
            Copy-JarToDestinations -JarPath $_.FullName -ProjectPath $ProjectPath
        }
    } else {
        Write-Warning "No libs directory found for $ProjectName at $LibsDir"
    }
    Write-Host ""
}

# Function to build standalone project (like Fabric)
function Build-StandaloneProject {
    param (
        [string]$ProjectName,
        [string]$ProjectPath
    )

    Write-Host "Building $ProjectName..." -ForegroundColor Green
    $ProjectDir = Join-Path $RootDir $ProjectPath
    Push-Location $ProjectDir
    
    try {
        $GradlePath = Join-Path $ProjectDir "gradlew.bat"
        & $GradlePath build
        if ($LASTEXITCODE -ne 0) {
            Write-Error "Build failed for $ProjectName"
            return
        }
    }
    finally {
        Pop-Location
    }

    $LibsDir = Join-Path $ProjectDir "build/libs"
    if (Test-Path $LibsDir) {
        Get-ChildItem -Path $LibsDir -Filter "*$Version*.jar" | Where-Object { $_.Name -notlike "*-sources*" } | ForEach-Object {
            Copy-JarToDestinations -JarPath $_.FullName -ProjectPath $ProjectPath
        }
    } else {
        Write-Warning "No libs directory found for $ProjectName at $LibsDir"
    }
    Write-Host ""
}

# Build NeoForge 1.20.1
Build-StandaloneProject -ProjectName "NeoForge 1.20.1" -ProjectPath "neoforge/1201"

# Build NeoForge 1.21.1
Build-StandaloneProject -ProjectName "NeoForge 1.21.1" -ProjectPath "neoforge/1211"

# Build Fabric 1.20.1 (standalone project)
Build-StandaloneProject -ProjectName "Fabric 1.20.1" -ProjectPath "fabric/1201"

Write-Host "All builds completed!" -ForegroundColor Cyan
Write-Host "Artifacts are in: $BuildsDir" -ForegroundColor Cyan

# List copied files
Write-Host ""
Write-Host "Built JARs:" -ForegroundColor Green
Get-ChildItem -Path $BuildsDir -Filter "*$Version*.jar" | Where-Object { $_.Name -notlike "*-sources*" } | ForEach-Object {
    Write-Host "  - $($_.Name)" -ForegroundColor White
}
