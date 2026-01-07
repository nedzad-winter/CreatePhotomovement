# Create Photomovement

<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-1.21.1-green" alt="Minecraft 1.21.1">
  <img src="https://img.shields.io/badge/NeoForge-21.1.206-orange" alt="NeoForge">
  <img src="https://img.shields.io/badge/Create-6.0.10-blue" alt="Create Mod">
  <img src="https://img.shields.io/badge/License-MIT-yellow" alt="MIT License">
</p>

A [Create](https://github.com/Creators-of-Create/Create) mod addon that adds **solar-powered rotation sources** to your mechanical contraptions!

## Features

### Solar Generator
The **Solar Generator** is a block that generates rotational force when exposed to sunlight, providing a passive power source for your Create machines.

- **Generates 16 RPM** with **16 SU** stress capacity when powered by sunlight
- Requires **sky light level of 12 or higher** to operate (dynamic based on time of day and weather)
- Must be oriented with the **shaft horizontal** (glass panel facing up)
- **Sneak + Wrench** to reverse rotation direction
- Compatible with Engineer's Goggles for status display

### Color Variants
Solar Generators come in **17 color variants** - the default clear glass version plus all 16 Minecraft dye colors:

| | | | |
|:---:|:---:|:---:|:---:|
| White | Orange | Magenta | Light Blue |
| Yellow | Lime | Pink | Gray |
| Light Gray | Cyan | Purple | Blue |
| Brown | Green | Red | Black |

#### Changing Colors
You can change the color of any Solar Generator in two ways:

1. **Right-click** a placed generator with any dye
2. **Craft** any generator with a dye in a crafting table

## Crafting Recipes

### Solar Generator (Default)
```
G G G
S R S
H A H
```
- **G** - Glass (or Stained Glass for colored variants)
- **S** - Polished Deepslate Slab
- **R** - Redstone
- **H** - Create Shaft
- **A** - Andesite Casing

### Dyeing Recipe
Simply combine any Solar Generator with a dye in a shapeless recipe to change its color.

## How It Works

1. **Place the Solar Generator** with the glass panel facing upward (shaft must be horizontal)
2. **Connect shafts** to either side to transfer rotation to your machines
3. The generator **automatically activates** when it receives sufficient sunlight (light level of 12 or higher)
4. Power generation **reduces during storms** and **stops at night**
5. Make sure **nothing blocks the sky** above the generator

## Requirements

- Minecraft **1.21.1**
- NeoForge **21.1.206+**
- Create **6.0.10+**
- Flywheel **1.0.6+**
- Registrate **MC1.21-1.3.0+67**

## Installation

1. Install [NeoForge](https://neoforged.net/) for Minecraft 1.21.1
2. Install [Create](https://www.curseforge.com/minecraft/mc-mods/create) and its dependencies
3. Download the latest release from the [Releases](../../releases) page
4. Place the `.jar` file in your `mods` folder
5. Launch the game!

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
- Inspired by solar panels from various tech mods

---

<p align="center">
  <i>Harness the power of the sun for your mechanical dreams!</i>
</p>
