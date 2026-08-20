# Bump mod_version in the root gradle.properties.
#
# mod_version is defined once at the root; all three subprojects inherit it, so this
# is the only file that needs touching.
#
# Usage:
#   .\bump_version.ps1                  # interactive — shows current version and prompts for the new one
#   .\bump_version.ps1 -Version 0.3.3   # non-interactive
#   .\bump_version.ps1 -DryRun          # show what would change without writing
#
# Reminder: this script does NOT touch CHANGELOG.md. Add a new section there by hand
# (the human-readable description doesn't auto-template well).

param(
    [string]$Version,
    [switch]$DryRun
)

$ErrorActionPreference = "Stop"
$RootDir = (Resolve-Path "$PSScriptRoot/..").Path

$Files = @(
    Join-Path $RootDir "gradle.properties"
)

# Subprojects must NOT redefine mod_version -- a local definition silently shadows the
# root value and this script would not catch it. Flag any that reappear.
$SubprojectFiles = @(
    Join-Path $RootDir "fabric/1201/gradle.properties"
    Join-Path $RootDir "neoforge/1201/gradle.properties"
    Join-Path $RootDir "neoforge/1211/gradle.properties"
)

# Pattern: capture the "mod_version = " prefix (group 1) and the value (group 2).
$linePattern = '^(\s*mod_version\s*=\s*)(.+?)\s*$'

function Get-ModVersion {
    param([string]$Path)
    foreach ($line in Get-Content -LiteralPath $Path) {
        if ($line -match $linePattern) {
            return $matches[2]
        }
    }
    return $null
}

# 1. Discover the current version from the root file.
$current = Get-ModVersion -Path $Files[0]
if (-not $current) {
    throw "Could not find a 'mod_version = ...' line in $($Files[0])"
}
Write-Host "Current mod_version: $current" -ForegroundColor Cyan

# 2. Sanity-check: no subproject may shadow the root definition.
foreach ($file in $SubprojectFiles) {
    if (-not (Test-Path -LiteralPath $file)) {
        continue
    }
    $v = Get-ModVersion -Path $file
    if ($v) {
        Write-Warning "  $file redefines mod_version ($v) and shadows the root value. Remove that line."
    }
}

# 3. Get the new version — either from -Version or interactively.
if (-not $Version) {
    $Version = Read-Host "Enter new version (current: $current)"
}
$Version = $Version.Trim()
if (-not $Version) {
    throw "No version supplied."
}
if ($Version -eq $current) {
    Write-Host "New version equals current. Nothing to do." -ForegroundColor Yellow
    exit 0
}
if ($Version -notmatch '^\d+\.\d+\.\d+([-+].+)?$') {
    Write-Warning "'$Version' doesn't look like SemVer (e.g. 0.3.3). Continuing anyway."
}

# 4. Rewrite each file, preserving the original "mod_version = " prefix exactly.
foreach ($file in $Files) {
    if (-not (Test-Path -LiteralPath $file)) {
        Write-Warning "Skipping missing file: $file"
        continue
    }

    $lines = Get-Content -LiteralPath $file
    $matched = $false
    $newLines = foreach ($line in $lines) {
        if ($line -match $linePattern) {
            $matched = $true
            "$($matches[1])$Version"
        } else {
            $line
        }
    }

    if (-not $matched) {
        Write-Warning "Skipping $file - no mod_version line found"
        continue
    }

    $rel = $file.Substring($RootDir.Length + 1)
    if ($DryRun) {
        Write-Host "  [dry-run] $rel : $current -> $Version" -ForegroundColor DarkGray
    } else {
        # -Encoding utf8 writes a BOM on Windows PowerShell 5.1, which then sits in
        # front of the first line of gradle.properties on every bump. Write plain
        # UTF-8 instead.
        $utf8NoBom = New-Object System.Text.UTF8Encoding($false)
        [System.IO.File]::WriteAllLines($file, $newLines, $utf8NoBom)
        Write-Host "  bumped   $rel : $current -> $Version" -ForegroundColor Green
    }
}

Write-Host ""
if ($DryRun) {
    Write-Host "Dry run complete. No files were written." -ForegroundColor Yellow
} else {
    Write-Host "Done. Don't forget to add a CHANGELOG.md entry for $Version." -ForegroundColor Cyan
}
