# Generates the game test arena as a Minecraft structure .nbt file.
#
# The arena is deliberately trivial -- a bedrock floor on the bottom layer and air
# everywhere above -- which is exactly what a structure block would have produced,
# so writing the NBT directly saves building it by hand in the client.
#
# Usage:
#   .\make_gametest_structure.ps1                 # writes for all targets
#   .\make_gametest_structure.ps1 -DryRun         # show what would be written
#
# The file format is the vanilla structure template: a size, a block state palette,
# one entry per block position and an empty entity list. DataVersion differs per
# Minecraft version so the game does not run the file through the data fixer.

param(
    [int]$Width = 16,
    [int]$Height = 8,
    [int]$Depth = 9,
    [string]$Name = 'solar_platform',
    [switch]$DryRun
)

$ErrorActionPreference = 'Stop'
$RootDir = (Resolve-Path "$PSScriptRoot/..").Path

# Where each target expects the file. 1.21.1 renamed the resource folder to the
# singular; 1.20.1 still uses the plural. DataVersion is the world format version of
# that Minecraft release.
$Targets = @(
    @{ Path = 'neoforge/1211'; Folder = 'structure';  DataVersion = 3955 }
    @{ Path = 'neoforge/1201'; Folder = 'structures'; DataVersion = 3465 }
    @{ Path = 'fabric/1201';   Folder = 'structures'; DataVersion = 3465 }
)

# ---------------------------------------------------------------- NBT primitives
#
# NBT is big-endian; .NET's BinaryWriter is not, so every multi-byte value is
# written by hand.

$TAG_End = 0; $TAG_Int = 3; $TAG_String = 8; $TAG_List = 9; $TAG_Compound = 10

function Write-Byte([System.IO.Stream]$s, [int]$v) {
    $s.WriteByte([byte]$v)
}

function Write-Short([System.IO.Stream]$s, [int]$v) {
    $s.WriteByte([byte](($v -shr 8) -band 0xFF))
    $s.WriteByte([byte]($v -band 0xFF))
}

function Write-Int([System.IO.Stream]$s, [int]$v) {
    $s.WriteByte([byte](($v -shr 24) -band 0xFF))
    $s.WriteByte([byte](($v -shr 16) -band 0xFF))
    $s.WriteByte([byte](($v -shr 8) -band 0xFF))
    $s.WriteByte([byte]($v -band 0xFF))
}

function Write-StringPayload([System.IO.Stream]$s, [string]$v) {
    $bytes = [System.Text.Encoding]::UTF8.GetBytes($v)
    Write-Short $s $bytes.Length
    $s.Write($bytes, 0, $bytes.Length)
}

# A named tag: type, name, then the payload the caller writes.
function Write-TagHeader([System.IO.Stream]$s, [int]$type, [string]$name) {
    Write-Byte $s $type
    Write-StringPayload $s $name
}

function Write-IntList([System.IO.Stream]$s, [string]$name, [int[]]$values) {
    Write-TagHeader $s $TAG_List $name
    Write-Byte $s $TAG_Int
    Write-Int $s $values.Length
    foreach ($v in $values) { Write-Int $s $v }
}

# ---------------------------------------------------------------- the structure

function Write-Structure([System.IO.Stream]$s, [int]$w, [int]$h, [int]$d, [int]$dataVersion) {
    # Root compound, unnamed.
    Write-Byte $s $TAG_Compound
    Write-Short $s 0

    Write-TagHeader $s $TAG_Int 'DataVersion'
    Write-Int $s $dataVersion

    Write-IntList $s 'size' @($w, $h, $d)

    # Palette: index 0 is the floor, index 1 is everything else.
    Write-TagHeader $s $TAG_List 'palette'
    Write-Byte $s $TAG_Compound
    Write-Int $s 2
    foreach ($blockName in @('minecraft:bedrock', 'minecraft:air')) {
        Write-TagHeader $s $TAG_String 'Name'
        Write-StringPayload $s $blockName
        Write-Byte $s $TAG_End
    }

    # Every position is listed explicitly rather than relying on unlisted positions
    # defaulting to air -- the structure then describes the arena completely.
    Write-TagHeader $s $TAG_List 'blocks'
    Write-Byte $s $TAG_Compound
    Write-Int $s ($w * $h * $d)
    for ($y = 0; $y -lt $h; $y++) {
        $state = if ($y -eq 0) { 0 } else { 1 }
        for ($x = 0; $x -lt $w; $x++) {
            for ($z = 0; $z -lt $d; $z++) {
                Write-IntList $s 'pos' @($x, $y, $z)
                Write-TagHeader $s $TAG_Int 'state'
                Write-Int $s $state
                Write-Byte $s $TAG_End
            }
        }
    }

    # No entities. An empty list is written with element type TAG_End, which is what
    # Minecraft itself emits.
    Write-TagHeader $s $TAG_List 'entities'
    Write-Byte $s $TAG_End
    Write-Int $s 0

    Write-Byte $s $TAG_End
}

function Build-Bytes([int]$w, [int]$h, [int]$d, [int]$dataVersion) {
    $raw = New-Object System.IO.MemoryStream
    try {
        Write-Structure $raw $w $h $d $dataVersion

        # Structure files are gzip-compressed.
        $out = New-Object System.IO.MemoryStream
        $gzip = New-Object System.IO.Compression.GZipStream($out, [System.IO.Compression.CompressionMode]::Compress, $true)
        $bytes = $raw.ToArray()
        $gzip.Write($bytes, 0, $bytes.Length)
        $gzip.Dispose()
        return $out.ToArray()
    } finally {
        $raw.Dispose()
    }
}

# ---------------------------------------------------------------- main

Write-Host "Structure '$Name': ${Width}x${Height}x${Depth}, bedrock floor on layer 0" -ForegroundColor Cyan

foreach ($target in $Targets) {
    $dir = Join-Path $RootDir "$($target.Path)/src/main/resources/data/createphotomovement/$($target.Folder)"
    $file = Join-Path $dir "$Name.nbt"
    $data = Build-Bytes $Width $Height $Depth $target.DataVersion

    $rel = $file.Substring($RootDir.Length + 1)
    if ($DryRun) {
        Write-Host "  [dry-run] $rel  ($($data.Length) bytes, DataVersion $($target.DataVersion))" -ForegroundColor DarkGray
    } else {
        if (-not (Test-Path $dir)) { New-Item -ItemType Directory -Force $dir | Out-Null }
        [System.IO.File]::WriteAllBytes($file, $data)
        Write-Host "  wrote $rel  ($($data.Length) bytes, DataVersion $($target.DataVersion))" -ForegroundColor Green
    }
}

Write-Host ""
Write-Host "Run the tests with: gradlew :neoforge:1211:runGameTestServer" -ForegroundColor Cyan
