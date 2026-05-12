# Create Photomovement - Code Documentation (Version 1.21.1)

**Last Updated:** February 7, 2026  
**Minecraft Version:** 1.21.1  
**Mod Loader:** NeoForge  
**Programming Language:** Java

---

## Table of Contents

1. [Overview](#overview)
2. [Project Structure](#project-structure)
3. [Main Entry Points](#main-entry-points)
4. [Registration Classes](#registration-classes)
5. [Solar Generator System](#solar-generator-system)
6. [Solar Windmill System](#solar-windmill-system)
7. [Configuration System](#configuration-system)
8. [Rendering System](#rendering-system)
9. [Ponder System (In-Game Tutorial)](#ponder-system)
10. [Key Concepts and Learning Guide](#key-concepts-and-learning-guide)

---

## Overview

Create Photomovement is a Minecraft mod that adds solar-powered kinetic devices to the Create mod. It introduces:

- **Solar Generators**: Generate rotational power from sunlight (vertical and horizontal variants)
- **Advanced Solar Generators**: Generate 2x the rotational power of regular solar generators
- **Solar Windmill Bearing**: A special bearing that provides bonus stress capacity when using Solar Sails
- **Solar Sails**: Windmill sails that provide double stress units under solar conditions

The mod integrates seamlessly with Create's kinetic system, following its patterns for rotational power generation and stress capacity.

---

## Project Structure

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
│       ├── solargenerator/            # Solar generator implementations
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
│       └── solarwindmill/             # Solar windmill implementations
│           ├── SolarWindmillBearingBlock.java
│           ├── SolarWindmillBearingBlockEntity.java
│           ├── SolarSailBlock.java
│           └── SolarBearingContraption.java
├── infrastructure/
│   └── config/                        # Configuration system
│       ├── PMConfigs.java
│       └── PMServer.java
└── ponder/                            # In-game tutorials
    ├── PhotomovementPonderPlugin.java
    └── SolarGeneratorScenes.java
```

---

## Main Entry Points

### CreatePhotomovement.java

**Purpose:** This is the main mod class that gets loaded when Minecraft starts. It registers all the mod's content and sets up event handlers.

**Key Components:**

```java
@Mod(CreatePhotomovement.MOD_ID)
public class CreatePhotomovement {
    public static final String MOD_ID = "createphotomovement";
```

- The `@Mod` annotation tells NeoForge this is the main mod class
- `MOD_ID` is used throughout the mod to identify this mod's content

**Constructor:** This is called when the mod is loaded. It performs these actions:

1. **Registers all content** with the mod event bus:
   ```java
   AllBlocks.BLOCKS.register(modEventBus);
   AllItems.ITEMS.register(modEventBus);
   AllCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
   AllBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
   AllContraptionTypes.register(modEventBus);
   ```
   - Each of these lines tells Minecraft about the blocks, items, creative tabs, block entities, and contraptions this mod adds

2. **Registers configuration**:
   ```java
   PMConfigs.register(ModLoadingContext.get(), modContainer);
   ```
   - This sets up the mod's configuration file that lets users change settings

3. **Registers movement checks for Solar Sails**:
   ```java
   com.simibubi.create.api.contraption.BlockMovementChecks
       .registerAttachedCheck((state, world, pos, direction) -> {
           if (state.getBlock() instanceof SolarSailBlock) {
               return CheckResult.of(
                   direction.getAxis() != state.getValue(SolarSailBlock.FACING).getAxis()
               );
           }
           return CheckResult.PASS;
       });
   ```
   - This tells Create how Solar Sails can be moved by contraptions
   - Solar Sails can only be moved perpendicular to their facing direction

**What this means for learning:**
- The constructor is like the "setup" function - it runs once when Minecraft starts
- It connects all the different pieces of the mod together
- The `modEventBus` is like a notification system - when you "register" something, you're telling Minecraft "I have this new thing, please add it to the game"

---

### CreatePhotomovementClient.java

**Purpose:** This class handles client-side (visual) setup. It's only loaded on the client, not on dedicated servers.

**Key Methods:**

1. **onClientSetup()** - Called during client initialization:
   ```java
   @SubscribeEvent
   public static void onClientSetup(FMLClientSetupEvent event) {
       PonderIndex.addPlugin(new PhotomovementPonderPlugin());
   }
   ```
   - Registers the Ponder plugin for in-game tutorials
   - The `@SubscribeEvent` annotation means this method listens for the client setup event

2. **registerRenderers()** - Registers custom renderers for blocks:
   ```java
   @SubscribeEvent
   public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
       event.registerBlockEntityRenderer(AllBlockEntityTypes.SOLAR_GENERATOR.get(), 
           SolarGeneratorRenderer::new);
       // ... more renderer registrations
   }
   ```
   - Tells Minecraft how to draw each solar generator variant on screen
   - Uses the `::new` syntax which is a "method reference" - a shorthand for creating new objects

**What this means for learning:**
- Client code handles visual effects, rendering, and user interface
- Server code handles game logic, world changes, and data
- This separation ensures the mod works on both singleplayer and multiplayer servers

---

## Registration Classes

### AllBlocks.java

**Purpose:** Registers all blocks (placeable objects in the world) that this mod adds.

**Structure:**
```java
public class AllBlocks {
    public static final DeferredRegister.Blocks BLOCKS = 
        DeferredRegister.createBlocks(CreatePhotomovement.MOD_ID);
```

**What is DeferredRegister?**
- Think of it as a "waiting list" for blocks
- Instead of immediately adding blocks to the game, we add them to this list
- Later, the main mod class calls `.register(modEventBus)` to actually add them all at once
- This is more efficient and prevents timing issues

**Block Registration Pattern:**
```java
public static final DeferredBlock<SolarGeneratorBlock> SOLAR_GENERATOR = 
    BLOCKS.register("solar_generator",
        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOD)        // Appears wood-colored on maps
            .strength(1.5F)                 // Takes 1.5 seconds to break
            .sound(SoundType.WOOD)          // Makes wood sounds
            .noOcclusion()));               // Doesn't block light
```

**Explanation:**
- `DeferredBlock<T>` is a holder for a block that will be registered later
- `BLOCKS.register(name, supplier)` adds the block to the registry
- The `() ->` creates a "supplier" - a function that creates the block when needed
- `BlockBehaviour.Properties` defines physical properties like hardness, map color, sounds

**Color Variants:**
The mod registers 16 color variants for each generator type (matching Minecraft's dye colors):
- White, Orange, Magenta, Light Blue, Yellow, Lime, Pink, Gray, Light Gray, Cyan, Purple, Blue, Brown, Green, Red, Black

This is done by repeating the registration pattern with different names and map colors.

**Solar Sails:**
```java
public static final DeferredBlock<SolarSailBlock> SOLAR_SAIL = BLOCKS.register(
    "solar_sail",
    () -> SolarSailBlock.withCanvas(BlockBehaviour.Properties.of()
        .mapColor(MapColor.WOOL)
        .sound(SoundType.WOOL)
        .strength(0.8F),
        DyeColor.WHITE));
```

- Solar Sails use `.withCanvas()` factory method
- They're softer (0.8F strength) than generators (1.5F)
- Use wool sounds and map color to feel lighter

**What this means for learning:**
- Each block needs a unique registry name (like "solar_generator")
- Properties define how the block behaves physically
- Color variants are separate blocks with different names

---

### AllItems.java

**Purpose:** Registers all items (things in the player's inventory) that this mod adds.

**Structure:**
```java
public class AllItems {
    public static final DeferredRegister.Items ITEMS = 
        DeferredRegister.createItems(CreatePhotomovement.MOD_ID);
```

**Item Registration Pattern:**
```java
public static final DeferredItem<BlockItem> SOLAR_GENERATOR = 
    ITEMS.registerSimpleBlockItem("solar_generator", AllBlocks.SOLAR_GENERATOR);
```

**What is a BlockItem?**
- It's the item form of a block
- When you place a block, you're using its BlockItem
- When you break a block, it drops its BlockItem
- `registerSimpleBlockItem()` automatically creates a BlockItem for a block

**Why separate items and blocks?**
- Blocks exist in the world (placed)
- Items exist in inventories
- They need separate registrations even though they're connected

**What this means for learning:**
- Most blocks need a corresponding item so players can hold and place them
- The item registry mirrors the block registry
- Simple blocks use `registerSimpleBlockItem()` for convenience

---

### AllBlockEntityTypes.java

**Purpose:** Registers Block Entity Types. Block Entities are like "brains" for blocks that need to store data or perform complex logic.

**What is a Block Entity?**
- Normal blocks are simple - they just exist in the world
- Block Entities can:
  - Store custom data (like how much energy they have)
  - Run code every game tick (like checking sunlight)
  - Have custom rendering
- Examples in vanilla Minecraft: Chests, Furnaces, Signs

**Registration Pattern:**
```java
public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarGeneratorBlockEntity>> 
    SOLAR_GENERATOR = BLOCK_ENTITY_TYPES.register("solar_generator",
        () -> BlockEntityType.Builder.of(
            (pos, state) -> new SolarGeneratorBlockEntity(
                AllBlockEntityTypes.SOLAR_GENERATOR.get(), pos, state),
            AllBlocks.SOLAR_GENERATOR.get(),
            // List all color variants that use this same block entity
            AllBlocks.WHITE_SOLAR_GENERATOR.get(),
            AllBlocks.ORANGE_SOLAR_GENERATOR.get(),
            // ... more variants
        ).build(null));
```

**Explanation:**
- `BlockEntityType.Builder.of()` creates a builder
- First parameter: A factory function that creates the block entity
- Remaining parameters: All blocks that use this block entity type
- `.build(null)` finalizes the builder (null means no data fixer)

**Why do color variants share a block entity?**
- The color is just visual (the block model)
- The logic (power generation) is the same for all colors
- This saves memory and reduces code duplication

**What this means for learning:**
- Block Entities add "intelligence" to blocks
- Multiple blocks can share the same block entity type if they work the same way
- The `(pos, state)` parameters tell the block entity where it is and what block it represents

---

### AllCreativeTabs.java

**Purpose:** Creates and populates the Creative Mode inventory tab for this mod.

**Creative Mode Tabs:**
- When you open the Creative inventory, you see tabs at the top
- Each mod can add its own tab
- Players can browse all mod items in this tab

**Registration:**
```java
public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = 
    CREATIVE_MODE_TABS.register("main",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.createphotomovement"))
            .icon(() -> AllItems.SOLAR_GENERATOR.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(AllItems.SOLAR_GENERATOR.get());
                output.accept(AllItems.HORIZONTAL_SOLAR_GENERATOR.get());
                // ... more items
            })
            .build());
```

**Explanation:**
- `.title()` sets the tab name (uses translation for multiple languages)
- `.icon()` sets the tab icon (appears at top of creative menu)
- `.displayItems()` defines what items appear in the tab
- `output.accept()` adds each item to the tab

**Item Order:**
The mod organizes items in a specific order for better user experience:
1. Basic generators (clear glass)
2. Advanced generators (both vertical and horizontal)
3. Color variants (organized by Minecraft's standard dye order)
4. Solar Sails

**What this means for learning:**
- Creative tabs organize mod content for players
- The order matters - put most important items first
- Translation keys (like "itemGroup.createphotomovement") must be defined in language files

---

### AllContraptionTypes.java

**Purpose:** Registers custom Contraption types for Create's contraption system.

**What is a Contraption?**
- In Create, contraptions are moving structures (like windmills, elevators, trains)
- When you assemble a contraption, it becomes a single entity that moves together
- Each type of contraption needs a registered type

**Registration:**
```java
public static final DeferredHolder<ContraptionType, ContraptionType> SOLAR_BEARING = 
    CONTRAPTION_TYPES.register("solar_bearing", 
        () -> new ContraptionType(SolarBearingContraption::new));
```

**Explanation:**
- `SolarBearingContraption::new` is a reference to the constructor
- This tells Create how to create a new solar bearing contraption
- The contraption type is used when assembling/disassembling structures

**What this means for learning:**
- Contraptions are advanced Create features
- They turn multiple blocks into a single moving entity
- Each contraption type needs a factory function to create instances

---

## Solar Generator System

This section explains how solar generators work, from the block you place to the power they generate.

### SolarGeneratorBlock.java

**Purpose:** Defines the Block class for vertical solar generators.

**Class Hierarchy:**
```java
public class SolarGeneratorBlock extends RotatedPillarKineticBlock
        implements IBE<SolarGeneratorBlockEntity>, IWrenchable
```

**Explanation of inheritance and interfaces:**
- `extends RotatedPillarKineticBlock`: Inherits from Create's class for blocks that generate rotation and can be rotated along an axis (like logs)
- `implements IBE<SolarGeneratorBlockEntity>`: Marks this as having an associated Block Entity
- `implements IWrenchable`: Allows players to use a wrench to rotate/configure this block

**Key Features:**

1. **Color Mapping System:**
   ```java
   private static final Map<DyeColor, Supplier<Block>> COLOR_TO_BLOCK = new HashMap<>();
   
   static {
       COLOR_TO_BLOCK.put(DyeColor.WHITE, () -> AllBlocks.WHITE_SOLAR_GENERATOR.get());
       COLOR_TO_BLOCK.put(DyeColor.ORANGE, () -> AllBlocks.ORANGE_SOLAR_GENERATOR.get());
       // ... more colors
   }
   ```
   - `Map<K, V>` is a data structure that stores key-value pairs
   - `DyeColor` is the key (the dye), `Supplier<Block>` is the value (which block to use)
   - `static { }` is a "static initializer" - runs once when the class loads
   - This creates a lookup table: "For this dye color, use this block"

2. **Shaft Connection Logic:**
   ```java
   @Override
   public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
       return face.getAxis() == state.getValue(AXIS);
   }
   ```
   - Called by Create to determine which sides can connect to shafts
   - `state.getValue(AXIS)` gets the block's rotation axis (X, Y, or Z)
   - Returns true only for the two faces aligned with that axis
   - Example: If axis is X, only the east and west faces can connect to shafts

3. **Placement Logic:**
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
   - Called when a player places the block
   - `context` contains information about how/where the block was placed
   - Normal placement: axis faces the player
   - Shift + placement: axis is rotated 90 degrees (clockwise)

4. **Dye Interaction:**
   ```java
   @Override
   protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, 
           BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
       
       if (stack.getItem() instanceof DyeItem dyeItem) {
           DyeColor color = dyeItem.getDyeColor();
           // Skip modded dye colors that aren't in our color map to avoid NPEs / world corruption
           if (!COLOR_TO_BLOCK.containsKey(color)) {
               return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
           }
           Block targetBlock = COLOR_TO_BLOCK.get(color).get();
           
           if (state.getBlock() == targetBlock) {
               return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
           }
           
           if (!level.isClientSide) {
               Direction.Axis currentAxis = state.getValue(AXIS);
               BlockState newState = targetBlock.defaultBlockState().setValue(AXIS, currentAxis);
               level.setBlock(pos, newState, 3);
               level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
               
               if (!player.isCreative()) {
                   stack.shrink(1);
               }
           }
           
           return ItemInteractionResult.sidedSuccess(level.isClientSide);
       }
       
       return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }
   ```

   **Step-by-step breakdown:**
   - Check if player is holding a dye: `stack.getItem() instanceof DyeItem`
   - Get which color: `dyeItem.getDyeColor()`
   - Check we recognize this color: `COLOR_TO_BLOCK.containsKey(color)` — bail out with `PASS_TO_DEFAULT_BLOCK_INTERACTION` if not (modded dye)
   - Look up the target block: `COLOR_TO_BLOCK.get(color).get()`
   - If already that color, do nothing (early return)
   - `!level.isClientSide` means "only on server"  
     (changes to the world must happen server-side to sync to all players)
   - Get current rotation and preserve it: `currentAxis`
   - Create new colored block with same rotation: `newState`
   - Replace the block: `level.setBlock(pos, newState, 3)`
   - Play dye sound
   - Consume dye unless creative mode
   - Return success result

5. **Wrench Interaction:**
   ```java
   @Override
   public InteractionResult onWrenched(BlockState state, UseOnContext context) {
       Level level = context.getLevel();
       BlockPos pos = context.getClickedPos();
       
       if (level.isClientSide)
           return InteractionResult.SUCCESS;
       
       Direction.Axis currentAxis = state.getValue(AXIS);
       Direction.Axis newAxis = currentAxis == Direction.Axis.X ? 
           Direction.Axis.Z : Direction.Axis.X;
       
       level.setBlock(pos, state.setValue(AXIS, newAxis), 3);
       level.playSound(null, pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, 
           SoundSource.BLOCKS, 1.0F, 1.0F);
       return InteractionResult.SUCCESS;
   }
   ```
   
   **Explanation:**
   - Called when player uses wrench on the block
   - Only rotates between horizontal axes (X and Z)
   - Doesn't use Y axis (vertical) - keeps solar panel facing up
   - Toggles between X and Z: `currentAxis == X ? Z : X`

**What this means for learning:**
- Blocks handle player interactions and physical properties
- Color variants are achieved by replacing the block, not storing color data
- Preserving state (like rotation) when changing blocks is important for good user experience
- Server-side checks (`!level.isClientSide`) prevent cheating and ensure synchronization

---

### SolarGeneratorBlockEntity.java

**Purpose:** The "brain" of the solar generator that calculates power generation every game tick.

**Class Hierarchy:**
```java
public class SolarGeneratorBlockEntity extends GeneratingKineticBlockEntity
```

- `GeneratingKineticBlockEntity` is Create's base class for blocks that generate rotational power
- Provides the framework for generating RPM and stress capacity (SU)

**Key Methods:**

1. **Initialization:**
   ```java
   @Override
   public void onLoad() {
       super.onLoad();
       if (level != null && !level.isClientSide) {
           updateGeneratedRotation();
       }
   }
   ```
   - `onLoad()` is called when the block entity is first loaded (world load or chunk load)
   - Forces an immediate update to sync with the kinetic network
   - Important for preventing "ghost power" bugs after server restarts

2. **Speed Calculation:**
   ```java
   @Override
   public float getGeneratedSpeed() {
       if (!canGeneratePower())
           return 0;
       
       float speed = PMConfigs.server().generationSpeed.get();
       
       if (level != null && level.isRainingAt(worldPosition.above())) {
           speed = speed / 2;
       }
       return speed;
   }
   ```
   
   **Explanation:**
   - Called every tick to determine current RPM
   - `PMConfigs.server().generationSpeed.get()` reads from config file (default: 16 RPM)
   - `worldPosition.above()` checks the block directly above
   - Rain reduces power by 50% (8 RPM instead of 16 RPM)
   - Returns 0 if conditions aren't met (night, obstructions, etc.)

3. **Stress Capacity Calculation:**
   ```java
   @Override
   public float calculateAddedStressCapacity() {
       return PMConfigs.server().stressCapacity.get();
   }
   ```
   - Stress capacity (SU) determines how much load this generator can handle
   - Read from config (default: 16 SU)
   - Advanced variant overrides this to return 2x capacity

4. **Power Generation Check:**
   ```java
   protected boolean canGeneratePower() {
       if (level == null)
           return false;
       
       // Must see sky
       if (!level.canSeeSky(worldPosition.above()))
           return false;
       
       // Need light level 12 or higher (daytime)
       int skyLight = level.getBrightness(LightLayer.SKY, worldPosition.above());
       int currentSkyLight = skyLight - level.getSkyDarken();
       if (currentSkyLight < 12) {
           return false;
       }
       
       // Check for obstructing blocks
       BlockPos abovePos = worldPosition.above();
       BlockState aboveState = level.getBlockState(abovePos);
       Block aboveBlock = aboveState.getBlock();
       
       // Block specific obstructions
       if (aboveBlock instanceof SnowLayerBlock) return false;
       if (aboveBlock instanceof CarpetBlock) return false;
       if (aboveBlock == Blocks.MOSS_CARPET) return false;
       if (aboveBlock == Blocks.SNOW) return false;
       
       // Block light-blocking blocks
       if (aboveState.getLightBlock(level, abovePos) > 0)
           return false;
       
       return true;
   }
   ```
   
   **Step-by-step explanation:**
   1. **Sky visibility check:** The block above must have a direct path to the sky
      - `level.canSeeSky()` returns false if there are solid blocks in the way
      - Glass and other transparent blocks return true
   
   2. **Light level check:** Must have skylight level 12 or higher
      - `level.getBrightness(LightLayer.SKY, pos)` gets the raw sky light
      - `level.getSkyDarken()` accounts for time of day (0 at noon, 11 at night)
      - Net result: Requires daytime (roughly 6 AM to 6 PM)
      - Thunder also reduces sky light, stopping generation
   
   3. **Specific block checks:** Snow layers, carpets, and moss carpet block generation
      - These are special cases that don't block sky visibility but still obstruct
   
   4. **Light blocking check:** Any block that blocks light (opacity > 0) stops generation
      - `.getLightBlock()` returns how much light a block blocks
      - 0 = transparent (air, glass)
      - 15 = fully opaque (stone, dirt)

5. **Continuous Update Loop:**
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
   
   **Explanation:**
   - `tick()` is called 20 times per second (every game tick)
   - Only runs on server (clients sync from server)
   - Checks if current speed matches target speed
   - Updates the kinetic network if speed changed
   - `Math.signum()` checks the sign (positive vs negative) to detect direction changes

**Important Concepts:**

- **Skylight vs Block Light:**
  - Sky light: Natural light from the sun/moon
  - Block light: Artificial light from torches, lava, etc.
  - Solar generators only work with sky light

- **Client vs Server:**
  - Server: Calculates game logic, makes decisions
  - Client: Displays graphics, plays sounds, handles input
  - `!level.isClientSide` means "only on server"
  - This prevents:
    - Client-side modifications (cheating)
    - Double-execution of code
    - Desync between client and server

- **Kinetic Networks:**
  - Create groups connected kinetic blocks into networks
  - When one block changes speed, the entire network updates
  - `updateGeneratedRotation()` tells the network to recalculate

**What this means for learning:**
- Block entities run complex logic that blocks can't handle
- Environmental checks (light, weather, obstructions) make generators feel realistic
- Game ticks create smooth, continuous updates (20 times per second)
- Server-side logic ensures fair multiplayer gameplay

---

### HorizontalSolarGeneratorBlock.java

**Purpose:** Horizontal variant of the solar generator that faces sideways instead of up.

**Key Differences from Vertical Generator:**

1. **Different Base Class:**
   ```java
   public class HorizontalSolarGeneratorBlock extends HorizontalKineticBlock
   ```
   - `HorizontalKineticBlock` is for blocks that rotate around a horizontal axis
   - Examples: Water wheels, mechanical bearings pointing sideways

2. **Facing Direction:**
   ```java
   @Override
   public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
       return face == state.getValue(HORIZONTAL_FACING).getOpposite();
   }
   ```
   - Only one shaft connection (back side)
   - `HORIZONTAL_FACING` stores which direction the panel faces
   - Shaft comes out the opposite side (back/gearbox side)

3. **Placement Behavior:**
   ```java
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
       Direction facing = context.getHorizontalDirection().getOpposite();
       if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
           facing = context.getHorizontalDirection();
       }
       return defaultBlockState().setValue(HORIZONTAL_FACING, facing);
   }
   ```
   - Normal: Faces toward player (panel visible from front)
   - Shift+place: Faces away from player (panel on back)
   - Only uses horizontal directions (North, South, East, West)

4. **Rotation with Wrench:**
   ```java
   @Override
   public BlockState rotate(BlockState state, Rotation rotation) {
       return state.setValue(HORIZONTAL_FACING, rotation.rotate(state.getValue(HORIZONTAL_FACING)));
   }
   ```
   - Uses Create's built-in rotation system
   - Rotates in 90-degree increments around vertical axis

5. **Force Update on Placement:**
   ```java
   @Override
   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
       super.onPlace(state, level, pos, oldState, movedByPiston);
       if (!level.isClientSide && state.getBlock() == this) {
           withBlockEntityDo(level, pos, HorizontalSolarGeneratorBlockEntity::forceUpdate);
       }
   }
   ```
   - `onPlace()` runs when block is placed or changed
   - Immediately tells the block entity to recalculate power
   - Prevents delay before power generation starts

**What this means for learning:**
- Horizontal blocks use `HORIZONTAL_FACING` instead of `AXIS`
- Different base classes provide different rotation behaviors
- Force updates ensure responsive gameplay

---

### HorizontalSolarGeneratorBlockEntity.java

**Purpose:** Block entity for horizontal solar generators with time-of-day based power scaling.

**Key Differences from Vertical Generator:**

1. **Dynamic Stress Capacity:**
   ```java
   private float currentStressCapacity;
   private int updateTimer;
   private int warmup = 10;
   ```
   - Unlike vertical generators (fixed 16 SU), horizontal generators have variable capacity
   - Capacity changes based on time of day and sun position
   - Checked every 10 seconds (200 ticks) for efficiency

2. **Speed Calculation:**
   ```java
   @Override
   public float getGeneratedSpeed() {
       if (!canGeneratePower()) {
           return 0;
       }
       float speed = PMConfigs.server().generationSpeed.get();
       
       if (level != null && level.isRainingAt(
               worldPosition.relative(getBlockState().getValue(HORIZONTAL_FACING)))) {
           speed = speed / 2;
       }
       return speed;
   }
   ```
   - Same as vertical (16 RPM)
   - Checks rain in front of panel instead of above
   - Uses `worldPosition.relative(facing)` to check the correct position

3. **Power Generation Check:**
   ```java
   protected boolean canGeneratePower() {
       if (level == null)
           return false;
       
       BlockState state = getBlockState();
       Direction facing = state.getValue(HORIZONTAL_FACING);
       BlockPos frontPos = worldPosition.relative(facing);
       
       // Check skylight in front of panel
       int skyLight = level.getBrightness(LightLayer.SKY, frontPos);
       int currentSkyLight = skyLight - level.getSkyDarken();
       if (currentSkyLight < 12) {
           return false;
       }
       
       // Block directly in front must not be solid
       BlockState frontState = level.getBlockState(frontPos);
       if (frontState.getLightBlock(level, frontPos) > 0) {
           return false;
       }
       
       return true;
       }
   ```
   - Checks the block in front of panel, not above
   - Immediate obstruction (1 block away) stops all power
   - More lenient than vertical (doesn't need direct sky access)

4. **Stress Capacity Calculation (The Complex Part):**
   ```java
   private void updateStressCapacity() {
       // First verify base generation conditions
       if (!canGeneratePower()) {
           if (currentStressCapacity != 0) {
               currentStressCapacity = 0;
               updateGeneratedRotation();
               notifyUpdate();
           }
           return;
       }
       
       BlockState state = getBlockState();
       Direction facing = state.getValue(HORIZONTAL_FACING);
       
       float base = PMConfigs.server().stressCapacity.get();  // 16
       float min = 8;   // Minimum capacity
       float newCapacity = min;
       
       // Check for distant obstructions (2-10 blocks away)
       boolean distantObstruction = false;
       for (int i = 2; i <= 10; i++) {
           BlockPos checkPos = worldPosition.relative(facing, i);
           BlockState checkState = level.getBlockState(checkPos);
           if (checkState.getLightBlock(level, checkPos) > 0) {
               distantObstruction = true;
               break;
           }
       }
       
       if (distantObstruction) {
           newCapacity = min;  // 8 SU if obstructed
       } else {
           // Calculate time-based capacity
           long time = level.getDayTime() % 24000;  // 0-23999
           float peak = 4 * base;  // 64 SU
           
           long daylightTime = Math.min(time, 12000);
           float ratio = (float) daylightTime / 12000.0f;  // 0.0 to 1.0
           ratio = Mth.clamp(ratio, 0f, 1f);
           
           if (facing == Direction.EAST) {
               // East-facing: High at sunrise, low at sunset
               float factor = (1 - ratio) * (1 - ratio);  // Quadratic falloff
               newCapacity = min + (peak - min) * factor;
           } else if (facing == Direction.WEST) {
               // West-facing: Low at sunrise, high at sunset
               float factor = ratio * ratio;  // Quadratic increase
               newCapacity = min + (peak - min) * factor;
           } else {
               // North/South: Constant minimum
               newCapacity = min;
           }
       }
       
       newCapacity = Math.round(newCapacity);  // Integer for clean display
       
       if (Math.abs(newCapacity - currentStressCapacity) > 0.01f) {
           currentStressCapacity = newCapacity;
           updateGeneratedRotation();
           notifyUpdate();
       }
   }
   ```
   
   **Detailed Explanation:**
   
   **Time of Day Calculation:**
   - Minecraft's day cycle: 24000 ticks = 20 minutes real time
   - Time 0 = 6 AM (sunrise)
   - Time 6000 = 12 PM (noon)
   - Time 12000 = 6 PM (sunset)
   - Time 18000 = 12 AM (midnight)
   
   **Capacity Ranges:**
   - Minimum: 8 SU (always available if generating)
   - Maximum: 64 SU (4x the base 16 SU)
   - Range: 56 SU variation throughout the day
   
   **East-Facing Generators:**
   - Best at sunrise (time 0): ~64 SU
   - Worst at sunset (time 12000): ~8 SU
   - Formula: `(1 - ratio)²` creates smooth falloff
   - Simulates sun moving from east to west
   
   **West-Facing Generators:**
   - Worst at sunrise: ~8 SU
   - Best at sunset: ~64 SU
   - Formula: `ratio²` creates smooth increase
   - Catches afternoon/evening sun
   
   **North/South Facing:**
   - Constant 8 SU (minimum)
   - Sun never directly hits from these directions
   
   **Quadratic Curves:**
   - `x²` creates accelerating curves (slow start, fast end)
   - `(1-x)²` creates decelerating curves (fast start, slow end)
   - Makes power changes feel natural, not linear

   **Distant Obstructions:**
   - Immediate obstruction (1 block): No power at all
   - Obstructions 2-10 blocks away: Power but minimum capacity (8 SU)
   - No obstructions within 10 blocks: Full time-based capacity
   - Simulates shading effects

5. **Data Persistence:**
   ```java
   @Override
   protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
       super.write(compound, registries, clientPacket);
       if (clientPacket) {
           compound.putFloat("CurrentStressCapacity", currentStressCapacity);
       }
   }
   
   @Override
   protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
       super.read(compound, registries, clientPacket);
       if (clientPacket) {
           currentStressCapacity = compound.getFloat("CurrentStressCapacity");
       }
   }
   ```
   
   **Explanation:**
   - `write()` saves data to NBT (Named Binary Tag) format
   - `read()` loads data from NBT
   - `clientPacket = true` means syncing to client for display
   - `clientPacket = false` means saving to disk
   - Only syncs capacity to clients (for goggles display)
   - Server recalculates everything on load

6. **Update Timing:**
   ```java
   @Override
   public void tick() {
       super.tick();
       
       if (level == null || level.isClientSide)
           return;
       
       if (warmup > 0) {
           warmup--;
           if (warmup == 0) {
               updateStressCapacity();
           }
           return;
       }
       
       if (updateTimer++ >= 200) {  // Every 10 seconds
           updateTimer = 0;
           updateStressCapacity();
       }
   }
   ```
   
   **Explanation:**
   - 10-tick warmup prevents immediate recalculation on chunk load
   - After warmup, checks every 200 ticks (10 seconds)
   - Infrequent checking improves performance
   - Players won't notice 10-second delays in gradual capacity changes

**Advanced Generators (Adv variants):**
```java
public class AdvSolarGeneratorBlockEntity extends SolarGeneratorBlockEntity {
    @Override
    public float getGeneratedSpeed() {
        if (!canGeneratePower())
            return 0;
        
        float generatedSpeed = PMConfigs.server().generationSpeed.get() * 2;
        
        if (level != null && level.isRainingAt(worldPosition.above())) {
            generatedSpeed = generatedSpeed / 2;
        }
        return generatedSpeed;
    }
}
```
- Extends the regular generator
- Only overrides speed to be 2x (32 RPM instead of 16 RPM)
- Everything else is identical
- Same pattern for horizontal advanced variant

**What this means for learning:**
- Time-based mechanics require understanding Minecraft's time system
- Quadratic formulas create realistic, smooth transitions
- Different facing directions can have different behaviors
- Infrequent updates improve performance for calculations that don't need real-time precision
- Inheritance allows code reuse (advanced generators)

---

## Solar Windmill System

The solar windmill system is more complex than generators because it involves moving contraptions.

### SolarWindmillBearingBlock.java

**Purpose:** The block players place to create a solar windmill contraption.

**Class Hierarchy:**
```java
public class SolarWindmillBearingBlock extends WindmillBearingBlock
```
- Extends Create's windmill bearing
- Adds solar-specific functionality

**Key Method:**
```java
@Override
public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
    return (l, p, s, be) -> {
        if (be instanceof SolarWindmillBearingBlockEntity solarBe) {
            solarBe.tick();           // Standard tick (from parent)
            solarBe.solarTick();       // Solar logic tick (custom)
        }
    };
}
```

**Explanation:**
- `getTicker()` returns a function that runs every game tick
- Lambda syntax: `(l, p, s, be) -> { }` is shorthand for a function
- Calls both regular tick and solar tick
- This dual-tick system allows:
  - Standard windmill logic (rotation, contraption control)
  - Additional solar bonus logic (capacity multipliers)

**What this means for learning:**
- Lambda expressions create inline functions
- Custom tickers allow per-tick custom behavior
- Dual-tick pattern separates concerns (windmill logic vs solar logic)

---

### SolarWindmillBearingBlockEntity.java

**Purpose:** The complex logic for solar windmill bearings - tracks two types of sails and applies solar bonuses.

**Important Context:**
The comments at the top reveal the challenges:
```java
/*
 * I want to thank stackoverflow, reddit, my prof who inspired me......
 * who am I kidding this shit took 5 whole days to make, troubleshoot and test.
 * 
 * Biggest problem is that I am using AI to code all of this and I don't have
 * any clue about java or how the Create mod works.
 * I surely did learn a bit how Create works even if it's just a tiny bit.
 * 
 * Why am I doing this again?
 * 
 * - The SU doubling bug fix (2026-02-04)
 */
