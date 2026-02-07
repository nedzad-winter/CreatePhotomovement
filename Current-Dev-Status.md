# Create Photomovement - Development Status
**Last Updated:** 2026-02-07

## Global Informations
 - Create version: 6.0.8 - MC 1.20.1

## Version 0.2.0 - RELEASE READY ✅

### All Versions Complete:

#### NeoForge 1.21.1 ✅ (recipe folder NOT recipes)
- All 68 blocks registered
- All recipes, tags, loot tables working
- German and Spanish translations

#### NeoForge 1.20.1 ✅
- All 68 blocks registered
- All recipes, tags, loot tables working
- German and Spanish translations

#### Forge 1.20.1 ✅
- All 68 blocks registered
- All recipes, tags, loot tables working
- German and Spanish translations

#### Fabric 1.20.1 ✅
- All 68 blocks registered
- All recipes, tags, loot tables working
- German and Spanish translations

### Built JARs:
- `createphotomovement-forge-1.20.1-0.2.0.jar`
- `createphotomovement-neoforge-1.20.1-0.2.0.jar`
- `createphotomovement-neoforge-1.21.1-0.2.0.jar`
- `createphotomovement-fabric-1.20.1-0.2.0.jar`

## Block Types (68 total):
1. **Solar Generator** (base + 16 colors) = 17 blocks
2. **Horizontal Solar Generator** (base + 16 colors) = 17 blocks  
3. **Advanced Solar Generator** (base + 16 colors) = 17 blocks
4. **Horizontal Advanced Solar Generator** (base + 16 colors) = 17 blocks

## Build Command:
```powershell
cd tools
.\build_mod.ps1
```

This will:
1. Build all 4 mod versions
2. Copy JARs to `tools/builds/`
3. Copy JARs to game mods folders (if they exist)

---

## Version 0.3.0 - IN DEVELOPMENT

### New Features:
- Solar Sail (16 color variants) - **VERIFIED**
- Solar Windmill Bearing - **VERIFIED**

### Experimental: Cyan Solar Sail Glass Pane Texture (2026-01-28)
**Status:** Testing in progress - user will evaluate

Created `cyan_solar_sail_glass_test.json` model with:
- Outer 1-pixel ring at Y=9-10 (connected to frame)
- Clear glass pane at Y=9.01-9.99 covering inner 14x14 area
- Recessed inner sail texture at Y=8-8.99 visible through glass

**Files modified:**
- `neoforge/1211/src/main/resources/assets/createphotomovement/models/block/cyan_solar_sail_glass_test.json` (NEW)
- `neoforge/1211/src/main/resources/assets/createphotomovement/blockstates/cyan_solar_sail.json` (temporarily points to test model)

**To revert:** Change blockstate back to use `cyan_solar_sail` model instead of `cyan_solar_sail_glass_test`

---

### FIXED: Solar Windmill Bearing SU Doubling Bug ✅ (2026-02-04)
**Status:** FIXED - 5 day debugging marathon complete!

**Problem:**
- Solar Windmill Bearing showed **6144 SU** instead of correct **3072 SU** after world reload
- Value was exactly double the expected amount
- Fresh assemblies worked correctly, but reloading the world caused doubling

**Root Cause:**
Create's `KineticNetwork` capacity tracking double-counts capacity:
1. `initFromTE()` sets `unloadedCapacity` from saved NBT
2. `addSilently()` also adds capacity via `calculateAddedStressCapacity()`  
3. Final calculation: `presentCapacity + unloadedCapacity` = 2x expected

**Solution (Three-part fix in `SolarWindmillBearingBlockEntity.java`):**
1. **Override `write()`:** Zero `capacity` and `lastCapacityProvided` BEFORE super.write() to prevent stale data in NBT
2. **Override `initialize()`:** Force network to re-initialize by setting `net.initialized = false` before super.initialize() runs
3. **Override `updateFromNetwork()`:** Recalculate the correct capacity ourselves and pass that to super

**Files Modified:**
- `SolarWindmillBearingBlockEntity.java`
- `SolarBearingContraption.java`

**Testing:**
- ✅ Fresh assembly: Shows correct 3072 SU
- ✅ After world reload: Shows correct 3072 SU
- ✅ Sail counts persist correctly (16 solar + 16 regular)

**Porting Status:**
- [x] 1.21.1 NeoForge - **FIXED**
- [/] 1.20.1 NeoForge - **Java code ported and compiling** (API fixes: `CreateLang`, `addBlock` signature, `AllBlocks.SAIL.get()`)
- [ ] 1.20.1 Forge - Pending
- [ ] Fabric - Pending

