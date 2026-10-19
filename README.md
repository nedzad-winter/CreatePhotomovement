# Create Photomovement

<p align="center">
  <a href="../../actions/workflows/build.yml"><img src="../../actions/workflows/build.yml/badge.svg" alt="Build"></a>
  <img src="https://img.shields.io/badge/Minecraft-1.20.1%20|%201.21.1-green" alt="Minecraft Versions">
  <img src="https://img.shields.io/badge/Mod%20Loaders-NeoForge%20|%20Fabric-orange" alt="Mod Loaders">
  <img src="https://img.shields.io/badge/Create-6.0+-blue" alt="Create Mod">
  <img src="https://img.shields.io/badge/License-MIT-yellow" alt="MIT License">
</p>

Create Photomovement is a [Create](https://github.com/Creators-of-Create/Create) addon that adds solar-powered rotation sources to your mechanical contraptions. Two small generators make power from the sun on their own, and a larger windmill setup turns a field of solar sails into a big source of stress.

## The Blocks at a Glance

| Block | Size | Power | Best for |
| --- | --- | --- | --- |
| Solar Generator | 1 block | 16 RPM / 16 SU, constant | A simple, steady source |
| Horizontal Solar Generator | 1 block | 16 RPM, output follows the sun | Sun-tracking output on one side |
| Advanced Solar Generator | 1 block | 32 RPM / 32 SU, constant | Double the basic output |
| Horizontal Advanced Solar Generator | 1 block | 32 RPM, output follows the sun | Double the sun-tracking output |
| Solar Windmill Bearing | 1 block + sails | Scales with the number of sails | Large arrays |
| Solar Sail | 1 sail | Boosts the windmill in sunlight and rain | The windmill's power |

Each generator comes in 17 colours (clear glass plus the 16 dye colours). Each sail comes in 16 colours. Colour does not change how much power a block makes; it's purely for looks.

---

## Solar Generator

A reliable, vertical-facing power source.

- **Output**: 16 RPM and 16 SU, constant.
- **Requirement**: Sky light of 12 or higher at the panel, so it needs direct sky access. It stops at night or when something covers it.
- **Orientation**: Place it with the glass panel facing up.
- **Rain**: Speed is halved to 8 RPM.
- **Look**: A glass panel on top, a polished deepslate base and an andesite casing.
- **Status**: Check it with Engineer's Goggles.

**Crafting**

```
G G G
S R S
H A H
```

G = glass, S = polished deepslate slab, R = redstone, H = Create shaft, A = Create andesite casing.

---

## Horizontal Solar Generator

A directional version that follows the sun across the sky. Its output is a stress capacity that changes with the time of day instead of a fixed number.

- **Facing east**: Peak output in the morning, falling through the day.
- **Facing west**: Output builds through the day, peaking in the evening.
- **Facing north or south**: Does not track the sun and stays at the minimum, 8 SU per RPM.
- **Peak**: 64 SU per RPM with the default config (the base stress capacity of 16 times 4). The peak can be changed in the server config.
- **Obstruction**: A solid block touching the panel stops generation entirely. Blocks 2 to 10 blocks in front pin the output to the minimum of 8 SU per RPM. Blocks 11 or more away have no effect.
  - Clear glass never obstructs.
  - Tinted glass, leaves and ice do.
- **Rain**: Speed is halved.

**Crafting**: Craft a Solar Generator into a Horizontal Solar Generator with no other ingredients, and back again.

---

## Advanced Solar Generator

The basic generator with a brass casing for double the output.

- **Output**: 32 RPM and 32 SU, constant.
- **Requirement**: The same as the basic Solar Generator.
- **Rain**: Speed is halved to 16 RPM.
- **Look**: Brass casing, with the shaft sides showing Create's brass gearbox texture.

**Crafting**

```
G G G
S R S
H A H
```

G = glass, S = polished blackstone slab, R = redstone, H = Create shaft, A = Create brass casing.

---

## Horizontal Advanced Solar Generator

The horizontal version of the Advanced Solar Generator.

- **Output**: 32 RPM base, with the same sun-tracking stress capacity as the basic horizontal generator.
- **Obstruction and rain**: Work the same as on the basic horizontal generator, with the rain speed at half.

**Crafting**

```
G G G
S R S
A H A
```

G = glass, S = polished blackstone slab, R = redstone, H = Create shaft, A = Create brass casing.

You can also craft an Advanced Solar Generator into a Horizontal Advanced Solar Generator, and back again.

---

## Solar Windmill Bearing

A Create Windmill Bearing made for solar sails. It works the same way: it attaches to a windmill of sails and turns them into stress.

- **Connection**: Attaches to Solar Sails and to regular Create sails.
- **Output**: Stress Units (SU) based on the number of sails and the weather.
- **Requirement**: Needs sky access for the solar multipliers. See the table below.
- **Mixed sails**: Regular Create sails count too. The bearing fills each group of 8 with solar sails first and gives the remaining spots to regular sails, so a mixed windmill still gets solar output for its solar sails.
- **Look**: Each side has an inset solar panel in a brass frame. Create's rotating top is unchanged.

**Crafting**

```
   
R B R
R R R
```

R = redstone, B = Create windmill bearing.

---

## Solar Sail

Specialised sails that harness sunlight for boosted power. They are the reason the Solar Windmill Bearing is worth building.

- **Look**: A dark blue solar panel, four cells divided by silver busbars, with a highlight across the cells. The dye colour shows as a thin stitched border, so a coloured windmill keeps its colour.
- **Colours**: 16 colours, with the plain sail as white.
- **Power by weather**:
  - **Sunny**: 2x the output of a white sail.
  - **Raining**: 1.5x the output.
  - **Night, thunder or covered**: 1x, the same as a white sail.

**Crafting**

```
 G 
W R W
 S 
```

G = glass pane, W = wool, R = redstone, S = Create white sail.

**Dyeing**: Craft a Solar Sail with any dye to recolour it, or right-click a placed sail with dye.

### Sail Output

| Sails | Speed (RPM) | Standard SU | Solar (Rain) SU | Solar (Sunny) SU |
| :---: | :---: | :---: | :---: | :---: |
| 8 | 1 | 512 | 768 | 1024 |
| 16 | 2 | 1024 | 1536 | 2048 |
| 32 | 4 | 2048 | 3072 | 4096 |
| 64 | 8 | 4096 | 6144 | 8192 |
| 128 | 16 | 8192 | 12288 | 16384 |

Sail counts are rounded down to whole groups of 8. The bearing's speed stops increasing at 16 RPM, which Create's default sail count of 128 reaches.

---

## Colours and Conversions

- **Colours**: Every generator comes in clear glass and the 16 dye colours, 68 blocks in total across the four generator types. Sails come in 16 colours.
- **Dyeing**: Craft any generator or sail with a dye to change its colour, or right-click a placed block with a dye to recolour it without crafting. Coloured blocks can be recoloured again.
- **Conversions**: Crafting converts between the vertical and horizontal versions of the same colour, so you never lose the colour when you switch.

## Ponder

Ponder scenes for the solar generators and solar sails are included. Hold the Ponder key on an item in your inventory to see one.

## How It Works

1. **Place the generator** with the panel facing the sky (or the direction you want, for the horizontal version).
2. **Connect shafts** to carry the rotation to your machines.
3. The generator **starts working** when the sky light at the panel is 12 or higher.
4. It **stops at night** or when something blocks it.
5. Keep **nothing above or in front of it** that blocks the sky.

## Supported Platforms

| Platform | Minecraft | Status |
| --- | --- | --- |
| NeoForge | 1.21.1 | ✅ |
| NeoForge | 1.20.1 | ✅ |
| Fabric | 1.20.1 | ✅ |

There is no separate Forge build. The NeoForge 1.20.1 jar may load under Forge, but Forge is not tested and is not supported.

## Requirements

- **Create** 6.0 or later (the version depends on your Minecraft version)
- **Flywheel** (included with Create)

## Translations

- English (en_us) ✅
- German (de_de) ✅
- Spanish (es_es) ✅

Want to help translate? Submit a PR with your language file!

## License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Feel free to:
- Report bugs or suggest features via [Issues](../../issues)
- Submit pull requests with improvements
- Help with translations

## Credits

- **Create Mod Team** for the amazing Create mod and its API
- **Flywheel** for the rendering engine
- **albertosaurio65** for the Spanish translation
- Inspired by solar panels from various tech mods and the real world

---

<p align="center">
  <i>Harness the power of the sun for your mechanical dreams!</i>
</p>
