#!/usr/bin/env python3
"""Builds a showcase world for Create Photomovement (Minecraft 1.21.1) for screenshots.

Every block of the mod is put on display in one compact block that fits a single
screenshot: four rows of generators (the plain one plus
16 colours each), a row of sails with the windmill bearing, and a small assembled
windmill. The world is a superflat creative world; a data pack inside it builds
everything the first time the world is opened, so no command has to be typed.

    python tools/showcase/make_showcase_world.py

The world is written to neoforge/1211/run/saves/Create Photomovement Showcase, which is
where the dev client looks. Open it from the world list, wait a few seconds, then fly
around and take screenshots.

The game cannot be started from here, so the world is not written chunk by chunk.
Instead it consists of the two things the game needs to open a world -- a level.dat
and a data pack -- and the game does the rest. The level.dat is copied from an existing
1.21.1 superflat world (its Player entry is dropped), because writing one from scratch
would mean guessing every field the game expects.

Options:
    --template NAME   save to take level.dat from (default: New World)
    --out DIR         saves folder to write into (default: the dev client's)
    --origin X Y Z    where the display is built; Y is the height blocks stand on
                      (default 0 -60 0, the surface of a default superflat world)
"""
import argparse
import gzip
import json
import os
import shutil
import struct
import sys
import time

MOD = "createphotomovement"
NS = "cpm_showcase"
WORLD_NAME = "Create Photomovement Showcase"

COLORS = ["white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
          "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"]

SPACING_X = 1          # blocks between two neighbours in a row (touching)
SPACING_Z = 2          # blocks between two rows (one block of gap for the label)
COLUMNS = 17           # the plain block plus 16 colours


# ---------------------------------------------------------------- the layout

