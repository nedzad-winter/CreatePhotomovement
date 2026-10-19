#!/usr/bin/env python3
"""Builds a guide world that explains what each block of Create Photomovement does.

Every block gets its own station -- its own platform, a board that says what it does and
live demos made of real blocks with a speedometer and a stress gauge next to them -- so
each can be photographed on its own. A clickable menu in the chat teleports between the
stations and switches time of day and weather.

    python tools/showcase/make_guide_world.py

Like make_showcase_world.py this writes only a level.dat and a data pack; the game builds
the stations itself the first time the world is opened. See that script for how the world
is put together and for the --template / --out / --origin options.

The numbers on the boards are the ones the code produces, not the ones the README
promises: the demos are checked against them by running the build in the game.
"""
import argparse
import json
import math
import os
import shutil
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import make_showcase_world as base  # noqa: E402  (NBT writer, level.dat copy, colour list)

MOD = base.MOD
NS = "cpm_guide"
WORLD_NAME = "Create Photomovement Block Guide"
STATION_SPACING = 40
START_TIME = 1000          # morning: the east-facing panels are near their peak
COLORS = base.COLORS

GOLD, AQUA, WHITE, GRAY, GREEN, RED, YELLOW = "gold", "aqua", "white", "gray", "green", "red", "yellow"


# ---------------------------------------------------------------- scene helpers

class Scene:
    """Everything one station puts into the world, in coordinates relative to its origin."""

    def __init__(self, index, name, view=(11, 4, 12)):
        self.index, self.name, self.view = index, name, view
        self.blocks = []     # (x, y, z, block id, state string or "")
        self.labels = []     # (x, y, z, components, scale, with background)
        self.expect = []     # checks the in-game verification runs

    # --- placing things
    def block(self, x, y, z, block, state=""):
        self.blocks.append((x, y, z, block, state))

    # --- text
    def label(self, x, y, z, lines, scale=0.9, background=False):
        self.labels.append((x, y, z, lines, scale, background))

    def board(self, x, y, z, title, lines, scale=1.2):
        comps = [(title + "\n", GOLD, True)]
        comps += [(text + "\n", color, False) for text, color in lines]
        self.labels.append((x, y, z, comps, scale, True))

    def title(self, x, y, z, text, scale=3.2):
        self.labels.append((x, y, z, [(text, GOLD, True)], scale, False))

    # --- the pieces the demos are made of
    def speedometer(self, x, y, z, rotation_axis):
        self.block(x, y, z, "create:speedometer", gauge_state(rotation_axis))

    def stressometer(self, x, y, z, rotation_axis):
        self.block(x, y, z, "create:stressometer", gauge_state(rotation_axis))

    def fan(self, x, y, z, blowing):
        self.block(x, y, z, "create:encased_fan", f"facing={blowing}")

    # --- checks
    def expect_speed(self, name, x, y, z, rpm):
        self.expect.append({"name": f"{self.name}: {name}", "kind": "speed", "pos": [x, y, z], "value": rpm,
                            "index": self.index})

    def expect_capacity(self, name, x, y, z, per_rpm):
        self.expect.append({"name": f"{self.name}: {name}", "kind": "capacity", "pos": [x, y, z],
                            "value": per_rpm, "index": self.index})

    def expect_sails(self, name, x, y, z, regular, solar):
        self.expect.append({"name": f"{self.name}: {name}, sails", "kind": "sails", "pos": [x, y, z],
                            "value": [regular, solar], "index": self.index})

    def expect_running(self, name, x, y, z):
        self.expect.append({"name": f"{self.name}: {name}", "kind": "running", "pos": [x, y, z], "value": 1,
                            "index": self.index})


def gauge_state(rotation_axis):
    """Create's gauges rotate around an axis that depends on where the dial faces.

    The dial faces south (towards the viewer) for the x and y axes and up for z, which
    gives these combinations (see DirectionalAxisKineticBlock.getRotationAxis).
    """
    return {"x": "facing=south,axis_along_first=true",
            "y": "facing=south,axis_along_first=false",
            "z": "facing=up,axis_along_first=false"}[rotation_axis]


def result(text, color):
    return (text, color, False)


def lines(*pairs):
    return list(pairs)


# ---------------------------------------------------------------- the stations