```
This is honest documentation about the learning process! The bug mentioned was stress units being counted twice.

**Key Features:**

1. **Sail Tracking:**
   ```java
   private int regularSailCount = 0;
   private int solarSailCount = 0;
   private boolean hasSkyAccess = false;
   private float lastSolarMultiplier = -1;
   private int warmup = 10;
   ```
   - Tracks two sail types separately
   - Regular sails: Normal Create windmill sails (wool)
   - Solar sails: This mod's solar sails (2x SU bonus)
   - Sky access: Checked once at assembly, cached
   - Warmup delay: Prevents instant recalculation on chunk load

2. **Solar Multiplier System:**
   ```java
   private float getSolarMultiplier() {
       if (!hasSkyAccess) return 1.0f;
       if (isNight()) return 1.0f;
       if (isThundering()) return 1.0f;
       if (isRaining()) return 1.5f;
       return 2.0f;  // Full solar bonus
   }
   ```
   
   **Conditions and Multipliers:**
   - **No sky access:** 1.0x (no bonus) - Trees, buildings blocking sky
   - **Night time:** 1.0x (no bonus) - No sunlight
   - **Thunderstorm:** 1.0x (no bonus) - Dark clouds
   - **Rain:** 1.5x (reduced bonus) - Some light still gets through
   - **Clear day:** 2.0x (full bonus) - Maximum solar power!
   
   This multiplier only affects SU (stress capacity), not RPM (speed).

3. **Custom Assembly Process:**
   ```java
   @Override
   public void assemble() {
       if (!(level.getBlockState(worldPosition).getBlock() instanceof BearingBlock))
           return;
       
       Direction direction = getBlockState().getValue(BearingBlock.FACING);
       
       // Use custom contraption type that tracks solar sails
       SolarBearingContraption contraption = new SolarBearingContraption(true, direction);
       try {
           if (!contraption.assemble(level, worldPosition))
               return;
           lastException = null;
       } catch (AssemblyException e) {
           lastException = e;
           sendData();
           return;
       }
       
       // Cache sail counts from contraption
       this.solarSailCount = contraption.getSolarSailBlocks();
       this.regularSailCount = contraption.getRegularSailBlocks();
       this.hasSkyAccess = contraption.hasSkyAccess();
       
       // Standard Create progression awards
       award(AllAdvancements.WINDMILL);
       if (contraption.getSailBlocks() >= 16 * 8)
           award(AllAdvancements.WINDMILL_MAXED);
       
       // Convert blocks to moving entity
       contraption.removeBlocksFromWorld(level, BlockPos.ZERO);
       movedContraption = ControlledContraptionEntity.create(level, this, contraption);
       BlockPos anchor = worldPosition.relative(direction);
       movedContraption.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
       movedContraption.setRotationAxis(direction.getAxis());
       level.addFreshEntity(movedContraption);
       
       AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(level, worldPosition);
       
       if (contraption.containsBlockBreakers())
           award(AllAdvancements.CONTRAPTION_ACTORS);
       
       running = true;
       angle = 0;
       sendData();
       updateGeneratedRotation();
   }
   ```
   
   **Step-by-step breakdown:**
   1. Verify this is actually a bearing block
   2. Get which direction it's facing
   3. Create custom `SolarBearingContraption` instead of regular `BearingContraption`
   4. Attempt to assemble (checks for glue, size limits, etc.)
   5. Cache the counts - this is critical for performance (don't recalculate every tick)
   6. Award advancements if requirements met
   7. Remove blocks from world (they become part of the entity)
   8. Create moving entity to hold the contraption
   9. Position and orient the entity
   10. Add it to the world
   11. Play assembly sound
   12. Mark as running and update network

4. **Speed Calculation:**
   ```java
   @Override
   public float getGeneratedSpeed() {
       if (!running) {
           return 0;
       }
       if (movedContraption == null) {
           return 0;
       }
       
       int totalSails = regularSailCount + solarSailCount;
       int sailsPerRPM = AllConfigs.server().kinetics.windmillSailsPerRPM.get();
       int rpm = totalSails / sailsPerRPM;
       
       float speed = Mth.clamp(rpm, 1, 16) * getAngleSpeedDirection();
       
       return speed;
   }
   ```
   
   **Explanation:**
   - Both sail types count equally for RPM calculation
   - Default: 8 sails per 1 RPM
   - So 64 sails = 8 RPM, 128 sails = 16 RPM (max)
   - `Mth.clamp(rpm, 1, 16)` ensures 1-16 RPM range
   - `getAngleSpeedDirection()` determines clockwise vs counter-clockwise

5. **Stress Capacity Calculation (The Critical Part):**
   ```java
   @Override
   public float calculateAddedStressCapacity() {
       // Guard against invalid bearings
       if (!running || movedContraption == null)
           return 0;
       
       // Get Create's windmill config
       int sailsPerBracket = AllConfigs.server().kinetics.windmillSailsPerRPM.get();  // 8
       float suPerBracket = 512f;
       
       // Normal sails: standard bracket calculation
       int normalBrackets = regularSailCount / sailsPerBracket;
       float normalSU = normalBrackets * suPerBracket;
       
       // Solar sails: bracket calculation WITH solar multiplier
       int solarBrackets = solarSailCount / sailsPerBracket;
       float solarMultiplier = getSolarMultiplier();
       float solarSU = solarBrackets * suPerBracket * solarMultiplier;
       
       // Total capacity is the sum
       float totalSU = normalSU + solarSU;
       
       // Divide by RPM to get the per-RPM value (Create's internal representation)
       int totalSails = regularSailCount + solarSailCount;
       int rpm = Math.max(1, totalSails / sailsPerBracket);
       float result = totalSU / rpm;
       
       this.lastCapacityProvided = result;
       return result;
   }
   ```
   
   **Understanding SU Brackets:**
   
   Create windmills use a "bracket" system:
   - Every 8 sails = 1 bracket
   - Each bracket provides 512 SU
   - Formula: `(sailCount / 8) * 512`
   
   **Example Calculation:**
   
   32 regular sails + 32 solar sails, clear day (2.0x multiplier):
   
   1. Regular sails:
      - Brackets: 32 / 8 = 4
      - SU: 4 * 512 = 2048 SU
   
   2. Solar sails:
      - Brackets: 32 / 8 = 4
      - Base SU: 4 * 512 = 2048 SU
      - With multiplier: 2048 * 2.0 = 4096 SU
   
   3. Total:
      - Combined SU: 2048 + 4096 = 6144 SU
      - RPM: 64 sails / 8 = 8 RPM
      - Per-RPM capacity: 6144 / 8 = 768
   
   During rain (1.5x):
   - Solar contribution: 2048 * 1.5 = 3072 SU
   - Total: 2048 + 3072 = 5120 SU
   - Per-RPM: 5120 / 8 = 640
   
   At night (1.0x - no bonus):
   - Solar contribution: 2048 * 1.0 = 2048 SU
   - Total: 2048 + 2048 = 4096 SU
   - Per-RPM: 4096 / 8 = 512

6. **The Double-Counting Bug Fix:**
   ```java
   @Override
   public void updateGeneratedRotation() {
       if (movedContraption != null) {
           Contraption c = movedContraption.getContraption();
           
           if (c instanceof SolarBearingContraption sbc) {
               this.solarSailCount = sbc.getSolarSailBlocks();
               this.regularSailCount = sbc.getRegularSailBlocks();
               this.hasSkyAccess = sbc.hasSkyAccess();
               
               // CRITICAL: Zero out parent's sailBlocks to prevent double-counting!
               sbc.setSailBlocks(0);
           }
       }
       super.updateGeneratedRotation();
   }
   ```
   
   **Why this is necessary:**
   - Parent class (`WindmillBearingBlockEntity`) has its own `sailBlocks` field
   - Parent uses this field to calculate capacity
   - We calculate capacity ourselves using separate solar/regular counts
   - If parent's field isn't zeroed, capacity gets counted twice:
     1. Once by parent's calculation
     2. Once by our calculation
   - Setting it to 0 tells parent "don't add any capacity yourself"
   - We handle ALL capacity through `calculateAddedStressCapacity()`

7. **Data Persistence:**
   ```java
   @Override
   public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
       if (clientPacket) {
           compound.putInt("RegularSails", regularSailCount);
           compound.putInt("SolarSails", solarSailCount);
           compound.putBoolean("HasSkyAccess", hasSkyAccess);
       } else {
           // CRITICAL: Zero both capacity fields BEFORE super.write()!
           this.capacity = 0;
           this.lastCapacityProvided = 0;
       }
       super.write(compound, registries, clientPacket);
   }
   
   @Override
   protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
       super.read(compound, registries, clientPacket);
       
       if (clientPacket) {
           regularSailCount = compound.getInt("RegularSails");
           solarSailCount = compound.getInt("SolarSails");
           hasSkyAccess = compound.getBoolean("HasSkyAccess");
       } else {
           regularSailCount = 0;
           solarSailCount = 0;
           hasSkyAccess = false;
           this.lastCapacityProvided = 0;
           this.capacity = 0;
       }
   }
   ```
   
   **Why zero fields on save?**
   - Prevents "ghost SU" on world reload
   - Forces fresh calculation from contraption
   - Ensures no stale data persists
   - Server resets everything, recalculates from scratch

8. **Network Initialization Override:**
   ```java
   @Override
   public void initialize() {
       if (hasNetwork() && !level.isClientSide) {
           KineticNetwork net = getOrCreateNetwork();
           if (net.initialized) {
               net.initialized = false;
           }
       }
       super.initialize();
   }
   ```
   
   **Purpose:**
   - Forces kinetic network to recalculate from scratch
   - Prevents using cached/stale capacity values
   - Ensures our zeroed capacity is used, not old saved values

9. **Network Update Override:**
   ```java
   @Override
   public void updateFromNetwork(float maxStress, float currentStress, int networkSize) {
       float ourCapacity = calculateAddedStressCapacity();
       float ourSpeed = Math.abs(getGeneratedSpeed());
       float correctTotalSU = ourCapacity * ourSpeed;
       
       super.updateFromNetwork(correctTotalSU, currentStress, networkSize);
   }
   ```
   
   **Purpose:**
   - Network tries to tell us our capacity
   - But network might have wrong (doubled) value
   - We recalculate the correct value
   - Pass our correct value to parent
   - This fixes display issues in goggles/UI

**What this means for learning:**
- Complex systems require careful tracking of state
- Double-counting bugs are common when multiple systems track the same data
- Zeroing fields prevents stale data issues
- Overriding methods allows fixing parent class issues
- Caching values improves performance (don't recalculate every tick)
- Server-side resets on load ensure consistent state

---

### SolarSailBlock.java

**Purpose:** The block for solar sails - works like Create's sails but participates in the solar bonus system.

**Key Features:**

1. **Color System:**
   ```java
   public enum GlassColor implements StringRepresentable {
       CLEAR("clear"),
       WHITE("white"),
       ORANGE("orange"),
       // ... all 16 colors
   }
   
   public static final EnumProperty<GlassColor> GLASS_COLOR = 
       EnumProperty.create("glass_color", GlassColor.class);
   ```
   - Enums create a list of named constants
   - `StringRepresentable` allows serialization (saving to files)
   - `EnumProperty` is a block state property (like FACING or AXIS)

2. **Factory Method:**
   ```java
   public static SolarSailBlock withCanvas(Properties properties, DyeColor color) {
       return new SolarSailBlock(properties, color);
   }
   ```
   - Static factory method for creating instances
   - All solar sails have canvas (no frame variant)
   - Takes a dye color parameter

3. **Dye Interaction:**
   ```java
   @Override
   protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world,
           BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray) {
       
       if (stack.getItem() instanceof DyeItem dyeItem) {
           DyeColor dyeColor = dyeItem.getDyeColor();
           
           if (!world.isClientSide) {
               world.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0f,
                       1.1f - world.random.nextFloat() * .2f);
               applyDye(state, world, pos, ray.getLocation(), dyeColor);
           }
           return ItemInteractionResult.sidedSuccess(world.isClientSide);
       }
       
       return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }
   ```
   - Detects dye item
   - Plays dye sound with slight random pitch variation
   - Calls `applyDye()` to handle the color change

4. **Smart Dye Application:**
   ```java
   public void applyDye(BlockState state, Level world, BlockPos pos, Vec3 hit, @Nullable DyeColor dyeColor) {
       if (dyeColor == null)
           return;
       
       BlockState newState = getSolarSailForColor(dyeColor).defaultBlockState();
       newState = BlockHelper.copyProperties(state, newState);
       
       // Try to dye the block itself
       if (state != newState) {
           world.setBlockAndUpdate(pos, newState);
           return;
       }
       
       // Try to dye adjacent sails
       List<Direction> directions = IPlacementHelper.orderedByDistanceExceptAxis(
           pos, hit, state.getValue(FACING).getAxis());
       for (Direction d : directions) {
           BlockPos offset = pos.relative(d);
           BlockState adjacentState = world.getBlockState(offset);
           Block block = adjacentState.getBlock();
           if (!(block instanceof SolarSailBlock))
               continue;
           if (state.getValue(FACING) != adjacentState.getValue(FACING))
               continue;
           if (state == adjacentState)
               continue;
           world.setBlockAndUpdate(offset, newState);
           return;
       }
       
       // Flood-fill dye all connected sails
       List<BlockPos> frontier = new ArrayList<>();
       frontier.add(pos);
       Set<BlockPos> visited = new HashSet<>();
       int timeout = 100;
       
       while (!frontier.isEmpty()) {
           if (timeout-- < 0)
               break;
           
           BlockPos currentPos = frontier.remove(0);
           visited.add(currentPos);
           
           for (Direction d : Iterate.directions) {
               if (d.getAxis() == state.getValue(FACING).getAxis())
                   continue;
               BlockPos offset = currentPos.relative(d);
               if (visited.contains(offset))
                   continue;
               BlockState adjacentState = world.getBlockState(offset);
               Block block = adjacentState.getBlock();
               if (!(block instanceof SolarSailBlock))
                   continue;
               if (adjacentState.getValue(FACING) != state.getValue(FACING))
                   continue;
               if (state != adjacentState)
                   world.setBlockAndUpdate(offset, newState);
               frontier.add(offset);
               visited.add(offset);
           }
       }
   }
   ```
   
   **Three-tier dye system:**
   
   1. **Dye the clicked block:** If it's already a different color, dye just that one
   
   2. **Dye adjacent:** If clicked block is already the right color, find nearest adjacent sail and dye that
      - Ordered by distance from click point
      - Only considers sails facing the same direction
   
   3. **Flood-fill all connected:** If no adjacent sail found, dye entire connected structure
      - Uses breadth-first search (BFS) algorithm
      - Only follows perpendicular directions (not along facing axis)
      - Timeout at 100 blocks prevents infinite loops
      - `visited` Set prevents checking same block twice

5. **Soft Landing:**
   ```java
   @Override
   public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
       super.fallOn(level, state, pos, entity, 0);
   }
   
   @Override
   public void updateEntityAfterFallOn(BlockGetter world, Entity entity) {
       if (entity.isSuppressingBounce()) {
           super.updateEntityAfterFallOn(world, entity);
       } else {
           this.bounce(entity);
       }
   }
   
   private void bounce(Entity entity) {
       Vec3 velocity = entity.getDeltaMovement();
       if (velocity.y < 0.0D) {
           double d0 = entity instanceof LivingEntity ? 1.0D : 0.8D;
           entity.setDeltaMovement(velocity.x, -velocity.y * 0.26F * d0, velocity.z);
       }
   }
   ```
   
   **Explanation:**
   - `fallOn()`: Negates fall damage (passes 0 distance)
   - `updateEntityAfterFallOn()`: Adds bounce effect
   - `bounce()`: Reverses vertical velocity with 26% efficiency
   - Living entities get full bounce (1.0), others get 80% (0.8)
   - Result: Sails act like trampolines!

**What this means for learning:**
- Flood-fill algorithms spread effects across connected blocks
- Three-tier priority systems ( Self > Adjacent > All) provide good UX
- Physics modifications (bounce, no fall damage) add character
- Enums organize related constants

---

### SolarBearingContraption.java

**Purpose:** Custom contraption type that tracks solar sails separately and checks sky access during assembly.

**Class Hierarchy:**
```java
public class SolarBearingContraption extends BearingContraption
```

**Key Fields:**
```java
protected int solarSailBlocks = 0;
protected int regularSailBlocks = 0;
protected boolean hasSkyAccess = false;
```

**Key Methods:**

1. **Assembly:**
   ```java
   @Override
   public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
       solarSailBlocks = 0;
       regularSailBlocks = 0;
       
       boolean result = super.assemble(world, pos);
       if (result) {
           hasSkyAccess = checkSkyAccess(world, pos);
           LOGGER.info("assemble FINISHED: SolarSails={}, TotalSails={}, TotalBlocks={}",
               solarSailBlocks, getSailBlocks(), getBlocks().size());
       }
       return result;
   }
   ```
   - Reset counters before assembly
   - Call parent to do standard assembly
   - Check sky access at bearing position
   - Log results for debugging

2. **Block Addition:**
   ```java
   @Override
   public void addBlock(Level level, BlockPos pos, Pair<StructureBlockInfo, BlockEntity> capture) {
       BlockPos localPos = pos.subtract(anchor);
       boolean isNew = !getBlocks().containsKey(localPos);
       
       if (isNew) {
           BlockState state = capture.getKey().state();
           if (state.getBlock() instanceof SolarSailBlock) {
               solarSailBlocks++;
               LOGGER.debug("addBlock: Found SolarSail at {}, Count={}", localPos, solarSailBlocks);
           } else if (isSail(state)) {
               regularSailBlocks++;
           }
       }
       
       super.addBlock(level, pos, capture);
   }
   ```
   - Called for each block found during assembly
   - Check if this block is new (not already in the contraption)
   - Identify solar sails specifically
   - Count regular sails separately
   - Let parent handle actual storage

3. **Sky Access Check:**
   ```java
   private boolean checkSkyAccess(Level world, BlockPos pos) {
       for (int x = -2; x <= 2; x++) {
           for (int z = -2; z <= 2; z++) {
               if (world.canSeeSky(pos.offset(x, 1, z))) {
                   return true;
               }
           }
       }
       return false;
   }
   ```
   - Checks 5x5 area around bearing
   - Only needs ONE position with sky access
   - Returns true if ANY position can see sky
   - Tolerates shaft blocks and supports
   - Position checked: 1 block above bearing level

4. **Sail Detection:**
   ```java
   protected boolean isSail(BlockState state) {
       if (state.getBlock() instanceof SolarSailBlock)
           return true;
       
       if (state.is(com.simibubi.create.AllTags.AllBlockTags.WINDMILL_SAILS.tag))
           return true;
       
       return false;
   }
   ```
   - Two ways to identify sails:
     1. `instanceof SolarSailBlock` - our custom sails
     2. Create's windmill sail tag - regular Create sails
   - This allows both types to work together

5. **NBT Serialization:**
   ```java
   @Override
   public CompoundTag writeNBT(HolderLookup.Provider registries, boolean spawnPacket) {
       this.sailBlocks = 0;  // CRITICAL
       
       CompoundTag tag = super.writeNBT(registries, spawnPacket);
       tag.putInt("SolarSails", solarSailBlocks);
       tag.putInt("RegularSails", regularSailBlocks);
       tag.putBoolean("HasSkyAccess", hasSkyAccess);
       
       if (!spawnPacket) {
           LOGGER.info("writeNBT (DISK SAVE): solarSails={}, regularSails={}, hasSkyAccess={}, SUPER.Sails={}",
               solarSailBlocks, regularSailBlocks, hasSkyAccess, getSailBlocks());
       }
       return tag;
   }
   
   @Override
   public void readNBT(Level world, CompoundTag tag, boolean spawnData) {
       super.readNBT(world, tag, spawnData);
       
       // Recalculate from actual blocks
       int recalcSolar = 0;
       int recalcTotal = 0;
       
       for (StructureBlockInfo info : getBlocks().values()) {
           BlockState state = info.state();
           boolean isSolar = state.getBlock() instanceof SolarSailBlock;
           
           if (isSolar) {
               recalcSolar++;
               recalcTotal++;
           } else if (isSail(state)) {
               recalcTotal++;
           }
       }
       
       this.solarSailBlocks = recalcSolar;
       this.regularSailBlocks = recalcTotal - recalcSolar;
       this.sailBlocks = 0;  // CRITICAL
       
       hasSkyAccess = tag.getBoolean("HasSkyAccess");
       
       LOGGER.info("readNBT: solarSails={}, regularSails={}, recalcTotal={}, sailBlocks SET TO 0, hasSkyAccess={}",
           solarSailBlocks, regularSailBlocks, recalcTotal, hasSkyAccess);
   }
   ```
   
   **Key Points:**
   - **Always zero `sailBlocks`** before writing/reading
   - **Recalculate counts** from block data on load (don't trust saved integers)
   - **Sky access** is the only thing we trust from NBT (can't easily recalculate)
   - Extensive logging helps debug issues

**Getters:**
```java
public int getSolarSailBlocks() {
    return solarSailBlocks;
}