def layout(ox=0, oy=-60, oz=0):
    """Returns (blocks, labels, platform) in world coordinates.

    blocks:   list of (x, y, z, block id, state string)
    labels:   list of (x, y, z, text, scale)
    platform: (x1, z1, x2, z2) of the floor under the display
    """
    blocks, labels = [], []
    first_row_z = oz - 8

    def row_x(i):
        return ox + (i - COLUMNS // 2) * SPACING_X

    families = [
        ("Solar Generator", "solar_generator", "{c}_solar_generator", "axis=z"),
        ("Horizontal Solar Generator", "horizontal_solar_generator",
         "{c}_horizontal_solar_generator", "facing=south"),
        ("Advanced Solar Generator", "adv_solar_generator", "{c}_adv_solar_generator", "axis=z"),
        ("Horizontal Advanced Solar Generator", "horz_adv_solar_generator",
         "{c}_horz_adv_solar_generator", "facing=south"),
    ]
    row = 0
    for title, plain, coloured, state in families:
        z = first_row_z + row * SPACING_Z
        ids = [plain] + [coloured.format(c=c) for c in COLORS]
        for i, block in enumerate(ids):
            blocks.append((row_x(i), oy, z, f"{MOD}:{block}", state))
        labels.append((ox, oy + 0.05, z + 1.5, title, 1.0))
        row += 1

    # sails row: the bearing, then the white sail and the 15 dyed ones
    z = first_row_z + row * SPACING_Z
    blocks.append((row_x(0), oy, z, f"{MOD}:solar_windmill_bearing", "facing=up"))
    sails = ["solar_sail"] + [f"{c}_solar_sail" for c in COLORS if c != "white"]
    for i, block in enumerate(sails):
        blocks.append((row_x(i + 1), oy, z, f"{MOD}:{block}", "facing=south"))
    labels.append((ox, oy + 0.05, z + 1.5, "Solar Windmill Bearing and Solar Sails", 1.0))

    # a small assembled windmill beside the grid: bearing facing south with a 3x3 plane
    # of sails on its south side, so the sails face whoever looks at the display
    wx, wz = ox + 13, oz - 5
    blocks.append((wx, oy + 1, wz, f"{MOD}:solar_windmill_bearing", "facing=south"))
    for dx in (-1, 0, 1):
        for dy in (-1, 0, 1):
            blocks.append((wx + dx, oy + 1 + dy, wz + 1, f"{MOD}:solar_sail", "facing=south"))
    labels.append((wx, oy + 0.05, wz + 3.3, "Solar Windmill", 1.0))

    labels.append((ox + 2, oy + 3.2, first_row_z - 2, "Create Photomovement", 3.5))

    platform = (ox - 11, first_row_z - 5, ox + 17, oz + 5)
    return blocks, labels, platform


# ---------------------------------------------------------------- the data pack

def text_component(text):
    return json.dumps({"text": text, "color": "white"})


def build_function(ox, oy, oz):
    blocks, labels, (px1, pz1, px2, pz2) = layout(ox, oy, oz)
    lines = [
        "# Builds the showcase. Safe to run again: it clears the area first.",
        f"kill @e[type=minecraft:text_display,tag={NS}]",
        f"fill {px1} {oy + 1} {pz1} {px2} {oy + 10} {pz2} minecraft:air",
        f"fill {px1} {oy - 1} {pz1} {px2} {oy - 1} {pz2} minecraft:smooth_quartz",
        f"fill {px1} {oy} {pz1} {px2} {oy} {pz2} minecraft:air",
    ]
    for x, y, z, block, state in blocks:
        lines.append(f"setblock {x} {y} {z} {block}[{state}]")
    for x, y, z, text, scale in labels:
        s = f"{scale}f"
        lines.append(
            f"summon minecraft:text_display {x} {y} {z} "
            "{Tags:[\"" + NS + "\"],billboard:\"vertical\",alignment:\"center\","
            "text:'" + text_component(text).replace("'", "\\'") + "',"
            "transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],"
            f"translation:[0f,0f,0f],scale:[{s},{s},{s}]}}}}")
    lines += [
        "time set 6000",
        "weather clear",
        "gamerule doDaylightCycle false",
        "gamerule doWeatherCycle false",
        "gamerule doMobSpawning false",
        "difficulty peaceful",
        f"setworldspawn {ox} {oy} {oz + 13} 180",
        f"tp @a {ox} {oy} {oz + 13} 180 20",
        f"scoreboard players set #built {NS} 1",
        "forceload remove all",
        'tellraw @a {"text":"Create Photomovement showcase is ready. Fly with double-tap space.",'
        '"color":"gold"}',
    ]
    return "\n".join(lines) + "\n"


def write_datapack(world, ox, oy, oz):
    pack = os.path.join(world, "datapacks", NS)
    if os.path.isdir(pack):
        shutil.rmtree(pack)
    fn = os.path.join(pack, "data", NS, "function")
    tags = os.path.join(pack, "data", "minecraft", "tags", "function")
    os.makedirs(fn)
    os.makedirs(tags)

    with open(os.path.join(pack, "pack.mcmeta"), "w", newline="\n") as f:
        json.dump({"pack": {"pack_format": 48,
                            "description": "Builds the Create Photomovement showcase"}}, f, indent=2)
    with open(os.path.join(tags, "load.json"), "w", newline="\n") as f:
        json.dump({"values": [f"{NS}:load"]}, f, indent=2)

    # chunks the display needs, with a margin
    layout_blocks, _, (px1, pz1, px2, pz2) = layout(ox, oy, oz)
    files = {
        "load.mcfunction":
            f"scoreboard objectives add {NS} dummy\n"
            f"execute unless score #built {NS} matches 1 run function {NS}:start\n",
        "start.mcfunction":
            f"forceload add {px1 - 8} {pz1 - 8} {px2 + 8} {oz + 22}\n"
            f"schedule function {NS}:build 100t\n",
        "build.mcfunction": build_function(ox, oy, oz),
    }
    for name, text in files.items():
        with open(os.path.join(fn, name), "w", newline="\n") as f:
            f.write(text)


# ---------------------------------------------------------------- minimal NBT

TAG_END, TAG_BYTE, TAG_SHORT, TAG_INT, TAG_LONG, TAG_FLOAT, TAG_DOUBLE = 0, 1, 2, 3, 4, 5, 6
TAG_BYTE_ARRAY, TAG_STRING, TAG_LIST, TAG_COMPOUND, TAG_INT_ARRAY, TAG_LONG_ARRAY = 7, 8, 9, 10, 11, 12

_FIXED = {TAG_BYTE: ">b", TAG_SHORT: ">h", TAG_INT: ">i", TAG_LONG: ">q", TAG_FLOAT: ">f",
          TAG_DOUBLE: ">d"}


class Reader:
    def __init__(self, data):
        self.data, self.pos = data, 0

    def take(self, n):
        chunk = self.data[self.pos:self.pos + n]
        self.pos += n
        return chunk

    def unpack(self, fmt):
        return struct.unpack(fmt, self.take(struct.calcsize(fmt)))[0]

    def string(self):
        return self.take(self.unpack(">H")).decode("utf-8", "surrogatepass")

    def payload(self, tag):
        if tag in _FIXED:
            return self.unpack(_FIXED[tag])
        if tag == TAG_STRING:
            return self.string()
        if tag == TAG_BYTE_ARRAY:
            return self.take(self.unpack(">i"))
        if tag == TAG_INT_ARRAY:
            return self.take(4 * self.unpack(">i"))
        if tag == TAG_LONG_ARRAY:
            return self.take(8 * self.unpack(">i"))
        if tag == TAG_LIST:
            inner = self.unpack(">b")
            return inner, [self.payload(inner) for _ in range(self.unpack(">i"))]
        if tag == TAG_COMPOUND:
            out = {}
            while True:
                inner = self.unpack(">b")
                if inner == TAG_END:
                    return out
                name = self.string()
                out[name] = (inner, self.payload(inner))
        raise ValueError(f"unknown NBT tag {tag}")


def read_nbt(data):
    r = Reader(data)
    tag = r.unpack(">b")
    name = r.string()
    return name, (tag, r.payload(tag))


def _string(s):
    raw = s.encode("utf-8", "surrogatepass")
    return struct.pack(">H", len(raw)) + raw


def _payload(tag, value):
    if tag in _FIXED:
        return struct.pack(_FIXED[tag], value)
    if tag == TAG_STRING:
        return _string(value)
    if tag == TAG_BYTE_ARRAY:
        return struct.pack(">i", len(value)) + value
    if tag == TAG_INT_ARRAY:
        return struct.pack(">i", len(value) // 4) + value
    if tag == TAG_LONG_ARRAY:
        return struct.pack(">i", len(value) // 8) + value
    if tag == TAG_LIST:
        inner, items = value
        return struct.pack(">bi", inner, len(items)) + b"".join(_payload(inner, v) for v in items)
    if tag == TAG_COMPOUND:
        body = b"".join(struct.pack(">b", t) + _string(n) + _payload(t, v) for n, (t, v) in value.items())
        return body + struct.pack(">b", TAG_END)
    raise ValueError(f"unknown NBT tag {tag}")


def write_nbt(name, tagged):
    tag, value = tagged
    return struct.pack(">b", tag) + _string(name) + _payload(tag, value)


# ---------------------------------------------------------------- the world

def write_level_dat(template, world, spawn, world_name=WORLD_NAME, day_time=6000):
    with gzip.open(os.path.join(template, "level.dat")) as f:
        raw = f.read()
    name, root = read_nbt(raw)
    if write_nbt(name, root) != raw:  # the writer must reproduce the game's own file exactly
        raise RuntimeError("NBT round trip changed the template; refusing to write a world from it")

    data = root[1]["Data"][1]
    gen = data["WorldGenSettings"][1]["dimensions"][1]["minecraft:overworld"][1]["generator"][1]
    if gen["type"][1] != "minecraft:flat":
        raise RuntimeError(f"template world is not superflat ({gen['type'][1]}); pick another with --template")

    data["LevelName"] = (TAG_STRING, world_name)
    data.pop("Player", None)
    data.pop("CustomBossEvents", None)
    data["SpawnX"] = (TAG_INT, spawn[0])
    data["SpawnY"] = (TAG_INT, spawn[1])
    data["SpawnZ"] = (TAG_INT, spawn[2])
    data["SpawnAngle"] = (TAG_FLOAT, 180.0)
    data["Time"] = (TAG_LONG, 0)
    data["DayTime"] = (TAG_LONG, day_time)
    data["LastPlayed"] = (TAG_LONG, int(time.time() * 1000))
    data["raining"] = (TAG_BYTE, 0)
    data["thundering"] = (TAG_BYTE, 0)
    data["GameType"] = (TAG_INT, 1)
    data["allowCommands"] = (TAG_BYTE, 1)

    os.makedirs(world, exist_ok=True)
    with gzip.open(os.path.join(world, "level.dat"), "wb") as f:
        f.write(write_nbt(name, root))


def main():
    here = os.path.dirname(os.path.abspath(__file__))
    repo = os.path.abspath(os.path.join(here, "..", ".."))
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--template", default="New World")
    parser.add_argument("--out", default=os.path.join(repo, "neoforge", "1211", "run", "saves"))
    parser.add_argument("--origin", nargs=3, type=int, default=[0, -60, 0], metavar=("X", "Y", "Z"))
    args = parser.parse_args()

    ox, oy, oz = args.origin
    template = os.path.join(args.out, args.template)
    if not os.path.isfile(os.path.join(template, "level.dat")):
        sys.exit(f"No level.dat in {template}. Create any superflat 1.21.1 world once, or pass --template.")

    world = os.path.join(args.out, WORLD_NAME)
    if os.path.exists(world):
        shutil.rmtree(world)
    write_level_dat(template, world, (ox, oy, oz + 13))
    write_datapack(world, ox, oy, oz)

    blocks, labels, _ = layout(ox, oy, oz)
    print(f"Wrote {world}")
    print(f"  {len(blocks)} blocks and {len(labels)} labels, built when the world is first opened")


if __name__ == "__main__":
    main()