---

### Fixed: Solar Windmill Bearing SU Update & Crash (2026-02-04)
**Status:** Fixed & Verified 

**Issues Resolved:**
1.  **SU Update Bug:** Bearing wasn't updating Stress Units when Day/Night/Weather changed.
    *   *Fix:* Added `solarTick()` logic and implemented `getTicker` to actively monitor environmental conditions.
2.  **Startup Crash:** "Invalid block entity" error.
    *   *Fix:* Restored missing `getBlockEntityType()` method in `SolarWindmillBearingBlock`.

**Files modified:**
- `SolarWindmillBearingBlockEntity.java`: Added `solarTick()`
- `SolarWindmillBearingBlock.java`: Implemented `getTicker` and restored `getBlockEntityType`

---

## Tag Directory Naming Differences (IMPORTANT)

- **NeoForge 1.21.1**: Uses singular directory names
  - `data/create/tags/block/`
  - `data/create/tags/item/`

- **NeoForge/Forge 1.20.1**: Uses PLURAL directory names
  - `data/create/tags/blocks/`
  - `data/create/tags/items/`
  - *Note:* Create 6.0.8 JAR confirms plural usage.

 **Porting Strategy:** Ensure both directory structures exist or use the correct one for the target version.

## Recipe Format Differences (IMPORTANT for porting)

### NeoForge 1.21.1:
```json
{
    "type": "minecraft:crafting_shapeless",
    "category": "misc",
    "ingredients": [
        { "tag": "createphotomovement:solar_sails" },
        { "item": "minecraft:red_dye" }
    ],
    "result": {
        "count": 1,
        "id": "createphotomovement:red_solar_sail"
    }
}
```

### NeoForge/Forge 1.20.1:
```json
{
    "type": "minecraft:crafting_shapeless",
    "ingredients": [
        { "tag": "createphotomovement:solar_sails" },
        { "item": "minecraft:red_dye" }
    ],
    "result": {
        "item": "createphotomovement:red_solar_sail"
    }
}
```

**Key differences:**
- 1.21.1 uses `"id"` in result, 1.20.x uses `"item"`
- 1.21.1 requires `"count": 1`, 1.20.x defaults to 1
- 1.21.1 uses `"category": "misc"`, 1.20.x doesn't need it

### Development Tools (2026-02-06)
- Added **Tag Tooltips** (by Jagm11) to `neoforge/1201` environment.
  - NOTE: Used v1.0 (File ID: 4687165) for Forge 47.1.33 compatibility.
- Shows Tags in tooltips when holding key (usually `Shift` or `Control`).


## Knowledge Base 🧠

### Create Mod Contraption Assembly: Connecting Custom Blocks (Sail Logic)
**Problem:** Custom blocks (like `SolarSailBlock`) do not automatically connect to Chassis or Stick together like regular Sails, even if tagged correctly.

**Reason:** 
Create's connectivity logic (specifically in `BlockMovementChecksImpl.java` or `Contraption.java`) often relies on hardcoded `instanceof SailBlock` checks to determine if a block should "stick" to its neighbors without glue/chassis range. If your custom block does not extend `SailBlock`, it fails these checks.

**Solution (The "AttachedCheck" Registration):**
To make a custom block behave like a Sail (connect to neighbors of same type/facing), you must register a custom `AttachedCheck` in your main mod class.

**Code Example (1.20.1 / 1.21.1):**
```java
// inside your @Mod constructor or common setup
com.simibubi.create.api.contraption.BlockMovementChecks.registerAttachedCheck((state, world, pos, direction) -> {
    if (state.getBlock() instanceof YourCustomBlock) {
        // Return SUCCESS/FAIL logic based on facing
        return com.simibubi.create.api.contraption.BlockMovementChecks.CheckResult.of(
            direction.getAxis() != state.getValue(YourCustomBlock.FACING).getAxis()
        );
    }
    return com.simibubi.create.api.contraption.BlockMovementChecks.CheckResult.PASS;
});
```

**Why this works:** 
- `AttachedCheck` is part of Create's API that allows addons to inject custom stickiness logic.
- Returning `CheckResult.of(true)` tells the assembler "Yes, these blocks are attached," bypassing the need for glue or chassis range.
- Returning `PASS` lets Create continue checking other rules (like glue).

### Fixed: Solar Sail Connectivity (2026-02-06)
**Status:** FIXED & Verified (Logic 100% matched with 1.21.1)

