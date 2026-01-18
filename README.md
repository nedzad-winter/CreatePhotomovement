# Create Photomovement

<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-1.20.1%20|%201.21.1-green" alt="Minecraft Versions">
  <img src="https://img.shields.io/badge/Forge%20|%20NeoForge%20|%20Fabric-orange" alt="Mod Loaders">
  <img src="https://img.shields.io/badge/Create-6.0+-blue" alt="Create Mod">
  <img src="https://img.shields.io/badge/License-MIT-yellow" alt="MIT License">
</p>

A [Create](https://github.com/Creators-of-Create/Create) mod addon that adds **solar-powered rotation sources** to your mechanical contraptions!

## Features

### Solar Generator
A reliable, vertical-facing power source.

- **Generation**: 16 RPM and 16 SU (constant)
- **Requirement**: Light Level 12+ (needs direct sky access)
- **Weather**: Speed is halved to 8 RPM during rain
- **Orientation**: Must be placed with the glass panel facing up
- **Compatibility**: Check status with Engineer's Goggles

### Horizontal Solar Generator
A directional variant that tracks the sun's position.

- **Dynamic Output**:
  - **East Facing**: Peak generation in the morning
  - **West Facing**: Peak generation in the evening
- **Smart Obstruction System**:
  - **Block touching face**: Stops generation (0 RPM)
  - **Block within 2-10 blocks**: Reduced output
- **Weather**: Speed is halved during rain

### Advanced Solar Generator (NEW in 0.2.0!)
A powerful upgrade with brass casing for 2x power output.

- **Generation**: 32 RPM and 32 SU (double the basic version!)
- **Requirement**: Same as basic Solar Generator
- **Weather**: Speed is halved to 16 RPM during rain
- **Crafting**: Upgrade your basic generator with brass components

###  Horizontal Advanced Solar Generator (NEW in 0.2.0!)
The horizontal version of the advanced generator.

- **Generation**: 32 RPM base with dynamic stress capacity
- **Dynamic Output**: Same sun-tracking behavior as the basic horizontal
- **Weather**: Speed is halved during rain

### Color Variants
- **68 Blocks Total**: 4 generator types × 17 colors each
- **17 Color Options**: Clear glass + 16 Minecraft dye colors
- **Easy Customization**: 
  - Right-click placed blocks with dye
  - Craft any generator with dye
  - Re-dye colored blocks to change color
- **Conversion Recipes**: Convert between horizontal and vertical variants

## 🔧 How It Works

1. **Place the Generator** with the photovoltaic cells facing the sky (or desired direction for horizontal).
2. **Connect shafts** to transfer rotation to your machines.
3. The generator **automatically activates** when it receives sufficient sunlight (light level of 12 or higher).
4. **Stops at night** or when obstructed.
5. Make sure **nothing blocks the sky** above/in front of the generator.

##  Supported Platforms

| Platform | Minecraft | Status |
|----------|-----------|--------|
| NeoForge | 1.21.1 | ✅ |
| NeoForge | 1.20.1 | ✅ |
| Forge | 1.20.1 | ✅ |
| Fabric | 1.20.1 | ✅ |

## Requirements

- **Create Mod** 6.0+ (version depends on Minecraft version)
- **Flywheel** (included with Create)

## Translations

- English (en_us) ✅
- German (de_de) ✅
- Spanish (es_es) ✅

Want to help translate? Submit a PR with your language file!

## License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Feel free to:
- Report bugs or suggest features via [Issues](../../issues)
- Submit pull requests with improvements
- Help with translations

## Credits

- **Create Mod Team** - For the amazing Create mod and its API
- **Flywheel** - For the rendering engine
- **albertosaurio65** - Spanish translation
- Inspired by solar panels from various tech mods and the real world

---

<p align="center">
  <i>Harness the power of the sun for your mechanical dreams! </i>
</p>
