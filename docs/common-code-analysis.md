# Phase 2a — Analyse der dreifach vorhandenen Java-Klassen

Vergleich der 27 Java-Klassen über `neoforge/1201`, `neoforge/1211` und `fabric/1201`.
Stand: Branch `cleanup/common-and-tests`, nach Commit `8ab2fdc` (Phase 1).

Einstufung der Unterschiede:

| Code | Bedeutung | Konsequenz |
|------|-----------|------------|
| **(A)** | loaderbedingt — braucht `net.neoforged` / `net.minecraftforge` / `net.fabricmc` / `fuzs.` | bleibt dreifach |
| **(B)** | MC-versionsbedingt — Signatur unterscheidet sich zwischen 1.20.1 und 1.21.1 | bleibt dreifach |
| **(C)** | unnötige Drift — identisch möglich, nur anders geschrieben | zusammenführen |

---

## Gesamtergebnis

| Einstufung | Klassen |
|---|---|
| (A) loaderbedingt | 9 |
| (B) versionsbedingt | 8 |
| (D) blockiert durch `AllBlocks`-Form | 4 |
| (C) nur Drift — verschiebbar | 6 |
| **Summe** | **27** |

„(D)" ist keine Kategorie aus der Aufgabenstellung, sondern ein Befund: vier Klassen
haben **weder** Loader-Import **noch** Versionsunterschied, lassen sich aber trotzdem
nicht verschieben, weil `AllBlocks` auf NeoForge Registry-Wrapper (`AllBlocks.X.get()`)
und auf Fabric direkte `Block`-Referenzen (`AllBlocks.X`) liefert. Diese vier hängen am
`AllBlocks`/`AllItems`-Umbau, der ausdrücklich **nicht** Teil dieses Branches ist.

---

## (A) Loaderbedingt — 9 Klassen, bleiben dreifach

Zählung loaderspezifischer Imports pro Ziel:

| Klasse | nf1201 | nf1211 | fa1201 |
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

`PMConfigs` ist das deutlichste Beispiel: drei völlig verschiedene Config-APIs —
`ForgeConfigSpec` + `@Mod.EventBusSubscriber` (1201), `ModConfigSpec` +
`container.registerConfig` (1211), `ForgeConfigRegistry` aus dem Forge Config API Port
mit Listener-Registrierung statt Annotationen (Fabric).

---

## (B) MC-versionsbedingt — 8 Klassen, bleiben dreifach

### B1 — `use()` → `useItemOn()` (5 Klassen)

1.21.1 hat die Block-Interaktion umgebaut:

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

Betrifft: `SolarGeneratorBlock`, `AdvSolarGeneratorBlock`, `HorizontalSolarGeneratorBlock`,
`HorzAdvSolarGeneratorBlock`, `SolarSailBlock`.

Diese fünf sind **doppelt blockiert** — zusätzlich zur Signatur nutzen sie die
`AllBlocks.X.get()`-Form (siehe D).

### B2 — `HolderLookup.Provider` in NBT-Methoden (3 Klassen)

```java
// 1.20.1
protected void write(CompoundTag compound, boolean clientPacket)
protected void read(CompoundTag compound, boolean clientPacket)

// 1.21.1
protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket)
protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket)
```

Betrifft: `HorizontalSolarGeneratorBlockEntity`, `SolarWindmillBearingBlockEntity`,
`SolarBearingContraption`.

---

## (D) Blockiert durch die Form von `AllBlocks` — 4 Klassen

Kein Loader-Import, kein Versionsunterschied — aber:

```java
// NeoForge (1201 und 1211)
COLOR_TO_BLOCK.put(DyeColor.WHITE, () -> AllBlocks.WHITE_SOLAR_GENERATOR.get());
// Fabric
COLOR_TO_BLOCK.put(DyeColor.WHITE, () -> AllBlocks.WHITE_SOLAR_GENERATOR);
```

