# Phase 2a — Analysis of the Java classes that exist three times

Comparison of the 27 Java classes across `neoforge/1201`, `neoforge/1211` and `fabric/1201`.
Status: branch `cleanup/common-and-tests`, after commit `8ab2fdc` (phase 1).

Classification of the differences:

| Code | Meaning | Consequence |
|------|-----------|------------|
| **(A)** | loader-dependent — needs `net.neoforged` / `net.minecraftforge` / `net.fabricmc` / `fuzs.` | stays triplicated |
| **(B)** | MC-version-dependent — signature differs between 1.20.1 and 1.21.1 | stays triplicated |
| **(C)** | unnecessary drift — could be identical, just written differently | merge |

---

## Overall result

| Classification | Classes |
|---|---|
| (A) loader-dependent | 9 |
| (B) version-dependent | 8 |
| (D) blocked by the shape of `AllBlocks` | 4 |
| (C) drift only — movable | 6 |
| **Total** | **27** |

"(D)" is not a category from the original task but a finding: four classes have **neither**
a loader import **nor** a version difference, but still cannot be moved, because `AllBlocks`
returns registry wrappers on NeoForge (`AllBlocks.X.get()`) and direct `Block` references
on Fabric (`AllBlocks.X`). These four depend on the `AllBlocks`/`AllItems` rework, which is
explicitly **not** part of this branch.

---

## (A) Loader-dependent — 9 classes, stay triplicated

Count of loader-specific imports per target:

| Class | nf1201 | nf1211 | fa1201 |
|---|---|---|---|
| `AllBlockEntityTypes` | 2 | 2 | 1 |
| `AllBlocks` | 3 | 2 | 0 |
| `AllContraptionTypes` | 3 | 3 | 0 |
| `AllCreativeTabs` | 2 | 2 | 1 |
| `AllItems` | 3 | 2 | 0 |
| `CreatePhotomovement` | 5 | 4 | 1 |
| `CreatePhotomovementClient` | 5 | 5 | 2 |
| `infrastructure/config/PMConfigs` | 6 | 7 | 4 |
| `ponder/PhotomovementPonderPlugin` | 1 | 1 | 0 |

`PMConfigs` is the clearest example: three completely different config APIs —
`ForgeConfigSpec` + `@Mod.EventBusSubscriber` (1201), `ModConfigSpec` +
`container.registerConfig` (1211), `ForgeConfigRegistry` from the Forge Config API Port
with listener registration instead of annotations (Fabric).

---

## (B) MC-version-dependent — 8 classes, stay triplicated

### B1 — `use()` → `useItemOn()` (5 classes)

1.21.1 reworked block interaction:

```java
// 1.20.1
public InteractionResult use(BlockState state, Level level, BlockPos pos,
                             Player player, InteractionHand hand, BlockHitResult hit)
    return InteractionResult.PASS;

// 1.21.1
protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                             BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
```

Affects: `SolarGeneratorBlock`, `AdvSolarGeneratorBlock`, `HorizontalSolarGeneratorBlock`,
`HorzAdvSolarGeneratorBlock`, `SolarSailBlock`.

These five are **blocked twice** — in addition to the signature, they use the
`AllBlocks.X.get()` form (see D).

### B2 — `HolderLookup.Provider` in NBT methods (3 classes)

```java
// 1.20.1
protected void write(CompoundTag compound, boolean clientPacket)
protected void read(CompoundTag compound, boolean clientPacket)

// 1.21.1
protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket)
protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket)
```

Affects: `HorizontalSolarGeneratorBlockEntity`, `SolarWindmillBearingBlockEntity`,
`SolarBearingContraption`.

---

## (D) Blocked by the shape of `AllBlocks` — 4 classes

No loader import, no version difference — but:

```java
// NeoForge (1201 and 1211)
COLOR_TO_BLOCK.put(DyeColor.WHITE, () -> AllBlocks.WHITE_SOLAR_GENERATOR.get());
// Fabric
COLOR_TO_BLOCK.put(DyeColor.WHITE, () -> AllBlocks.WHITE_SOLAR_GENERATOR);
```

