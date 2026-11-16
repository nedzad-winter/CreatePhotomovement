# ADR-001: Gemeinsamer Code wird als Quellordner eingebunden

Status: gültig. Nachträglich dokumentiert am 2026-11-16.

## Ausgangslage

Ein Teil des Codes ist auf allen drei Zielen gleich. Er soll nur einmal existieren.
Die drei Ziele kompilieren aber gegen verschiedene Minecraft- und Create-Versionen.

## Entscheidung

`common/src/main/java` (und `common/src/main/resources`) wird in jedem Ziel als
zusätzlicher Quellordner eingetragen (`sourceSets.main.java.srcDir ...` in der
`build.gradle` jedes Ziels). Jedes Ziel kompiliert dieselben Dateien also selbst, gegen
seine eigene Minecraft-Version.

`common/` ist zusätzlich ein eigenes kleines Gradle-Projekt, das **nur** das Paket
`logic` kompiliert — die Klassen ohne Minecraft — und die schnellen Tests ausführt.

## Begründung

- Eine fertig gebaute gemeinsame Bibliothek könnte nur gegen *eine* Minecraft-Version
  gebaut sein. Als Quellordner passt derselbe Code zu jeder.
- Schreibt jemand etwas Loader-Spezifisches nach `common/`, brechen sofort die anderen
  Builds. Das ist gewollt. Der Test `CommonSourceRuleTest` meldet es schon vorher.
- Kein zusätzliches Werkzeug (Architectury o. Ä.) nötig.

## Folgen

- Ein Ziel kann nicht für sich allein gebaut werden, nur als Teil des Gesamtprojekts
  (`./gradlew :neoforge:1211:build`).
- Code in `common/` darf nur benutzen, was es in beiden Minecraft-Versionen und unter
  beiden Loadern gleich gibt.
- Für Ressourcen gilt dasselbe Prinzip: Was auf allen Zielen gleich ist, liegt einmal
  in `common/src/main/resources` (seit 2026-11-16). Eine Datei darf nicht zugleich dort
  und in einem Ziel liegen; `CommonResourcesTest` prüft das.