Betrifft: `ponder/SolarGeneratorScenes`, `ponder/HorizontalSolarGeneratorScenes`,
`ponder/SolarSailScenes` und (zusammen mit A) `AllBlockEntityTypes`.

**Diese vier werden erst nach dem `AllBlocks`/`AllItems`-Umbau verschiebbar.**
Das ist genau der Umbau, der laut Auftrag in einen eigenen Branch gehört. Die drei
Ponder-Scene-Klassen sind damit der beste Gradmesser dafür, ob dieser spätere Umbau
gelungen ist: sie sind der einzige Grund, warum sie heute noch dreifach existieren.

---

## (C) Reine Drift — verschiebbar

### C1 — `infrastructure/config/PMServer` — sofort verschiebbar

Alle drei Kopien sind **byte-identisch** (16 Zeilen). Keine offene Frage.

### C2 — `AdvSolarGeneratorBlockEntity` und `HorzAdvSolarGeneratorBlockEntity`

nf1201 und fa1201 sind byte-identisch. nf1211 unterscheidet sich um **ein Wort**:

```java
// 1201 + Fabric
float generatedSpeed = PMConfigs.server().generationSpeed.get() * 2;
// 1211
float generatedSpeed = PMConfigs.server().generationSpeed.get().floatValue() * 2;
```

`generationSpeed` ist in allen drei Zielen ein `ConfigInt` (siehe `PMServer`), `.get()`
liefert also überall ein `Integer`. `.floatValue()` ist auf `Integer` in beiden
MC-Versionen vorhanden. Vermutlich (C), nicht (B) — der gemeinsame Source-Set wird es
beweisen: kompiliert die vereinheitlichte Fassung für alle drei Ziele, war es (C).

### C3 — `SolarGeneratorRenderer`

nf1201 hat einen unbenutzten Import (`dev.engine_room.flywheel.lib.transform.TransformStack`),
den 1211 und Fabric nicht haben. Sonst identisch. Reine Kosmetik.

### C4 — `HorizontalSolarGeneratorRenderer`

nf1201 und nf1211 identisch; Fabric schreibt `HorizontalDirectionalBlock.FACING`
statt `HorizontalSolarGeneratorBlock.HORIZONTAL_FACING`. Das ist **dieselbe
Property-Instanz** (`BlockStateProperties.HORIZONTAL_FACING`), nur über einen anderen
Namen erreicht. Reine Kosmetik.

### C5 — `SolarGeneratorBlockEntity` — mit Verhaltenswirkung ⚠️

nf1201 und nf1211 unterscheiden sich nur um **eine Leerzeile**. Fabric weicht inhaltlich ab.
Wichtig: `getLightBlock(level, abovePos)` kompiliert unter **beiden** MC-Versionen — das ist
also nicht (B), sondern echte Drift.

Zwei Unterschiede mit Verhaltenswirkung → **Entscheidung nötig, siehe unten (E1, E2)**.

Kosmetik daneben: Variablenname `speed` vs. `generatedSpeed`, Leerzeilen zwischen Imports.

### C6 — `SolarWindmillBearingBlock` — mit Verhaltenswirkung ⚠️

```java
// 1201: Solar-Tick nur auf dem Server
if (!level.isClientSide) {
    solarBe.solarTick();
}
// 1211: Solar-Tick auch auf dem Client
solarBe.solarTick();
```

→ **Entscheidung nötig, siehe unten (E5)**.

Kosmetik daneben: 1211 nutzt voll qualifizierte Namen statt Imports;
`BlockBehaviour.Properties` vs. `Properties`.