def hub():
    s = Scene(0, "Start", view=(10, 0, 8))
    s.title(10, 5.5, -6, "Create Photomovement", 3.6)
    s.board(10, 2.6, -4, "Block Guide", lines(
        ("One station for each block, each on its own platform", WHITE),
        ("Click a station in the chat to teleport there", AQUA),
        ("Show the menu again: /function cpm_guide:menu", GRAY),
        ("", WHITE),
        ("Speedometer: shows the RPM", WHITE),
        ("Stress gauge with Engineer's Goggles: shows the SU", WHITE),
        ("Time of day and weather: buttons in the chat menu", WHITE),
    ), 1.3)
    return s


def solar_generator(i):
    s = Scene(i, "Solar Generator")
    s.title(11, 9.5, -6, "Solar Generator")
    s.board(11, 6.2, -4, "Turns sunlight into rotation", lines(
        ("16 RPM, 16 SU per RPM  (256 SU)", WHITE),
        ("Needs open sky above its glass panel", WHITE),
        ("Rain: half speed (8 RPM)", WHITE),
        ("Night and thunderstorms: no power", WHITE),
        ("Right-click with a dye to recolour it", WHITE),
    ))
    demos = [
        ("Open sky", "16 RPM", GREEN, None, "solar_generator", 16),
        ("Glass above", "16 RPM, light passes", GREEN, "minecraft:glass", "solar_generator", 16),
        ("Solid block above", "0 RPM", RED, "minecraft:stone", "solar_generator", 0),
        ("Carpet on top", "0 RPM, same for snow", RED, "minecraft:white_carpet", "solar_generator", 0),
        ("Dyed variant", "works the same", GREEN, None, "blue_solar_generator", 16),
    ]
    for n, (title, detail, color, cover, block, rpm) in enumerate(demos):
        x = 1 + n * 5
        s.block(x, 0, 0, f"{MOD}:{block}", "axis=x")
        s.speedometer(x + 1, 0, 0, "x")
        if cover:
            s.block(x, 1, 0, cover)
        s.label(x + 0.5, 0.05, 2.3, [(title + "\n", WHITE, True), (detail, color, False)])
        s.expect_speed(title, x + 1, 0, 0, rpm)
    return s


def horizontal_tracking(i):
    s = Scene(i, "Horizontal: sun tracking")
    s.title(11, 9.5, -6, "Horizontal Solar Generator")
    s.board(11, 6.2, -4, "Panel on the side, it follows the sun", lines(
        ("16 RPM, 128 to 1024 SU depending on direction and time", WHITE),
        ("East-facing: peak in the morning", WHITE),
        ("West-facing: peak in the evening", WHITE),
        ("North- and south-facing: always the minimum (128 SU)", WHITE),
        ("The capacity updates about every 10 seconds", GRAY),
        ("Change the time with the chat menu", AQUA),
    ))
    # east-facing: the shaft is at the back, so the chain runs west
    x = 3
    s.block(x, 0, 0, f"{MOD}:horizontal_solar_generator", "facing=east")
    s.speedometer(x - 1, 0, 0, "x")
    s.stressometer(x - 2, 0, 0, "x")
    s.fan(x - 3, 0, 0, "west")
    s.label(x - 0.5, 0.05, 2.3, [("East-facing\n", WHITE, True), ("morning peak", YELLOW, False)])
    s.expect_speed("east, speed", x - 1, 0, 0, 16)
    s.expect_capacity("east, morning", x, 0, 0, 55)
    # west-facing: chain runs east. It stands one row further back on purpose: on the same
    # row it would sit 9 blocks in front of the east-facing panel and count as an obstruction.
    x, z = 12, 5
    s.block(x, 0, z, f"{MOD}:horizontal_solar_generator", "facing=west")
    s.speedometer(x + 1, 0, z, "x")
    s.stressometer(x + 2, 0, z, "x")
    s.fan(x + 3, 0, z, "east")
    s.label(x + 1.5, 0.05, z + 2.3, [("West-facing\n", WHITE, True), ("evening peak", YELLOW, False)])
    s.expect_speed("west, speed", x + 1, 0, z, 16)
    s.expect_capacity("west, morning", x, 0, z, 8)
    # south-facing: chain runs north
    x, z = 22, 3
    s.block(x, 0, z, f"{MOD}:horizontal_solar_generator", "facing=south")
    s.speedometer(x, 0, z - 1, "z")
    s.stressometer(x, 0, z - 2, "z")
    s.fan(x, 0, z - 3, "north")
    s.label(x + 0.5, 0.05, z + 2.3, [("South-facing\n", WHITE, True), ("always the minimum", YELLOW, False)])
    s.expect_speed("south, speed", x, 0, z - 1, 16)
    s.expect_capacity("south", x, 0, z, 8)
    return s


