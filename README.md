# Create Photomovement

<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-1.21.1-green" alt="Minecraft 1.21.1">
  <img src="https://img.shields.io/badge/NeoForge-21.1.206-orange" alt="NeoForge">
  <img src="https://img.shields.io/badge/Create-6.0.9-blue" alt="Create Mod">
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
- **Dyeing**: Can be dyed any of the 16 colors

### Color Variants
- **17 Options**: Clear glass + 16 Minecraft dye colors
- **Customization**: Right-click placed blocks with dye or craft with dye

## How It Works

1. **Place the Generator** with the photovoltaic cells facing the sky.
2. **Connect shafts** to transfer rotation to your machines.
3. The generator **automatically activates** when it receives sufficient sunlight (light level of 12 or higher).
4. **Stops at night** or when obstructed.
5. Make sure **nothing blocks the sky** above the generator.


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
- Inspired by solar panels from various tech mods and the real world.

---

<p align="center">
  <i>Harness the power of the sun for your mechanical dreams!</i>
</p>
