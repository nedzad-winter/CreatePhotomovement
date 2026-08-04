# GameTests — In-Game-Tests

Die Tests aus Phase 3 laufen ohne Minecraft und prüfen Rechenlogik und Ressourcen.
Diese hier prüfen das, was wirklich eine Welt braucht: echte Himmelssicht, echte
Blockplatzierung, Contraption-Zusammenbau.

> **Bauanleitung für die Testwelt und die manuelle Prüfliste:
> [docs/test-world.md](test-world.md).** Dieses Dokument hier beschreibt Aufbau und
> Stand der Tests; dort steht, was du im Client tun musst.

## Die Strukturvorlage

Alle Tests teilen sich **eine einzige** Vorlage namens `solar_platform`, und die
wird von einem Skript erzeugt:

```
powershell -File tools/make_gametest_structure.ps1
gradlew :neoforge:1211:runGameTestServer
```

Die Dateien sind eingecheckt; das Skript brauchst du nur, wenn sich die Größe der
Arena ändern soll. Details und der Ordner-Stolperstein (`structure` im Mod,
`structures` beim Export aus dem Spiel) stehen in
[docs/test-world.md](test-world.md).

> **Korrektur:** Hier stand vorher eine Anleitung zum Bau von Hand mit dem Hinweis,
> eine `.nbt` lasse sich nicht aus Code erzeugen. Das war falsch und ungeprüft aus
> dem ursprünglichen Plan übernommen.

## Warum Fabric keine Vorlage braucht

Fabric API bringt eine leere Struktur mit:
`@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)`. NeoForge hat kein
Äquivalent — in 21.1.206 wurde nach einer `@EmptyTemplate`-Annotation gesucht, die
es dort noch nicht gibt. Deshalb der Handbau für die beiden NeoForge-Ziele.

## Aufbau der Tests

Die Testklassen selbst können **nicht** geteilt werden: NeoForge verlangt statische
Methoden mit `net.neoforged.neoforge.gametest.*` (bzw. `net.minecraftforge.gametest.*`
in 1.20.1), Fabric verlangt Instanzmethoden und ein `FabricGameTest`-Interface.

Geteilt wird stattdessen alles darunter, in
`common/src/main/java/com/createphotomovement/gametest/SolarAssertions.java`:
Weltvorbereitung (Zeit, Wetter) und alle Prüfungen. Das Gerüst pro Loader bleibt
dünn und enthält nur Annotationen und Positionen.

Dass diese Hilfsklasse in allen drei Zielen kompiliert, belegt nebenbei, dass
`ServerLevel.setWeatherParameters`, `GameTestAssertException(String)` und die
Create-Kinetik-API in 1.20.1 und 1.21.1 identisch sind.

### Verifizierte API-Fakten

Alle im Repo gegen die tatsächlichen Artefakte geprüft, nicht aus der Erinnerung:

| Fakt | Fundstelle |
|---|---|
| Beide Versionen nutzen die Annotations-API | `net/minecraft/gametest/framework/GameTest.class` |
| 1.21.1: NeoForge-Paketnamen | `net/neoforged/neoforge/gametest/GameTestHolder.class` |
| 1.20.1: noch Forge-Paketnamen | `net/minecraftforge/gametest/GameTestHolder.class` |
| Export- vs. Ressourcenordner | `StructureTemplateManager.STRUCTURE_RESOURCE_DIRECTORY_NAME` / `STRUCTURE_GENERATED_DIRECTORY_NAME` |
| Exportbasis ist der Weltordner | `LevelResource.GENERATED_DIR = "generated"` |

### Run-Konfigurationen

| Ziel | Task | Namespace-Property |
|---|---|---|
| NeoForge 1.21.1 | `:neoforge:1211:runGameTestServer` | `neoforge.enabledGameTestNamespaces` |
| NeoForge 1.20.1 | `:neoforge:1201:runGameTestServer` | `forge.enabledGameTestNamespaces` |
| Fabric 1.20.1 | `:fabric:1201:runGametest` | `-Dfabric-api.gametest` |

## Stand

Umgesetzt für **neoforge/1211**: 35 Szenarien in drei Klassen.

```
========= 35 GAME TESTS COMPLETE IN 2.709 s ======================
All 35 required tests passed :)
```

`SolarGeneratorGameTests` (10) — vertikale Generatoren: freier Himmel, Stein, Glas,
Teppich, Schneeschicht, Nacht, Advanced gegen Basis, Farbvariante gegen
ungefärbte, und SU-Stabilität über zwei Tag-Nacht-Zyklen.

