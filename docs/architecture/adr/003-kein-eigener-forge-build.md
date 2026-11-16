# ADR-003: Kein eigener Forge-Build

Status: gültig. Entschieden am 2026-02-07, hier nachträglich dokumentiert.

## Ausgangslage

Es gab einen eigenen Ordner `forge/1201`. Der Code in `neoforge/1201` benutzt aber
ohnehin die klassischen Forge-Schnittstellen (`net.minecraftforge.*`) und wird mit der
Legacy-Variante des NeoForge-Build-Plugins gebaut.

## Entscheidung

`forge/1201` wurde entfernt (in `settings.gradle` steht die Zeile noch auskommentiert).
Für Minecraft 1.20.1 gibt es nur das Jar aus `neoforge/1201`.

## Begründung

Zwei Ziele mit praktisch gleichem Code zu pflegen, verdoppelt die Arbeit ohne Nutzen.

## Folgen

- Das NeoForge-1.20.1-Jar kann unter Forge 1.20.1 laden, wird dort aber nicht getestet
  und nicht unterstützt (so steht es auch in der README).
- `tools/build_mod.sh` und ältere Notizen erwähnten Forge noch als eigenes Ziel; das
  wurde am 2026-11-16 bereinigt.
