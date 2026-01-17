$colors = @("white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black")
$dirs = @(
    "c:\Users\nedza\Desktop\CreatePhotomovement\neoforge\1201\src\main\resources\data\createphotomovement\recipes\crafting",
    "c:\Users\nedza\Desktop\CreatePhotomovement\neoforge\1211\src\main\resources\data\createphotomovement\recipes\crafting"
)

foreach ($baseDir in $dirs) {
    if (!(Test-Path $baseDir)) {
        New-Item -ItemType Directory -Force -Path $baseDir | Out-Null
    }

    foreach ($color in $colors) {
        # 1. Adv -> Horz Adv
        $json1 = @"
{
    "type": "minecraft:crafting_shapeless",
    "category": "misc",
    "ingredients": [
        {
            "item": "createphotomovement:${color}_adv_solar_generator"
        }
    ],
    "result": {
        "count": 1,
        "item": "createphotomovement:${color}_horz_adv_solar_generator"
    }
}
"@
        $file1 = Join-Path $baseDir "conversion-${color}-horz_adv_solar_generator.json"
        $json1 | Set-Content -Path $file1 -Encoding UTF8

        # 2. Horz Adv -> Adv
        $json2 = @"
{
    "type": "minecraft:crafting_shapeless",
    "category": "misc",
    "ingredients": [
        {
            "item": "createphotomovement:${color}_horz_adv_solar_generator"
        }
    ],
    "result": {
        "count": 1,
        "item": "createphotomovement:${color}_adv_solar_generator"
    }
}
"@
        $file2 = Join-Path $baseDir "conversion-${color}-adv_solar_generator.json"
        $json2 | Set-Content -Path $file2 -Encoding UTF8
    }
    Write-Host "Generated recipes in $baseDir"
}