def horizontal_obstructions(i):
    s = Scene(i, "Horizontal: obstructions", view=(11, 5, 22))
    s.title(11, 9.5, -6, "Obstructions")
    s.board(11, 6.2, -4, "The panel looks up to 10 blocks ahead", lines(
        ("Solid block directly in front: stops (0 RPM)", WHITE),
        ("Solid block 2 to 10 blocks away: minimum (128 SU)", WHITE),
        ("Solid block 11 or more blocks away: no effect", WHITE),
        ("Clear glass never obstructs. Leaves, ice and tinted glass do.", WHITE),
        ("Watch the stress gauges: they show the share of the capacity in use", GRAY),
    ))
    rows = [
        ("Open", "full output", GREEN, None, 16, 55),
        ("Stone touching", "stops", RED, (1, "minecraft:stone"), 0, 0),
        ("Stone 5 blocks away", "minimum output", YELLOW, (5, "minecraft:stone"), 16, 8),
        ("Stone 12 blocks away", "no effect", GREEN, (12, "minecraft:stone"), 16, 55),
        ("Glass touching", "no effect", GREEN, (1, "minecraft:glass"), 16, 55),
        ("Leaves touching", "stops", RED, (1, "minecraft:oak_leaves[persistent=true]"), 0, 0),
    ]
    gx = 3
    for n, (title, detail, color, obstacle, rpm, cap) in enumerate(rows):
        z = n * 3
        s.block(gx, 0, z, f"{MOD}:horizontal_solar_generator", "facing=east")
        s.speedometer(gx - 1, 0, z, "x")
        s.stressometer(gx - 2, 0, z, "x")
        s.fan(gx - 3, 0, z, "west")
        if obstacle:
            dist, block = obstacle
            if "[" in block:
                bid, st = block.split("[")
                s.block(gx + dist, 0, z, bid, st.rstrip("]"))
            else:
                s.block(gx + dist, 0, z, block)
        s.label(gx + 16, 0.05, z + 0.5, [(title + "\n", WHITE, True), (detail, color, False)], 0.9)
        s.expect_speed(title + ", speed", gx - 1, 0, z, rpm)
        s.expect_capacity(title + ", capacity", gx, 0, z, cap)
    return s


def advanced_generator(i):
    s = Scene(i, "Advanced Solar Generator")
    s.title(11, 9.5, -6, "Advanced Solar Generator")
    s.board(11, 6.2, -4, "Brass casing: double speed", lines(
        ("32 RPM, 16 SU per RPM  (512 SU)", WHITE),
        ("Needs the same as the basic one: open sky, light level 12+", WHITE),
        ("Rain: half speed (16 RPM)", WHITE),
        ("Made from a Solar Generator and brass", WHITE),
        ("Right-click with a dye to recolour it", WHITE),
    ))
    demos = [
        (3, "solar_generator", "Solar Generator", "16 RPM", 16),
        (12, "adv_solar_generator", "Advanced Solar Generator", "32 RPM", 32),
        (21, "red_adv_solar_generator", "Dyed Advanced", "32 RPM", 32),
    ]
    for x, block, title, detail, rpm in demos:
        s.block(x, 0, 0, f"{MOD}:{block}", "axis=x")
        s.speedometer(x + 1, 0, 0, "x")
        s.fan(x + 2, 0, 0, "east")
        s.label(x + 1, 0.05, 2.3, [(title + "\n", WHITE, True), (detail, GREEN, False)])
        s.expect_speed(title, x + 1, 0, 0, rpm)
    return s


