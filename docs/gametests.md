# GameTests — In-Game-Tests

Die Tests aus Phase 3 laufen ohne Minecraft und prüfen Rechenlogik und Ressourcen.
Diese hier prüfen das, was wirklich eine Welt braucht: echte Himmelssicht, echte
Blockplatzierung, Contraption-Zusammenbau.

## Was du tun musst: die Strukturvorlage bauen

**Das ist der einzige Schritt, den ich nicht für dich erledigen kann.** Eine
`.nbt`-Strukturvorlage lässt sich nicht sinnvoll aus Code erzeugen — sie muss im
Spiel mit einem Strukturblock gespeichert werden.

Alle Tests teilen sich **eine einzige** Vorlage namens `solar_platform`. Du baust
sie also genau einmal.

### Schritt für Schritt

1. **Dev-Client starten**

   ```
   gradlew :neoforge:1211:runClient
   ```

   Die Run-Konfiguration öffnet automatisch die Welt `CPM Testwork`.

2. **In den Kreativmodus und an einen freien, flachen Ort mit freiem Himmel**

   ```
   /gamemode creative
   ```

   Wichtig: **kein Dach, keine Bäume, keine Überhänge.** Wenn über der Vorlage
   etwas steht, liefert `canSeeSky()` überall `false` und *jeder* Solartest meldet
   „erzeugt nichts". Der Test `generatesUnderOpenSky` prüft das als Erstes und sagt
   es dir ausdrücklich — verbieg dann nicht die Erwartungen, sondern baue die
   Vorlage neu.

3. **Testbereich erzeugen**

   ```
   /test create solar_platform 9 5 9
   ```

   Das setzt einen Strukturblock und markiert einen 9×5×9-Bereich.

4. **Boden legen**

   Fülle die **unterste Schicht** (relative Höhe 0) des markierten Bereichs
   vollständig mit einem soliden Block — Stein reicht. Die Generatoren stehen im
   Test auf Höhe 1.

   Der Rest des Bereichs bleibt **Luft**. Nichts darüber.

5. **Speichern**

   Rechtsklick auf den Strukturblock. Im Feld „Structure Name" muss stehen:

   ```
   createphotomovement:solar_platform
   ```

   Dann auf **SAVE**. Modus des Strukturblocks muss „Save" sein.

6. **Datei kopieren**

   Exportiert wird nach — und hier ist eine Stolperfalle:

   ```
   neoforge/1211/run/saves/CPM Testwork/generated/createphotomovement/structures/solar_platform.nbt
   ```

   Beachte: der **Export**-Ordner heißt `structures` (Plural), auch in 1.21.1.
   Der **Ressourcen**-Ordner im Mod heißt in 1.21.1 dagegen `structure` (Singular).
   Das ist nicht dasselbe Wort, und das Vertauschen ist der häufigste Grund, warum
   die Vorlage später nicht gefunden wird.

   | Version | Ressourcen-Ordner (im Mod) | Export-Ordner (aus dem Spiel) |
   |---|---|---|
   | 1.20.1 | `data/<ns>/structures/` | `generated/<ns>/structures/` |
   | 1.21.1 | `data/<ns>/structure/`  | `generated/<ns>/structures/` |

   Kopiere die Datei also nach:

   ```
   neoforge/1211/src/main/resources/data/createphotomovement/structure/solar_platform.nbt
   ```

   (Beim späteren Portieren nach 1.20.1 und Fabric: dort in `structures/`.)

7. **Tests laufen lassen**

   ```
   gradlew :neoforge:1211:runGameTestServer
   ```

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

Umgesetzt für **neoforge/1211**: neun Szenarien für die vertikalen Generatoren —
freier Himmel, Stein, Glas, Teppich, Schneeschicht, Nacht, Regen, Advanced gegen
Basis, und eine Farbvariante gegen die ungefärbte.

Der Glas-Test ist der interessante: er schreibt die Entscheidung fest, dass
lichtdurchlässige Blöcke die Erzeugung **nicht** blockieren. Das war der Punkt, an
dem NeoForge und Fabric auseinanderliefen.

Der Farbvarianten-Test sichert den späteren `AllBlocks`-Umbau ab.

### Was bereits verifiziert ist

`gradlew :neoforge:1211:runGameTestServer` wurde ausgeführt. Der Server startet, die
Tests werden gefunden, und der Lauf scheitert an genau einer Stelle — der fehlenden
Vorlage:

```
[GameTestHooks]: Enabled Gametest Namespaces: [createphotomovement]
[GameTestServer]: Started game test server
[GameTestServer]: 9 tests are now running at position -9176360, -59, 2380497!
java.lang.IllegalStateException: Missing test structure: createphotomovement:solar_platform
```

Run-Konfiguration, Namespace-Property, Annotationen und Testerkennung sind damit
bestätigt. Sobald die `.nbt` liegt, laufen die neun Tests durch.

### Noch offen

- Die `.nbt`-Vorlage (siehe oben) — ohne sie laufen die NeoForge-Tests nicht.
- Horizontale Generatoren: Ertrag je Blickrichtung zu verschiedenen Tageszeiten,
  Verdeckung durch eine Wand in Abstand 2 und 10.
- Solarsegel + Windmühlenlager: Contraption-Zusammenbau, und dass das Lager nachts
  **weiterdreht** — siehe unten.
- Portierung nach neoforge/1201 und fabric/1201.

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

Festgehalten wird das von `WindmillOutputTest`:

- `stillTurnsAtNight` — gleiche Drehzahl bei Tag und Nacht
- `atNightBehavesLikeAPlainWindmill` — Solarsegel liefern nachts exakt so viel wie
  dieselbe Zahl gewöhnlicher Segel

Der GameTest für die Contraption muss diese Erwartung übernehmen: nachts prüft er
**Weiterdrehen bei reduziertem SU**, nicht Stillstand. Ein Test, der Stillstand
erwartet, wäre grün, obwohl das Verhalten falsch wäre.