**Zusätzlicher Befund:** Die Fabric-Fassung hat eine `getBlockEntityClass()`-Override,
die es in keinem NeoForge-Ziel gibt. Der Kommentar darin denkt laut und kommt zu keinem
Ergebnis (»Actually, looking at WindmillBearingBlock in Fabric, it might just use standard
BE registration. I'll stick to the NeoForge pattern... However, I need to cast it safely.«),
und die Methode gibt am Ende `WindmillBearingBlockEntity.class` zurück — also nicht die
Solar-Variante. Siehe **E6**.

---

## Klassen mit Drift, die diesen Branch nicht verlassen

`HorizontalSolarGeneratorBlockEntity`, `SolarWindmillBearingBlockEntity` und
`SolarBearingContraption` sind wegen (B) ohnehin dreifach. Sie enthalten aber
zusätzlich erhebliche Drift, die dokumentiert gehört, weil sie später — nach der
Extraktion der Rechenlogik — verschwinden soll.

### `HorizontalSolarGeneratorBlockEntity` (nf1201 vs. nf1211)

| Aspekt | nf1201 | nf1211 |
|---|---|---|
| Startverzögerung | `firstTick`-Flag | `warmup = 10` Zähler |
| `onLoad()` | erzwingt Stress-Neuberechnung + Rotationsupdate | leer (nur `super`) |
| Kapazität < 0 | wird auf 0 geklemmt | keine Klemmung, setzt stattdessen `lastCapacityProvided` |
| NBT | schreibt/liest immer | nur bei `clientPacket` |

Unbenutzte Imports in beiden NeoForge-Kopien (`Block`, `Blocks`, `CarpetBlock`,
`SnowLayerBlock`) — Fabric hat sie aufgeräumt. Die zugehörige Schnee-/Teppich-Logik
existiert in **keinem** der drei Ziele; es sind schlicht Importleichen.

### `SolarWindmillBearingBlockEntity` (nf1201 vs. nf1211)

| Aspekt | nf1201 | nf1211 |
|---|---|---|
| `warmup` | 20 | 10 |
| Netzwerk-Update | ruft zusätzlich `getOrCreateNetwork().updateCapacityFor(...)` | nicht vorhanden |
| Contraption weg | Segelzahlen bleiben stehen (Kommentar: „keep them") | werden auf 0 zurückgesetzt („prevent ghost SU") |
| Kapazitäts-Guard | `if (!running)` | `if (!running \|\| movedContraption == null)` |
| NBT-Segelzahlen | immer geschrieben | nur bei `clientPacket` |

Die Fabric-Fassung entspricht weitgehend nf1201, nur ohne Kommentare.

Am Kopf der 1211-Fassung steht ein Kommentarblock, der nicht ins Repo gehört
(»who am I kidding this shit took 5 whole days«). Reine Kosmetik, aber
erwähnenswert, weil das Repo öffentlich ist.

---

## Offene Entscheidungen (Verhaltenswirkung — nicht selbst entschieden)

### E1 — Glas über dem Solargenerator

```java
// NeoForge 1201 + 1211
if (aboveState.getLightBlock(level, abovePos) > 0)
    return false;
// Fabric: diese Prüfung fehlt
```

Beide Ziele prüfen `canSeeSky(abovePos)`. NeoForge prüft **zusätzlich**
`getLightBlock(...) > 0`, Fabric nicht. NeoForge ist damit strikt strenger: jeder Block,
den NeoForge durchlässt, lässt auch Fabric durch, aber nicht umgekehrt.

Für welche konkreten Blöcke die beiden Prüfungen tatsächlich auseinanderfallen, habe ich
**nicht** empirisch verifiziert — `getLightBlock` hängt an `propagatesSkylightDown` und
`isSolidRender` und ist je nach Block nicht offensichtlich. Glas, Glasscheiben, gefärbtes
Glas, Blätter und Wasser sind die interessanten Kandidaten. Genau dafür ist der
GameTest aus Phase 4 gedacht — er misst es, statt es zu behaupten.

**Frage: soll ein lichtdurchlässiger Block über dem Generator die Erzeugung blockieren
oder nicht?**

- NeoForge heute: `canSeeSky` **und** `getLightBlock > 0` → strenger
- Fabric heute: nur `canSeeSky` → nachsichtiger

### E2 — Wann wird die Rotation aktualisiert? (`SolarGeneratorBlockEntity.tick`)

```java
// NeoForge: vergleicht Soll- gegen Ist-Drehzahl
if (Math.abs(speed) != Math.abs(targetSpeed) || ...) updateGeneratedRotation();

// Fabric: vergleicht nur, ob überhaupt erzeugt wird
if (canGenerate != wasGenerating) { ...; updateGeneratedRotation(); notifyUpdate(); }
```

**Das ist vermutlich ein echter Fabric-Bug:** wenn es zu regnen anfängt, halbiert sich
`getGeneratedSpeed()`, aber `canGeneratePower()` bleibt `true`. Fabric erkennt das nicht
und lässt die Drehzahl auf dem Sonnenwert stehen. NeoForge fängt es ab.

Umgekehrt hat Fabric eine `firstTick`-Behandlung samt `notifyUpdate()`, die NeoForge fehlt
— gedacht gegen fehlende Synchronisierung direkt nach dem Platzieren.

**Empfehlung: beides kombinieren** — Drehzahlvergleich von NeoForge *und*
`firstTick`-Behandlung von Fabric. Das ist aber eine Verhaltensänderung für beide Ziele.

### E3 — Startverzögerung horizontaler Generator

`firstTick`-Flag (1201) oder `warmup`-Zähler von 10 Ticks (1211)?

### E4 — Segelzahlen beim Abbau der Contraption

Stehenlassen (1201/Fabric) oder auf 0 zurücksetzen (1211)? Die 1211-Fassung nennt als
Grund „prevent ghost SU", die 1201-Fassung „allows SU to work immediately after world
reload". Beide adressieren einen echten Fall — vermutlich braucht es eine Unterscheidung
zwischen „Contraption abgebaut" und „Welt lädt gerade".

### E5 — Solar-Tick auf dem Client

Nur Server (1201/Fabric) oder auch Client (1211)?

### E6 — `getBlockEntityClass()` in der Fabric-`SolarWindmillBearingBlock`

Override entfernen oder auf die Solar-Variante korrigieren? Sie gibt aktuell
`WindmillBearingBlockEntity.class` zurück und existiert in den NeoForge-Zielen nicht.

---

## Vorschlag für Phase 2b/2c

Das Ergebnis der Analyse verschiebt den Schwerpunkt: **direkt verschiebbar sind nur
6 Klassen**, davon 5 mit offenen Fragen. Der große Gewinn liegt woanders —
in Schritt 1 von Phase 2c.

1. **Rechenlogik in MC-freie Klassen extrahieren** (der eigentliche Hebel).
   Diese Klassen haben keinerlei Minecraft-Import, sind damit garantiert über alle
   drei Ziele identisch und in Phase 3 mit normalen Unit-Tests prüfbar:
   - Ertragskurve über den Tagesverlauf je Blickrichtung
   - Wetter-Multiplikatoren (Regen, Gewitter)
   - Verdeckungsabschlag nach Abstand
   - Warmup-Verlauf
   - Segelanzahl → Drehzahl und Stress-Kapazität
   Die drei BlockEntity-Fassungen rufen diese Klassen danach nur noch auf. Damit
   verschwindet die Drift genau dort, wo sie inhaltlich wehtut, **ohne** dass die
   Klassen selbst verschoben werden müssen.
2. **`PMServer` nach `common/`** — identisch, risikofrei.
3. **`SolarGeneratorRenderer` und `HorizontalSolarGeneratorRenderer`** — nach Klärung
   der Kosmetik verschiebbar.
4. **`AdvSolarGeneratorBlockEntity` / `HorzAdvSolarGeneratorBlockEntity`** — nach
   Vereinheitlichung auf `.floatValue()`.
5. **`SolarGeneratorBlockEntity` und `SolarWindmillBearingBlock`** — erst nach
   Entscheidung E1/E2 bzw. E5.