Affects: `ponder/SolarGeneratorScenes`, `ponder/HorizontalSolarGeneratorScenes`,
`ponder/SolarSailScenes` and (together with A) `AllBlockEntityTypes`.

**These four can only be moved after the `AllBlocks`/`AllItems` rework.**
That is exactly the rework that, per the task, belongs in its own branch. The three
Ponder scene classes are therefore the best gauge of whether that later rework succeeded:
they are the only reason why they still exist three times today.

---

## (C) Pure drift — movable

### C1 — `infrastructure/config/PMServer` — movable immediately

All three copies are **byte-identical** (16 lines). No open question.

### C2 — `AdvSolarGeneratorBlockEntity` and `HorzAdvSolarGeneratorBlockEntity`

nf1201 and fa1201 are byte-identical. nf1211 differs by **one word**:

```java
// 1201 + Fabric
float generatedSpeed = PMConfigs.server().generationSpeed.get() * 2;
// 1211
float generatedSpeed = PMConfigs.server().generationSpeed.get().floatValue() * 2;
```

`generationSpeed` is a `ConfigInt` in all three targets (see `PMServer`), so `.get()`
returns an `Integer` everywhere. `.floatValue()` exists on `Integer` in both MC versions.
Probably (C), not (B) — the shared source set will prove it: if the unified version compiles
for all three targets, it was (C).

### C3 — `SolarGeneratorRenderer`

nf1201 has an unused import (`dev.engine_room.flywheel.lib.transform.TransformStack`) that
1211 and Fabric do not have. Otherwise identical. Purely cosmetic.

### C4 — `HorizontalSolarGeneratorRenderer`

nf1201 and nf1211 are identical; Fabric writes `HorizontalDirectionalBlock.FACING` instead
of `HorizontalSolarGeneratorBlock.HORIZONTAL_FACING`. This is **the same property instance**
(`BlockStateProperties.HORIZONTAL_FACING`), just reached through a different name. Purely cosmetic.

### C5 — `SolarGeneratorBlockEntity` — with behavioral effect ⚠️

nf1201 and nf1211 differ only by **one blank line**. Fabric deviates in content.
Important: `getLightBlock(level, abovePos)` compiles under **both** MC versions — so this is
not (B) but real drift.

Two differences with behavioral effect → **decision needed, see below (E1, E2)**.

Cosmetic on the side: variable name `speed` vs. `generatedSpeed`, blank lines between imports.

### C6 — `SolarWindmillBearingBlock` — with behavioral effect ⚠️

```java
// 1201: solar tick only on the server
if (!level.isClientSide) {
    solarBe.solarTick();
}
// 1211: solar tick on the client as well
solarBe.solarTick();
```

→ **decision needed, see below (E5)**.

Cosmetic on the side: 1211 uses fully qualified names instead of imports;
`BlockBehaviour.Properties` vs. `Properties`.

