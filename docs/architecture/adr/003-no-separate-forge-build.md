# ADR-003: No separate Forge build

Status: valid. Decided on 2026-02-07, documented retroactively here.

## Initial situation

There was a separate folder `forge/1201`. However, the code in `neoforge/1201` already
uses the classic Forge interfaces (`net.minecraftforge.*`) and is built with the legacy
variant of the NeoForge build plugin.

## Decision

`forge/1201` was removed (the corresponding line in `settings.gradle` is still present,
commented out). For Minecraft 1.20.1, only the jar from `neoforge/1201` exists.

## Rationale

Maintaining two targets with practically identical code doubles the work without any benefit.

## Consequences

- The NeoForge 1.20.1 jar can load under Forge 1.20.1, but it is not tested there and not
  supported (this is also stated in the README).
- `tools/build_mod.sh` and older notes still listed Forge as a separate target; this was
  cleaned up on 2026-11-16.