def advanced_horizontal(i):
    s = Scene(i, "Advanced Horizontal")
    s.title(11, 9.5, -6, "Advanced Horizontal Solar Generator")
    s.board(11, 6.2, -4, "Same sun tracking, double speed", lines(
        ("32 RPM, 256 to 2048 SU depending on direction and time", WHITE),
        ("The basic one: 16 RPM, 128 to 1024 SU", WHITE),
        ("Obstructions work exactly like on the basic one", WHITE),
        ("Both shown here facing east, at their morning peak", GRAY),
    ))
    rows = [
        ("horizontal_solar_generator", "Horizontal", "16 RPM", 16, 0),
        ("horz_adv_solar_generator", "Advanced Horizontal", "32 RPM", 32, 4),
    ]
    gx = 3
    for block, title, detail, rpm, z in rows:
        s.block(gx, 0, z, f"{MOD}:{block}", "facing=east")
        s.speedometer(gx - 1, 0, z, "x")
        s.stressometer(gx - 2, 0, z, "x")
        s.fan(gx - 3, 0, z, "west")
        s.label(gx + 4, 0.05, z + 0.5, [(title + "\n", WHITE, True), (detail, GREEN, False)])
        s.expect_speed(title + ", speed", gx - 1, 0, z, rpm)
        s.expect_capacity(title + ", capacity", gx, 0, z, 55)
    return s


def windmills(i):
    s = Scene(i, "Solar Windmill Bearing", view=(15, 7, 13))
    s.title(15, 12.5, -6, "Solar Windmill Bearing and Solar Sails")
    s.board(15, 9.2, -4, "Works like a Windmill Bearing, built for Solar Sails", lines(
        ("Right-click the bearing with an empty hand to start it, again to stop it", AQUA),
        ("Bearing at the bottom, a column of Radial Chassis on top, the sails on both sides", WHITE),
        ("Sunny: 2x the power of regular sails.  Rain: 1.5x", WHITE),
        ("Night, thunder or no sky access: 1x", WHITE),
        ("16 sails give 2 RPM: 2048 SU with solar sails, 1024 SU with regular ones", WHITE),
        ("Regular Create sails can be mixed in. Sails stick together without glue.", WHITE),
        ("Right-click a sail with a dye to recolour the connected sails", WHITE),
    ))
    # Each windmill: a tower of two gauges and the bearing, 4 radial chassis on top, and a wing of
    # 2 x 4 sails on each side of the chassis. 16 sails make 2 RPM.
    rows = ["orange", "yellow", "lime", "cyan"]   # the dyed windmill, one colour per level
    mills = [
        (4, "Solar sails", "16 solar sails: 2048 SU", GREEN, ("solar", "solar"), 1024, 0, 16),
        (12, "Regular sails", "16 Create sails: 1024 SU", WHITE, ("plain", "plain"), 512, 16, 0),
        (20, "Mixed", "8 solar + 8 regular: 1536 SU", YELLOW, ("solar", "plain"), 768, 8, 8),
        (28, "Dyed solar sails", "same output as the first", GREEN, ("dyed", "dyed"), 1024, 0, 16),
    ]
    for x, title, detail, color, (east, west), per_rpm, regular, solar in mills:
        s.stressometer(x, 0, 0, "y")
        s.speedometer(x, 1, 0, "y")
        s.block(x, 2, 0, f"{MOD}:solar_windmill_bearing", "facing=up")
        for level in range(4):
            y = 3 + level
            s.block(x, y, 0, "create:radial_chassis", "axis=y,sticky_east=true,sticky_west=true")
            for side, kind in ((1, east), (-1, west)):
                for reach in (1, 2):
                    if kind == "solar":
                        block = f"{MOD}:solar_sail"
                    elif kind == "plain":
                        block = "create:white_sail"
                    else:
                        block = f"{MOD}:{rows[level]}_solar_sail"
                    s.block(x + side * reach, y, 0, block, "facing=south")
        s.label(x + 0.5, 0.05, 3.2, [(title + "\n", WHITE, True), (detail, color, False)])
        s.expect_running(title, x, 2, 0)
        s.expect_capacity(title, x, 2, 0, per_rpm)
        s.expect_speed(title + ", speed", x, 1, 0, 2)
        s.expect_sails(title, x, 2, 0, regular, solar)
    return s


def all_scenes():
    builders = [hub, solar_generator, horizontal_tracking, horizontal_obstructions, advanced_generator,
                advanced_horizontal, windmills]
    return [b() if b is hub else b(n) for n, b in enumerate(builders)]


