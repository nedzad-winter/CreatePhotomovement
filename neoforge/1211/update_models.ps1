$files = Get-ChildItem "src/main/resources/assets/createphotomovement/models/block/*_solar_sail.json"
foreach ($file in $files) {
    if ($file.Name -eq "solar_sail_base.json") { continue }
    $content = Get-Content $file.FullName -Raw
    # Replace parent with our base model
    $content = $content -replace '"parent":\s*"create:block/sail/[^"]+"', '"parent": "createphotomovement:block/solar_sail_base"'
    # Replace texture key "0" with "sail"
    $content = $content -replace '"0":', '"sail":'
    Set-Content $file.FullName $content
    Write-Host "Updated $($file.Name)"
}
