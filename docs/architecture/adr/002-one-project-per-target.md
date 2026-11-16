# ADR-002: One Gradle project per target, dependent classes exist three times

Status: valid. Documented retroactively on 2026-11-16.

## Initial situation

The mod should be released for NeoForge 1.21.1, NeoForge 1.20.1 and Fabric 1.20.1. Around
22 classes cannot be shared:

- **loader-dependent** — they use interfaces that only one loader has (registering the
  blocks, the entry point of the mod, the config);
- **version-dependent** — between 1.20.1 and 1.21.1, Minecraft renamed methods or added
  parameters (right-clicking blocks, saving and loading);
- **blocked** — they depend on `AllBlocks`, which looks different on NeoForge and Fabric.

## Decision

Each target is its own Gradle project in `<loader>/<mc-version without dots>/` with its
own build plugin. The classes that cannot be shared exist in each target in their own
version, under the same package and file name. Resources that differ (mainly the
`data/` folder) also live per target.

## Rationale

- The three build plugins (NeoForge ModDev, its legacy variant, and Fabric Loom) cannot
  be mixed in one project.
- For a single author, three readable versions of a class are easier to understand than
  one version with an abstraction layer on top.
- Identical file names make it easy to compare the targets.

## Consequences

- Changes to these classes must be made three times.
- Unintentional drift is possible. The analysis and the cleanup are described in
  [docs/common-code-analysis.md](../../common-code-analysis.md).
- Some asset files differ between the targets without a known reason (arc42, chapter 11, no. 1).
