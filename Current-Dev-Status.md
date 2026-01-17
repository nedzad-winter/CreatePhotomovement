# Create Photomovement - AI Context

## Project Overview
**Create Photomovement** is a Minecraft mod serving as an addon to the **Create** mod. It introduces solar-powered rotational force generation.
The mod is multi-loader (NeoForge, Fabric) and multi-version (1.20.1, 1.21.1).

## directory Structure
- `neoforge/1201`: NeoForge implementation for Minecraft 1.20.1. (Primary development focus).
- `neoforge/1211`: NeoForge implementation for Minecraft 1.21.1.
- `fabric/1201`: Fabric implementation for Minecraft 1.20.1.
- `tools`: Helper scripts for building and launching clients.
- `common`: (If exists) Common code shared between loaders.

## Key Features
1.  **Solar Generator (Basic)**
    *   Generates Stress Units (SU) when exposed to sunlight.
    *   **Vertical Variant**: `solar_generator`.
    *   **Horizontal Variant**: `horizontal_solar_generator`.
    *   **Logic**:
        *   Requires Light Level 12 or higher.
        *   **STRICT Sky Access**: The block directly above MUST be able to see the sky (transparents blocks allowed if they don't block skylight, but code enforces `level.canSeeSky`).
        *   **Rain Penalty**: Speed (RPM) is halved during rain. SU remains constant.
    *   **Dyeable**: Comes in all 16 colors. Recipes allow dyeing and re-dyeing.

2.  **Advanced Solar Generator**
    *   **2x** the output of the Basic Solar Generator.
    *   **Vertical Variant**: `adv_solar_generator`.
    *   **Horizontal Variant**: `horz_adv_solar_generator`.
    *   **Logic**: Uses `AdvSolarGeneratorBlockEntity` inheriting from basic but doubles the config speed. Same sky/rain logic.
    *   **Dyeable**: Comes in all 16 colors.

## Codebase State (As of 2026-01-17)

### NeoForge 1.20.1
*   **Status**: Most feature-complete.
*   **Blocks**: All Basic and Advanced generators registered.
*   **Items**: All item forms registered.
*   **Renderers**: `SolarGeneratorRenderer` and `HorizontalSolarGeneratorRenderer` registered for both Basic and Advanced.
*   **Recipes**:
    *   Crafting conversion (Vertical <-> Horizontal) exists for both Basic and Advanced.
    *   Dyeing recipes exist for Basic (shapeless).
    *   **NOTE**: `tags/item` and `tags/block` were renamed to `tags/items` and `tags/blocks` to fix recipe loading.
*   **Issues**: None currently known.

### NeoForge 1.21.1
*   **Status**: Code-complete, Data-incomplete.
*   **Blocks/Items**: Registered matching 1.20.1.
*   **Logic**: Synced strict sky access logic with 1.20.1.
*   **Recipes**: **MISSING** most JSON recipes (dyeing, crafting basic generators). Only Conversion recipes for Advanced generators exist.
*   **Issues**: Needs massive data pack import (recipes, tags) to match 1.20.1.

### Fabric 1.20.1
*   **Status**: **Lagging Behind**.
*   **Missing Features**:
    *   Does **NOT** have Advanced Solar Generators (`AdvSolarGenerator`).
    *   Likely missing latest logic updates (rain check, strict sky check).
*   **Issues**: Needs porting of Advanced Generators and logic updates.

## Key Classes (NeoForge)
*   **Registers**: `AllBlocks.java`, `AllItems.java`, `AllBlockEntityTypes.java`, `AllCreativeTabs.java`.
*   **Client**: `CreatePhotomovementClient.java` (Renderer registration).
*   **Logic**:
    *   `content/kinetics/solargenerator/SolarGeneratorBlockEntity.java` (Base logic).
    *   `content/kinetics/solargenerator/AdvSolarGeneratorBlockEntity.java` (Advanced logic).
    *   `content/kinetics/solargenerator/HorizontalSolarGeneratorBlockEntity.java` (Horizontal logic).

## Build & Run
*   **Scripts**: `tools/start-clients.ps1` (PowerShell) and `tools/start-clients.sh` (Bash).
*   **Commands**:
    *   NeoForge 1.20.1: `.\gradlew.bat :neoforge:1201:runClient`
    *   NeoForge 1.21.1: `.\gradlew.bat :neoforge:1211:runClient`
    *   Fabric 1.20.1: `.\gradlew.bat :fabric:1201:runClient`

## Recent Changes
1.  **Strict Sky Access**: Added `!level.canSeeSky(up)` check to `SolarGeneratorBlockEntity` (both 1.20.1 and 1.21.1).
2.  **Advanced Renderer**: Added missing renderer registration in 1.20.1 Client.
3.  **Recipes**:
    *   Added Vertical <-> Horizontal conversion for Advanced blocks.
    *   Fixed `tags` directory name (singular -> plural) in 1.20.1/1.21.1 to fix dye recipes.
4.  **Dependencies**: Added JEI and BetterF3 to NeoForge 1.20.1 for debugging.
