# Create Photomovement — Code Documentation

**Last Updated:** 2026-04-08  
**Versions Covered:** NeoForge 1.21.1, NeoForge 1.20.1, Fabric 1.20.1  
**Language:** Java

This document covers every Java source file in the mod with per-class, per-method detail. All explanations are written to be understood even without prior Minecraft modding experience.

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Project Structure](#2-project-structure)
3. [Entry Points](#3-entry-points)
4. [Registry Classes](#4-registry-classes)
5. [Solar Generator System](#5-solar-generator-system)
6. [Solar Windmill System](#6-solar-windmill-system)
7. [Configuration System](#7-configuration-system)
8. [Rendering System](#8-rendering-system)
9. [Ponder System (In-Game Tutorials)](#9-ponder-system)
10. [Key Concepts and Learning Guide](#10-key-concepts-and-learning-guide)
11. [Version Differences and Porting](#11-version-differences-and-porting)
12. [Troubleshooting](#12-troubleshooting)

---

## 1. Project Overview

Create Photomovement adds solar-powered kinetic devices to the Create mod:

- **Solar Generators** — vertical blocks that generate 16 RPM / 16 SU from sunlight
- **Horizontal Solar Generators** — directional variants with time-of-day based capacity scaling
- **Advanced Solar Generators** — 2x power variants (32 RPM / 32 SU)
- **Solar Windmill Bearing** — custom bearing that gives Solar Sails a bonus multiplier
- **Solar Sails** — windmill sail blocks that double stress capacity under full sun

All blocks have 17 color variants (1 base + 16 dye colors). Total: 86 registered blocks.

---

## 2. Project Structure

### Multi-Loader Layout

```
neoforge/1211/    NeoForge MC 1.21.1  (primary, most up-to-date)
neoforge/1201/    NeoForge MC 1.20.1
fabric/1201/      Fabric MC 1.20.1
```

All three share the same feature set. The source code is nearly identical — differences are at the mod entry point and NBT read/write signatures only (see [§11](#11-version-differences-and-porting)).

### Source Layout (same for all versions)

```
src/main/java/com/createphotomovement/
├── CreatePhotomovement.java           # Main mod entry point (server-side)
├── CreatePhotomovementClient.java     # Client-side registration
├── AllBlocks.java                     # All block registrations
├── AllItems.java                      # All item registrations
├── AllBlockEntityTypes.java           # Block entity type registrations
├── AllCreativeTabs.java               # Creative mode tab setup
├── AllContraptionTypes.java           # Contraption type registrations
├── content/
│   └── kinetics/
│       ├── solargenerator/
│       │   ├── SolarGeneratorBlock.java
│       │   ├── SolarGeneratorBlockEntity.java
│       │   ├── HorizontalSolarGeneratorBlock.java
│       │   ├── HorizontalSolarGeneratorBlockEntity.java
│       │   ├── AdvSolarGeneratorBlock.java
│       │   ├── AdvSolarGeneratorBlockEntity.java
│       │   ├── HorzAdvSolarGeneratorBlock.java
│       │   ├── HorzAdvSolarGeneratorBlockEntity.java
│       │   ├── SolarGeneratorRenderer.java
│       │   └── HorizontalSolarGeneratorRenderer.java
│       └── solarwindmill/
│           ├── SolarWindmillBearingBlock.java
│           ├── SolarWindmillBearingBlockEntity.java
│           ├── SolarSailBlock.java
│           └── SolarBearingContraption.java
├── infrastructure/
│   └── config/
│       ├── PMConfigs.java
│       └── PMServer.java
└── ponder/
    ├── PhotomovementPonderPlugin.java
    ├── SolarGeneratorScenes.java
    ├── HorizontalSolarGeneratorScenes.java
    └── SolarSailScenes.java
```

---

## 3. Entry Points

### 3.1 CreatePhotomovement.java

**Purpose:** Main mod class. Loaded once at startup. Connects all parts of the mod to Minecraft.

**NeoForge 1.21.1:**
```java
@Mod(CreatePhotomovement.MOD_ID)
public class CreatePhotomovement {
    public static final String MOD_ID = "createphotomovement";

    public CreatePhotomovement(IEventBus modEventBus, ModContainer modContainer) {
        AllBlocks.BLOCKS.register(modEventBus);
        AllItems.ITEMS.register(modEventBus);
        AllCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        AllBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        AllContraptionTypes.register(modEventBus);
        PMConfigs.register(ModLoadingContext.get(), modContainer);
        // ... AttachedCheck for Solar Sails
    }
}
```

What the constructor does, in order:
1. Queues all block registrations.
2. Queues all item registrations.
3. Queues creative tab.
4. Queues block entity types.
5. Queues contraption types.
6. Loads config files.
7. **Registers `AttachedCheck` for `SolarSailBlock`.** This is the most important line. Without it, Create will NOT pick up Solar Sails during contraption assembly.

**Why the AttachedCheck matters:**  
Create's contraption assembly walks outward from the bearing and asks each neighboring block "are you attached?" Standard Create only recognizes `SailBlock` subclasses. `SolarSailBlock` extends `WrenchableDirectionalBlock`, not `SailBlock`, so Create ignores it entirely. The `AttachedCheck` lambda fixes this by returning `CheckResult.of(true)` when the movement direction axis differs from the sail's FACING axis — the same logic as a normal sail.

---

### 3.2 CreatePhotomovementClient.java

**Purpose:** Client-side initialization. Only loaded on the client, never on dedicated servers. Handles renderers and Ponder tutorials.

**Key annotation:** `@EventBusSubscriber(modid = MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)`

**Methods:**

`onClientSetup(FMLClientSetupEvent event)`:
- Annotated `@SubscribeEvent`.
- Registers the Ponder plugin: `PonderIndex.addPlugin(new PhotomovementPonderPlugin())`.

`registerRenderers(EntityRenderersEvent.RegisterRenderers event)`:
- Annotated `@SubscribeEvent`.
- Registers 5 block entity renderers:
  - `SOLAR_GENERATOR` → `SolarGeneratorRenderer`
  - `HORIZONTAL_SOLAR_GENERATOR` → `HorizontalSolarGeneratorRenderer`
  - `ADV_SOLAR_GENERATOR` → `SolarGeneratorRenderer` (reused — visually identical)
  - `HORZ_ADV_SOLAR_GENERATOR` → `HorizontalSolarGeneratorRenderer` (reused)
  - `SOLAR_WINDMILL_BEARING` → `BearingRenderer` (Create's built-in, no custom rendering needed)

**What client/server separation means:**  
The client handles visuals and sounds. The server handles game logic and world state. This split ensures the mod works in multiplayer — one server, many clients.

---

## 4. Registry Classes

### 4.1 AllBlocks.java

**Purpose:** Registers all 86 blocks.

**NeoForge pattern** (DeferredRegister):
```java
public static final DeferredRegister.Blocks BLOCKS =
    DeferredRegister.createBlocks(MOD_ID);

public static final DeferredBlock<SolarGeneratorBlock> SOLAR_GENERATOR =
    BLOCKS.register("solar_generator",
        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOD)
            .strength(1.5F)
            .sound(SoundType.WOOD)
            .noOcclusion()));
```

`DeferredRegister` is a "waiting list". Blocks are added to it during class load, but are only actually registered into the game when `BLOCKS.register(modEventBus)` is called in the mod constructor. This prevents timing issues.

**Fabric pattern** (direct registration):
```java
public static final SolarGeneratorBlock SOLAR_GENERATOR =
    new SolarGeneratorBlock(BlockBehaviour.Properties.of()
        .mapColor(MapColor.WOOD).strength(1.5F).sound(SoundType.WOOD).noOcclusion());

public static void register() {
    Registry.register(BuiltInRegistries.BLOCK,
        ResourceLocation.fromNamespaceAndPath(MOD_ID, "solar_generator"),
        SOLAR_GENERATOR);
}
```

Fabric does not have `DeferredRegister`. Blocks are instantiated as static fields, then registered via explicit `Registry.register()` calls in a `register()` method.

**Block properties:**
- Generators: `strength(1.5F)`, `SoundType.WOOD`, `noOcclusion()` (lets light through).
- Advanced Generators: `strength(2.0F)`, `SoundType.METAL`, `MapColor.GOLD`.
- Solar Sails: `strength(0.8F)`, `SoundType.WOOL`. Uses `SolarSailBlock.withCanvas(props, DyeColor)` factory.
- Solar Windmill Bearing: `strength(3.5F)`, `SoundType.WOOD`.

**Block count summary:**

| Type | Count |
|---|---|
| SolarGeneratorBlock | 17 (1 base + 16 colors) |
| HorizontalSolarGeneratorBlock | 17 |
| AdvSolarGeneratorBlock | 17 |
| HorzAdvSolarGeneratorBlock | 17 |
| SolarSailBlock | 17 |
| SolarWindmillBearingBlock | 1 |
| **Total** | **86** |

---

### 4.2 AllItems.java

**Purpose:** Registers the item form of every block (so players can hold and place them).

**NeoForge pattern:**
```java
public static final DeferredItem<BlockItem> SOLAR_GENERATOR =
    ITEMS.registerSimpleBlockItem("solar_generator", AllBlocks.SOLAR_GENERATOR);
```

`registerSimpleBlockItem()` automatically creates a `BlockItem` (the inventory item) for a given block. The item name must match the block name exactly.

**Why separate blocks and items?**  
Blocks exist in the world. Items exist in inventories. They're separate objects in Minecraft's design, even though they're connected. Every block that players can pick up and place needs both.

---

### 4.3 AllBlockEntityTypes.java

**Purpose:** Registers the 5 block entity types. Block Entities are "brains" for blocks that need custom data or tick logic.

**What is a Block Entity?**  
Normal blocks are stateless — they just exist. Block Entities can store data (like current stress capacity), run code every tick (like checking sunlight), and have custom rendering. Examples in vanilla: chests, furnaces, signs.

**NeoForge registration pattern:**
```java
public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarGeneratorBlockEntity>>
    SOLAR_GENERATOR = BLOCK_ENTITY_TYPES.register("solar_generator",
        () -> BlockEntityType.Builder.of(
            (pos, state) -> new SolarGeneratorBlockEntity(
                AllBlockEntityTypes.SOLAR_GENERATOR.get(), pos, state),
            AllBlocks.SOLAR_GENERATOR.get(),
            AllBlocks.WHITE_SOLAR_GENERATOR.get(),
            // ... all 17 color variants
        ).build(null));
```

**Why do all 17 color variants share one block entity type?**  
Color is purely visual (different block model). The generation logic is identical. Sharing one type saves memory and avoids code duplication.

**Registered types:**

| Name | BE Class | Blocks |
|---|---|---|
| `solar_generator` | `SolarGeneratorBlockEntity` | 17 |
| `horizontal_solar_generator` | `HorizontalSolarGeneratorBlockEntity` | 17 |
| `adv_solar_generator` | `AdvSolarGeneratorBlockEntity` | 17 |
| `horz_adv_solar_generator` | `HorzAdvSolarGeneratorBlockEntity` | 17 |
| `solar_windmill_bearing` | `SolarWindmillBearingBlockEntity` | 1 |

---

### 4.4 AllCreativeTabs.java

**Purpose:** Creates and populates the Creative Mode inventory tab.

```java
public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB =
    CREATIVE_MODE_TABS.register("main",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.createphotomovement"))
            .icon(() -> AllItems.SOLAR_GENERATOR.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(AllItems.SOLAR_GENERATOR.get());
                // ... all items in display order
            })
            .build());
```

Item order: basic generators first → advanced generators → color variants → solar sails.

---

### 4.5 AllContraptionTypes.java

**Purpose:** Registers the custom `SolarBearingContraption` type with Create's contraption system.

```java
public static final DeferredHolder<ContraptionType, ContraptionType> SOLAR_BEARING =
    CONTRAPTION_TYPES.register("solar_bearing",
        () -> new ContraptionType(SolarBearingContraption::new));
```

`SolarBearingContraption::new` is a constructor reference. It tells Create how to instantiate this contraption type when loading from disk or spawning a new entity.

---

## 5. Solar Generator System

This section explains how solar generators work, from the block you place to the power they generate.

### 5.1 SolarGeneratorBlock.java

**Purpose:** Defines the block class for vertical solar generators.

**Class hierarchy:**
```java
public class SolarGeneratorBlock extends RotatedPillarKineticBlock
        implements IBE<SolarGeneratorBlockEntity>, IWrenchable
```

- `RotatedPillarKineticBlock` — Create's base for rotation-generating blocks that can be oriented along an axis (like logs).
- `IBE<T>` — marks this block as having an associated Block Entity of type T.
- `IWrenchable` — allows players to rotate/configure this block with a wrench.

**Key features:**

**1. Color Mapping System:**
```java
private static final Map<DyeColor, Supplier<Block>> COLOR_TO_BLOCK = new HashMap<>();

static {
    COLOR_TO_BLOCK.put(DyeColor.WHITE, () -> AllBlocks.WHITE_SOLAR_GENERATOR.get());
    COLOR_TO_BLOCK.put(DyeColor.ORANGE, () -> AllBlocks.ORANGE_SOLAR_GENERATOR.get());
    // ... all 16 colors
}
```
`static { }` is a static initializer — runs once when the class loads. The map is a lookup table: "for this dye, use this block."

**2. Shaft Connection Logic:**
```java
@Override
public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
    return face.getAxis() == state.getValue(AXIS);
}
```
Called by Create to determine which faces can connect to shafts. Only the two faces aligned with the block's rotation axis are valid connection points.

**3. Placement Logic:**
```java
@Override
public BlockState getStateForPlacement(BlockPlaceContext context) {
    if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown())
        return defaultBlockState().setValue(AXIS,
            context.getHorizontalDirection().getClockWise().getAxis());
    return defaultBlockState().setValue(AXIS,
        context.getHorizontalDirection().getAxis());
}
```
Normal placement: axis faces the player. Shift + place: axis rotated 90° clockwise.

**4. Dye Interaction:**
```java
@Override
protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
        BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

    if (stack.getItem() instanceof DyeItem dyeItem) {
        DyeColor color = dyeItem.getDyeColor();
        Block targetBlock = COLOR_TO_BLOCK.get(color).get();

        if (state.getBlock() == targetBlock)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (!level.isClientSide) {
            Direction.Axis currentAxis = state.getValue(AXIS);
            BlockState newState = targetBlock.defaultBlockState().setValue(AXIS, currentAxis);
            level.setBlock(pos, newState, 3);
            level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!player.isCreative()) stack.shrink(1);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
}
```
Step-by-step: check if holding a dye → look up target block → if already that color do nothing → server-side only: preserve axis, set new colored block, play sound, consume dye.

`!level.isClientSide` — world changes must only happen server-side. The client displays the result via sync packets.

**5. Wrench Interaction:**
```java
@Override
public InteractionResult onWrenched(BlockState state, UseOnContext context) {
    if (level.isClientSide) return InteractionResult.SUCCESS;

    Direction.Axis currentAxis = state.getValue(AXIS);
    Direction.Axis newAxis = currentAxis == Direction.Axis.X ?
        Direction.Axis.Z : Direction.Axis.X;

    level.setBlock(pos, state.setValue(AXIS, newAxis), 3);
    return InteractionResult.SUCCESS;
}
```
Only rotates between X and Z — never Y. This keeps the solar panel facing upward at all times.

---

### 5.2 SolarGeneratorBlockEntity.java

**Purpose:** The "brain" of the vertical solar generator. Checks sunlight conditions and feeds power into Create's kinetic network.

**Class hierarchy:**
```java
public class SolarGeneratorBlockEntity extends GeneratingKineticBlockEntity
```
`GeneratingKineticBlockEntity` is Create's base for blocks that generate rotational power. It provides the framework — subclasses just override `getGeneratedSpeed()` and `calculateAddedStressCapacity()`.

**Key methods:**

**`onLoad()`:**
```java
@Override
public void onLoad() {
    super.onLoad();
    if (level != null && !level.isClientSide) {
        updateGeneratedRotation();
    }
}
```
Called when the block entity loads (world start or chunk load). Forces an immediate network update to prevent "ghost power" after server restarts.

**`getGeneratedSpeed()`:**
```java
@Override
public float getGeneratedSpeed() {
    if (!canGeneratePower()) return 0;
    float speed = PMConfigs.server().generationSpeed.get();  // default: 16
    if (level != null && level.isRainingAt(worldPosition.above())) {
        speed = speed / 2;
    }
    return speed;
}
```
Called every tick. Returns 0 if conditions aren't met. Rain halves the speed (8 RPM instead of 16).

**`calculateAddedStressCapacity()`:**
```java
@Override
public float calculateAddedStressCapacity() {
    return PMConfigs.server().stressCapacity.get();  // default: 16 SU
}
```
Returns how much load capacity this generator provides. The advanced variant overrides this to return 2x.

**`canGeneratePower()`:**
```java
protected boolean canGeneratePower() {
    if (level == null) return false;

    // Must see sky
    if (!level.canSeeSky(worldPosition.above())) return false;

    // Need skylight level 12+ (daytime only)
    int skyLight = level.getBrightness(LightLayer.SKY, worldPosition.above());
    int currentSkyLight = skyLight - level.getSkyDarken();
    if (currentSkyLight < 12) return false;

    // Specific block obstructions
    BlockPos abovePos = worldPosition.above();
    BlockState aboveState = level.getBlockState(abovePos);
    Block aboveBlock = aboveState.getBlock();
    if (aboveBlock instanceof SnowLayerBlock) return false;
    if (aboveBlock instanceof CarpetBlock) return false;
    if (aboveBlock == Blocks.MOSS_CARPET) return false;
    if (aboveBlock == Blocks.SNOW) return false;

    // Any light-blocking block stops generation
    if (aboveState.getLightBlock(level, abovePos) > 0) return false;

    return true;
}
```

The four checks explained:
1. **Sky visibility** — `canSeeSky()` returns false if any solid block is above. Glass returns true.
2. **Skylight level** — Raw sky brightness minus the day-cycle darkening value. At noon = 15, at night = 4. Threshold 12 means roughly 6 AM–6 PM. Thunder also lowers skylight.
3. **Specific blocks** — Snow layers, carpets, and moss carpet don't block sky visibility but physically obstruct the panel.
4. **Opacity check** — `.getLightBlock()` returns 0 for transparent blocks (air, glass) and 15 for solid blocks.

**`tick()`:**
```java
@Override
public void tick() {
    super.tick();
    if (!level.isClientSide) {
        float targetSpeed = getGeneratedSpeed();
        if (Math.abs(speed) != Math.abs(targetSpeed)
                || (targetSpeed != 0 && Math.signum(speed) != Math.signum(targetSpeed))) {
            updateGeneratedRotation();
        }
    }
}
```
Runs 20 times per second. Only recalculates when the target speed changes — this is an optimization. `Math.signum()` catches direction reversals even at the same absolute speed.

---

### 5.3 HorizontalSolarGeneratorBlock.java

**Purpose:** Horizontal variant that faces sideways. Connects to shafts on one side only.

**Key differences from vertical:**

```java
public class HorizontalSolarGeneratorBlock extends HorizontalKineticBlock
```
`HorizontalKineticBlock` — Create's base for blocks that rotate around a horizontal axis (like water wheels).

**Shaft connection (back side only):**
```java
@Override
public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
    return face == state.getValue(HORIZONTAL_FACING).getOpposite();
}
```
Only the back side connects. The shaft comes out opposite the panel face.

**Placement:**
- Normal: Panel faces toward player.
- Shift + place: Panel faces away from player.

**Force update on place:**
```java
@Override
public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
    super.onPlace(state, level, pos, oldState, movedByPiston);
    if (!level.isClientSide && state.getBlock() == this) {
        withBlockEntityDo(level, pos, HorizontalSolarGeneratorBlockEntity::forceUpdate);
    }
}
```
Immediately triggers power calculation when the block is placed. Prevents the initial delay before generation starts.

---

### 5.4 HorizontalSolarGeneratorBlockEntity.java

**Purpose:** Horizontal generator with time-of-day based power scaling.

**Key differences from vertical:**

```java
private float currentStressCapacity;
private int updateTimer;
private int warmup = 10;
```
Unlike vertical generators (fixed 16 SU), horizontal generators have variable capacity. It's checked every 10 seconds, not every tick.

**`getGeneratedSpeed()` — rain check position:**
```java
if (level != null && level.isRainingAt(
        worldPosition.relative(getBlockState().getValue(HORIZONTAL_FACING)))) {
    speed = speed / 2;
}
```
Checks rain at the block in *front* of the panel, not above — because the panel faces horizontally.

**`canGeneratePower()` — skylight in front:**
```java
BlockPos frontPos = worldPosition.relative(facing);
int skyLight = level.getBrightness(LightLayer.SKY, frontPos);
int currentSkyLight = skyLight - level.getSkyDarken();
if (currentSkyLight < 12) return false;

BlockState frontState = level.getBlockState(frontPos);
if (frontState.getLightBlock(level, frontPos) > 0) return false;
```
Checks the block immediately in front of the panel. A solid block 1 block away stops all power.

**`updateStressCapacity()` — the complex part:**
```java
private void updateStressCapacity() {
    if (!canGeneratePower()) {
        if (currentStressCapacity != 0) {
            currentStressCapacity = 0;
            updateGeneratedRotation();
            notifyUpdate();
        }
        return;
    }

    Direction facing = getBlockState().getValue(HORIZONTAL_FACING);
    float base = PMConfigs.server().stressCapacity.get();  // 16
    float min = 8;
    float newCapacity = min;

    // Check for distant obstructions (2–10 blocks away)
    boolean distantObstruction = false;
    for (int i = 2; i <= 10; i++) {
        BlockPos checkPos = worldPosition.relative(facing, i);
        if (level.getBlockState(checkPos).getLightBlock(level, checkPos) > 0) {
            distantObstruction = true;
            break;
        }
    }

    if (!distantObstruction) {
        long time = level.getDayTime() % 24000;
        float peak = 4 * base;  // 64 SU
        long daylightTime = Math.min(time, 12000);
        float ratio = (float) daylightTime / 12000.0f;
        ratio = Mth.clamp(ratio, 0f, 1f);

        if (facing == Direction.EAST) {
            float factor = (1 - ratio) * (1 - ratio);   // Quadratic falloff
            newCapacity = min + (peak - min) * factor;
        } else if (facing == Direction.WEST) {
            float factor = ratio * ratio;                // Quadratic increase
            newCapacity = min + (peak - min) * factor;
        }
        // North/South: stays at min (8 SU)
    }

    newCapacity = Math.round(newCapacity);
    if (Math.abs(newCapacity - currentStressCapacity) > 0.01f) {
        currentStressCapacity = newCapacity;
        updateGeneratedRotation();
        notifyUpdate();
    }
}
```

**Time-of-day system:**
- Minecraft day cycle = 24000 ticks = 20 min real time
- Tick 0 = 6 AM (sunrise), 6000 = noon, 12000 = 6 PM (sunset)

**Capacity ranges:**
- Minimum: 8 SU (when obstructed or facing north/south)
- Maximum: 64 SU (4× the base 16, at peak sun angle)

**Directional behavior:**
- East-facing: Best at sunrise, worst at sunset. Formula: `(1−ratio)²` → smooth falloff.
- West-facing: Worst at sunrise, best at sunset. Formula: `ratio²` → smooth increase.
- North/South: Constant 8 SU — sun never hits from these directions.
- Quadratic (`x²`) curves feel natural vs linear. `(1−x)²` decelerates, `x²` accelerates.

**Obstructions:**
- 1 block away: No power at all (from `canGeneratePower()`).
- 2–10 blocks away: Power but only minimum capacity (8 SU).
- Nothing within 10 blocks: Full time-based capacity.

**Tick pattern:**
```java
@Override
public void tick() {
    super.tick();
    if (level == null || level.isClientSide) return;
    if (warmup > 0) { warmup--; if (warmup == 0) updateStressCapacity(); return; }
    if (updateTimer++ >= 200) { updateTimer = 0; updateStressCapacity(); }
}
```
10-tick warmup prevents recalculation on chunk load before the network is ready. After warmup, recalculates every 200 ticks (10 seconds). Gradual capacity changes aren't noticeable at 10-second intervals.

**NBT serialization** (NeoForge 1.21.1 signature):
```java
@Override
protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
    super.write(compound, registries, clientPacket);
    if (clientPacket) compound.putFloat("CurrentStressCapacity", currentStressCapacity);
}
```
Only syncs capacity to clients (for Engineer's Goggles display). Server recalculates from scratch on load.

---

### 5.5 Advanced Generator Variants

`AdvSolarGeneratorBlockEntity` extends `SolarGeneratorBlockEntity` and only overrides one method:

```java
@Override
public float getGeneratedSpeed() {
    if (!canGeneratePower()) return 0;
    float generatedSpeed = PMConfigs.server().generationSpeed.get() * 2;  // 32 RPM
    if (level != null && level.isRainingAt(worldPosition.above())) {
        generatedSpeed = generatedSpeed / 2;  // 16 RPM during rain
    }
    return generatedSpeed;
}
```

Everything else (sky check, SU capacity, NBT) is inherited. `HorzAdvSolarGeneratorBlockEntity` does the same for the horizontal variant.

---

## 6. Solar Windmill System

The windmill system is more complex than generators because it involves moving contraptions — structures that become a single entity and rotate together.

### 6.1 SolarWindmillBearingBlock.java

**Purpose:** The block players place to create a solar windmill contraption.

```java
public class SolarWindmillBearingBlock extends WindmillBearingBlock
```

**Custom ticker:**
```java
@Override
public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
    return (l, p, s, be) -> {
        if (be instanceof SolarWindmillBearingBlockEntity solarBe) {
            solarBe.tick();        // Standard windmill logic
            solarBe.solarTick();   // Solar bonus logic
        }
    };
}
```
The dual-tick pattern separates concerns: standard windmill rotation from solar capacity multipliers. The `(l, p, s, be) -> { }` is a lambda expression — an inline function.

---

### 6.2 SolarWindmillBearingBlockEntity.java

**Purpose:** Tracks two types of sails separately and applies solar bonus to stress capacity.

> This class took 5 days to implement correctly. The main problem was the SU doubling bug — see §6.2.6.

**Fields:**
```java
private int regularSailCount = 0;
private int solarSailCount = 0;
private boolean hasSkyAccess = false;
private float lastSolarMultiplier = -1;
private int warmup = 10;
```

**6.2.1 Solar Multiplier:**
```java
private float getSolarMultiplier() {
    if (!hasSkyAccess) return 1.0f;
    if (isNight()) return 1.0f;
    if (isThundering()) return 1.0f;
    if (isRaining()) return 1.5f;
    return 2.0f;
}
```

| Condition | Multiplier | Reason |
|---|---|---|
| No sky access | 1.0x | Trees/buildings blocking sky |
| Night | 1.0x | No sunlight |
| Thunder | 1.0x | Dark clouds |
| Rain | 1.5x | Some light still gets through |
| Clear day | 2.0x | Maximum solar power |

This multiplier only affects SU (stress capacity), not RPM (speed).

**6.2.2 Assembly:**
```java
@Override
public void assemble() {
    Direction direction = getBlockState().getValue(BearingBlock.FACING);
    SolarBearingContraption contraption = new SolarBearingContraption(true, direction);

    try {
        if (!contraption.assemble(level, worldPosition)) return;
    } catch (AssemblyException e) {
        lastException = e; sendData(); return;
    }

    // Cache counts from contraption
    this.solarSailCount = contraption.getSolarSailBlocks();
    this.regularSailCount = contraption.getRegularSailBlocks();
    this.hasSkyAccess = contraption.hasSkyAccess();

    // Remove blocks and create moving entity
    contraption.removeBlocksFromWorld(level, BlockPos.ZERO);
    movedContraption = ControlledContraptionEntity.create(level, this, contraption);
    BlockPos anchor = worldPosition.relative(direction);
    movedContraption.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
    movedContraption.setRotationAxis(direction.getAxis());
    level.addFreshEntity(movedContraption);

    running = true;
    sendData();
    updateGeneratedRotation();
}
```

What happens: create custom contraption → assemble (walks the block structure) → cache sail counts → remove blocks from world → spawn contraption entity → mark running.

The key is using `SolarBearingContraption` instead of the standard `BearingContraption`. This custom class counts solar vs regular sails separately.

**6.2.3 Speed Calculation:**
```java
@Override
public float getGeneratedSpeed() {
    if (!running || movedContraption == null) return 0;

    int totalSails = regularSailCount + solarSailCount;
    int sailsPerRPM = AllConfigs.server().kinetics.windmillSailsPerRPM.get();  // 8
    int rpm = totalSails / sailsPerRPM;
    return Mth.clamp(rpm, 1, 16) * getAngleSpeedDirection();
}
```
Both sail types count equally toward RPM. 8 sails = 1 RPM, max 16 RPM at 128 sails. `getAngleSpeedDirection()` returns +1 or -1 for rotation direction.

**6.2.4 Stress Capacity Calculation:**
```java
@Override
public float calculateAddedStressCapacity() {
    if (!running || movedContraption == null) return 0;

    int sailsPerBracket = AllConfigs.server().kinetics.windmillSailsPerRPM.get();  // 8
    float suPerBracket = 512f;

    int normalBrackets = regularSailCount / sailsPerBracket;
    float normalSU = normalBrackets * suPerBracket;

    int solarBrackets = solarSailCount / sailsPerBracket;
    float solarSU = solarBrackets * suPerBracket * getSolarMultiplier();

    float totalSU = normalSU + solarSU;

    int totalSails = regularSailCount + solarSailCount;
    int rpm = Math.max(1, totalSails / sailsPerBracket);
    float result = totalSU / rpm;

    this.lastCapacityProvided = result;
    return result;
}
```

**The bracket system (Create windmills):**
- Every 8 sails = 1 bracket
- Each bracket = 512 SU
- Formula: `(sailCount / 8) × 512`
- Solar sails get this multiplied by the solar multiplier

**Example — 32 regular + 32 solar sails, clear day (2.0x):**
1. Regular: `(32/8) × 512 = 2048 SU`
2. Solar: `(32/8) × 512 × 2.0 = 4096 SU`
3. Total: `6144 SU`
4. RPM: `64 / 8 = 8`
5. Per-RPM value: `6144 / 8 = 768`

During rain (1.5x): solar → 3072, total → 5120, per-RPM → 640  
At night (1.0x): solar → 2048, total → 4096, per-RPM → 512

**6.2.5 Data Persistence:**
```java
// NeoForge 1.21.1 / 1.20.1 signature differs — see §11
public void write(CompoundTag compound, ..., boolean clientPacket) {
    if (clientPacket) {
        compound.putInt("RegularSails", regularSailCount);
        compound.putInt("SolarSails", solarSailCount);
        compound.putBoolean("HasSkyAccess", hasSkyAccess);
    } else {
        // CRITICAL: Zero both capacity fields BEFORE super.write()
        this.capacity = 0;
        this.lastCapacityProvided = 0;
    }
    super.write(compound, ...);
}

protected void read(CompoundTag compound, ..., boolean clientPacket) {
    super.read(compound, ...);
    if (clientPacket) {
        regularSailCount = compound.getInt("RegularSails");
        solarSailCount = compound.getInt("SolarSails");
        hasSkyAccess = compound.getBoolean("HasSkyAccess");
    } else {
        // Server resets everything and recalculates from scratch
        regularSailCount = 0; solarSailCount = 0; hasSkyAccess = false;
        this.lastCapacityProvided = 0; this.capacity = 0;
    }
}
```

Why zero fields on disk save? Prevents "ghost SU" on world reload. Server resets to zero and recalculates everything fresh.

**6.2.6 The Double-Counting Bug Fix:**
```java
@Override
public void updateGeneratedRotation() {
    if (movedContraption != null) {
        Contraption c = movedContraption.getContraption();
        if (c instanceof SolarBearingContraption sbc) {
            this.solarSailCount = sbc.getSolarSailBlocks();
            this.regularSailCount = sbc.getRegularSailBlocks();
            this.hasSkyAccess = sbc.hasSkyAccess();
            // CRITICAL: Zero out parent's sailBlocks to prevent double-counting
            sbc.setSailBlocks(0);
        }
    }
    super.updateGeneratedRotation();
}
```

Why this is necessary:
- Parent class (`WindmillBearingBlockEntity`) has its own `sailBlocks` field.
- Parent uses this field in its own capacity calculation.
- We calculate capacity ourselves via `calculateAddedStressCapacity()`.
- If parent's `sailBlocks` isn't zeroed, capacity gets counted twice — once by parent, once by us.
- Setting it to 0 tells parent "don't add capacity yourself."

**6.2.7 Network Initialization:**
```java
@Override
public void initialize() {
    if (hasNetwork() && !level.isClientSide) {
        KineticNetwork net = getOrCreateNetwork();
        if (net.initialized) net.initialized = false;
    }
    super.initialize();
}
```
Forces the kinetic network to recalculate from scratch, preventing it from using cached (possibly wrong) capacity values.

---

### 6.3 SolarSailBlock.java

**Purpose:** The solar sail block. Works like Create's standard sails but participates in the solar bonus system.

**Color system:**
```java
public enum GlassColor implements StringRepresentable {
    CLEAR("clear"), WHITE("white"), ORANGE("orange"), // ... all 16
}

public static final EnumProperty<GlassColor> GLASS_COLOR =
    EnumProperty.create("glass_color", GlassColor.class);
```
`StringRepresentable` allows the enum to be stored as a string in block states and NBT. `EnumProperty` is a block state property (like `FACING` or `AXIS`).

**Factory method:**
```java
public static SolarSailBlock withCanvas(Properties properties, DyeColor color) {
    return new SolarSailBlock(properties, color);
}
```
All sails are created via this factory. No frameless/framed distinction — all solar sails have a canvas.

**Dye interaction — three-tier system:**
```java
public void applyDye(BlockState state, Level world, BlockPos pos, Vec3 hit, DyeColor dyeColor) {
    BlockState newState = getSolarSailForColor(dyeColor).defaultBlockState();
    newState = BlockHelper.copyProperties(state, newState);

    // Tier 1: Dye the clicked block (if it's not already that color)
    if (state != newState) { world.setBlockAndUpdate(pos, newState); return; }

    // Tier 2: Dye the nearest adjacent sail of a different color
    List<Direction> directions = IPlacementHelper.orderedByDistanceExceptAxis(
        pos, hit, state.getValue(FACING).getAxis());
    for (Direction d : directions) {
        BlockPos offset = pos.relative(d);
        BlockState adj = world.getBlockState(offset);
        if (!(adj.getBlock() instanceof SolarSailBlock)) continue;
        if (state.getValue(FACING) != adj.getValue(FACING)) continue;
        if (state == adj) continue;
        world.setBlockAndUpdate(offset, newState); return;
    }

    // Tier 3: Flood-fill all connected sails
    List<BlockPos> frontier = new ArrayList<>();
    Set<BlockPos> visited = new HashSet<>();
    frontier.add(pos);
    int timeout = 100;
    while (!frontier.isEmpty()) {
        if (timeout-- < 0) break;
        BlockPos curr = frontier.remove(0);
        visited.add(curr);
        for (Direction d : Iterate.directions) {
            if (d.getAxis() == state.getValue(FACING).getAxis()) continue;
            BlockPos offset = curr.relative(d);
            if (visited.contains(offset)) continue;
            BlockState adj = world.getBlockState(offset);
            if (!(adj.getBlock() instanceof SolarSailBlock)) continue;
            if (adj.getValue(FACING) != state.getValue(FACING)) continue;
            if (state != adj) world.setBlockAndUpdate(offset, newState);
            frontier.add(offset); visited.add(offset);
        }
    }
}
```

The three tiers: dye clicked block first → if already correct color, dye nearest adjacent sail → if no adjacent sail, flood-fill the entire connected structure.

Flood-fill uses BFS (breadth-first search). `visited` prevents infinite loops. Timeout at 100 blocks caps performance cost. Only follows perpendicular directions (not along the facing axis) to avoid leaking across panels.

**Bounce physics:**
```java
@Override
public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
    super.fallOn(level, state, pos, entity, 0);  // 0 = no fall damage
}

private void bounce(Entity entity) {
    Vec3 velocity = entity.getDeltaMovement();
    if (velocity.y < 0.0D) {
        double d0 = entity instanceof LivingEntity ? 1.0D : 0.8D;
        entity.setDeltaMovement(velocity.x, -velocity.y * 0.26F * d0, velocity.z);
    }
}
```
Solar Sails negate fall damage and bounce entities like a trampoline at 26% efficiency. Living entities get full bounce (1.0), non-living get 80% (0.8).

---

### 6.4 SolarBearingContraption.java

**Purpose:** Custom contraption type that counts solar vs regular sails separately and checks sky access at assembly time.

```java
public class SolarBearingContraption extends BearingContraption
```

**Fields:**
```java
protected int solarSailBlocks = 0;
protected int regularSailBlocks = 0;
protected boolean hasSkyAccess = false;
```

**Assembly:**
```java
@Override
public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
    solarSailBlocks = 0; regularSailBlocks = 0;
    boolean result = super.assemble(world, pos);
    if (result) hasSkyAccess = checkSkyAccess(world, pos);
    return result;
}
```
Resets counters, calls parent (which walks the block structure and calls `addBlock()` for each found block), then checks sky access.

**Block counting:**
```java
@Override
public void addBlock(Level level, BlockPos pos, Pair<StructureBlockInfo, BlockEntity> capture) {
    BlockPos localPos = pos.subtract(anchor);
    boolean isNew = !getBlocks().containsKey(localPos);

    if (isNew) {
        BlockState state = capture.getKey().state();
        if (state.getBlock() instanceof SolarSailBlock)
            solarSailBlocks++;
        else if (isSail(state))
            regularSailBlocks++;
    }

    super.addBlock(level, pos, capture);
}
```
`isNew` guard prevents counting blocks that were already added by the parent's assembly walk.

**Sky access check:**
```java
private boolean checkSkyAccess(Level world, BlockPos pos) {
    for (int x = -2; x <= 2; x++)
        for (int z = -2; z <= 2; z++)
            if (world.canSeeSky(pos.offset(x, 1, z)))
                return true;
    return false;
}
```
Checks a 5×5 area (25 positions) one block above the bearing. Only one position needs sky access — this tolerates shafts and structural supports.

**Sail detection:**
```java
protected boolean isSail(BlockState state) {
    if (state.getBlock() instanceof SolarSailBlock) return true;
    if (state.is(com.simibubi.create.AllTags.AllBlockTags.WINDMILL_SAILS.tag)) return true;
    return false;
}
```
Two ways to identify sails: our custom class or Create's windmill sail block tag. Allows both types to work in the same contraption.

**NBT serialization:**
```java
@Override
public CompoundTag writeNBT(HolderLookup.Provider registries, boolean spawnPacket) {
    this.sailBlocks = 0;  // CRITICAL — zero before super writes it

    CompoundTag tag = super.writeNBT(registries, spawnPacket);
    tag.putInt("SolarSails", solarSailBlocks);
    tag.putInt("RegularSails", regularSailBlocks);
    tag.putBoolean("HasSkyAccess", hasSkyAccess);
    return tag;
}

@Override
public void readNBT(Level world, CompoundTag tag, boolean spawnData) {
    super.readNBT(world, tag, spawnData);

    // Recalculate from actual block data — don't trust saved integers
    int recalcSolar = 0, recalcTotal = 0;
    for (StructureBlockInfo info : getBlocks().values()) {
        BlockState state = info.state();
        if (state.getBlock() instanceof SolarSailBlock) { recalcSolar++; recalcTotal++; }
        else if (isSail(state)) recalcTotal++;
    }

    this.solarSailBlocks = recalcSolar;
    this.regularSailBlocks = recalcTotal - recalcSolar;
    this.sailBlocks = 0;  // CRITICAL — zero after super loads it

    hasSkyAccess = tag.getBoolean("HasSkyAccess");
}
```

Key principle: **always recalculate from actual blocks on load**, don't trust the saved integers. The only thing trusted from NBT is `hasSkyAccess` (sky access can't be easily recalculated from block data).

---

## 7. Configuration System

### 7.1 PMConfigs.java

**Purpose:** Central configuration manager.

```java
@EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class PMConfigs {
    private static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<>(ModConfig.Type.class);
    private static PMServer server;

    public static PMServer server() { return server; }
}
```

Config types:
- `SERVER` — synced to clients, affects gameplay. This mod only uses SERVER config.
- `CLIENT` — local only, affects visuals/UI.
- `COMMON` — shared.

**Registration (NeoForge 1.21.1):**
```java
public static void register(ModLoadingContext context, ModContainer container) {
    server = register(PMServer::new, ModConfig.Type.SERVER);
    for (Entry<ModConfig.Type, ConfigBase> pair : CONFIGS.entrySet())
        container.registerConfig(pair.getKey(), pair.getValue().specification);
}
```

**Event handlers:**
```java
@SubscribeEvent
public static void onLoad(ModConfigEvent.Loading event) { ... }

@SubscribeEvent
public static void onReload(ModConfigEvent.Reloading event) { ... }
```
Listen for config load/reload events and notify config objects to update their cached values.

---

### 7.2 PMServer.java

**Purpose:** Defines the two configurable gameplay values.

```java
public class PMServer extends ConfigBase {
    public final ConfigInt generationSpeed = i(16, 1, "generationSpeed",
        "Speed of the Solar Generator in RPM");

    public final ConfigInt stressCapacity = i(16, 1, "stressCapacity",
        "Stress Capacity of the Solar Generator in SU per RPM");
}
```

**Config file location:** `config/createphotomovement-server.toml`

```toml
[server]
    #Speed of the Solar Generator in RPM
    #Range: > 1
    generationSpeed = 16

    #Stress Capacity of the Solar Generator in SU per RPM
    #Range: > 1
    stressCapacity = 16
```

`generationSpeed` defaults to 16 RPM (advanced generators multiply this by 2 in code).  
`stressCapacity` defaults to 16 SU (horizontal and windmill generators also use this as a base).

---

## 8. Rendering System

### 8.1 SolarGeneratorRenderer.java

**Purpose:** Custom renderer for vertical solar generators — draws the rotating shaft.

```java
public class SolarGeneratorRenderer extends KineticBlockEntityRenderer<SolarGeneratorBlockEntity> {

    @Override
    protected void renderSafe(SolarGeneratorBlockEntity be, float partialTicks, PoseStack ms,
            MultiBufferSource buffer, int light, int overlay) {

        BlockState shaftState = shaft(getRotationAxisOf(be));
        SuperByteBuffer superBuffer = CachedBuffers.block(shaftState);
        standardKineticRotationTransform(superBuffer, be, light);
        superBuffer.renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }
}
```

`partialTicks` — fraction between game ticks (0.0–1.0). Used to interpolate position for smooth 60 FPS animation on 20 TPS game logic.  
`PoseStack` — matrix stack tracking position and rotation transformations.  
`standardKineticRotationTransform()` — Create's helper that applies rotation based on the block entity's current speed.

---

### 8.2 HorizontalSolarGeneratorRenderer.java

**Purpose:** Same as above but renders only the back half-shaft.

```java
@Override
protected void renderSafe(HorizontalSolarGeneratorBlockEntity be, float partialTicks, PoseStack ms,
        MultiBufferSource buffer, int light, int overlay) {

    Direction facing = be.getBlockState().getValue(HORIZONTAL_FACING);
    Direction shaftDirection = facing.getOpposite();  // Shaft on back side
    SuperByteBuffer halfShaft = CachedBuffers.partialFacing(
        AllPartialModels.SHAFT_HALF, be.getBlockState(), shaftDirection);
    standardKineticRotationTransform(halfShaft, be, light);
    halfShaft.renderInto(ms, buffer.getBuffer(RenderType.solid()));
}
```

Uses `SHAFT_HALF` (a partial model) instead of a full shaft. Only renders on the back side. Advanced variants reuse these same two renderers.

---

## 9. Ponder System

Ponder is Create's built-in in-game tutorial system. Scenes are scripted sequences that run in a mini-world.

### 9.1 PhotomovementPonderPlugin.java

**Purpose:** Registers Ponder scenes for this mod's blocks.

```java
public class PhotomovementPonderPlugin implements PonderPlugin {

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<DeferredBlock<?>> HELPER =
            helper.withKeyFunction(DeferredBlock::getId);

        HELPER.forComponents(AllBlocks.SOLAR_GENERATOR)
            .addStoryBoard("solar_generator/basics", SolarGeneratorScenes::basics,
                AllCreatePonderTags.KINETIC_SOURCES)
            .addStoryBoard("solar_generator/weather", SolarGeneratorScenes::weather)
            .addStoryBoard("solar_generator/obstructions", SolarGeneratorScenes::obstructions)
            .addStoryBoard("solar_generator/dyeing", SolarGeneratorScenes::dyeing);
    }
}
```

`KINETIC_SOURCES` tag makes the scene appear in Create's Ponder index under "Kinetic Sources."

---

### 9.2 SolarGeneratorScenes.java

**Purpose:** The actual Ponder scene content — scripted tutorial sequences.

**Scene structure:**
```java
public static void basics(SceneBuilder builder, SceneBuildingUtil util) {
    builder.title("solar_generator_basics", "Solar Generator Basics");
    builder.configureBasePlate(0, 0, 5);
    builder.showBasePlate();

    BlockPos generatorPos = util.grid().at(2, 1, 2);
    builder.idle(10);
    builder.addKeyframe();

    builder.world().showSection(util.select().position(generatorPos), Direction.DOWN);
    builder.idle(20);

    builder.overlay().showText(60)
        .text("The Solar Generator creates rotational force from sunlight")
        .pointAt(util.vector().blockSurface(generatorPos, Direction.UP))
        .placeNearTarget();
    builder.idle(70);
}
```

**Key Ponder APIs:**

| Call | Effect |
|---|---|
| `builder.title()` | Sets scene title |
| `builder.configureBasePlate(x, z, size)` | Defines world size |
| `builder.idle(ticks)` | Waits N ticks |
| `builder.addKeyframe()` | Creates chapter marker (players can skip to these) |
| `builder.world().showSection()` | Reveals blocks |
| `builder.world().setBlock()` | Changes a block |
| `builder.overlay().showText(duration)` | Shows text overlay |
| `.pointAt(vec)` | Adds arrow pointing to a location |
| `.placeNearTarget()` | Positions text near the arrow |

**Scenes in this mod:**
1. **basics** — shaft connections, rotation, wrench usage
2. **weather** — day/night cycle, rain effects, thunder
3. **obstructions** — sky access, glass vs solid, snow/carpet
4. **dyeing** — color changing with dye items

---

## 10. Key Concepts and Learning Guide

### 10.1 Object-Oriented Programming

**Inheritance:** Child class reuses and extends parent's behavior.
```java
// AdvSolarGeneratorBlockEntity gets all solar generator logic for free
// and only overrides speed to be 2x
public class AdvSolarGeneratorBlockEntity extends SolarGeneratorBlockEntity {
    @Override
    public float getGeneratedSpeed() {
        return super.getGeneratedSpeed() * 2;  // Not exactly, but conceptually
    }
}
```

**Interfaces:** Define a required contract.
```java
public class SolarGeneratorBlock extends RotatedPillarKineticBlock
        implements IBE<SolarGeneratorBlockEntity>, IWrenchable {
    // Must implement IBE's getBlockEntityClass() and IWrenchable's onWrenched()
}
```

---

### 10.2 Registry System

**DeferredRegister** (NeoForge): A waiting list for game objects.
```java
// 1. Create a register
DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);

// 2. Add entries (doesn't register yet)
DeferredBlock<SolarGeneratorBlock> SOLAR_GENERATOR =
    BLOCKS.register("solar_generator", () -> new SolarGeneratorBlock(props));

// 3. Register all entries when mod loads
BLOCKS.register(modEventBus);
```

**Fabric:** Direct registration in a `register()` method.
```java
static void register() {
    Registry.register(BuiltInRegistries.BLOCK,
        ResourceLocation.fromNamespaceAndPath(MOD_ID, "solar_generator"), SOLAR_GENERATOR);
}
```

---

### 10.3 Block States

Block states store a block's current configuration (facing, axis, powered, etc.).

```java
// Read a property
Direction.Axis axis = state.getValue(AXIS);

// Create new state with changed property (states are immutable)
BlockState newState = state.setValue(AXIS, Direction.Axis.X);

// Set a block in the world
level.setBlock(pos, newState, 3);  // 3 = update flags (notify neighbors + send to clients)
```

---

### 10.4 Block Entities

Block entities add "intelligence" to blocks. They can store data and run per-tick logic.

```java
@Override
public void tick() {
    super.tick();
    // Called 20 times per second on both sides
    if (!level.isClientSide) {
        // Server-only logic
    }
}
```

Pattern for reading/writing custom data:
```java
@Override
protected void write(CompoundTag tag, ...) {
    super.write(tag, ...);
    tag.putInt("MySavedValue", myValue);
}

@Override
protected void read(CompoundTag tag, ...) {
    super.read(tag, ...);
    myValue = tag.getInt("MySavedValue");
}
```

---

### 10.5 Client vs Server

| | Server | Client |
|---|---|---|
| Authority | Yes — source of truth | No — receives updates |
| World changes | Yes | Never |
| Rendering | No | Yes |
| Check: | `!level.isClientSide` | `level.isClientSide` |

The most common pattern:
```java
if (!level.isClientSide) {
    // Make the actual change
    level.setBlock(pos, newState, 3);
} else {
    // Play feedback sounds/particles
}
```

---

### 10.6 Create's Kinetic System

**RPM** — rotation speed. Positive = clockwise, negative = counter-clockwise.

**SU (Stress Units)** — capacity/load measure. Generators provide SU. Machines consume SU. If consumption exceeds capacity, the network stops.

**Kinetic networks** — groups of connected kinetic blocks that share RPM and track total capacity vs stress.

**Implementing a generator:**
```java
public class MyGenerator extends GeneratingKineticBlockEntity {

    @Override
    public float getGeneratedSpeed() {
        // return RPM, or 0 if not active
        return 16f;
    }

    @Override
    public float calculateAddedStressCapacity() {
        // return SU per RPM
        return 16f;
    }
}
```

**Windmill bracket system:**
- Every 8 sails = 1 bracket
- Each bracket = 512 SU
- `totalSU = (sailCount / 8) × 512`
- Create divides by RPM internally: `capacity_per_RPM = totalSU / RPM`

---

### 10.7 NBT Serialization

NBT (Named Binary Tag) is Minecraft's data serialization format for saving/loading block entity data.

```java
// Saving (called on disk write and client sync)
compound.putInt("SolarSails", solarSailCount);
compound.putFloat("Capacity", currentCapacity);
compound.putBoolean("HasSkyAccess", hasSkyAccess);

// Loading
solarSailCount = compound.getInt("SolarSails");
currentCapacity = compound.getFloat("Capacity");
hasSkyAccess = compound.getBoolean("HasSkyAccess");
```

`clientPacket = true` — syncing to client (for goggles display, etc.)  
`clientPacket = false` — saving to disk

---

## 11. Version Differences and Porting

This section documents every known difference between the three supported versions.

### 11.1 Mod Entry Point

| Version | Pattern |
|---|---|
| NeoForge 1.21.1 | Constructor receives `IEventBus` and `ModContainer` directly via injection |
| NeoForge 1.20.1 | No-arg constructor; bus retrieved via `FMLJavaModLoadingContext.get().getModEventBus()` |
| Fabric 1.20.1 | Implements `ModInitializer`; registration done via `onInitialize()` with static `register()` calls |

**NeoForge 1.21.1:**
```java
@Mod(CreatePhotomovement.MOD_ID)
public class CreatePhotomovement {
    public CreatePhotomovement(IEventBus modEventBus, ModContainer modContainer) {
        AllBlocks.BLOCKS.register(modEventBus);
        PMConfigs.register(ModLoadingContext.get(), modContainer);
    }
}
```

**NeoForge 1.20.1:**
```java
@Mod(CreatePhotomovement.MOD_ID)
public class CreatePhotomovement {
    public CreatePhotomovement() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        AllBlocks.BLOCKS.register(modEventBus);
        PMConfigs.register(ModLoadingContext.get());  // No ModContainer parameter
        MinecraftForge.EVENT_BUS.register(this);
    }
}
```

**Fabric 1.20.1:**
```java
public class CreatePhotomovement implements ModInitializer {
    @Override
    public void onInitialize() {
        AllBlocks.register();    // Static register() methods instead of DeferredRegister
        AllItems.register();
        AllBlockEntityTypes.register();
        AllCreativeTabs.register();
        AllContraptionTypes.register();
        PMConfigs.register();
    }
}
```

---

### 11.2 Block Registration

| Version | Pattern |
|---|---|
| NeoForge 1.21.1 | `DeferredRegister.Blocks` → `DeferredBlock<T>` |
| NeoForge 1.20.1 | `DeferredRegister.Blocks` → `DeferredBlock<T>` (same) |
| Fabric 1.20.1 | Direct static fields + `Registry.register()` calls |

Fabric `AllBlocks.java` declares blocks as plain static fields (not `DeferredBlock<T>`) and calls `Registry.register(BuiltInRegistries.BLOCK, ...)` directly in the `register()` method.

---

### 11.3 NBT Read/Write Signatures

This is the most common porting mistake.

| Version | Signature |
|---|---|
| NeoForge 1.21.1 | `write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket)` |
| NeoForge 1.20.1 | `write(CompoundTag tag, boolean clientPacket)` |
| Fabric 1.20.1 | `write(CompoundTag tag, boolean clientPacket)` |

**NeoForge 1.21.1:**
```java
@Override
protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
    super.write(compound, registries, clientPacket);
    compound.putInt("MySavedValue", myValue);
}

@Override
protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
    super.read(compound, registries, clientPacket);
    myValue = compound.getInt("MySavedValue");
}
```

**NeoForge 1.20.1 and Fabric 1.20.1:**
```java
@Override
public void write(CompoundTag compound, boolean clientPacket) {
    super.write(compound, clientPacket);
    compound.putInt("MySavedValue", myValue);
}

@Override
protected void read(CompoundTag compound, boolean clientPacket) {
    super.read(compound, clientPacket);
    myValue = compound.getInt("MySavedValue");
}
```

---

### 11.4 Config Registration

| Version | `PMConfigs.register()` signature |
|---|---|
| NeoForge 1.21.1 | `register(ModLoadingContext context, ModContainer container)` |
| NeoForge 1.20.1 | `register(ModLoadingContext context)` — no `ModContainer` |
| Fabric 1.20.1 | `register()` — no parameters, uses Fabric's AutoConfig or direct file access |

---

### 11.5 ContraptionType NBT (writeNBT/readNBT)

**NeoForge 1.21.1** — `writeNBT` receives `HolderLookup.Provider`:
```java
@Override
public CompoundTag writeNBT(HolderLookup.Provider registries, boolean spawnPacket) { ... }

@Override
public void readNBT(Level world, CompoundTag tag, boolean spawnData) { ... }
```

**NeoForge 1.20.1 and Fabric 1.20.1** — no `HolderLookup.Provider`:
```java
@Override
public CompoundTag writeNBT(boolean spawnPacket) { ... }

@Override
public void readNBT(Level world, CompoundTag tag, boolean spawnData) { ... }
```

---

### 11.6 Summary Checklist for Porting 1.20.1 → 1.21.1

- [ ] Change constructor signature to receive `IEventBus` and `ModContainer`.
- [ ] Remove `FMLJavaModLoadingContext.get()`.
- [ ] Remove `MinecraftForge.EVENT_BUS.register(this)`.
- [ ] Add `HolderLookup.Provider registries` parameter to all `write()` / `read()` overrides.
- [ ] Add `HolderLookup.Provider registries` parameter to `writeNBT()` in contraption classes.
- [ ] Update `PMConfigs.register()` to accept `ModContainer`.

---

## 12. Troubleshooting

**Generator not producing power:**
- Check sky access (F3 screen, or remove roof blocks and test).
- Verify it's daytime (`/time set day`).
- Remove obstructions — including snow, carpet, and moss carpet.
- Check kinetic network with Engineer's Goggles.

**Windmill capacity wrong:**
- Check if it's day vs night vs rain — multiplier changes.
- Reload the world (ghost SU fix clears on load).
- Read server logs at assembly — they show sail counts and hasSkyAccess.

**Windmill capacity doubled:**
- Ensure `SolarBearingContraption` is being used (not standard `BearingContraption`).
- Verify `sbc.setSailBlocks(0)` is called in `updateGeneratedRotation()`.
- Verify `this.capacity = 0` is set before `super.write()` on disk save.

**Solar sails not picked up by bearing:**
- Verify `AttachedCheck` is registered in the mod constructor.
- The check must return `CheckResult.of(true)` when `direction.getAxis() != FACING.getAxis()`.

**Code changes not applying:**
- Run `./gradlew build` then restart the game.
- Check that you're editing the correct version's source folder.

**Crash on startup:**
- Check that Create and NeoForge/Fabric versions match `gradle.properties`.
- Read the crash log — it always has a root cause at the bottom.