**Additional finding:** The Fabric version has a `getBlockEntityClass()` override that does
not exist in any NeoForge target. The comment inside it thinks out loud and reaches no
conclusion ("Actually, looking at WindmillBearingBlock in Fabric, it might just use standard
BE registration. I'll stick to the NeoForge pattern... However, I need to cast it safely."),
and the method ultimately returns `WindmillBearingBlockEntity.class` — not the solar variant.
See **E6**.

---

## Classes with drift that remain on this branch

`HorizontalSolarGeneratorBlockEntity`, `SolarWindmillBearingBlockEntity` and
`SolarBearingContraption` are triplicated anyway because of (B). They also contain
considerable drift, which should be documented because it is supposed to disappear later —
after the calculation logic has been extracted.

### `HorizontalSolarGeneratorBlockEntity` (nf1201 vs. nf1211)

| Aspect | nf1201 | nf1211 |
|---|---|---|
| Startup delay | `firstTick` flag | `warmup = 10` counter |
| `onLoad()` | forces stress recalculation + rotation update | empty (only `super`) |
| Capacity < 0 | clamped to 0 | no clamping, sets `lastCapacityProvided` instead |
| NBT | always writes/reads | only with `clientPacket` |

Unused imports in both NeoForge copies (`Block`, `Blocks`, `CarpetBlock`,
`SnowLayerBlock`) — Fabric has cleaned them up. The associated snow/carpet logic exists in
**none** of the three targets; these are simply dead imports.

### `SolarWindmillBearingBlockEntity` (nf1201 vs. nf1211)

| Aspect | nf1201 | nf1211 |
|---|---|---|
| `warmup` | 20 | 10 |
| Network update | additionally calls `getOrCreateNetwork().updateCapacityFor(...)` | not present |
| Contraption gone | sail counts remain (comment: "keep them") | reset to 0 ("prevent ghost SU") |
| Capacity guard | `if (!running)` | `if (!running \|\| movedContraption == null)` |
| NBT sail counts | always written | only with `clientPacket` |

The Fabric version largely corresponds to nf1201, just without comments.

At the top of the 1211 version there is a comment block that does not belong in the repo
("who am I kidding this shit took 5 whole days"). Purely cosmetic, but worth mentioning
because the repo is public.

---

## Open decisions (behavioral effect — not decided by me)

### E1 — Glass above the solar generator

```java
// NeoForge 1201 + 1211
if (aboveState.getLightBlock(level, abovePos) > 0)
    return false;
// Fabric: this check is missing
```

Both targets check `canSeeSky(abovePos)`. NeoForge **additionally** checks
`getLightBlock(...) > 0`, Fabric does not. NeoForge is therefore strictly stricter: every
block that NeoForge lets through, Fabric lets through too, but not the reverse.

I have **not** verified empirically for which concrete blocks the two checks actually
diverge — `getLightBlock` depends on `propagatesSkylightDown` and `isSolidRender` and is not
obvious for every block. Glass, glass panes, stained glass, leaves and water are the
interesting candidates. That is exactly what the GameTest from phase 4 is meant for — it
measures this instead of asserting it.

**Question: should a light-transmitting block above the generator block its output or not?**

- NeoForge today: `canSeeSky` **and** `getLightBlock > 0` → stricter
- Fabric today: only `canSeeSky` → more lenient

### E2 — When is the rotation updated? (`SolarGeneratorBlockEntity.tick`)

```java
// NeoForge: compares target speed against current speed
if (Math.abs(speed) != Math.abs(targetSpeed) || ...) updateGeneratedRotation();

// Fabric: only compares whether it is generating at all
if (canGenerate != wasGenerating) { ...; updateGeneratedRotation(); notifyUpdate(); }
```

**This is probably a real Fabric bug:** when it starts raining, `getGeneratedSpeed()` is
halved, but `canGeneratePower()` remains `true`. Fabric does not detect this and leaves the
speed at the sunny value. NeoForge catches it.

Conversely, Fabric has `firstTick` handling together with `notifyUpdate()`, which NeoForge
lacks — intended against missing synchronization directly after placement.

**Recommendation: combine both** — the speed comparison from NeoForge *and* the
`firstTick` handling from Fabric. This is a behavioral change for both targets, though.

### E3 — Startup delay of the horizontal generator

`firstTick` flag (1201) or `warmup` counter of 10 ticks (1211)?

### E4 — Sail counts when the contraption is removed

Leave them (1201/Fabric) or reset to 0 (1211)? The 1211 version gives "prevent ghost SU"
as its reason, the 1201 version "allows SU to work immediately after world reload". Both
address a real case — presumably a distinction is needed between "contraption disassembled"
and "world is currently loading".

### E5 — Solar tick on the client

Server only (1201/Fabric) or also the client (1211)?

### E6 — `getBlockEntityClass()` in the Fabric `SolarWindmillBearingBlock`

Remove the override or correct it to the solar variant? It currently returns
`WindmillBearingBlockEntity.class` and does not exist in the NeoForge targets.

---

---

## Result of phases 2b/2c (added afterwards)

Done. Each target went from **27 to 21** own Java files; `common/` contains
13 files, of which 6 are pure calculation classes without any Minecraft import.

### Moved to `common/`

| File | Note |
|---|---|
| `logic/DayCycle` | time of day, night, daylight factor, `clamp` without `Mth` |
| `logic/Weather` | `CLEAR` / `RAIN` / `THUNDER` from Minecraft's two booleans |
| `logic/SolarFacing` | `EAST` / `WEST` / `OTHER` without `Direction` |
| `logic/SolarGeneratorOutput` | speed, multipliers, `MIN_SKY_LIGHT` |
| `logic/HorizontalSolarOutput` | yield curve, shading scan range |
| `logic/WindmillOutput` | sails → speed and SU, solar multiplier |
| `content/.../SolarFacings` | bridge `Direction` → `SolarFacing` (the only MC import in this chain) |
| `content/.../SolarGeneratorBlockEntity` | unified, see E1 and E2 |
| `content/.../AdvSolarGeneratorBlockEntity` | now only a multiplier override |
| `content/.../HorzAdvSolarGeneratorBlockEntity` | same |
| `content/.../SolarGeneratorRenderer` | unused import removed |
| `content/.../HorizontalSolarGeneratorRenderer` | property name unified |
| `infrastructure/config/PMServer` | was already identical |

The `.floatValue()` question from C2 is thus **answered empirically**: the unified version
compiles for all three targets, so it was (C) and not (B).

### Decisions made

| # | Decision | Implemented in |
|---|---|---|
| E1 | Light-transmitting blocks do **not** block (Fabric behavior) | `getLightBlock` check removed from both NeoForge targets |
| E2 | Speed comparison **and** `firstTick` combined | shared `SolarGeneratorBlockEntity.tick()` |
| E3 | `firstTick` instead of `warmup` counter | nf1211 aligned with nf1201/Fabric |
| E5 | `solarTick()` server-side only | nf1211 aligned with nf1201/Fabric |
| E6 | Fabric's `getBlockEntityClass()` remains unchanged | — |

### Remained triplicated — and why

- **9 classes (A)**: `AllBlocks`, `AllItems`, `AllBlockEntityTypes`,
  `AllContraptionTypes`, `AllCreativeTabs`, `CreatePhotomovement`,
  `CreatePhotomovementClient`, `PMConfigs`, `PhotomovementPonderPlugin`
- **5 block classes (B + D)**: `use()`/`useItemOn()` **and** the
  `AllBlocks.X.get()` form
- **3 block entities/contraption (B)**: `HolderLookup.Provider` in `read`/`write`.
  Their calculation logic now calls `common/logic`, so the drift only sits in
  the NBT scaffolding.
- **3 Ponder scene classes (D)**: only because of `AllBlocks.X.get()`
- **`SolarWindmillBearingBlock` (D)**: likewise

### Still open

- **E4** — sail counts when the contraption is removed: deliberately left untouched,
  because both variants solve a real but different case.
- `HorizontalSolarGeneratorBlockEntity`: nf1211 still sets `lastCapacityProvided` in
  `calculateAddedStressCapacity()` instead of clamping the capacity to 0, and writes NBT
  only with `clientPacket`. Neither was touched — the comment calls it "vital for correct
  Network Delta updates", and without a GameTest there is no way to prove that aligning it
  would not break 1211.

---

## Original proposal for phases 2b/2c

The result of the analysis shifts the focus: **only 6 classes can be moved directly**,
5 of them with open questions. The big gain lies elsewhere — in step 1 of phase 2c.

1. **Extract calculation logic into MC-free classes** (the real lever).
   These classes have no Minecraft imports at all, so they are guaranteed to be identical
   across all three targets and testable with normal unit tests in phase 3:
   - yield curve over the course of the day per facing direction
   - weather multipliers (rain, thunder)
   - shading deduction by distance
   - warmup progression
   - sail count → speed and stress capacity
   The three block entity versions then only call these classes. This way the drift
   disappears exactly where it hurts in content, **without** the classes themselves having
   to be moved.
2. **Move `PMServer` to `common/`** — identical, no risk.
3. **`SolarGeneratorRenderer` and `HorizontalSolarGeneratorRenderer`** — movable after the
   cosmetics are cleared up.
4. **`AdvSolarGeneratorBlockEntity` / `HorzAdvSolarGeneratorBlockEntity`** — after
   unifying on `.floatValue()`.
5. **`SolarGeneratorBlockEntity` and `SolarWindmillBearingBlock`** — only after
   decision E1/E2 or E5 respectively.
