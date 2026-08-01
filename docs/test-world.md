# Testwelt bauen

Diese Anleitung deckt genau das ab, was du gebaut haben musst, bevor die GameTests
laufen können — und die Prüfungen, die kein automatischer Test übernehmen kann.

## Kurzfassung

Du baust **eine** Strukturvorlage: eine leere Plattform, 16 × 8 × 9, mit freiem
Himmel. Alle Generator-Tests teilen sie sich. Die Generatoren und Hindernisse setzt
der Test selbst — du baust nur den Boden.

---

## Teil 1: Die Strukturvorlage

### 1. Dev-Client starten

```
gradlew :neoforge:1211:runClient
```

Öffnet automatisch die Welt `CPM Testwork`.

### 2. Freien Platz suchen

```
/gamemode creative
```

Wichtig: **nichts über dem Bauplatz.** Kein Dach, keine Bäume, keine Überhänge, kein
Berg daneben, der hineinragt.

Warum das so kritisch ist: `canSeeSky()` ist in Wahrheit eine Himmelslicht-Abfrage —

```java
default boolean canSeeSky(BlockPos pos) {
    return this.getBrightness(LightLayer.SKY, pos) >= this.getMaxLightLevel();
}
```

Steht irgendetwas darüber, ist das Himmelslicht unter 15 und **jeder** Solartest
meldet „erzeugt nichts". Der Test `generatesUnderOpenSky` prüft das als Allererstes
und sagt es dir ausdrücklich. Wenn er anschlägt: Vorlage neu bauen, nicht die
Erwartungen anpassen.

### 3. Bereich erzeugen

```
/test create solar_platform 16 8 9
```

16 lang (X), 8 hoch (Y), 9 breit (Z). Die Länge von 16 ist kein Zufall — die
Beschattungstests des horizontalen Generators brauchen eine freie Bahn von 11 Blöcken
vor dem Panel, um auch „Abstand 11 wirkt nicht mehr" prüfen zu können.

### 4. Boden legen

Fülle **nur die unterste Schicht** (relative Höhe 0) vollständig mit Stein.
16 × 9 = 144 Blöcke.

Alles darüber bleibt **Luft**. Die Generatoren stehen im Test auf Höhe 1.

### 5. Speichern

Rechtsklick auf den Strukturblock, Modus „Save". In „Structure Name" eintragen:

```
createphotomovement:solar_platform
```

Dann **SAVE**.

### 6. Datei kopieren

Exportiert wird nach:

```
neoforge/1211/run/saves/CPM Testwork/generated/createphotomovement/structures/solar_platform.nbt
```

Kopieren nach:

```
neoforge/1211/src/main/resources/data/createphotomovement/structure/solar_platform.nbt
```

> **Stolperfalle:** Der Export-Ordner heißt `structures` (Plural), der
> Ressourcen-Ordner in 1.21.1 aber `structure` (Singular). Beim späteren Portieren
> auf 1.20.1 und Fabric heißt der Ressourcen-Ordner dort wieder `structures`.

### 7. Laufen lassen

```
gradlew :neoforge:1211:runGameTestServer
```

---

## Teil 2: Was die GameTests abdecken

Nach deiner Szenarienliste, aufgeteilt nach dem, was automatisierbar ist.

### Solargenerator und Advanced Solargenerator

| Szenario | Automatisiert |
|---|---|
| Freier Himmel → erzeugt | ✅ |
| Stein darüber → erzeugt nicht | ✅ |
| Glas darüber → erzeugt weiter | ✅ |
| Teppich / Schneeschicht darüber → erzeugt nicht | ✅ |
| Nacht → erzeugt nicht | ✅ |
| Regen → halbe Drehzahl | ✅ |
| Advanced liefert mehr als Basis | ✅ |
| Farbvariante verhält sich wie ungefärbt | ✅ |
| SU bleibt gleich bei Nacht → Tag → Nacht | ✅ |
| SU bleibt gleich nach Spielneustart | ❌ **manuell**, siehe Teil 3 |

### Horizontaler und Advanced Horizontaler Solargenerator

| Szenario | Automatisiert |
|---|---|
| Freier Himmel → erzeugt | ✅ |
| Nacht → erzeugt nicht | ✅ |
| Block **direkt** davor → erzeugt nicht | ✅ |
| Block in Abstand 2 → reduziert | ✅ |
| Block in Abstand 8 (Gap von 7) → reduziert | ✅ |
| Block in Abstand 11 → **keine** Wirkung mehr | ✅ |
| Glas davor → blockt nicht | ✅ |
| Glas in Abstand 8 → reduziert nicht | ✅ |
| Sonnenstandskurve: Ost morgens hoch, abends niedrig | ✅ |
| Sonnenstandskurve: West umgekehrt | ✅ |
| Nord/Süd immer Minimum | ✅ |
| Advanced-Multiplikator stimmt | ✅ |
| SU bleibt gleich nach Spielneustart | ❌ **manuell**, siehe Teil 3 |

