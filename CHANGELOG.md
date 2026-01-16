# Changelog

## [0.1.1] - 2026-01-12

### Fixed
- Fixed recipe loading on Forge and NeoForge 1.20.1
  - Changed recipe result format from `"id"` to `"item"` (1.20.1 format)
  - Renamed `recipe/` directory to `recipes/` to match Minecraft's data pack structure
- Added missing MixinExtras dependency for Forge 1.20.1

### Notes
- This release adds support for Forge 1.20.1 and NeoForge 1.20.1
- All crafting recipes (Solar Generator, Horizontal Solar Generator, and all color variants) now load correctly

## [0.1.0] - 2026-01-09

### Added
- Initial release
- Solar Generator block - generates rotational power from sunlight
- Horizontal Solar Generator variant
- 16 color variants for both generator types
- Ponder scenes for tutorials
- JEI integration