`HorizontalSolarGameTests` (13) — horizontale Generatoren: freier Himmel, Nacht,
Block direkt davor, Glas direkt davor, Verdeckung in Abstand 2 und 8, Abstand 11
ohne Wirkung, Glas in Abstand 8 ohne Wirkung, Ost-Peak am Morgen, West-Peak am
Abend, Nord immer am Minimum, Advanced-Multiplikator, und SU-Stabilität.

`SolarWindmillGameTests` (12) — das Solar-Windmühlenlager, siehe unten.

Der Glas-Test ist der interessante: er schreibt die Entscheidung fest, dass
lichtdurchlässige Blöcke die Erzeugung **nicht** blockieren. Das war der Punkt, an
dem NeoForge und Fabric auseinanderliefen.

Der Farbvarianten-Test sichert den späteren `AllBlocks`-Umbau ab.

## Die Windmühlen-Tests

Das Lager ist die Stelle mit dem meisten handgeschriebenen Zustand: Segelzahlen und
Himmelssicht werden **einmal beim Zusammenbau** ermittelt und danach in Feldern
gehalten, ins NBT geschrieben, beim Laden absichtlich wieder genullt und aus der
Blockliste der Contraption neu gezählt. Jeder dieser Schritte existiert wegen einer
Kapazität, die einmal doppelt gezählt wurde. Die Formel deckt `WindmillOutputTest`
ab; ob die richtigen Zahlen dort ankommen, kann nur eine Welt sagen.

Jeder Test baut zwei identische Lager nebeneinander — eines mit Solarsegeln, eines
mit Creates gewöhnlichen Segeln — und vergleicht sie miteinander statt gegen feste
SU-Zahlen. Damit misst der Test den Bonus und nicht die Konfiguration.

| Test | Erwartung |
|---|---|
| `assemblesFromSolarSails` | 9 Solarsegel, 0 reguläre, Himmelssicht, dreht |
| `plainSailsAreNotCountedAsSolar` | 9 reguläre, 0 solare |
| `solarSailsDoubleTheCapacityByDay` | ×2,0 gegenüber der Referenz |
| `sailTypeDoesNotChangeSpeed` | gleiche Drehzahl — der Bonus liegt auf SU, nicht auf RPM |
| `noSkyAccessMeansNoBonus` | überdacht: ×1,0, **dreht aber weiter** |
| `keepsTurningAtNight` | dreht, und zwar mit der Drehzahl der Referenz |
| `losesOnlyTheBonusAtNight` | ×1,0 |
| `rainReducesTheBonus` | ×1,5 |
| `thunderRemovesTheBonus` | ×1,0, dreht weiter |
| `networkCapacityReturnsAfterANightDayCycle` | Netz-Kapazität nach zwei Zyklen unverändert |
| `speedIsUnaffectedByNightfall` | Drehzahl exakt identisch über Tag → Nacht → Tag |
| `disassemblingClearsTheOutput` | nach dem Zerlegen 0 RPM **und** 0 SU |

### Regen ist hier messbar, bei den Panels nicht

