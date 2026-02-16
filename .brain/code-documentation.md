# Create Photomovement - Complete Code Documentation (NeoForge 1.21.1)

**Last Updated:** 2026-02-12
**Primary Version:** NeoForge 1.21.1
**Source:** `neoforge/1211/src/main/java/com/createphotomovement/`

This document covers every Java source file in the mod with per-class, per-method detail.

---

## Table of Contents

1. [Entry Points](#1-entry-points)
2. [Registry Classes](#2-registry-classes)
3. [Configuration System](#3-configuration-system)
4. [Solar Generator System](#4-solar-generator-system)
5. [Solar Windmill System](#5-solar-windmill-system)
6. [Ponder System](#6-ponder-system)
7. [Version Differences and Porting](#7-version-differences-and-porting)
8. [Design Decisions](#8-design-decisions)

---

## 1. Entry Points

### 1.1 CreatePhotomovement.java (38 lines)

**File:** Main mod entry point, server-side initialization.
**Annotation:** `@Mod(CreatePhotomovement.MOD_ID)`
**Class:** `CreatePhotomovement`

**Fields:**
- `MOD_ID = "createphotomovement"` -- unique identifier used everywhere.

**Constructor:** `CreatePhotomovement(IEventBus modEventBus, ModContainer modContainer)`
NeoForge 1.21.1 injects both `IEventBus` and `ModContainer` directly into the constructor. This is different from 1.20.1 which uses `FMLJavaModLoadingContext.get().getModEventBus()`.

What it does, in order:
1. `AllBlocks.BLOCKS.register(modEventBus)` -- queues all block registrations.
2. `AllItems.ITEMS.register(modEventBus)` -- queues all item registrations.
3. `AllCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus)` -- queues creative tab.
4. `AllBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus)` -- queues block entity types.
5. `AllContraptionTypes.register(modEventBus)` -- queues contraption types.
6. `PMConfigs.register(ModLoadingContext.get(), modContainer)` -- loads config files.
7. **Registers AttachedCheck for SolarSailBlock.** This is the most important line. Without it, Create will NOT pick up Solar Sails during contraption assembly. The lambda checks: if the block is a `SolarSailBlock`, it returns `CheckResult.of(true)` when the movement direction axis differs from the sail's FACING axis. Otherwise returns `PASS` (let Create handle it).

**Why the AttachedCheck matters:**
Create's contraption assembly walks outward from the bearing. For each neighboring block, it asks "is this block attached?" Standard Create only does `instanceof SailBlock` checks. Our `SolarSailBlock` extends `WrenchableDirectionalBlock`, not `SailBlock`, so Create ignores it entirely. The AttachedCheck is the API hook that fixes this.

---

### 1.2 CreatePhotomovementClient.java (35 lines)

**File:** Client-side initialization for renderers and Ponder.
**Annotation:** `@EventBusSubscriber(modid = MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)`
**Class:** `CreatePhotomovementClient`

This class is never loaded on a dedicated server. `Dist.CLIENT` ensures it only runs on clients.

**Methods:**

`onClientSetup(FMLClientSetupEvent event)`:
- Annotated with `@SubscribeEvent`.
- Calls `PonderIndex.addPlugin(new PhotomovementPonderPlugin())` to register in-game tutorials.

`registerRenderers(EntityRenderersEvent.RegisterRenderers event)`:
- Annotated with `@SubscribeEvent`.
- Registers 5 block entity renderers:
  - `SOLAR_GENERATOR` -> `SolarGeneratorRenderer` (renders kinetic shaft on AXIS).
  - `HORIZONTAL_SOLAR_GENERATOR` -> `HorizontalSolarGeneratorRenderer` (renders half shaft on back side).
  - `ADV_SOLAR_GENERATOR` -> `SolarGeneratorRenderer` (reuses same renderer, advanced is visually identical shaft-wise).
  - `HORZ_ADV_SOLAR_GENERATOR` -> `HorizontalSolarGeneratorRenderer` (same reuse).
  - `SOLAR_WINDMILL_BEARING` -> `BearingRenderer` (uses Create's built-in bearing renderer, no custom rendering needed).

---

## 2. Registry Classes

### 2.1 AllBlocks.java (750 lines)

**File:** Registers every block in the mod.
**Class:** `AllBlocks`

**Registry:** `DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID)`

This file is large because each of the 4 generator types has 17 variants (1 base + 16 colors) = 68 generator blocks, plus 17 solar sails + 1 solar windmill bearing = 86 total blocks.

**Registration pattern (repeated 86 times):**
```java
public static final DeferredBlock<SolarGeneratorBlock> SOLAR_GENERATOR =
    BLOCKS.register("solar_generator",
        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOD)
            .strength(1.5F)
            .sound(SoundType.WOOD)
            .noOcclusion()));
```

**Key properties:**
- Generators: `strength(1.5F)`, `SoundType.WOOD`, `noOcclusion()` (lets light through).
- Solar Sails: `strength(0.8F)`, `SoundType.WOOL`, `noOcclusion()`. Uses `SolarSailBlock.withCanvas(props, DyeColor)` factory.
- Solar Windmill Bearing: `strength(3.5F)`, `SoundType.WOOD`.
- Colored variants use the appropriate `MapColor` for their dye color (for map display).

**Block count summary:**

| Type | Count |
|---|---|
| SolarGeneratorBlock | 17 (1 base + 16 colored) |
| HorizontalSolarGeneratorBlock | 17 |
| AdvSolarGeneratorBlock | 17 |
| HorzAdvSolarGeneratorBlock | 17 |
| SolarSailBlock | 17 |
| SolarWindmillBearingBlock | 1 |
| **Total** | **86** |

---

### 2.2 AllItems.java (358 lines)

**File:** Registers the item form of every block.
**Class:** `AllItems`

**Registry:** `DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID)`

**Pattern:**
```java
public static final DeferredItem<BlockItem> SOLAR_GENERATOR =
    ITEMS.registerSimpleBlockItem("solar_generator", AllBlocks.SOLAR_GENERATOR);
```

Every block gets a corresponding `BlockItem` via `registerSimpleBlockItem`. The item name must match the block name exactly. There are 86 items total, one for each block.

---

### 2.3 AllBlockEntityTypes.java (124 lines)

**File:** Registers the 5 block entity types.
**Class:** `AllBlockEntityTypes`

**Registry:** `DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES`

Each registration uses `BlockEntityType.Builder.of()` with:
- A factory lambda: `(pos, state) -> new XBlockEntity(AllBlockEntityTypes.X.get(), pos, state)`
- A list of ALL blocks that use this block entity type (17 blocks per type for generators).

**Registered types:**

| Name | BE Class | Valid Blocks |
|---|---|---|
| `horizontal_solar_generator` | `HorizontalSolarGeneratorBlockEntity` | 17 horizontal generator blocks |
| `solar_generator` | `SolarGeneratorBlockEntity` | 17 vertical generator blocks |
| `adv_solar_generator` | `AdvSolarGeneratorBlockEntity` | 17 advanced vertical blocks |
| `horz_adv_solar_generator` | `HorzAdvSolarGeneratorBlockEntity` | 17 advanced horizontal blocks |
| `solar_windmill_bearing` | `SolarWindmillBearingBlockEntity` | 1 bearing block |

**Why all 17 color variants are listed per type:**
Minecraft validates that a block entity type is only allowed on blocks it was registered for. If you place a `BLUE_SOLAR_GENERATOR` but the BE type only lists `SOLAR_GENERATOR`, you get "Invalid block entity" crashes. All color variants share the same logic, so they share one BE type.

---

### 2.4 AllContraptionTypes.java (23 lines)

**File:** Registers the custom contraption type for the solar bearing.
**Class:** `AllContraptionTypes`

**Registry:** `DeferredRegister<ContraptionType> CONTRAPTION_TYPES` using `CreateRegistries.CONTRAPTION_TYPE`.

**Registration:**
```java
public static final DeferredHolder<ContraptionType, ContraptionType> SOLAR_BEARING =
    CONTRAPTION_TYPES.register("solar_bearing",
        () -> new ContraptionType(SolarBearingContraption::new));
```

`SolarBearingContraption::new` is the factory that Create calls when deserializing a saved contraption. Without this registration, a saved contraption would be deserialized as a generic `BearingContraption`, losing all solar sail data.

**Method:** `register(IEventBus)` -- called from main class to attach to event bus.

---

### 2.5 AllCreativeTabs.java (121 lines)

**File:** Defines the creative mode tab for the mod.
**Class:** `AllCreativeTabs`

**Registry:** `DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS`

Creates one tab with:
- Title: `Component.translatable("itemGroup.createphotomovement")` (localized).
- Icon: `AllItems.SOLAR_GENERATOR.get().getDefaultInstance()`.
- Items: All 86 items added via `output.accept()`, ordered as:
  1. Base generators (solar, horizontal, adv, horz_adv).
  2. Colored generators in Minecraft dye order (white, orange, magenta...).
  3. Solar windmill bearing.
  4. Solar sails (base + 16 colors).

---

## 3. Configuration System

### 3.1 PMConfigs.java (67 lines)

**File:** Central config manager.
**Class:** `PMConfigs`
**Annotation:** `@EventBusSubscriber(modid = MOD_ID, bus = Bus.MOD)`

**Fields:**
- `CONFIGS`: `EnumMap<ModConfig.Type, ConfigBase>` -- stores all config categories.
- `server`: `PMServer` -- the server-side config instance.

**Methods:**

`server()` -- returns the `PMServer` singleton.

`register(Supplier<T> factory, ModConfig.Type side)`:
- Uses `ModConfigSpec.Builder` to build a NeoForge config spec from our `ConfigBase`.
- `config.registerAll(builder)` iterates all `ConfigInt`/`ConfigBool` fields and adds them to the spec.
- Stores the result in `CONFIGS`.

`register(ModLoadingContext, ModContainer)`:
- Called from main class.
- Creates the `PMServer` config.
- Iterates `CONFIGS` and calls `container.registerConfig()` for each.

`onLoad(ModConfigEvent.Loading)` / `onReload(ModConfigEvent.Reloading)`:
- `@SubscribeEvent` handlers.
- Match the event's config spec to our stored configs and call `onLoad()`/`onReload()`.

### 3.2 PMServer.java (16 lines)

**File:** Server-side config values.
**Class:** `PMServer extends ConfigBase`

**Fields:**
- `generationSpeed`: `ConfigInt`, default `16`, min `1`. Controls RPM output of all solar generators.
- `stressCapacity`: `ConfigInt`, default `16`, min `1`. Controls SU per RPM of solar generators.

**Method:** `getName()` returns `"server"` (used for file naming: `createphotomovement-server.toml`).

---

## 4. Solar Generator System

### 4.1 SolarGeneratorBlock.java (154 lines)

**File:** Block class for vertical solar generators.
**Class:** `SolarGeneratorBlock extends RotatedPillarKineticBlock implements IBE<SolarGeneratorBlockEntity>, IWrenchable`

**Inheritance explained:**
- `RotatedPillarKineticBlock`: Create's base for blocks that rotate on an axis (like shafts). The block has an `AXIS` property (X, Y, or Z).
- `IBE<T>`: Create's interface that links a block to its block entity class and type.
- `IWrenchable`: Create's interface that enables wrench interaction.

**Fields:**
- `COLOR_TO_BLOCK`: `Map<DyeColor, Supplier<Block>>` -- maps all 16 dye colors to their colored block variant. Populated in a `static {}` initializer block.

**Methods:**

`SolarGeneratorBlock(Properties)` -- constructor, passes to super.

`getBlockEntityType()` -- returns `AllBlockEntityTypes.SOLAR_GENERATOR.get()`. Required by `IBE`.

`getBlockEntityClass()` -- returns `SolarGeneratorBlockEntity.class`. Required by `IBE`.

`hasShaftTowards(LevelReader, BlockPos, BlockState, Direction)`:
- Returns `true` when `face.getAxis() == state.getValue(AXIS)`.
- This means the block has shafts on BOTH ends of its axis. If AXIS is X, shafts on EAST and WEST.

`getStateForPlacement(BlockPlaceContext)`:
- Normal: AXIS = player's horizontal direction axis (face the player).
- Shift held: AXIS = clockwise of that (90 degrees rotated).
- Never sets Y axis -- keeps the panel facing upward.

`useItemOn(ItemStack, BlockState, Level, BlockPos, Player, InteractionHand, BlockHitResult)`:
- **Dye interaction.** If the held item is a `DyeItem`:
  1. Look up the target block from `COLOR_TO_BLOCK`.
  2. If already that color, return `PASS_TO_DEFAULT_BLOCK_INTERACTION`.
  3. Server-side only (`!level.isClientSide`):
     - Read current AXIS, create new blockstate preserving AXIS.
     - `level.setBlock(pos, newState, 3)` -- flag 3 = update neighbors + send to clients.
     - Play `DYE_USE` sound.
     - Consume 1 dye (unless creative).
  4. Return `sidedSuccess(level.isClientSide)`.

`getRotationAxis(BlockState)` -- returns `state.getValue(AXIS)`. Used by Create's kinetic system.

`onWrenched(BlockState, UseOnContext)`:
- Server-side only.
- Toggles AXIS between X and Z (never Y).
- Plays `ITEM_FRAME_ROTATE_ITEM` sound.

`getShape(BlockState, BlockGetter, BlockPos, CollisionContext)` -- returns `Shapes.block()` (full cube).

---

### 4.2 SolarGeneratorBlockEntity.java (97 lines)

**File:** Block entity (the "brain") for vertical solar generators.
**Class:** `SolarGeneratorBlockEntity extends GeneratingKineticBlockEntity`

`GeneratingKineticBlockEntity` is Create's base for blocks that produce rotational power. It handles network integration, speed propagation, and stress capacity reporting.

**Constructor:** `(BlockEntityType<?>, BlockPos, BlockState)` -- standard, passes to super.

**Methods:**

`onLoad()`:
- Called when chunk loads.
- Forces `updateGeneratedRotation()` on server side.
- This syncs the block with the kinetic network immediately, preventing "ghost power" where the network thinks the generator is off.

`getGeneratedSpeed()`:
- Returns `0` if `canGeneratePower()` is false.
- Otherwise returns `PMConfigs.server().generationSpeed.get()` (default 16 RPM).
- If raining at `worldPosition.above()`, halves the speed (8 RPM).
- Rain check uses `level.isRainingAt()` which considers biome + exposure.

`calculateAddedStressCapacity()`:
- Returns `PMConfigs.server().stressCapacity.get()` (default 16 SU).
- This is SU per RPM. Create multiplies by speed internally.

`canGeneratePower()` -- the core environmental check:
1. `level.canSeeSky(worldPosition.above())` -- must have unbroken vertical path to sky. Glass passes this check, solid blocks fail.
2. Sky light - sky darken >= 12. `getBrightness(LightLayer.SKY, pos)` returns raw sky light (15 in open air). `getSkyDarken()` returns 0 at noon, rises to 11 at midnight. So effective light drops below 12 at roughly 6 PM and rises above 12 at roughly 6 AM. Thunder also increases sky darken.
3. Block directly above must NOT be: `SnowLayerBlock`, `CarpetBlock`, `Blocks.MOSS_CARPET`, `Blocks.SNOW`. These are special cases because they pass `canSeeSky` but still physically cover the panel.
4. Block above must have `getLightBlock() == 0`. This catches any remaining light-blocking blocks. Glass = 0, stone = 15.

`tick()`:
- Called 20 times per second.
- Server-side only.
- Compares current `speed` with `getGeneratedSpeed()`. If they differ (magnitude or sign), calls `updateGeneratedRotation()`.
- The sign check (`Math.signum`) detects direction reversal.

---

### 4.3 HorizontalSolarGeneratorBlock.java (151 lines)

**File:** Block class for horizontal solar generators.
**Class:** `HorizontalSolarGeneratorBlock extends HorizontalKineticBlock implements IBE<HorizontalSolarGeneratorBlockEntity>, IWrenchable`

Structurally identical pattern to `SolarGeneratorBlock` but uses `HORIZONTAL_FACING` (Direction) instead of `AXIS`.

**Key differences from vertical:**

`hasShaftTowards()` -- returns `true` only for `face == state.getValue(HORIZONTAL_FACING).getOpposite()`. Only ONE shaft connection (the back side), not two.

`getRotationAxis()` -- returns `state.getValue(HORIZONTAL_FACING).getAxis()`.

`getStateForPlacement()`:
- Normal: faces toward the player (panel visible from front).
- Shift: faces away from player.

`rotate(BlockState, Rotation)` -- rotates the HORIZONTAL_FACING by the given rotation (used by structure blocks, etc).

`useItemOn()` -- same dye logic as vertical, but preserves `HORIZONTAL_FACING` instead of `AXIS`. Falls through to `super.useItemOn()` instead of `PASS_TO_DEFAULT_BLOCK_INTERACTION` (lets Create handle wrench via super).

`onPlace(BlockState, Level, BlockPos, BlockState, boolean)`:
- After placement, calls `withBlockEntityDo(level, pos, HorizontalSolarGeneratorBlockEntity::forceUpdate)`.
- This triggers an immediate capacity recalculation so the generator starts working instantly.

---

### 4.4 HorizontalSolarGeneratorBlockEntity.java (209 lines)

**File:** Block entity for horizontal solar generators with time-of-day capacity scaling.
**Class:** `HorizontalSolarGeneratorBlockEntity extends GeneratingKineticBlockEntity`

This is significantly more complex than the vertical version because capacity changes dynamically based on facing direction and time of day.

**Fields:**
- `currentStressCapacity`: `float` -- the current SU value, changes over time.
- `updateTimer`: `int` -- counts ticks between capacity recalculations.
- `warmup`: `int = 10` -- delay before first capacity check after load.

**Constructor:** Initializes `currentStressCapacity` from config.

**Methods:**

`forceUpdate()` -- resets timer and calls `updateStressCapacity()`. Called by the block on placement.

`getGeneratedSpeed()`:
- Same logic as vertical but checks rain at `worldPosition.relative(HORIZONTAL_FACING)` (the front face position, not above).

`calculateAddedStressCapacity()`:
- Returns `currentStressCapacity` (dynamic, not static config value).
- Sets `this.lastCapacityProvided = capacity` -- this is vital. Create's `KineticNetwork` uses `lastCapacityProvided` to calculate delta when capacity changes. If this is not set, the network does not properly update.

`tick()`:
- Server-side only.
- Warmup countdown: decrements `warmup`, calls `updateStressCapacity()` when it hits 0, returns early until then.
- After warmup: increments `updateTimer`, calls `updateStressCapacity()` every 200 ticks (10 seconds).

`updateStressCapacity()` -- the core time-based logic:
1. If `canGeneratePower()` is false, sets capacity to 0 and updates network.
2. Gets the block's `HORIZONTAL_FACING`.
3. Reads config base capacity and sets minimum at 8 SU.
4. **Distant obstruction check**: scans blocks 2-10 away in the facing direction. If any has `getLightBlock() > 0`, caps capacity at minimum. This simulates "something is blocking the sun's path at an angle."
5. **Time-of-day curve** (if no distant obstruction):
   - Gets `level.getDayTime() % 24000`.
   - Clamps to `[0, 12000]` for the daylight half.
   - Calculates `ratio = daylightTime / 12000.0` (0.0 = sunrise, 1.0 = sunset).
   - **EAST facing**: `factor = (1 - ratio)^2` -- quadratic curve, peaks at sunrise (64 SU), minimum at sunset (8 SU).
   - **WEST facing**: `factor = ratio^2` -- quadratic curve, peaks at sunset, minimum at sunrise.
   - **NORTH/SOUTH**: always minimum (8 SU). These directions never face the sun path.
   - Capacity = `min + (peak - min) * factor` where peak = `4 * base` (default 64 SU).
6. Rounds to nearest integer.
7. If capacity changed (delta > 0.01), calls `updateGeneratedRotation()` and `notifyUpdate()`.

`canGeneratePower()`:
- Checks `HORIZONTAL_FACING`'s front position.
- Sky light - sky darken >= 12 at front position.
- Front block must have `getLightBlock() == 0`.
- Does NOT check `canSeeSky` -- horizontal generators only need light, not direct sky line.
- Does NOT check for snow/carpet (those are above-block issues).

`write()` / `read()`:
- **Client packets only**: saves/loads `currentStressCapacity` as `"CurrentStressCapacity"` float.
- Server side: does NOT save `currentStressCapacity` to disk. On reload, it starts at 0 and recalculates after warmup. This prevents stale capacity values from persisting.

---

### 4.5 AdvSolarGeneratorBlock.java (103 lines)

**File:** Advanced vertical solar generator block.
**Class:** `AdvSolarGeneratorBlock extends SolarGeneratorBlock`

Extends `SolarGeneratorBlock` and overrides:
- `COLOR_TO_BLOCK` -- maps to `AllBlocks.*_ADV_SOLAR_GENERATOR` variants.
- `getBlockEntityType()` -- returns `AllBlockEntityTypes.ADV_SOLAR_GENERATOR.get()`.
- `useItemOn()` -- identical dye logic but uses the advanced color map.

All other methods (shaft, placement, wrench) are inherited from `SolarGeneratorBlock`.

### 4.6 AdvSolarGeneratorBlockEntity.java (28 lines)

**File:** Advanced vertical solar generator block entity.
**Class:** `AdvSolarGeneratorBlockEntity extends SolarGeneratorBlockEntity`

Only overrides `getGeneratedSpeed()`:
- Returns `PMConfigs.server().generationSpeed.get() * 2` (default 32 RPM).
- Still halves during rain (16 RPM).
- `canGeneratePower()` is inherited unchanged.
- `calculateAddedStressCapacity()` is inherited unchanged (same 16 SU).

### 4.7 HorzAdvSolarGeneratorBlock.java (105 lines)

**File:** Advanced horizontal solar generator block.
**Class:** `HorzAdvSolarGeneratorBlock extends HorizontalSolarGeneratorBlock`

Same pattern as `AdvSolarGeneratorBlock`: overrides `COLOR_TO_BLOCK`, `getBlockEntityType()`, and `useItemOn()` to point to advanced horizontal variants.

### 4.8 HorzAdvSolarGeneratorBlockEntity.java (29 lines)

**File:** Advanced horizontal solar generator block entity.
**Class:** `HorzAdvSolarGeneratorBlockEntity extends HorizontalSolarGeneratorBlockEntity`

Only overrides `getGeneratedSpeed()`:
- Returns `PMConfigs.server().generationSpeed.get() * 2` (default 32 RPM).
- Rain check uses `worldPosition.relative(HORIZONTAL_FACING)`.

---

### 4.9 SolarGeneratorRenderer.java (27 lines)

**File:** Client-side renderer for vertical solar generators.
**Class:** `SolarGeneratorRenderer extends KineticBlockEntityRenderer<SolarGeneratorBlockEntity>`

**Method:** `renderSafe()`:
1. `shaft(getRotationAxisOf(be))` -- gets the shaft model for the block's rotation axis.
2. `CachedBuffers.block(shaftState)` -- gets a cached render buffer for the shaft.
3. `standardKineticRotationTransform(superBuffer, be, light)` -- applies Create's rotation animation based on speed and partial ticks.
4. `superBuffer.renderInto(ms, buffer.getBuffer(RenderType.solid()))` -- renders the shaft in the solid render layer.

### 4.10 HorizontalSolarGeneratorRenderer.java (36 lines)

**File:** Client-side renderer for horizontal solar generators.
**Class:** `HorizontalSolarGeneratorRenderer extends KineticBlockEntityRenderer<HorizontalSolarGeneratorBlockEntity>`

**Method:** `renderSafe()`:
1. Gets `HORIZONTAL_FACING` from blockstate.
2. `shaftDirection = facing.getOpposite()` -- shaft comes out the back.
3. `CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, blockState, shaftDirection)` -- gets a HALF shaft model (not full shaft, only one side visible).
4. Applies kinetic rotation and renders.

---

## 5. Solar Windmill System

### 5.1 SolarSailBlock.java (341 lines)

**File:** Solar Sail block, the primary sail type for the Solar Windmill Bearing.
**Class:** `SolarSailBlock extends WrenchableDirectionalBlock`

**Why not extend Create's SailBlock?** Create's `SailBlock` has internal behaviors, instanceof checks, and tag logic that would conflict with our separate counting system. `WrenchableDirectionalBlock` gives us the `FACING` property and wrench support without baggage.

**Inner enum: `GlassColor`** (17 values):
- `CLEAR, WHITE, ORANGE, MAGENTA, LIGHT_BLUE, YELLOW, LIME, PINK, GRAY, LIGHT_GRAY, CYAN, PURPLE, BLUE, BROWN, GREEN, RED, BLACK`
- Implements `StringRepresentable` for blockstate serialization.
- Static method `fromDyeColor(DyeColor)` maps Minecraft dye colors to glass colors.

**Blockstate property:** `GLASS_COLOR` -- `EnumProperty<GlassColor>`. Stored in blockstate JSON.

**Fields:**
- `placementHelperId` -- static int from `PlacementHelpers.register(new PlacementHelper())`. Enables click-to-extend sail arrays.
- `color` -- the `DyeColor` this sail was created with.

**Factory:** `withCanvas(Properties, DyeColor)` -- static factory used in `AllBlocks`.

**Constructor:** Sets default state with `FACING = UP`, `GLASS_COLOR = CLEAR`.

**Methods:**

`createBlockStateDefinition()` -- adds `GLASS_COLOR` to the state definition (on top of `FACING` from super).

`getStateForPlacement()` -- gets super's state and flips the facing to opposite. This means the sail faces AWAY from the surface you click.

`useItemOn()`:
- If holding a `DyeItem`: plays DYE_USE sound and calls `applyDye()`.
- Otherwise returns `PASS_TO_DEFAULT_BLOCK_INTERACTION`.

`useWithoutItem()`:
- If not sneaking, checks the PlacementHelper. If holding a SolarSailBlock item, extends the sail array via `PlacementOffset`.

`applyDye(BlockState, Level, BlockPos, Vec3, DyeColor)` -- **three-tier dye system:**
1. **Tier 1: Dye self.** Gets the colored sail block via `getSolarSailForColor()`, copies blockstate properties. If the clicked block changes color, done.
2. **Tier 2: Dye adjacent.** If self is already the target color, find the nearest adjacent SolarSailBlock with the same facing but different color. Dye that one block. Uses `orderedByDistanceExceptAxis()` to prioritize closer neighbors.
3. **Tier 3: BFS flood-fill.** If no adjacent block changed, flood-fill ALL connected sails of the same facing. Uses a `List<BlockPos>` frontier and `Set<BlockPos>` visited. Timeout at 100 blocks to prevent lag on massive structures.

`getSolarSailForColor(DyeColor)` -- switch expression mapping 16 dye colors to their `AllBlocks.*_SOLAR_SAIL.get()`.

`getShape()` -- returns `AllShapes.SAIL.get(facing)`. Uses Create's built-in thin sail shape.

`getCollisionShape()` -- delegates to `getShape()`.

`getCloneItemStack()` -- picks the base `SOLAR_SAIL` if super returns empty (for colored variants that might not have items registered with the right metadata).

`fallOn()` -- passes `0` fall distance to super, implementing soft landing.

`updateEntityAfterFallOn()` -- if entity is NOT suppressing bounce, calls `bounce()`.

`bounce(Entity)` -- reverses Y velocity * 0.26 * (1.0 for living entities, 0.8 for others). Mimics bed bounce.

`isPathfindable()` -- returns `false`. Mobs cannot pathfind through sails.

`getColor()` -- returns the `DyeColor` field.

**Inner class: `PlacementHelper` implements `IPlacementHelper`:**
- `getItemPredicate()` -- matches any `BlockItem` whose block is `SolarSailBlock`.
- `getStatePredicate()` -- matches any blockstate that is `SolarSailBlock`.
- `getOffset()` -- finds the nearest replaceable neighbor along a direction perpendicular to the sail's FACING axis.

---

### 5.2 SolarBearingContraption.java (225 lines)

**File:** Custom contraption that tracks solar vs regular sails separately.
**Class:** `SolarBearingContraption extends BearingContraption`

**Fields:**
- `solarSailBlocks = 0` -- count of SolarSailBlock blocks.
- `regularSailBlocks = 0` -- count of regular sail blocks (Create's sails).
- `hasSkyAccess = false` -- cached sky check from assembly.
- `LOGGER` -- SLF4J logger for debug output.

**Methods:**

`getType()` -- returns `AllContraptionTypes.SOLAR_BEARING.get()`. This is what Create uses to serialize/deserialize the contraption. Without this, reloading maps the contraption to generic `BearingContraption`.

`assemble(Level, BlockPos)`:
1. Resets counters.
2. Calls `super.assemble()` which walks the structure and calls `addBlock()` for each block.
3. If successful, calls `checkSkyAccess()` and caches result.
4. Logs final counts.

`addBlock(Level, BlockPos, Pair<StructureBlockInfo, BlockEntity>)`:
1. Computes `localPos = pos.subtract(anchor)`.
2. Checks `isNew = !getBlocks().containsKey(localPos)`.
3. If new and `instanceof SolarSailBlock`, increments `solarSailBlocks`.
4. If new and `isSail(state)` (but not solar), increments `regularSailBlocks`.
5. Calls `super.addBlock()` which handles the actual block storage.

`checkSkyAccess(Level, BlockPos)`:
- Scans 5x5 grid centered on bearing at Y+1 (25 positions).
- Returns `true` if ANY position passes `world.canSeeSky()`.
- Tolerates shaft/support blocks directly above the bearing.

`writeNBT(HolderLookup.Provider, boolean spawnPacket)`:
- **CRITICAL: Sets `this.sailBlocks = 0` BEFORE calling `super.writeNBT()`.** This makes the parent class serialize 0 sails to NBT, preventing double-counting.
- After super, appends: `SolarSails` (int), `RegularSails` (int), `HasSkyAccess` (boolean).
- Logs on disk save (`!spawnPacket`).

`readNBT(Level, CompoundTag, boolean spawnData)`:
- Calls `super.readNBT()` first (populates the blocks map).
- **RECALCULATES** sail counts by iterating ALL blocks in `getBlocks().values()`:
  - If `instanceof SolarSailBlock` -> solar.
  - Else if `isSail(state)` -> regular.
- Sets `this.sailBlocks = 0` again.
- Reads `HasSkyAccess` from tag.
- **Why recalculate instead of trusting stored integers?** NBT can desync if a crash interrupted saving, or if a block was added/removed externally. Iterating blocks is ground truth.

`isSail(BlockState)`:
- Returns `true` for `instanceof SolarSailBlock`.
- Returns `true` for blocks tagged `AllTags.AllBlockTags.WINDMILL_SAILS.tag`.
- This mirrors Create's internal check.

Getters: `getSolarSailBlocks()`, `getRegularSailBlocks()`, `hasSkyAccess()`.
Setter: `setSailBlocks(int)` -- exposes the parent's `sailBlocks` field for zeroing.

---

### 5.3 SolarWindmillBearingBlock.java (41 lines)

**File:** The bearing block itself.
**Class:** `SolarWindmillBearingBlock extends WindmillBearingBlock`

**Methods:**

`getBlockEntityType()` -- returns `AllBlockEntityTypes.SOLAR_WINDMILL_BEARING.get()`.

`getTicker(Level, BlockState, BlockEntityType<T>)`:
- Returns a custom lambda that:
  1. Casts `be` to `SolarWindmillBearingBlockEntity`.
  2. Calls `solarBe.tick()` (standard kinetic tick from `SmartBlockEntity`).
  3. Calls `solarBe.solarTick()` (our custom solar condition check).
- This double-call pattern is necessary because `WindmillBearingBlock`'s default ticker only calls `tick()`. We need `solarTick()` as well.

---

### 5.4 SolarWindmillBearingBlockEntity.java (400 lines)

**File:** The most complex class in the mod. Handles all solar power calculation, network integration, and persistence.
**Class:** `SolarWindmillBearingBlockEntity extends WindmillBearingBlockEntity`

**Fields:**
- `regularSailCount = 0` -- cached count of regular sails from contraption.
- `solarSailCount = 0` -- cached count of solar sails from contraption.
- `hasSkyAccess = false` -- cached from contraption at assembly.
- `lastSolarMultiplier = -1` -- previous multiplier value, for change detection.
- `warmup = 10` -- ticks to wait after load before recalculating.

**Methods (in logical order):**

`tick()` -- override adds `solarTick()` call on server side (also called from `getTicker`).

`solarTick()`:
- Exits early on client or if level is null.
- Warmup countdown: decrements, calls `updateGeneratedRotation()` when warmup reaches 0.
- After warmup: gets `getSolarMultiplier()`. If it differs from `lastSolarMultiplier` by > 0.001, updates the kinetic network.

`assemble()` -- the full assembly override:
1. Validates bearing block.
2. Gets facing direction.
3. Creates `SolarBearingContraption(true, direction)`.
4. Calls `contraption.assemble(level, worldPosition)`.
5. Caches `solarSailCount`, `regularSailCount`, `hasSkyAccess` from contraption.
6. Awards Create's WINDMILL and WINDMILL_MAXED advancements.
7. Removes blocks from world, creates `ControlledContraptionEntity`, positions it, sets rotation axis, adds to world.
8. Sets `running = true`, `angle = 0`, sends data, updates rotation.

`updateGeneratedRotation()`:
- If `movedContraption` exists and is a `SolarBearingContraption`:
  - Updates cached counts from contraption.
  - **Zeros `sbc.setSailBlocks(0)`** to prevent parent's capacity contribution.
- If regular `BearingContraption`: uses parent's `getSailBlocks()` as regular count, no solar.
- If no contraption: resets all counts to 0.
- Calls `super.updateGeneratedRotation()`.

`getGeneratedSpeed()`:
- Returns 0 if `!running` or `movedContraption == null`.
- `totalSails = regularSailCount + solarSailCount`.
- `rpm = totalSails / sailsPerRPM` (from Create config, default 8).
- Clamped to [1, 16].
- Multiplied by `getAngleSpeedDirection()` for direction.
- **RPM is NOT affected by solar conditions.** Solar only affects SU.

`getSolarMultiplier()`:

| Condition | Multiplier | Why |
|---|---|---|
| `!hasSkyAccess` | 1.0 | Cannot benefit from sun |
| `isNight()` (13000-23000 ticks) | 1.0 | No sunlight |
| `isThundering()` | 1.0 | Sky completely dark |
| `isRaining()` (no thunder) | 1.5 | Reduced sunlight |
| Clear day + sky | 2.0 | Full solar bonus |

`calculateAddedStressCapacity()`:
- Returns 0 if `!running` or no contraption.
- `sailsPerBracket = AllConfigs.server().kinetics.windmillSailsPerRPM.get()` (default 8).
- `normalBrackets = regularSailCount / sailsPerBracket`, `normalSU = normalBrackets * 512`.
- `solarBrackets = solarSailCount / sailsPerBracket`, `solarSU = solarBrackets * 512 * getSolarMultiplier()`.
- `totalSU = normalSU + solarSU`.
- `rpm = max(1, totalSails / sailsPerBracket)`.
- Returns `totalSU / rpm`. Create multiplies this by speed internally, so dividing by RPM ensures the displayed SU matches.
- Sets `lastCapacityProvided = result`.

`write(CompoundTag, HolderLookup.Provider, boolean clientPacket)`:
- **Client packets:** Saves `RegularSails`, `SolarSails`, `HasSkyAccess` for display.
- **Server save:** Zeros `this.capacity` and `this.lastCapacityProvided` BEFORE `super.write()`. This ensures NBT stores 0 for capacity, preventing the double-counting bug on reload.

`read(CompoundTag, HolderLookup.Provider, boolean clientPacket)`:
- Calls `super.read()` first.
- **Client:** Loads sail counts and sky access.
- **Server:** Resets all counts to 0. Zeros `lastCapacityProvided` and `capacity`. Sail counts will be re-populated from the contraption during warmup.

`initialize()`:
- If on a network and server-side: sets `network.initialized = false` before `super.initialize()`.
- This forces the `KineticNetwork` to call `initFromTE()` again with our zeroed values instead of stale cached values.

`updateFromNetwork(float maxStress, float currentStress, int networkSize)`:
- Calculates `correctTotalSU = calculateAddedStressCapacity() * abs(getGeneratedSpeed())`.
- Passes `correctTotalSU` as `maxStress` to super instead of the network's potentially wrong value.

`addToGoggleTooltip()`:
- If not running and super did not add anything, shows "0su" generator stats so the goggles display is not blank.

Helper methods: `isNight()`, `isRaining()`, `isThundering()`, getters for counts/multiplier.

---

## 6. Ponder System

### 6.1 PhotomovementPonderPlugin.java (44 lines)

**Class:** `PhotomovementPonderPlugin implements PonderPlugin`

`getModId()` -- returns `CreatePhotomovement.MOD_ID`.

`registerScenes(PonderSceneRegistrationHelper<ResourceLocation>)`:
- Creates a `HELPER` that converts `DeferredBlock` to `ResourceLocation` via `DeferredBlock::getId`.
- Registers 4 scenes for `AllBlocks.SOLAR_GENERATOR`:
  - `solar_generator/basics` -> `SolarGeneratorScenes::basics` (tagged `KINETIC_SOURCES`).
  - `solar_generator/weather` -> `SolarGeneratorScenes::weather`.
  - `solar_generator/obstructions` -> `SolarGeneratorScenes::obstructions`.
  - `solar_generator/dyeing` -> `SolarGeneratorScenes::dyeing`.

`registerTags()` -- empty, uses Create's existing tags.

### 6.2 SolarGeneratorScenes.java (257 lines)

**Class:** `SolarGeneratorScenes` -- 4 static methods, each a Ponder storyboard.

`basics(SceneBuilder, SceneBuildingUtil)`:
- 5x5 base plate.
- Generator at center with shafts on 4 sides.
- 4 keyframes: intro, power output, wrench rotation, reverse direction.
- Text overlays explain sky access, RPM/SU output, wrench usage.

`weather(SceneBuilder, SceneBuildingUtil)`:
- 5x5 base plate, single generator.
- Explains: clear = 16 RPM, rain = 8 RPM, thunder = 0, night = 0.

`obstructions(SceneBuilder, SceneBuildingUtil)`:
- 7x5 base plate, 3 generators in a row.
- Shows everything at once, then explains: solid block = no power, glass = power, snow/carpet = no power.
- Uses `indicateRedstone()` for visual highlight.

`dyeing(SceneBuilder, SceneBuildingUtil)`:
- 5x5 base plate, single generator.
- Animates color changes: blue -> red -> yellow -> lime.
- Uses `world.setBlock()` with AXIS preserved to show inline color change.

---

## 7. Version Differences and Porting

### 7.1 Registration API

| Aspect | NeoForge 1.21.1 | NeoForge 1.20.1 | Fabric 1.20.1 |
|---|---|---|---|
| Block holders | `DeferredBlock<T>` | `RegistryObject<T>` | Direct `static final T` |
| Item holders | `DeferredItem<T>` | `RegistryObject<T>` | Direct `static final T` |
| BE holders | `DeferredHolder<..., BlockEntityType<T>>` | `RegistryObject<BlockEntityType<T>>` | Direct field |
| Block register | `DeferredRegister.createBlocks(MOD_ID)` | `DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID)` | `Registry.register()` |
| Event bus | `modEventBus` param in constructor | `FMLJavaModLoadingContext.get().getModEventBus()` | N/A |
| Entry point | `@Mod` with `(IEventBus, ModContainer)` constructor | `@Mod` with no-arg constructor | `implements ModInitializer` |

### 7.2 Method Signature Changes

| Method | NeoForge 1.21.1 | 1.20.1 (both) |
|---|---|---|
| Block interaction | `useItemOn(ItemStack, BlockState, Level, BlockPos, Player, InteractionHand, BlockHitResult)` returns `ItemInteractionResult` | `use(BlockState, Level, BlockPos, Player, InteractionHand, BlockHitResult)` returns `InteractionResult` |
| BE write | `write(CompoundTag, HolderLookup.Provider, boolean)` | `write(CompoundTag, boolean)` |
| BE read | `read(CompoundTag, HolderLookup.Provider, boolean)` | `read(CompoundTag, boolean)` |
| Contraption writeNBT | `writeNBT(HolderLookup.Provider, boolean)` | `writeNBT(boolean)` |
| Contraption readNBT | `readNBT(Level, CompoundTag, HolderLookup.Provider, boolean)` | `readNBT(Level, CompoundTag, boolean)` |
| getCloneItemStack | `(BlockState, HitResult, LevelReader, BlockPos, Player)` | `(BlockState, HitResult, BlockGetter, BlockPos, Player)` |
| isPathfindable | `(BlockState, PathComputationType)` | `(BlockState, BlockGetter, BlockPos, PathComputationType)` |

### 7.3 Other Differences

| Aspect | NeoForge 1.21.1 | 1.20.1 |
|---|---|---|
| SolarSail strength | `0.8F` | `0.1F` |
| SolarSail sound | `SoundType.WOOL` | `SoundType.SCAFFOLDING` |
| SWBBE warmup | 10 ticks | 20 ticks |
| Contraption type access | `.get()` (deferred) | `.get()` (Forge) / direct (Fabric) |
| Fabric render layers | N/A | `BlockRenderLayerMap.INSTANCE.putBlock()` for each block |
| Fabric contraption registry | N/A | `CreateBuiltInRegistries.CONTRAPTION_TYPE` |
| Tag dirs | `data/*/tags/block/` (singular) | `data/*/tags/blocks/` (plural) |
| Recipe result | `"id": "...", "count": 1` | `"item": "..."` |

### 7.4 Porting Checklist (1.21.1 -> 1.20.1 NeoForge)

- [ ] `DeferredBlock` -> `RegistryObject`
- [ ] `DeferredItem` -> `RegistryObject`
- [ ] `DeferredHolder` -> `RegistryObject`
- [ ] Imports: `net.neoforged` -> `net.minecraftforge`
- [ ] NBT: add/remove `HolderLookup.Provider` parameter
- [ ] Interaction: `useItemOn` -> `use`, `ItemInteractionResult` -> `InteractionResult`
- [ ] `PASS_TO_DEFAULT_BLOCK_INTERACTION` -> `InteractionResult.PASS`
- [ ] `getCloneItemStack`: `LevelReader` -> `BlockGetter`
- [ ] `isPathfindable`: add `BlockGetter, BlockPos` params
- [ ] Tag directories: singular -> plural
- [ ] Recipe format: `id` -> `item`, remove `count: 1`

### 7.5 Porting Checklist (NeoForge 1.20.1 -> Fabric 1.20.1)

- [ ] All above plus:
- [ ] Remove all `net.minecraftforge` imports
- [ ] Replace `DeferredRegister` with `Registry.register()` calls
- [ ] Replace `RegistryObject` with direct fields
- [ ] Entry: `@Mod` -> `implements ModInitializer`
- [ ] Client: `@EventBusSubscriber` -> `implements ClientModInitializer`
- [ ] Renderers: event-based -> `BlockEntityRenderers.register()` direct
- [ ] Add `BlockRenderLayerMap` calls for all non-default render types
- [ ] Contraption: `AllContraptionTypes.X.get()` -> `AllContraptionTypes.X`
- [ ] Registry: `CreateRegistries` -> `CreateBuiltInRegistries`

---

## 8. Design Decisions

**Why `sailBlocks = 0`?**
If we do not zero this in the contraption, `WindmillBearingBlockEntity` (parent) reads `getSailBlocks()` and adds its OWN capacity calculation. Combined with our custom calculation, this effectively double or quadruple-counts SU. By zeroing, the parent sees 0 sails and contributes 0 SU, leaving our `calculateAddedStressCapacity()` as the sole authority.

**Why `warmup`?**
On world load, the contraption entity (a Minecraft entity with a UUID) may not be loaded when the block entity first ticks. Checking sails immediately would see null contraption / 0 sails and reset the network to 0 SU. The warmup (10-20 ticks) gives the world time to load and link entities.

**Why recalculate sails in `readNBT`?**
Stored integer values can desync if a crash corrupted the save, if a mod updated, or if blocks were externally edited. Iterating `getBlocks()` is the definitive source of truth.

**Why not extend `SailBlock`?**
Create's `SailBlock` has `instanceof` checks in multiple places. Extending it would make Create count our sails in its own capacity calculation (via `WindmillBearingBlockEntity`), bypassing our custom logic. Using `WrenchableDirectionalBlock` gives us full control.

**Why `initialize()` forces `network.initialized = false`?**
Create's `KineticNetwork.initFromTE()` sets `unloadedCapacity` from the block entity's saved capacity. Then `addSilently()` adds the live capacity. If both contain the same value, capacity is doubled. By forcing re-init with our zeroed values, the network starts clean.

**Why does Fabric need `BlockRenderLayerMap`?**
NeoForge/Forge set render types via blockstate JSON properties or model configuration. Fabric API does not support this mechanism. Every block that needs `cutout()` (clear glass) or `translucent()` (stained glass) rendering must be explicitly registered via `BlockRenderLayerMap.INSTANCE.putBlock()`. This is why the Fabric client class is 120 lines.

**Why horizontal generators do not check `canSeeSky`?**
Horizontal generators face sideways. The sun's rays come at an angle, not straight down. Checking `canSeeSky` on a horizontal face does not make physical sense. Instead, they check sky light level at the front face and scan for distant obstructions 2-10 blocks away.