### Wie die Beschattung wirklich rechnet

Entschieden am 2026-08-01: **es bleibt beim festen Minimalwert**, keine Halbierung.

| Situation | SU pro RPM (Basis-Kapazität 16) |
|---|---|
| Ost, Morgengrauen, frei | 64 |
| Ost, Mittag, frei | 22 |
| Ost, Abenddämmerung, frei | 8 |
| West, Morgengrauen, frei | 8 |
| West, Abenddämmerung, frei | 64 |
| Nord oder Süd, immer | 8 |
| **Verdeckt (Abstand 2–10), egal wann und wohin** | **8** |
| Block direkt davor (Abstand 1) | **0 — erzeugt gar nicht** |

Ein verdeckter Generator liefert also den Minimalwert, nicht die Hälfte. Wer beim
Spielen „reduziert" liest, sollte das erwarten.

### Was „durchsichtig" technisch heißt

Der Verdeckungstest nutzt `getLightBlock(...) > 0`:

```java
if (state.isSolidRender(level, pos)) return 15;      // Stein, Holz, Wolle -> blockt
else return state.propagatesSkylightDown(...) ? 0 : 1;
```

- **Klarglas, Glasscheiben** → 0 → blockt **nicht**
- **Stein und alles Solide** → 15 → blockt
- **Getöntes Glas, Laub, Eis, Wasser** → 1 → blockt

Getöntes Glas blockt also, Klarglas nicht. Das ist beabsichtigt und wird getestet.

---

## Teil 3: Manuelle Prüfliste — der SU-Bug beim Neustart

Ein GameTest kann den Server nicht neu starten. Diese Prüfungen musst du im Client
machen. Es ist der Bug, der laut Changelog in einer früheren Version aufgetreten ist:
**die produzierte SU addierte sich beim Laden statt gleich zu bleiben.**

### Aufbau

1. Welt `CPM Testwork`, freier Himmel, Tag.
2. Einen Solargenerator setzen, eine Welle dran, ein Stressmessgerät (Stressometer)
   ans Netz.
3. Wert notieren.

### Prüfung A — Chunk neu laden

1. SU-Wert notieren.
2. Weit weg gehen, bis der Chunk entlädt (oder F3 + A reicht nicht — wirklich weit
   weg, > 10 Chunks).
3. Zurückkommen.
4. **Erwartung:** derselbe Wert. Nicht das Doppelte.

### Prüfung B — Welt verlassen und neu laden

1. SU-Wert notieren.
2. Welt speichern und verlassen, dann wieder laden.
3. **Erwartung:** derselbe Wert.

### Prüfung C — Spiel komplett neu starten

1. SU-Wert notieren.
2. Minecraft beenden, `runClient` neu starten, Welt laden.
3. **Erwartung:** derselbe Wert.

### Prüfung D — Nacht-Tag-Wechsel über mehrere Zyklen

1. `/time set day`, Wert notieren.
2. `/time set night` → Erwartung: Generator steht, SU fällt auf 0.
3. `/time set day` → **Erwartung: exakt der Wert aus Schritt 1.** Nicht mehr.
4. Drei- bis viermal wiederholen. Wenn der Wert bei jedem Zyklus steigt, ist der
   Bug zurück.

### Prüfung E — dasselbe mit dem Windmühlenlager

Solarsegel-Contraption bauen, dann A bis D wiederholen.

Zusätzlich: **nachts muss sich das Lager weiterdrehen** — nur der Solarbonus fällt
weg. Nachts ist es eine ganz normale Windmühle. Ein Stillstand wäre ein Fehler.

### Wenn eine Prüfung fehlschlägt

Notier, **welche** und **um welchen Faktor** der Wert danebenliegt (verdoppelt?
addiert sich der Startwert jedes Mal dazu?). Das grenzt es sofort ein: die
verdächtige Stelle ist `SolarWindmillBearingBlockEntity.initialize()` und
`onNetworkChange()`, wo `initFromTE()` die Kapazität sowohl als `unloadedCapacity`
als auch über die Quellenliste zählt. Bei den horizontalen Generatoren ist es
`calculateAddedStressCapacity()` zusammen mit `lastCapacityProvided`.

Dort gibt es außerdem noch eine offene Drift zwischen den Zielen, die ich bewusst
nicht angetastet habe — siehe `docs/common-code-analysis.md`, Abschnitt
„Weiterhin offen". Ergebnisse aus dieser Prüfliste entscheiden, welche Variante
gewinnt.