Beides ist dasselbe Wetter, aber zwei verschiedene Fragen. Das Lager fragt
`level.isRaining()` — globales Wetter, direkt setzbar. Die Panels fragen
`level.isRainingAt(pos)`, was zusätzlich die Heightmap heranzieht und in einer
Testarena immer „überdacht" meldet. Deshalb ist Regen für das Lager automatisiert
und für die Panels weiterhin Handarbeit
([Prüfung F](test-world.md#prüfung-f--regen-und-gewitter)).

### Warum die Netz-Kapazität gemessen wird und nicht die des Lagers

`calculateAddedStressCapacity()` rechnet bei jedem Aufruf neu aus den aktuellen
Feldern — dieser Wert kann sich gar nicht selbst widersprechen. Der gemeldete Bug
saß in `KineticNetwork.calculateCapacity()`, also in
`presentCapacity + unloadedCapacity`, wo dasselbe Lager einmal als lebende Quelle
und einmal als übrig gebliebene entladene Kapazität auftauchte. Der Test misst
deshalb die Netzsumme. Einen Serverneustart erreicht er trotzdem nicht — das
bleibt Prüfung A bis C.

### Zwei Dinge, die dabei ans Licht kamen

**Segel verbinden sich nur innerhalb ihrer eigenen Ebene.** Eine 3×3-Fläche
Solarsegel mit Standardausrichtung an einem nach Norden zeigenden Lager zerfiel in
Streifen, und der Zusammenbau scheiterte mit *„Attached structure does not include
enough sail-like blocks: 3"* — bei neun gesetzten Segeln. Die Ausrichtung der Segel
muss zur Lagerachse passen. Mit Creates eigenen Segeln fiel das nicht auf, weil
deren Standardausrichtung zufällig gepasst hat.

**Wetter wirkt nicht sofort.** `setWeatherParameters` setzt nur Schalter; gelesen
werden zwei Intensitätswerte, die pro Tick um 0,01 nachziehen. `isRaining()` will
über 0,2, `isThundering()` über 0,9 — ein Gewitter braucht also rund 90 Ticks, um
echt zu werden, und ebenso lange, um zu verschwinden. Da alle Batches sich eine Welt
teilen, lief mein Regen in die horizontalen Tests hinein, die daraufhin mit
`skyDarken=5, effective=10, needs>=12` scheiterten — ohne eigenes Verschulden.
`SolarAssertions` setzt die Intensität jetzt direkt mit, sodass das Wetter eine
Tatsache ist und keine Absicht.

### Drei Dinge, die auf dem Weg dorthin schiefgingen

**Alle Tests liefen gleichzeitig.** GameTests einer Batch teilen sich eine Welt, und
Tageszeit wie Wetter sind globaler Zustand — die Tests überschrieben sich gegenseitig.
Zwei Nacht-Tests meldeten unterschiedliche Tageszeiten. Behoben mit `batch = "..."`
pro benötigtem Weltzustand; Tests, die die Zeit *während* des Laufs ändern, haben eine
eigene Batch für sich.

**Die Bodenschicht lag eine Ebene zu hoch.** Ein Strukturblock setzt seinen Inhalt eine
Ebene über sich, also landet Template-Ebene 0 auf Helper-Höhe 1. Die vertikalen Tests
merkten das nicht — `setBlock` überschreibt den Boden, und sie schauen nur nach oben.
Die horizontalen schauten direkt in die Bodenschicht: `front=Bedrock,
frontLightBlock=15`. Alle Testpositionen liegen jetzt auf Höhe 2.

**Zwei Tests waren so gebaut, dass sie nichts prüfen konnten.** Sie starteten eine
zweite Sequenz innerhalb einer laufenden; die äußere ruft `thenSucceed()` auf, sobald
der Block zurückkehrt. Grün, ohne je zur Prüfung zu kommen. Jetzt lineare Sequenzen.

Ohne Diagnoseausgabe in den Assertions wäre keiner dieser drei Punkte auffindbar
gewesen — jede Fehlermeldung nennt jetzt Tageszeit, Himmelslicht, `skyDarken`, den
Block vor dem Panel und die Wetterlage.

### Noch offen

- Portierung nach neoforge/1201 und fabric/1201.
- Regen **auf die Panels**: in der Arena nicht herstellbar, siehe
  [docs/test-world.md](test-world.md#warum-regen-kein-gametest-ist). Manuell als
  Prüfung F. Für das Windmühlenlager ist Regen dagegen automatisiert.
- Neustart und Chunk-Neuladen: erreicht kein GameTest, Prüfungen A bis C.

## Wichtig: das Windmühlenlager steht nachts NICHT still

Für die Solargeneratoren gilt „nachts kein Ertrag". Für das Solar-Windmühlenlager
gilt das ausdrücklich **nicht**:

> Nachts ist der Solar-Windmill-Generator ein ganz normales Windmühlenlager.

Also:

| | Tag (klar, freier Himmel) | Nacht |
|---|---|---|
| Drehzahl | von der Segelzahl abhängig | **unverändert, gleiche Drehzahl** |
| Solarbonus | ×2,0 | ×1,0 — fällt weg |
| Gesamt-SU | doppelt | wie eine gewöhnliche Windmühle gleicher Segelzahl |

Der Code macht das bereits richtig, und zwar durch seinen Aufbau:
`WindmillOutput.generatedSpeed(...)` nimmt **überhaupt keinen** Zeit- oder
Wetterparameter entgegen — die Drehzahl kann von der Tageszeit gar nicht abhängen.
Nur `solarMultiplier(...)` kennt die Uhrzeit, und der wirkt ausschließlich auf die
Stress-Kapazität.

Festgehalten wird das an zwei Stellen. Rechnerisch von `WindmillOutputTest`:

- `stillTurnsAtNight` — gleiche Drehzahl bei Tag und Nacht
- `atNightBehavesLikeAPlainWindmill` — Solarsegel liefern nachts exakt so viel wie
  dieselbe Zahl gewöhnlicher Segel

Und in der Welt von `SolarWindmillGameTests`: `keepsTurningAtNight` prüft, dass eine
nachts zusammengebaute Solar-Windmühle mit derselben Drehzahl läuft wie die
Referenz mit gewöhnlichen Segeln, `losesOnlyTheBonusAtNight` den Faktor ×1,0, und
`speedIsUnaffectedByNightfall` die Drehzahl über einen vollständigen Wechsel.

Ein Test, der Stillstand erwartet, wäre grün, obwohl das Verhalten falsch wäre —
deshalb steht die Regel hier ausdrücklich und nicht nur im Code.