public int getRegularSailBlocks() {
    return regularSailBlocks;
}

public boolean hasSkyAccess() {
    return hasSkyAccess;
}

public void setSailBlocks(int count) {
    this.sailBlocks = count;
}
```

**What this means for learning:**
- Contraptions are complex objects that need careful state management
- Recalculating from source data is safer than trusting saved values
- Multiple counters allow separating different behaviors
- Logging is essential for debugging complex systems

---

## Configuration System

### PMConfigs.java

**Purpose:** Central configuration manager for the mod.

**Structure:**
```java
@EventBusSubscriber(modid = CreatePhotomovement.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class PMConfigs {
    private static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<>(ModConfig.Type.class);
    
    private static PMServer server;
    
    public static PMServer server() {
        return server;
    }
}
```

**Config Types:**
- `SERVER`: Synced to clients, affects gameplay
- `CLIENT`: Local only, affects visuals/UI
- `COMMON`: Shared between client and server

This mod only uses SERVER config.

**Registration:**
```java
public static void register(ModLoadingContext context, ModContainer container) {
    server = register(PMServer::new, ModConfig.Type.SERVER);
    
    for (Entry<ModConfig.Type, ConfigBase> pair : CONFIGS.entrySet())
        container.registerConfig(pair.getKey(), pair.getValue().specification);
}

private static <T extends ConfigBase> T register(Supplier<T> factory, ModConfig.Type side) {
    Pair<T, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(builder -> {
        T config = factory.get();
        config.registerAll(builder);
        return config;
    });
    
    T config = specPair.getLeft();
    config.specification = specPair.getRight();
    CONFIGS.put(side, config);
    return config;
}
```

**Event Handlers:**
```java
@SubscribeEvent
public static void onLoad(ModConfigEvent.Loading event) {
    for (ConfigBase config : CONFIGS.values())
        if (config.specification == event.getConfig().getSpec())
            config.onLoad();
}

@SubscribeEvent
public static void onReload(ModConfigEvent.Reloading event) {
    for (ConfigBase config : CONFIGS.values())
        if (config.specification == event.getConfig().getSpec())
            config.onReload();
}
```
- Listen for config load/reload events
- Notify config objects when their specs are loaded

**What this means for learning:**
- Config systems separate customizable values from code
- EnumMap is efficient for mapping enum keys to values
- Event-driven architecture allows reactive config updates

---

### PMServer.java

**Purpose:** Defines server-side configuration options.

```java
public class PMServer extends ConfigBase {
    public final ConfigInt generationSpeed = i(16, 1, "generationSpeed", 
        "Speed of the Solar Generator in RPM");
    
    public final ConfigInt stressCapacity = i(16, 1, "stressCapacity",
        "Stress Capacity of the Solar Generator in SU per RPM");
    
    @Override
    public String getName() {
        return "server";
    }
}
```

**Configuration Options:**
1. **generationSpeed**: 
   - Default: 16 RPM
   - Minimum: 1 RPM
   - Affects how fast generators spin
   
2. **stressCapacity**:
   - Default: 16 SU
   - Minimum: 1 SU
   - Affects how much load generators can handle

**Config File Location:**
`config/createphotomovement-server.toml`

**Example Config File:**
```toml
[server]
    #Speed of the Solar Generator in RPM
    #Range: > 1
    generationSpeed = 16
    
    #Stress Capacity of the Solar Generator in SU per RPM
    #Range: > 1
    stressCapacity = 16
```

**What this means for learning:**
- Config values use builders (`i()` for integer)
- Defaults and minimums prevent invalid values
- TOML format is human-readable and editable
- Server configs sync to clients in multiplayer

---

## Rendering System

### SolarGeneratorRenderer.java

**Purpose:** Custom renderer for vertical solar generators that displays the rotating shaft.

```java
public class SolarGeneratorRenderer extends KineticBlockEntityRenderer<SolarGeneratorBlockEntity> {
    
    public SolarGeneratorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }
    
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

**Explanation:**
- Extends Create's `KineticBlockEntityRenderer` for rotating blocks
- `renderSafe()` is called every frame to draw the block
- Gets shaft model based on rotation axis
- Applies rotation animation
- Renders into solid render layer

**Parameters:**
- `be`: The block entity being rendered
- `partialTicks`: Fraction between game ticks (for smooth animation)
- `ms`: PoseStack (transformation matrix stack)
- `buffer`: MultiBufferSource (where to draw)
- `light`: Light level for shading
- `overlay`: Overlay effects (damage cracks, etc.)

**What this means for learning:**
- Renderers handle visual representation
- Partial ticks enable smooth 60 FPS animation on 20 TPS game ticks
- Matrix stacks track position/rotation transformations
- Buffered rendering batches draw calls for performance

---

### HorizontalSolarGeneratorRenderer.java

**Purpose:** Custom renderer for horizontal solar generators.

```java
public class HorizontalSolarGeneratorRenderer extends KineticBlockEntityRenderer<HorizontalSolarGeneratorBlockEntity> {
    
    public HorizontalSolarGeneratorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }
    
    @Override
    protected void renderSafe(HorizontalSolarGeneratorBlockEntity be, float partialTicks, PoseStack ms,
            MultiBufferSource buffer, int light, int overlay) {
        
        BlockState blockState = be.getBlockState();
        Direction facing = blockState.getValue(HORIZONTAL_FACING);
        
        // Render half shaft on back side (opposite of facing)
        Direction shaftDirection = facing.getOpposite();
        SuperByteBuffer halfShaft = CachedBuffers.partialFacing(
            AllPartialModels.SHAFT_HALF, blockState, shaftDirection);
        standardKineticRotationTransform(halfShaft, be, light);
        halfShaft.renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }
}
```

**Key Difference:**
- Uses `SHAFT_HALF` instead of full shaft
- Only renders on back side (gearbox side)
- Shaft direction is opposite of panel facing

**What this means for learning:**
- Different block types can use different rendering strategies
- Partial models (like half shafts) reduce visual clutter
- Directional rendering respects block orientation

---

## Ponder System

Ponder is Create's in-game tutorial system.

### PhotomovementPonderPlugin.java

**Purpose:** Registers Ponder scenes for this mod's blocks.

```java
public class PhotomovementPonderPlugin implements PonderPlugin {
    
    @Override
    public String getModId() {
        return CreatePhotomovement.MOD_ID;
    }
    
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
    
    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        // Use Create's existing tags
    }
}
```

**Four Scenes:**
1. **basics**: Introduction, shaft connections, rotation
2. **weather**: Day/night cycle, rain effects
3. **obstructions**: Sky access requirements
4. **dyeing**: Color changing with dyes

**Scene Organization:**
- Each scene is a method reference (like `SolarGeneratorScenes::basics`)
- Scenes are organized in "storyboards" (sequences)
- Tagged with `KINETIC_SOURCES` to appear in Create's index

---

### SolarGeneratorScenes.java

**Purpose:** Defines the actual Ponder scene content.

**Scene Structure:**
```java
public static void basics(SceneBuilder builder, SceneBuildingUtil util) {
    builder.title("solar_generator_basics", "Solar Generator Basics");
    builder.configureBasePlate(0, 0, 5);
    builder.showBasePlate();
    
    BlockPos generatorPos = util.grid().at(2, 1, 2);
    Selection generator = util.select().position(generatorPos);
    
    builder.idle(10);
    builder.addKeyframe();
    
    builder.world().showSection(generator, Direction.DOWN);
    builder.idle(20);
    
    builder.overlay().showText(60)
        .text("The Solar Generator creates rotational force from sunlight")
        .pointAt(util.vector().blockSurface(generatorPos, Direction.UP))
        .placeNearTarget();
    builder.idle(70);
    
    // ... more steps
}
```

**Key Ponder Concepts:**

1. **Scene Setup:**
   - `title()`: Sets scene title
   - `configureBasePlate()`: Defines world size
   - `showBasePlate()`: Shows the floor

2. **Timing:**
   - `idle(ticks)`: Wait specified ticks
   - Typical: 10-20 ticks between actions
   - Text duration + 10 ticks for reading

3. **World Manipulation:**
   - `builder.world().showSection()`: Reveals blocks
   - `builder.world().hideSection()`: Hides blocks
   - `builder.world().setBlock()`: Changes blocks

4. **UI Elements:**
   - `builder.overlay().showText()`: Shows text overlay
   - `.pointAt()`: Arrow pointing to location
   - `.placeNearTarget()`: Positions text near pointer

5. **Keyframes:**
   - `builder.addKeyframe()`: Creates chapter markers
   - Players can skip between keyframes
   - Creates timeline in Ponder UI

**Scene Types in This Mod:**

1. **Basics Scene:**
   - 4 keyframes: Introduction, Power Output, Wrench Rotation, Reverse Direction
   - Shows generator with shafts on all 4 sides
   - Demonstrates rotation and wrench usage

2. **Weather Scene:**
   - Shows 4 weather conditions and their effects
   - Clear (full power), Rain (half), Thunder (none), Night (none)

3. **Obstructions Scene:**
   - Shows 3 generators side-by-side
   - Left: Solid block obstruction (no power)
   - Middle: Glass (allows power)
   - Right: Snow/carpet obstruction (no power)
   - Everything revealed at once, then explained

4. **Dyeing Scene:**
   - (Implementation would show dye interaction)

**What this means for learning:**
- In-game tutorials are scripted sequences
- Timing and pacing are critical for understanding
- Real block interactions are better than text descriptions
- Visual demonstrations teach through showing, not telling

---

## Key Concepts and Learning Guide

This section explains important programming concepts used throughout the mod.

### 1. Object-Oriented Programming (OOP)

**Classes and Objects:**
- **Class**: A blueprint (like a recipe)
- **Object**: An instance of a class (like a cake made from the recipe)

Example:
```java
// SolarGeneratorBlock is a class (blueprint)
public class SolarGeneratorBlock extends RotatedPillarKineticBlock {
    // ...
}

// Creating an object (instance) of the class
SolarGeneratorBlock generator = new SolarGeneratorBlock(properties);
```

**Inheritance:**
- Classes can extend other classes to reuse code
- Child class inherits parent's methods and fields
- Can override methods to change behavior

Example:
```java
// AdvSolarGeneratorBlock extends SolarGeneratorBlock
public class AdvSolarGeneratorBlock extends SolarGeneratorBlock {
    // Inherits all methods from SolarGeneratorBlock
    // Can override specific methods
}
```

**Interfaces:**
- Define a contract (methods a class must implement)
- A class can implement multiple interfaces
- Used for capabilities across different class hierarchies

Example:
```java
public class SolarGeneratorBlock extends RotatedPillarKineticBlock
        implements IBE<SolarGeneratorBlockEntity>, IWrenchable {
    // Must implement methods from IBE and IWrenchable
}
```

---

### 2. Minecraft's Registry System

**What are Registries?**
- Central catalogs of game content (blocks, items, entities, etc.)
- Each entry has a unique identifier (`mod_id:name`)
- Must register before content can be used

**DeferredRegister Pattern:**
```java
public static final DeferredRegister.Blocks BLOCKS = 
    DeferredRegister.createBlocks(MOD_ID);

public static final DeferredBlock<SolarGeneratorBlock> SOLAR_GENERATOR = 
    BLOCKS.register("solar_generator", 
        () -> new SolarGeneratorBlock(properties));
```

**Why Deferred?**
- Registration happens in specific phases
- Defer registration until the right time
- Prevents null reference errors

---

### 3. Block States

**What is BlockState?**
- Represents a block's current configuration
- Stores properties like facing direction, rotation, powered state
- Different states of same block (like door open/closed)

**Properties:**
```java
// AXIS property: X, Y, or Z
state.getValue(AXIS)

// HORIZONTAL_FACING property: NORTH, SOUTH, EAST, WEST
state.getValue(HORIZONTAL_FACING)

// Creating new state with changed property
newState = state.setValue(AXIS, Direction.Axis.X)
```

**Block States Are Immutable:**
- Can't modify existing BlockState
- Must create new BlockState with changes
- This prevents bugs from shared state

---

### 4. Block Entities (Tile Entities)

**What are Block Entities?**
- "Smart" blocks that can:
  - Store custom data
  - Run code every tick
  - Have custom rendering
  - Interact with inventories/energy

**When to Use:**
- Block needs to store data (more than block states allow)
- Block needs to do calculations (like checking sunlight)
- Block needs to interact with other systems

**Ticking:**
```java
@Override
public void tick() {
    super.tick();
    // Called 20 times per second
    // Put per-tick logic here
}
```

---

### 5. Client vs Server

**Two Sides:**
- **Server**: Game logic, world changes, truth authority
- **Client**: Rendering, sounds, player input

**Why Separate?**
- Prevents cheating (server validates everything)
- Enables multiplayer (one server, many clients)
- Improves performance (clients don't do heavy calculations)

**Checking Side:**
```java
if (!level.isClientSide) {
    // Server-side only code
    level.setBlock(pos, newState, 3);
}
```

**Common Pattern:**
```java
if (level.isClientSide) {
    // Client: Play sound, show particles
} else {
    // Server: Change world, update data
    // Then sync to clients
}
```

---

### 6. NBT (Named Binary Tag)

**What is NBT?**
- Minecraft's data serialization format
- Saves/loads block entity data
- Stores nested data structures

**Usage:**
```java
@Override
protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
    super.write(compound, registries, clientPacket);
    compound.putInt("SolarSails", solarSailCount);
    compound.putFloat("Capacity", currentCapacity);
    compound.putBoolean("HasSkyAccess", hasSkyAccess);
}

@Override
protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
    super.read(compound, registries, clientPacket);
    solarSailCount = compound.getInt("SolarSails");
    currentCapacity = compound.getFloat("Capacity");
    hasSkyAccess = compound.getBoolean("HasSkyAccess");
}
```

**Two Use Cases:**
- `clientPacket = true`: Syncing to client for display
- `clientPacket = false`: Saving to disk

---

### 7. Create's Kinetic System

**Key Concepts:**

1. **RPM (Rotations Per Minute):**
   - Speed of rotation
   - Can be positive (clockwise) or negative (counter-clockwise)

2. **SU (Stress Units):**
   - Load/capacity measure
   - Generators provide SU (capacity)
   - Machines consume SU (stress)

3. **Kinetic Networks:**
   - Connected kinetic blocks form a network
   - Network calculates total capacity and stress
   - Overstressed network stops working

4. **Capacity Formula:**
   - Each generator provides: `Capacity per RPM * Current RPM`
   - Example: 16 SU/RPM * 8 RPM = 128 SU capacity

**Bracket System (Windmills):**
- Every X sails = 1 bracket
- Each bracket provides fixed SU per RPM
- Formula: `(sailCount / sailsPerBracket) * suPerBracket`

---

### 8. Java Language Features

**Lambda Expressions:**
```java
// Old way (anonymous class)
Something x = new Something() {
    @Override
    public void doThing() {
        System.out.println("Hello");
    }
};

// New way (lambda)
Something x = () -> System.out.println("Hello");
```

**Method References:**
```java
// Lambda calling a method
supplier = () -> new Thing();

// Method reference (shorthand)
supplier = Thing::new;
```

**Static Initializer:**
```java
static {
    // Runs once when class loads
    // Used to initialize static fields
}
```

**Enums:**
```java
public enum Color {
    RED, GREEN, BLUE;
}

// Usage
Color c = Color.RED;
```

---

### 9. Design Patterns

**Factory Pattern:**
```java
public static SolarSailBlock withCanvas(Properties props, DyeColor color) {
    return new SolarSailBlock(props, color);
}
// Usage: SolarSailBlock.withCanvas(props, WHITE)
```

**Builder Pattern:**
```java
CreativeModeTab.builder()
    .title(title)
    .icon(icon)
    .displayItems(...)
    .build();
```

**Singleton Pattern:**
```java
private static PMServer server;

public static PMServer server() {
    return server;  // One instance, globally accessible
}
```

---

### 10. Common Minecraft Concepts

**BlockPos:**
- Immutable position in world (x, y, z)
- Integer coordinates

**Direction:**
- Enum: UP, DOWN, NORTH, SOUTH, EAST, WEST
- Has axis: X, Y, or Z

**VoxelShape:**
- Defines block's collision/selection box
- Can be complex (stairs) or simple (cube)

**Level:**
- Represents the world
- Client has its own Level
- Server has authoritative Level

---

## Troubleshooting Common Issues

**Problem:** Generator not producing power
- Check sky access (F3 debug screen, look at "local Difficulty")
- Verify it's daytime (time/set day)
- Remove obstructions above block
- Check Create kinetic network (Engineer's Goggles)

**Problem:** Windmill capacity seems wrong
- Verify sail counts (logs show assembly details)
- Check solar multiplier (day/night/weather)
- Look for ghost SU (reload world)

**Problem:** Code changes not applying
- Rebuild project (gradlew build)
- Clear run directory
- Check for multiple mod versions

**Problem:** Crash on startup
- Check forge/create version compatibility
- Verify all dependencies installed
- Read crash log for specific error

---

## Conclusion

This mod demonstrates:
- Integration with Create's kinetic systems
- Time-of-day and weather mechanics
- Contraption system usage
- Client-server synchronization
- Complex state management
- User-friendly interactions (dyeing, placement)

**Key Takeaways for Learning:**

1. **Start Simple:** Basic generator → Advanced generator
2. **Build on Create:** Extend existing classes, reuse systems
3. **Test Thoroughly:** Multiple scenarios, edge cases
4. **Document Challenges:** Comments explain difficult parts
5. **Use Logging:** Debug with logger messages
6. **Handle State Carefully:** Reset/recalculate to avoid bugs

The most complex parts are:
- Solar Windmill capacity calculation (double-counting prevention)
- Horizontal generator time-of-day scaling
- Contraption assembly and state management

These required careful thinking about:
- What data to store
- When to recalculate
- How to prevent duplication
- Client-server synchronization

Programming is iterative - the "5 days to fix SU doubling" shows this. Learning through solving real problems builds deep understanding.
