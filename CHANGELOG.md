# Changelog

## [0.1.3] - 2026-01-16

### Added
- **Fabric 1.20.1 Support**: Officially ported mod to Fabric 1.20.1 (migrated to MojMap)
- Spanish (es_es) translation (Community contribution: albertosaurio65)

### Fixed
- **Solar Generator Logic**:
  - Fixed sky access detection to correctly check for obstructions at any height
  - Fixed generator not starting immediately upon placement
- **Visuals**:
  - Fixed transparency rendering for colored Solar Generators (stained glass)
- **Recipes**:
  - Fixed JEI display issues for Solar Generator recipes (Fabric)

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
