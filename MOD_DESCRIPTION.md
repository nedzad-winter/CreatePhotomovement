# Create Photomovement

## Features
This is a WIP so things will change frequently.

### Solar Generator
The **Solar Generator** is a block that generates rotational force when exposed to sunlight, providing a passive power source for your Create machines.

- **Generates 16 RPM** with **16 SU** stress capacity when powered by sunlight
- Requires **sky light level of 12 or higher** to operate
- Must be oriented with the **shaft horizontal** (glass panel facing up)
- **Sneak + Wrench** to reverse rotation direction
- Compatible with Engineer's Goggles for status display

### Horizontal Solar Generator
The **Horizontal Solar Generator** is a new directional variant that tracks the sun's movement to optimize power generation.

- **Dynamic Stress Capacity**: Output varies based on time of day.
  - **East Facing**: Peak generation in the morning.
  - **West Facing**: Peak generation in the evening.
- **Smart Obstruction**: Detects obstructions up to 10 blocks away. When a block is placed directly next to the generating face it will stop generating. When it is placed at a distance 2 to 10 blocks away it will set the output to a minimum of 8SU/RPM.
- **Colorable**: Available in all 16 dye colors.

### Color Variants
Both Solar Generators come in **17 color variants** - the default clear glass version plus all 16 Minecraft dye colors.

#### Changing Colors
You can change the color of any Solar Generator in two ways:
1. **Right-click** a placed generator with any dye
2. **Craft** any generator with a dye in a crafting table

## How It Works

1. **Place the Generator** with the photovoltaic cells facing the sky.
2. **Connect shafts** to transfer rotation to your machines.
3. The generator **automatically activates** when it receives sufficient sunlight (light level of 12 or higher).
4. **Stops at night** or when obstructed.
5. Make sure **nothing blocks the sky** above the generator.

## Credits

- **Create Mod Team** - For the amazing Create mod and its API
- **Flywheel** - For the rendering engine
- Inspired by solar panels from various tech mods

---

<p align="center">
  <i>Harness the power of the sun for your mechanical dreams!</i>
</p>