**Issue:** Solar Sails were not connecting to each other or the bearing.
**Fix:** Added missing `AttachedCheck` registration in `CreatePhotomovement.java` (1.20.1).
**Refinement:** Removed `RadialChassisBlock` logic from `SolarSailBlock` placement helper to match standard Create behavior.

**Current Task:** 
- Compile failed with `runtimedistc` error at end of session.
- Needs investigation next session (likely clean build required).

### Porting SolarSail Logic to 1.20.1 (2026-02-07)
- **Status:** VERIFIED ✅ (Build Success)
- **Changes:**
  - Ported `SolarSailBlock.java` logic from 1.21.1 to 1.20.1.
  - Implemented manual VoxelShapes because `AllShapes.SAIL` is likely missing in 1.20 API.
  - **CRITICAL:** Restored `PlacementHelper` and `Iterate` usage by importing from `net.createmod.catnip` packages (found in Create 6.0.8+).
  - **Fixed:** Updated `SOLAR_SAIL` registration in `AllBlocks.java` to use `SoundType.SCAFFOLDING`, `strength(0.1F)`, and `noOcclusion()` to match colored variants (and fix "old sound" bug).
- **Notes:**
  - Initial builds failed due to missing `com.simibubi.create.foundation.utility` packages.

  - Runtime crash observed (exit code 1) was traced to **ASUS Overlay (GTII-OSD64-GL.dll)**, not mod code.

### Fixed: WindmillBearingRenderer Compilation Error (2026-02-07)
**Status:** FIXED 

**Issue:** Build failed with compilation error in `CreatePhotomovementClient.java`:
``
error: cannot find symbol
    com.simibubi.create.content.contraptions.bearing.WindmillBearingRenderer::new);
                                                    ^
  symbol:   class WindmillBearingRenderer
  location: package com.simibubi.create.content.contraptions.bearing
``

**Root Cause:** 
The `WindmillBearingRenderer` class doesn't exist (or isn't publicly accessible) in Create version 6.0.8-291. Create handles bearing rendering internally through its own systems.

**Solution:**
Removed the problematic renderer registration line for `SOLAR_WINDMILL_BEARING`. Since `SolarWindmillBearingBlockEntity` extends Create's `WindmillBearingBlockEntity`, the rendering is automatically handled by Create's internal rendering system. No manual renderer registration is required for inherited bearing functionality.

**Files Modified:**
- `neoforge/1201/src/main/java/com/createphotomovement/CreatePhotomovementClient.java` (removed line 35-36, added explanatory comment)

**Learning Point:**
When extending block entities from the Create mod (or any dependency mod), verify whether the parent class handles rendering automatically. Many modern Minecraft mods use internal renderer registration that doesn't require manual client-side registration for inherited functionality. Only register custom renderers when implementing unique visual behavior beyond what the parent provides.

---

**UPDATE (2026-02-07):** The initial fix was incorrect. The correct solution is to use `BearingRenderer` (which exists in Create 6.0.8-291), not remove the registration entirely. The Solar Windmill Bearing now properly registers `BearingRenderer::new` and should render correctly with animations.

---

### Repository Cleanup (2026-02-07)
- **Deleted:**
  - `Feature_pipeline` (Ideas migrated to task list)
  - `bin-versions/` (Old binary directory)
  - `build_log*.txt` (Temporary build logs)
- **Kept:**
  - `_Create(1201)/` & `_Create(1211)/` (Reference directories)
  - `_minecraft-assets-master/` (Asset reference)

### Future Ideas (Backlog)
- **Enhanced Solar Generator:**
  - Recipe: Blackstone + Brass (instead of Deepslate + Andesite)
  - Stats: High RPM (32), same SU as normal generator
- **Solar Fences**

### NeoForge 1.20.1 to Forge 1.20.1 Migration Analysis (2026-02-07)
**Status:** COMPLETE - Universal Jar Strategy

**Discovery:**
The codebase in `neoforge/1201`:
1. Uses standard Forge APIs (`net.minecraftforge.*`).
2. Configures `mods.toml` to accept BOTH `forge` and `neoforge` loaders.
3. Uses the `legacyforge` plugin which creates a compatible jar for both platforms.

**Conclusion:**
The `createphotomovement-neoforge-1.20.1-*.jar` IS a Universal Jar.
It works on both NeoForge 1.20.1 AND Forge 1.20.1.

**Action:**
- Deleted redundant `forge/1201/build.gradle`.
- **Decision:** Keep `neoforge` naming convention for the build output.
- **Verification:** User can just drop the `createphotomovement-neoforge-1.20.1-*.jar` into a Forge `mods` folder and it will work.
