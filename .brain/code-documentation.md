# Code Documentation
**Last Updated:** 2026-02-06
**Game Version:** NeoForge 1.21.1

This document outlines the code flow, logic, and tricks used in the `CreatePhotomovement` mod. It also includes notes for porting to 1.20.1.

---

## 1. Solar Generators (Horizontal & Base)

### Code Flow
The solar generators (Basic and Advanced) extend `GeneratingKineticBlockEntity`.
- **Initialization:** On load (`onLoad`), they force a network update to sync with the Create kinetic network.
- **Tick Loop:**
    - `HorizontalSolarGeneratorBlockEntity` has a **Warmup Phase** (10 ticks) to allow the world to load before checking conditions.
    - After warmup, it updates its Stress Capacity every **200 ticks (10 seconds)** or when the block is rotated (`forceUpdate`).
- **Power Calculation:**
    - `getGeneratedSpeed()`: Returns a constant speed defined in the config. **Logic:** If it is raining at the block's position, the speed is halved.
    - `calculateAddedStressCapacity()`: Returns the calculated `currentStressCapacity`.

### Logic Used
- **Generation Condition (`canGeneratePower`)**:
    - **Sky Visibility:** The block directly above (or in front for Horizontal) must see the sky.
    - **Light Level:** The computed skylight (minus sky darkening) must be **12 or higher**.
    - **Obstruction Check:** Explicitly checks for blocks that block light (Snow Layer, Carpet, etc.) directly on the panel.
- **Horizontal Capacity Logic (`updateStressCapacity`)**:
    - **Distance Check:** Scans 2-10 blocks in front. If any solid block is found, capacity drops to the minimum (8 SU).
    - **Time Curve:** Capacity varies based on the time of day and facing direction.
        - **East Facing:** Starts high at dawn, drops to minimum by noon. Formula: $(1 - ratio)^2$ where ratio is time/12000.
        - **West Facing:** Starts low, peaks at dusk. Formula: $ratio^2$.
        - **North/South:** Generally provides minimum capacity.

### Tricks & Workarounds
- **Floating Point Precision:** `Math.abs(new - old) > 0.01f` is used to prevent constant network updates for microscopic changes.
- **Warmup Timer:** A 10-tick delay prevents "ghost" zero-readings when the chunk is just loading.

### 1.20.1 Porting Notes
- **API Changes:** `GeneratinKineticBlockEntity` methods might have slightly different names or signatures in older Create versions.
- **Block State Access:** Ensure `level.getBlockState(pos)` usage remains consistent.

---

## 2. Solar Windmill Bearing

### Code Flow
Extends `WindmillBearingBlockEntity`.
- **Assembly:** When the bearing is activated, it assembles a `SolarBearingContraption` instead of a regular `BearingContraption`.
- **Tick Loop (`solarTick`)**:
    - Runs on server side.
    - Has a startup **warmup** (10 ticks).
    - Checks `getSolarMultiplier()` every tick. If the multiplier changes (e.g., rain starts/stops, day/night cycle), it updates the generated rotation.

### Logic Used
- **Custom Contraption (`SolarBearingContraption`)**:
    - Tracks `SolarSailBlock` and regular sails separately.
    - **Sky Access Check (`assemble`)**: Scans a 5x5 area above the bearing at assembly time to determine if it has sky access. This is cached.
- **SU Calculation (`calculateAddedStressCapacity`)**:
    - **Solar Sails:** Count * 512 * Solar Multiplier.
    - **Regular Sails:** Count * 512.
    - **Total SU:** (Solar SU + Regular SU) / RPM.
- **Solar Multiplier:**
    - **2.0x**: Clear Day + Sky Access.
    - **1.5x**: Raining + Sky Access.
    - **1.0x**: Night, Thunder, or No Sky Access.

