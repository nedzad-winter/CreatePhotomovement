# Build all three mod targets and copy the jars to tools/builds (and into the dev
# game profiles, if they exist).
#
# Everything runs through the ONE Gradle wrapper at the repository root. The three
# targets are subprojects of that build (see settings.gradle) and each of them pulls
# the shared sources in via rootProject.file('common/src/main/java') -- so a target
# cannot be built on its own, it has to be built as :neoforge:1211:build etc.
#
# Usage:
#   .\build_mod.ps1                 # headless tests, then build all three targets
#   .\build_mod.ps1 -SkipTests      # build only
#   .\build_mod.ps1 -Version 0.3.4  # override the version used to find the jars

param (
    [string]$Version,
    [switch]$SkipTests
)

$ErrorActionPreference = "Stop"
$RootDir = Resolve-Path "$PSScriptRoot/.."
$BuildsDir = Join-Path $PSScriptRoot "builds"
$GameProfilesDir = "D:\Minecraft\profiles"
$Gradle = Join-Path $RootDir "gradlew.bat"

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

# Run one Gradle task from the repository root and stop the script if it fails.
function Invoke-Gradle {
    param (
        [string]$Task,
        [string]$FailureMessage
    )

    Push-Location $RootDir
    try {
        & $Gradle $Task --console=plain
        if ($LASTEXITCODE -ne 0) {
            throw $FailureMessage
        }
    }
    finally {
        Pop-Location
    }
}

# Build one target. $ProjectPath is the folder ("neoforge/1211"), which maps directly
# onto the Gradle project path (":neoforge:1211").
function Build-Target {
    param (
        [string]$ProjectName,
        [string]$ProjectPath
    )

    $Task = ":" + ($ProjectPath -replace "/", ":") + ":build"

    Write-Host "Building $ProjectName ($Task)..." -ForegroundColor Green
    Invoke-Gradle -Task $Task -FailureMessage "Build failed for $ProjectName"

    $LibsDir = Join-Path $RootDir "$ProjectPath/build/libs"
    if (Test-Path $LibsDir) {
        Get-ChildItem -Path $LibsDir -Filter "*$Version*.jar" |
            Where-Object { $_.Name -notlike "*-sources*" -and $_.Name -notlike "*-dev*" } |
            ForEach-Object {
                Copy-JarToDestinations -JarPath $_.FullName -ProjectPath $ProjectPath
            }
    } else {
        Write-Warning "No libs directory found for $ProjectName at $LibsDir"
    }
    Write-Host ""
}

# Headless tests first. They check the shared logic and, just as importantly, that
# every block in every target actually has a blockstate, models, a recipe and a loot
# table -- a missing loot table means the block drops nothing when broken, which is
# not visible in a build log.
if ($SkipTests) {
    Write-Host "Skipping headless tests (-SkipTests)." -ForegroundColor Yellow
    Write-Host ""
} else {
    Write-Host "Running headless tests (:common:test)..." -ForegroundColor Green
    Invoke-Gradle -Task ":common:test" -FailureMessage "Headless tests failed - not building. Fix them or re-run with -SkipTests."
    Write-Host ""
}

# Build NeoForge 1.20.1
Build-Target -ProjectName "NeoForge 1.20.1" -ProjectPath "neoforge/1201"

# Build NeoForge 1.21.1
Build-Target -ProjectName "NeoForge 1.21.1" -ProjectPath "neoforge/1211"

# Build Fabric 1.20.1
Build-Target -ProjectName "Fabric 1.20.1" -ProjectPath "fabric/1201"

Write-Host "All builds completed!" -ForegroundColor Cyan
Write-Host "Artifacts are in: $BuildsDir" -ForegroundColor Cyan

# List copied files
Write-Host ""
Write-Host "Built JARs:" -ForegroundColor Green
Get-ChildItem -Path $BuildsDir -Filter "*$Version*.jar" | Where-Object { $_.Name -notlike "*-sources*" } | ForEach-Object {
    Write-Host "  - $($_.Name)" -ForegroundColor White
}