# ---------------------------------------------------------------- commands

def argb(alpha, rgb):
    value = (alpha << 24) | rgb
    return value - (1 << 32) if value >= 1 << 31 else value


def snbt_string(text):
    return "'" + text.replace("\\", "\\\\").replace("'", "\\'") + "'"


def components_json(comps):
    return json.dumps([{"text": t, "color": c, **({"bold": True} if b else {})} for t, c, b in comps],
                      ensure_ascii=False)


def station_origin(scene, ox):
    return ox + scene.index * STATION_SPACING


def platform_of(scene, ox):
    """The floor under a station: its blocks and labels plus a margin."""
    sx = station_origin(scene, ox)
    xs = [sx + b[0] for b in scene.blocks] + [sx + lab[0] for lab in scene.labels]
    zs = [b[2] for b in scene.blocks] + [lab[2] for lab in scene.labels]
    return math.floor(min(xs)) - 6, math.floor(min(zs)) - 5, math.ceil(max(xs)) + 6, math.ceil(max(zs)) + 9


def build_function(ox, oy, oz):
    scenes = all_scenes()
    lines_out = ["# Builds every station. Safe to run again: it clears each area first.",
                 f"kill @e[type=minecraft:text_display,tag={NS}]"]
    for sc in scenes:
        sx = station_origin(sc, ox)
        x1, z1, x2, z2 = platform_of(sc, ox)
        z1, z2 = z1 + oz, z2 + oz
        lines_out += [
            f"# --- station {sc.index}: {sc.name}",
            f"fill {x1} {oy + 1} {z1} {x2} {oy + 14} {z2} minecraft:air",
            f"fill {x1} {oy} {z1} {x2} {oy} {z2} minecraft:air",
            f"fill {x1} {oy - 1} {z1} {x2} {oy - 1} {z2} minecraft:smooth_quartz",
        ]
        for x, y, z, block, state in sc.blocks:
            suffix = f"[{state}]" if state else ""
            lines_out.append(f"setblock {sx + x} {oy + y} {oz + z} {block}{suffix}")
        for x, y, z, comps, scale, background in sc.labels:
            bg = f"background:{argb(0xB0, 0x0B1220)}," if background else f"background:{argb(0, 0)},"
            s = f"{scale}f"
            lines_out.append(
                f"summon minecraft:text_display {sx + x} {oy + y} {oz + z} "
                "{Tags:[\"" + NS + "\"],billboard:\"vertical\",alignment:\"center\",line_width:600," + bg
                + "text:" + snbt_string(components_json(comps)) + ","
                "transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],"
                f"translation:[0f,0f,0f],scale:[{s},{s},{s}]}}}}")
    hub_x = station_origin(scenes[0], ox)
    lines_out += [
        f"time set {START_TIME}",
        "weather clear",
        "gamerule doDaylightCycle false",
        "gamerule doWeatherCycle false",
        "gamerule doMobSpawning false",
        "difficulty peaceful",
        f"setworldspawn {hub_x + scenes[0].view[0]} {oy} {oz + scenes[0].view[2]} 180",
        f"tp @a {hub_x + scenes[0].view[0]} {oy} {oz + scenes[0].view[2]} 180 10",
        f"scoreboard players set #built {NS} 1",
        "forceload remove all",
        f"function {NS}:menu",
    ]
    return "\n".join(lines_out) + "\n"


def menu_function(ox, oy, oz):
    scenes = all_scenes()

    def click(text, color, command):
        return {"text": text, "color": color, "clickEvent": {"action": "run_command", "value": command}}

    parts = [{"text": "\n=== Create Photomovement Block Guide ===\n", "color": "gold", "bold": True}]
    for sc in scenes:
        sx = station_origin(sc, ox) + sc.view[0]
        y = oy + sc.view[1]
        z = oz + sc.view[2]
        label = f"[{sc.index}] {sc.name}\n" if sc.index else "[0] Start\n"
        parts.append(click(label, "aqua", f"/tp @s {sx} {y} {z} 180 25"))
    parts.append({"text": "Time: ", "color": "gray"})
    for name, tick in (("Morning", 1000), ("Noon", 6000), ("Evening", 11000), ("Night", 18000)):
        parts.append(click(f"[{name}] ", "yellow", f"/time set {tick}"))
    parts.append({"text": "\nWeather: ", "color": "gray"})
    parts.append(click("[Clear] ", "yellow", "/weather clear"))
    parts.append(click("[Rain]\n", "yellow", "/weather rain"))
    parts.append(click("[Engineer's Goggles]", "green", "/give @s create:goggles"))
    parts.append({"text": "   Menu: /function cpm_guide:menu\n", "color": "gray"})
    return "tellraw @a " + json.dumps(parts, ensure_ascii=False) + "\n"


