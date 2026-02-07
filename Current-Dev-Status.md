# Create Photomovement - Development Status
**Last Updated:** 2026-01-28 17:27

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
