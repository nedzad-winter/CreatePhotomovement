# Current Development Status

## Solar Windmill Bearing - SU Doubling Bug FIX ✅

**Status:** FIXED (2026-02-04)

### Problem
The Solar Windmill Bearing was showing **6144 SU** instead of the correct **3072 SU** after world reload. This was a double-counting issue in Create's `KineticNetwork` capacity tracking system.

### Root Cause
Create's `KineticNetwork` has a complex capacity tracking mechanism:
1. `initFromTE(capacity, ...)` sets `unloadedCapacity` from NBT data on world load
2. `addSilently()` also calls `calculateAddedStressCapacity()` and adds it to `sources`
3. `calculateCapacity()` returns `presentCapacity + unloadedCapacity`
4. If both have our capacity value, it **DOUBLES**

### Solution
Three-part fix in `SolarWindmillBearingBlockEntity.java`:

1. **Override `write()`:** Zero `capacity` and `lastCapacityProvided` BEFORE super.write() to prevent stale data in NBT

2. **Override `initialize()`:** Force network to re-initialize by setting `net.initialized = false` before super.initialize() runs. This ensures the network uses our zeroed values from NBT.

3. **Override `updateFromNetwork()`:** Recalculate the correct capacity ourselves and pass that to super, ignoring the network's potentially-wrong value.

### Files Modified
- `SolarWindmillBearingBlockEntity.java`
- `SolarBearingContraption.java`

### Testing
- ✅ Fresh assembly: Shows correct 3072 SU
- ✅ After world reload: Shows correct 3072 SU
- ✅ Sail counts persist correctly (16 solar + 16 regular)

### Porting Status
- [x] 1.21.1 NeoForge - **FIXED**
- [ ] 1.20.1 NeoForge - Pending
- [ ] 1.20.1 Forge - Pending
- [ ] Fabric - Pending