def write_datapack(world, ox, oy, oz):
    pack = os.path.join(world, "datapacks", NS)
    if os.path.isdir(pack):
        shutil.rmtree(pack)
    fn = os.path.join(pack, "data", NS, "function")
    tags = os.path.join(pack, "data", "minecraft", "tags", "function")
    os.makedirs(fn)
    os.makedirs(tags)
    with open(os.path.join(pack, "pack.mcmeta"), "w", newline="\n") as f:
        json.dump({"pack": {"pack_format": 48, "description": "Builds the Create Photomovement block guide"}},
                  f, indent=2)
    with open(os.path.join(tags, "load.json"), "w", newline="\n") as f:
        json.dump({"values": [f"{NS}:load"]}, f, indent=2)

    scenes = all_scenes()
    x1 = min(platform_of(sc, ox)[0] for sc in scenes)
    x2 = max(platform_of(sc, ox)[2] for sc in scenes)
    z1 = min(platform_of(sc, ox)[1] for sc in scenes) + oz
    z2 = max(platform_of(sc, ox)[3] for sc in scenes) + oz
    files = {
        "load.mcfunction":
            f"scoreboard objectives add {NS} dummy\n"
            f"execute unless score #built {NS} matches 1 run function {NS}:start\n",
        "start.mcfunction":
            f"forceload add {x1 - 8} {z1 - 8} {x2 + 8} {z2 + 8}\n"
            f"schedule function {NS}:build 100t\n",
        "build.mcfunction": build_function(ox, oy, oz),
        "menu.mcfunction": menu_function(ox, oy, oz),
    }
    for name, text in files.items():
        with open(os.path.join(fn, name), "w", newline="\n", encoding="utf-8") as f:
            f.write(text)


def expectations(ox, oy, oz):
    out = []
    for sc in all_scenes():
        sx = station_origin(sc, ox)
        for e in sc.expect:
            x, y, z = e["pos"]
            out.append({**e, "pos": [sx + x, oy + y, oz + z]})
    return out


def main():
    here = os.path.dirname(os.path.abspath(__file__))
    repo = os.path.abspath(os.path.join(here, "..", ".."))
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--template", default="New World")
    parser.add_argument("--out", default=os.path.join(repo, "neoforge", "1211", "run", "saves"))
    parser.add_argument("--origin", nargs=3, type=int, default=[0, -60, 0], metavar=("X", "Y", "Z"))
    parser.add_argument("--expectations", metavar="FILE", help="also write what the demos should show, as JSON")
    args = parser.parse_args()

    ox, oy, oz = args.origin
    template = os.path.join(args.out, args.template)
    if not os.path.isfile(os.path.join(template, "level.dat")):
        sys.exit(f"No level.dat in {template}. Create any superflat 1.21.1 world once, or pass --template.")

    world = os.path.join(args.out, WORLD_NAME)
    if os.path.exists(world):
        shutil.rmtree(world)
    hub_scene = all_scenes()[0]
    base.write_level_dat(template, world, (ox + hub_scene.view[0], oy, oz + hub_scene.view[2]),
                         world_name=WORLD_NAME, day_time=START_TIME)
    write_datapack(world, ox, oy, oz)
    if args.expectations:
        with open(args.expectations, "w") as f:
            json.dump(expectations(ox, oy, oz), f, indent=1)

    scenes = all_scenes()
    print(f"Wrote {world}")
    print(f"  {len(scenes)} stations, {sum(len(s.blocks) for s in scenes)} blocks, "
          f"{sum(len(s.labels) for s in scenes)} labels, {len(expectations(ox, oy, oz))} checks")


if __name__ == "__main__":
    main()
