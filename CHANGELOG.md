# Changelog

## [0.3.2] - 2026-05-09

### Fixed
- **Solar Windmill Bearing**: Fixed [#11](https://github.com/nedzad-winter/CreatePhotomovement/issues/11) — multiple visual issues with the bearing block:
    - Bearing top and back-shaft were invisible while Flywheel was active (Flywheel default). Registered Create's `BearingVisual` as the Flywheel visualization for `SOLAR_WINDMILL_BEARING`, mirroring Create's own `WindmillBearingBlockEntity` setup.
    - Bearing top showed a static "ghost" while the contraption rotated. Removed the static `Top` cuboid from `solar_windmill_bearing.json` — that Y 12-16 slot is reserved for the dynamic partial model.
    - Bearing top and shaft rendered nearly black even with open sky overhead. Added `.noOcclusion()` and overrode `propagatesSkylightDown` / `getLightBlock` on `SolarWindmillBearingBlock` so sky light reaches the bearing's position.

### Removed
- **Forge 1.20.1**: Removed the empty `forge/` build target. NeoForge 1.20.1 is a near-drop-in fork of Forge 1.20.1, so users on Forge can use the NeoForge 1.20.1 jar.

### Internal
- **Publish script**: Fixed `tools/publish_mod.ps1` so Modrinth uploads use the same per-loader naming as CurseForge (`createphotomovement-{loader}-{mc}-{version}`). The previous script sent the same `version_number` (`0.3.2`) for all three jars, which Modrinth rejects as a duplicate after the first upload — this is why only 2 of 3 jars made it onto Modrinth for v0.3.1.

### Supported Platforms
- NeoForge 1.21.1
- NeoForge 1.20.1
- Fabric 1.20.1

---

## [0.3.1] - 2026-02-16

### Added
- **Ponder**:
    - Created new Ponder scenes for Horizontal Solar Generator
    - Created new Ponder scenes for Solar Generator
    - Created new Ponder scenes for Solar Sails

### Fixed
- **Ponder**:
    - Fixed Ponder scene block pointing for obstructions

### General
- Mod version bumped to 0.3.1

## [0.3.0] - 2026-02-09

### Added
- **Solar Windmill Bearing & Sails**: New windmill bearing and sail components for generating rotational power from wind
- **16 color variants**: 16 colors for Solar Sails
- **Conversion recipes**: Convert between vertical and horizontal advanced generators (preserving color)
- **Dyeing recipes**: Dye any advanced generator to any of the 16 colors
- **In-world dyeing**: Right-click with dye to change color without crafting

### Changed
- All generator types now use item tags for dyeing recipes (allows re-dyeing colored blocks)
- Improved recipe organization with consistent naming conventions


### Supported Platforms
- NeoForge 1.21.1
- (Neo)Forge 1.20.1


## [0.3.0] - 2026-02-01

### Fixed
- **Solar Generator Visuals**: Raised glass layer on all solar generators (basic and advanced, vertical and horizontal) by 1 pixel to sit flush with the top of the block

---

## [0.2.0] - 2026-01-18

### Added
- **Advanced Solar Generator**: New brass-casing solar generator with 2x power output
- **Horizontal Advanced Solar Generator**: Horizontal variant of the advanced solar generator
- **34 new color variants**: 16 colors for Advanced and 16 for Horizontal Advanced generators
- **Conversion recipes**: Convert between vertical and horizontal advanced generators (preserving color)
- **Dyeing recipes**: Dye any advanced generator to any of the 16 colors
- **In-world dyeing**: Right-click with dye to change color without crafting

### Changed
- All generator types now use item tags for dyeing recipes (allows re-dyeing colored blocks)
- Improved recipe organization with consistent naming conventions

### Technical
- Added `adv_solar_generators` and `horz_adv_solar_generators` item/block tags
- All mod versions updated to 0.2.0

### Supported Platforms
- NeoForge 1.21.1
- NeoForge 1.20.1
- Forge 1.20.1
- Fabric 1.20.1

---

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
