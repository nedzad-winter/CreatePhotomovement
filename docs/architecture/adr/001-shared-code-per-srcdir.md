# ADR-001: Shared code is included as a source folder

Status: valid. Documented retroactively on 2026-11-16.

## Initial situation

Part of the code is identical on all three targets. It should exist only once.
However, the three targets compile against different Minecraft and Create versions.

## Decision

`common/src/main/java` (and `common/src/main/resources`) is added to every target as an
additional source folder (`sourceSets.main.java.srcDir ...` in each target's
`build.gradle`). Each target therefore compiles the same files itself, against its own
Minecraft version.

`common/` is additionally a small Gradle project of its own that compiles **only** the
`logic` package — the classes without Minecraft — and runs the fast tests.

## Rationale

- A prebuilt shared library could only be built against *one* Minecraft version. As a
  source folder, the same code fits every target.
- If someone writes something loader-specific into `common/`, the other builds break
  immediately. That is intended. The test `CommonSourceRuleTest` reports it even earlier.
- No additional tool (Architectury or similar) is needed.

## Consequences

- One target cannot be built on its own, only as part of the whole project
  (`./gradlew :neoforge:1211:build`).
- Code in `common/` may only use what exists identically in both Minecraft versions and
  under both loaders.
- The same principle applies to resources: what is identical on all targets lives once
  in `common/src/main/resources` (since 2026-11-16). A file must not exist both there and
  in a target; `CommonResourcesTest` checks this.
