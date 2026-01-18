# Start all Minecraft mod clients for testing
# This script launches all available mod versions in separate windows

param(
    [switch]$Fabric,
    [switch]$NeoForge,
    [switch]$All
)

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = Split-Path -Parent $scriptDir

# Define all available clients
$clients = @(
    @{ Name = "Fabric 1.20.1"; Path = "$projectRoot\fabric\1201"; Flag = "Fabric" },
    @{ Name = "NeoForge 1.20.1"; Path = "$projectRoot\neoforge\1201"; Flag = "NeoForge" },
    @{ Name = "NeoForge 1.21.1"; Path = "$projectRoot\neoforge\1211"; Flag = "NeoForge" }
)

# If no flags specified, default to All
if (-not ($Fabric -or $NeoForge -or $All)) {
    $All = $true
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Create Photomovement - Client Launcher" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$launchedCount = 0

foreach ($client in $clients) {
    $shouldLaunch = $All -or 
                    ($Fabric -and $client.Flag -eq "Fabric") -or
                    ($NeoForge -and $client.Flag -eq "NeoForge")
    
    if ($shouldLaunch) {
        if (Test-Path $client.Path) {
            Write-Host "Starting $($client.Name)..." -ForegroundColor Green
            
            # Start in a new window
            Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$($client.Path)'; Write-Host 'Launching $($client.Name)...' -ForegroundColor Yellow; .\gradlew.bat runClient"
            
            $launchedCount++
            
            # Small delay between launches to avoid overwhelming the system
            Start-Sleep -Seconds 2
        } else {
            Write-Host "Skipping $($client.Name) - Path not found: $($client.Path)" -ForegroundColor Yellow
        }
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Launched $launchedCount client(s)" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Usage:" -ForegroundColor Gray
Write-Host "  .\start-clients.ps1           # Start all clients" -ForegroundColor Gray
Write-Host "  .\start-clients.ps1 -All      # Start all clients" -ForegroundColor Gray
Write-Host "  .\start-clients.ps1 -Fabric   # Start only Fabric clients" -ForegroundColor Gray
Write-Host "  .\start-clients.ps1 -NeoForge # Start only NeoForge clients" -ForegroundColor Gray
Write-Host ""
