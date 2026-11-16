# ADR-002: Ein Gradle-Projekt pro Ziel, abhängige Klassen dreifach

Status: gültig. Nachträglich dokumentiert am 2026-11-16.

## Ausgangslage

Der Mod soll für NeoForge 1.21.1, NeoForge 1.20.1 und Fabric 1.20.1 erscheinen. Rund
22 Klassen lassen sich nicht teilen:

- **loaderbedingt** — sie benutzen Schnittstellen, die nur ein Loader hat (Anmeldung
  der Blöcke, Startpunkt des Mods, Config);
- **versionsbedingt** — Minecraft hat zwischen 1.20.1 und 1.21.1 Methoden umbenannt
  oder um Parameter erweitert (Rechtsklick auf Blöcke, Speichern und Laden);
- **blockiert** — sie hängen an `AllBlocks`, das unter NeoForge und Fabric verschieden
  aussieht.

## Entscheidung

Jedes Ziel ist ein eigenes Gradle-Projekt in `<loader>/<mc-version ohne Punkte>/` mit
eigenem Build-Plugin. Die nicht teilbaren Klassen liegen in jedem Ziel in eigener
Fassung, unter demselben Paket- und Dateinamen. Ressourcen, die sich unterscheiden
(vor allem der `data/`-Ordner), liegen ebenfalls pro Ziel.

## Begründung

- Die drei Build-Plugins (NeoForge ModDev, dessen Legacy-Variante, Fabric Loom) lassen
  sich nicht in einem Projekt mischen.
- Drei lesbare Fassungen einer Klasse sind für einen einzelnen Autor leichter zu
  verstehen als eine Fassung mit einer Abstraktionsschicht darüber.
- Gleiche Dateinamen machen den Vergleich zwischen den Zielen einfach.

## Folgen

- Änderungen an diesen Klassen müssen dreimal gemacht werden.
- Unbeabsichtigtes Auseinanderlaufen ist möglich. Die Analyse und die Bereinigung dazu
  stehen in [docs/common-code-analysis.md](../../common-code-analysis.md).
- Einige Asset-Dateien unterscheiden sich zwischen den Zielen, ohne dass ein Grund
  bekannt ist (arc42, Kapitel 11, Nr. 1).