### Tricks & Workarounds
- **The "SU Doubling" Fix (Critical):**
    - **Problem:** Create's kinetic network double-counts capacity on world load (once from saved NBT, once from the block entity reconnecting).
    - **Fix Part 1 (`initialize`):** We force `net.initialized = false` if it's already true. This forces the network to ignore stale cached data.
    - **Fix Part 2 (`write`/`read`):** We strictly write `0` for capacity to disk on the server. This ensures the block loads "clean".
    - **Fix Part 3 (`updateFromNetwork`):** We override this to calculate the *true* capacity locally and pass that to the parent, ignoring the network's potentially corrupted value.
    - **Contraption Safe-Guard:** `SolarBearingContraption` zeros out the parent `sailBlocks` field in `readNBT` and `writeNBT`. This prevents the parent `WindmillBearingBlockEntity` from trying to add its own capacity logic on top of ours.

### 1.20.1 Porting Notes
- **Method Overrides:** `updateFromNetwork` and `calculateAddedStressCapacity` are critical. Ensure the method signatures in Create 1.20.1 match exactly.
- **NBT handling:** Verify `CompoundTag` vs `CompoundNBT` mapping (NeoForge uses `CompoundTag`).
- **Contraption Registration (CRITICAL):**
    - **Issue:** 1.20.1 doesn't automatically register custom contraptions like 1.21.1 might.
    - **Symptom:** Solar Windmills load as generic `BearingContraption` after world reload (0 Solar Sails).
    - **Fix:** You MUST register the `ContraptionType` explicitly using `DeferredRegister`.
    - **File:** `AllContraptionTypes.java`.
    - **Method:** `SolarBearingContraption.getType()` must override and return this registered type.
    - **Note:** Existing contraptions in saved worlds must be disassembled/reassembled to pick up the new ID.

---

## 3. Solar Sails

### Code Flow
- **Block Class:** `SolarSailBlock` extends `WrenchableDirectionalBlock`.
- **Placement:** Uses a custom `PlacementHelper` to allow easy extension of sail rows/columns (similar to standard sails).
- **Dyeing:** Supports right-click dyeing with recursion to dye connected sails of the same facing.

### Logic Used
- **Connectivity (`AttachedCheck`):**
    - Solar Sails don't automatically connect like vanilla logs.
    - **Location:** Registered in the main mod class (`CreatePhotomovement.java`).
    - **Logic:** A lambda checks `BlockMovementChecks.registerAttachedCheck`.
    - Returns `SUCCESS` if the connection direction axis is **different** from the sail's facing axis. This allows them to stick side-by-side but not "stack" forward/backward without glue (simulating a flat panel structure).

### 1.20.1 Porting Notes
- **Registration:** In 1.20.1, `BlockMovementChecks` might be in a different package or have a different registration method.
- **Tags:**
    - **1.21.1:** `tags/block` (singular).
    - **1.20.1:** `tags/blocks` (plural). **This is a common point of failure.** Ensure you duplicate tag JSONs into the plural folder for 1.20.1.
- **Recipes:**
    - **1.21.1:** Uses `"id"` and `"count"`.
    - **1.20.1:** Uses `"item"` and implicit count.


### Solar Sail Implementation (NeoForge 1.20.1)

#### 1. Imports & Dependencies
- **Problem:** `com.simibubi.create.foundation.utility.Iterate` and placement packages were missing in 1.20.1 dev environment.
- **Solution:** Switched to using `net.createmod.catnip.data.Iterate` and `net.createmod.catnip.placement` packages. These are available in Create 6.0.8+.

#### 2. Placement Helper
- **Restored:** The `PlacementHelper` inner class was restored to match the 1.21.1 implementation.
- **Why:** It handles the logic for extending sail rows/columns easily.
- **Logic:**
    - `getItemPredicate`: Checks if the item held is a Solar Sail.
    - `getStatePredicate`: Checks if the target block is a Solar Sail.
    - `getOffset`: Uses `IPlacementHelper.orderedByDistanceExceptAxis` to find the best placement position.

#### 3. VoxelShapes
- **Restored:** `AllShapes.SAIL` is available in 1.20.1 and works correctly.
- **Switch:** Re-implemented the `getShape` method to use `AllShapes.SAIL.get(facing)`.

#### 4. Dyeing Logic
- **Restored:** The recursive dyeing logic (modifying connected sails of the same facing) was successfully ported using `Catnip` iterators.


