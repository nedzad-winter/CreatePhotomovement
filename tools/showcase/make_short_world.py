#!/usr/bin/env python3
"""Builds a film set for a YouTube Short about Create Photomovement (Minecraft 1.21.1).

The Short has no voice-over, only music and text added while editing, so every scene here
is clean: just the blocks doing their thing on plain grass. The whole film runs by itself:

    python tools/showcase/make_short_world.py

Open the world, wait until the chat menu appears, then:
    1. once per world: click [Start the windmill] and right-click the bearing,
    2. start recording, click [FILM], click into the game window and press F1,
    3. hands off: every shot plays one after the other (about 2.5 minutes),
    4. when the screen goes black at the end, stop recording.

Then cut it with tools/showcase/cut_short.py: it finds the start of the film in the
recording (the black screen before the first shot) and knows when each shot begins.

The camera is an invisible entity the player watches through (spectator mode), so moving
the mouse does nothing and the camera can glide. Each shot can also be played on its own
from the menu, to redo one.

The shots, in the order of the Short:
    1 field      a field of solar generators wakes up at sunrise, the camera glides in
    2 basic      a Solar Generator appears and starts a cogwheel (16 RPM)
    3 advanced   the advanced one in front, the basic one behind: twice as fast
    4 east/west  time-lapse of a whole day: the stress gauges of an east- and a
                 west-facing horizontal generator follow the sun (long; speed it up)
    5 colours    one generator cycles through all 17 colours
    6 windmill   a big pinwheel of solar sails turns, the camera moves closer
    7 end        the field at sunset, the camera rises, for the closing title

Like make_showcase_world.py this writes only a level.dat and a data pack; the game builds
the set itself the first time the world is opened. See that script for how the world is
put together and for the --template / --out / --origin options.

    --tour   after building, the world plays every shot by itself and prints checks to
             the chat. Used to test the set without anyone at the keyboard.
"""
import argparse
import json
import os
import shutil
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import make_showcase_world as base  # noqa: E402  (NBT writer, level.dat copy, colour list)

MOD = base.MOD
NS = "cpm_short"
WORLD_NAME = "Create Photomovement Short"
SCENE_SPACING = 48
COLORS = base.COLORS
RAINBOW = ["red", "orange", "yellow", "lime", "green", "cyan", "light_blue", "blue", "purple",
           "magenta", "pink"]
MANUAL_DELAY = 3.0     # a shot played on its own waits this long before its action
BLACK = 2.0            # seconds of black screen after the film
BLACK_START = 4.0      # and before it: longer, the game may need a second to draw the box

# times of day (ticks)
DAWN, MORNING, NOON, EVENING, SUNSET = 22800, 500, 6000, 11500, 12300


# ---------------------------------------------------------------- scenes

class Scene:
    """One part of the set, in coordinates relative to its own origin."""

    def __init__(self, index, name):
        self.index, self.name = index, name
        self.blocks = []     # (x, y, z, block id, state string or "")
        self.labels = []     # (x, y, z, title, subtitle)

    def block(self, x, y, z, block, state=""):
        self.blocks.append((x, y, z, block, state))

    def label(self, x, y, z, title, subtitle):
        self.labels.append((x, y, z, title, subtitle))

    def area(self):
        xs = [b[0] for b in self.blocks]
        zs = [b[2] for b in self.blocks]
        return min(xs), min(zs), max(xs), max(zs)


def field():
    """13 x 15 solar generators in a rainbow pattern.

    The generators turn around the z axis, so each north-south line shares one shaft. All
    of them are the same basic generator at the same speed, so the shared shafts never
    disagree. The lines are not connected to each other.
    """
    s = Scene(0, "Field")
    for x in range(13):
        for depth in range(15):
            colour = RAINBOW[(x + depth) // 2 % len(RAINBOW)]
            s.block(x, 0, -depth, f"{MOD}:{colour}_solar_generator", "axis=z")
    return s


def generator_on_cog(s, x, y, z, generator, support):
    """A generator on a pedestal driving a large cogwheel that faces the camera (south).

    The cogwheel is at (x, y, z); the generator two blocks behind it, at the same height,
    and the pedestal fills the space under the generator down to the ground."""
    for h in range(y):
        s.block(x, h, z - 2, support)
    s.block(x, y, z - 2, f"{MOD}:{generator}", "axis=z")
    s.block(x, y, z - 1, "create:shaft", "axis=z")
    s.block(x, y, z, "create:large_cogwheel", "axis=z")


def basic():
    s = Scene(1, "Basic")
    generator_on_cog(s, 0, 1, 0, "solar_generator", "create:andesite_casing")
    return s


def advanced():
    """The advanced generator in front, the basic one diagonally behind it. Seen from above
    at an angle, the one behind sits higher in the frame, so both fit a portrait frame and
    both generators show over their cogwheels."""
    s = Scene(2, "Advanced")
    generator_on_cog(s, 0, 1, 0, "adv_solar_generator", "create:brass_casing")
    generator_on_cog(s, 1, 1, -4, "solar_generator", "create:andesite_casing")
    return s


def east_west():
    """An east- and a west-facing horizontal generator, each with a stress gauge and two
    fans as the load (4 SU per RPM).

    A panel gives between 8 and about 64 SU per RPM depending on the sun, so the needle,
    which shows how much of the capacity is used, sits low while the panel faces the sun
    and climbs to half when it doesn't. Through one day the east one starts low and
    climbs, the west one does the opposite.

    The west unit stands behind the east one and three blocks higher, on a ledge, so both
    gauges are above each other in a portrait frame. Nothing stands within 10 blocks in
    front of either panel (that would count as an obstruction).
    """
    s = Scene(3, "East/West")
    # east-facing, on the ground: the chain runs west from the panel
    s.block(1, 0, 0, f"{MOD}:horizontal_solar_generator", "facing=east")
    s.block(0, 0, 0, "create:stressometer", "facing=south,axis_along_first=true")
    s.block(-1, 0, 0, "create:gearbox", "axis=y")
    s.block(-2, 0, 0, "create:encased_fan", "facing=west")
    s.block(-1, 0, -1, "create:encased_fan", "facing=north")
    s.label(0.5, 1.15, 0.5, "Facing east", "best in the morning")
    # west-facing, on a ledge: the chain runs east from the panel
    y, z = WEST_GEN[1], WEST_GEN[2]
    for x, dz in ((-1, 0), (0, 0), (1, 0), (2, 0), (1, -1)):
        s.block(x, y - 1, z + dz, "minecraft:smooth_stone")
    s.block(-1, y, z, f"{MOD}:horizontal_solar_generator", "facing=west")
    s.block(0, y, z, "create:stressometer", "facing=south,axis_along_first=true")
    s.block(1, y, z, "create:gearbox", "axis=y")
    s.block(2, y, z, "create:encased_fan", "facing=east")
    s.block(1, y, z - 1, "create:encased_fan", "facing=north")
    s.label(0.5, y + 1.15, z + 0.5, "Facing west", "best in the evening")
    return s


EAST_GEN = (1, 0, 0)
WEST_GEN = (-1, 3, -3)


def colours():
    s = Scene(4, "Colours")
    s.block(0, 0, 0, "minecraft:polished_deepslate")
    s.block(0, 1, 0, f"{MOD}:solar_generator", "axis=z")
    return s


WINDMILL_HUB = 8       # height of the bearing
BLADE = 6              # length of each blade


def windmill_sails():
    """A pinwheel of four blades, each 2 sails wide, around one centre sail.

    Returns (dx, dy, colour) relative to the centre. Every sail touches the centre or a
    sail of its blade, so the whole plane is one piece for the bearing to pick up.
    """
    out = [(0, 0, "white")]
    blades = [
        ("yellow", lambda i, w: (w, i)),          # up, on x 0 and 1
        ("orange", lambda i, w: (i, -w)),         # right, on y 0 and -1
        ("light_blue", lambda i, w: (-w, -i)),    # down, on x 0 and -1
        ("lime", lambda i, w: (-i, w)),           # left, on y 0 and 1
    ]
    for colour, place in blades:
        for i in range(1, BLADE + 1):
            for w in (0, 1):
                dx, dy = place(i, w)
                out.append((dx, dy, colour))
    return out


def windmill():
    s = Scene(5, "Windmill")
    for y in range(WINDMILL_HUB + 1):
        s.block(0, y, -1, "minecraft:stripped_dark_oak_log", "axis=y")
    s.block(0, WINDMILL_HUB, 0, f"{MOD}:solar_windmill_bearing", "facing=south")
    for dx, dy, colour in windmill_sails():
        block = "solar_sail" if colour == "white" else f"{colour}_solar_sail"
        s.block(dx, WINDMILL_HUB + dy, 1, f"{MOD}:{block}", "facing=south")
    return s


def all_scenes():
    return [field(), basic(), advanced(), east_west(), colours(), windmill()]


# ---------------------------------------------------------------- shots

class Shot:
    """One shot of the film.

    camera: where the camera's eye is, relative to the scene origin, and where it looks:
            (x, y, z, yaw, pitch); yaw 180 looks north, -90 east; negative pitch looks up.
    action: the function that starts what the shot is about, or None.
    lead:   seconds from the start of the shot to the action, in the film.
    length: seconds the shot lasts in the film.
    glide:  how the camera moves every tick (dx, dy, dz, dyaw, dpitch), or None.
    """

    def __init__(self, number, name, scene, camera, daytime, action, lead, length, glide=None):
        self.number, self.name, self.scene, self.camera = number, name, scene, camera
        self.daytime, self.action, self.lead, self.length, self.glide = daytime, action, lead, length, glide


SHOTS = [
    # looks across the field towards the east, where the sun comes up, and glides along
    Shot(1, "Field at sunrise", 0, (-3, 4.6, 1, -110, 22), DAWN, "act_sunrise", 1.0, 12,
         glide=(0.0141, 0, -0.0051, 0, 0)),
    # looks down from high enough to see the generator over the cogwheel
    Shot(2, "Solar Generator", 1, (1.4, 3.7, 3.2, 165, 30), NOON, "act_place_basic", 1.5, 7),
    Shot(3, "Advanced vs basic", 2, (2.8, 5.95, 3.95, 163, 38), NOON, None, 0, 7),
    Shot(4, "East and west", 3, (0.0, 2.8, 5.5, 180, 8), MORNING, "act_east_west", 1.0, 96),
    Shot(5, "Colours", 4, (0.5, 2.8, 3.2, 180, 18), NOON, "act_colours", 1.0, 12),
    Shot(6, "Windmill", 5, (0.5, 1.6, 21, 180, -18), NOON, None, 0, 10,
         glide=(0, 0, -0.02, 0, -0.02)),
    Shot(7, "End: field at sunset", 0, (6.5, 13.6, 9, 180, 42), SUNSET, None, 0, 8,
         glide=(0, 0.015, 0.03, 0, 0)),
]


def film_timeline():
    """When each shot starts in the film, in seconds after the black screen ends."""
    out, t = [], 0.0
    for shot in SHOTS:
        out.append((shot, t))
        t += shot.length
    return out, t


def origin(scene_index, ox):
    return ox + scene_index * SCENE_SPACING


def ticks(seconds):
    return max(1, round(seconds * 20))


# ---------------------------------------------------------------- commands

def setblock(x, y, z, block, state=""):
    return f"setblock {x} {y} {z} {block}" + (f"[{state}]" if state else "")


def scene_area(sc, ox, oz, margin=8):
    x1, z1, x2, z2 = sc.area()
    sx = origin(sc.index, ox)
    return sx + x1 - margin, oz + z1 - margin, sx + x2 + margin, oz + z2 + margin


def black_box(ox, oy, oz):
    """A closed box of black concrete; the camera sits inside it for the black screen."""
    return ox - 24, oy + 1, oz


def argb(alpha, rgb):
    value = (alpha << 24) | rgb
    return value - (1 << 32) if value >= 1 << 31 else value


def label_command(x, y, z, title, subtitle):
    text = json.dumps([{"text": title + "\n", "color": "white", "bold": True},
                       {"text": subtitle, "color": "gold"}], ensure_ascii=False)
    text = "'" + text.replace("\\", "\\\\").replace("'", "\\'") + "'"
    return (f"summon minecraft:text_display {x} {y} {z} "
            "{Tags:[\"" + NS + "\"],billboard:\"vertical\",alignment:\"center\",shadow:1b,"
            f"background:{argb(0x60, 0x000000)},text:{text},"
            "transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],"
            "translation:[0f,0f,0f],scale:[0.7f,0.7f,0.7f]}}")


def build_function(ox, oy, oz, tour):
    out = ["# Builds the whole set. Safe to run again: it clears each area first.",
           f"kill @e[type=minecraft:text_display,tag={NS}]",
           f"kill @e[type=minecraft:item_display,tag={NS}_cam]"]
    for sc in all_scenes():
        sx = origin(sc.index, ox)
        x1, z1, x2, z2 = scene_area(sc, ox, oz)
        out += [
            f"# --- scene {sc.index}: {sc.name}",
            f"fill {x1} {oy} {z1} {x2} {oy + 20} {z2} minecraft:air",
            f"fill {x1} {oy - 1} {z1} {x2} {oy - 1} {z2} minecraft:grass_block",
        ]
        for x, y, z, block, state in sc.blocks:
            out.append(setblock(sx + x, oy + y, oz + z, block, state))
        for x, y, z, title, sub in sc.labels:
            out.append(label_command(sx + x, oy + y, oz + z, title, sub))
    bx, by, bz = black_box(ox, oy, oz)
    out += [
        "# --- the black box",
        f"fill {bx - 1} {by - 1} {bz - 1} {bx + 1} {by + 1} {bz + 1} minecraft:black_concrete hollow",
        f"time set {NOON}",
        "weather clear",
        "gamerule doDaylightCycle false",
        "gamerule doWeatherCycle false",
        "gamerule doMobSpawning false",
        "gamerule doFireTick false",
        "gamerule sendCommandFeedback false",
        "difficulty peaceful",
        f"scoreboard players set #built {NS} 1",
        f"scoreboard players set #timelapse {NS} 0",
        f"scoreboard players set #colour {NS} -1",
        f"scoreboard players set #glide {NS} 0",
        "forceload remove all",
    ]
    # the whole set stays loaded, so a shot never waits for chunks
    for sc in all_scenes():
        x1, z1, x2, z2 = scene_area(sc, ox, oz, margin=24)
        out.append(f"forceload add {x1} {z1} {x2} {z2}")
    out.append(f"forceload add {bx - 8} {bz - 8} {bx + 8} {bz + 8}")
    out.append(f"function {NS}:tour" if tour else f"function {NS}:menu")
    return "\n".join(out) + "\n"


def camera_commands(x, y, z, yaw, pitch):
    """Moves the camera entity to (x, y, z) and makes every player watch through it.

    The old camera is removed and a new one summoned, so the cut is instant instead of a
    glide. The players are moved there first and start watching two ticks later: a player
    can only watch an entity their game already knows about."""
    return [
        f"execute as @a run spectate",
        f"kill @e[type=minecraft:item_display,tag={NS}_cam]",
        f"tp @a {x} {y - 1.62:.2f} {z} {yaw} {pitch}",
        f"summon minecraft:item_display {x} {y} {z} "
        f"{{Tags:[\"{NS}_cam\"],teleport_duration:1,Rotation:[{yaw}f,{pitch}f]}}",
        f"schedule function {NS}:watch_camera 2t",
    ]


def shot_setup(shot, ox, oy, oz):
    """Puts the scene and the camera in place for a shot, without starting its action."""
    cx, cy, cz, yaw, pitch = shot.camera
    out = [
        f"# Shot {shot.number}: {shot.name}",
        f"function {NS}:stop_actions",
        "gamemode spectator @a",
        "weather clear",
        f"time set {shot.daytime}",
        f"function {NS}:reset_{shot.scene}",
    ]
    out += camera_commands(origin(shot.scene, ox) + cx, oy + cy, oz + cz, yaw, pitch)
    out.append(f"scoreboard players set #glide {NS} {shot.number if shot.glide else 0}")
    if shot.glide:
        out.append(f"schedule function {NS}:glide 3t")
    return "\n".join(out) + "\n"


def shot_function(shot):
    """A shot played on its own from the menu."""
    out = [f"function {NS}:stop", f"function {NS}:setup_{shot.number}",
           f'title @a actionbar {{"text":"Shot {shot.number}: {shot.name}  (F1 hides this)","color":"gold"}}']
    if shot.action:
        out.append(f"schedule function {NS}:{shot.action} {ticks(MANUAL_DELAY)}t")
    return "\n".join(out) + "\n"


def glide_function(ox):
    out = [f"execute if score #glide {NS} matches ..0 run return 0"]
    for shot in SHOTS:
        if shot.glide:
            dx, dy, dz, dyaw, dpitch = shot.glide
            out.append(f"execute if score #glide {NS} matches {shot.number} "
                       f"as @e[type=minecraft:item_display,tag={NS}_cam] at @s "
                       f"run tp @s ~{dx} ~{dy} ~{dz} ~{dyaw} ~{dpitch}")
    out.append(f"schedule function {NS}:glide 1t")
    return "\n".join(out) + "\n"


def reset_functions(ox, oy, oz):
    """Puts each scene back the way a shot expects it."""
    files = {}
    for sc in all_scenes():
        sx = origin(sc.index, ox)
        lines = [f"# Resets scene {sc.index}: {sc.name}"]
        if sc.name == "Basic":
            lines.append(setblock(sx, oy + 1, oz - 2, "minecraft:air"))
        if sc.name == "East/West":
            # placed again, so they work out their output for the new time of day at once
            for (x, y, z), facing in ((EAST_GEN, "east"), (WEST_GEN, "west")):
                lines.append(setblock(sx + x, oy + y, oz + z, "minecraft:air"))
                lines.append(setblock(sx + x, oy + y, oz + z, f"{MOD}:horizontal_solar_generator",
                                      f"facing={facing}"))
        if sc.name == "Colours":
            lines.append(setblock(sx, oy + 1, oz, f"{MOD}:solar_generator", "axis=z"))
        if sc.name == "Windmill":
            px, py, pz = windmill_pad(ox, oy, oz)
            lines.append(setblock(px, py, pz, "minecraft:air"))
        files[f"reset_{sc.index}.mcfunction"] = "\n".join(lines) + "\n"
    return files


def action_functions(ox, oy, oz):
    basic_x = origin(1, ox)
    colour_x = origin(4, ox)
    colours = ["solar_generator"] + [f"{c}_solar_generator" for c in COLORS]
    files = {
        "act_place_basic.mcfunction":
            setblock(basic_x, oy + 1, oz - 2, f"{MOD}:solar_generator", "axis=z") + "\n"
            f"playsound minecraft:block.glass.place block @a {basic_x} {oy + 1} {oz - 2} 1 1\n",
        # sunrise: from dawn into the morning in about 9 seconds
        "act_sunrise.mcfunction": f"function {NS}:timelapse {{speed:15,until:1500}}\n",
        # a whole day in about 90 seconds; the generators work their output out every
        # 10 seconds, so a slow time-lapse gives the needles more steps
        "act_east_west.mcfunction": f"function {NS}:timelapse {{speed:6,until:{EVENING}}}\n",
        "act_colours.mcfunction":
            f"scoreboard players set #colour {NS} 0\n"
            f"function {NS}:colour_step\n",
        # time-lapse: adds $(speed) ticks of daytime every tick until the daytime reaches $(until)
        "timelapse.mcfunction":
            f"$scoreboard players set #speed {NS} $(speed)\n"
            f"$scoreboard players set #until {NS} $(until)\n"
            f"scoreboard players set #timelapse {NS} 1\n"
            f"function {NS}:timelapse_tick\n",
        "timelapse_tick.mcfunction":
            f"execute unless score #timelapse {NS} matches 1 run return 0\n"
            f"execute store result storage {NS}:tl add int 1 run scoreboard players get #speed {NS}\n"
            f"function {NS}:time_add with storage {NS}:tl\n"
            f"execute store result score #now {NS} run time query daytime\n"
            f"execute if score #now {NS} >= #until {NS} if score #now {NS} < #wrap {NS} "
            f"run scoreboard players set #timelapse {NS} 0\n"
            f"execute if score #timelapse {NS} matches 1 run schedule function {NS}:timelapse_tick 1t\n",
        "time_add.mcfunction": "$time add $(add)\n",
        "watch_camera.mcfunction":
            f"execute as @a[gamemode=spectator] run spectate @e[type=minecraft:item_display,tag={NS}_cam,limit=1] @s\n",
        # stops what a shot started (time-lapse, colours, the camera glide)
        "stop_actions.mcfunction":
            f"scoreboard players set #timelapse {NS} 0\n"
            f"scoreboard players set #colour {NS} -1\n"
            f"scoreboard players set #glide {NS} 0\n"
            f"schedule clear {NS}:timelapse_tick\n"
            f"schedule clear {NS}:colour_step\n"
            f"schedule clear {NS}:glide\n"
            + "".join(f"schedule clear {NS}:{a}\n" for a in sorted({s.action for s in SHOTS if s.action})),
        # stops everything, the film included, and gives the player back their own view
        "stop.mcfunction":
            f"function {NS}:stop_actions\n"
            + "".join(f"schedule clear {NS}:film_{n}\n" for n in range(len(SHOTS) * 2 + 2))
            + "execute as @a run spectate\n",
    }
    # colours: one step every 12 ticks through all 17, then back to the plain one
    step = [f"execute if score #colour {NS} matches ..-1 run return 0"]
    for n, block in enumerate(colours):
        step.append(f"execute if score #colour {NS} matches {n} run "
                    + setblock(colour_x, oy + 1, oz, f"{MOD}:{block}", "axis=z"))
    step += [
        f"execute if score #colour {NS} matches {len(colours)}.. run "
        + setblock(colour_x, oy + 1, oz, f"{MOD}:solar_generator", "axis=z"),
        f"execute if score #colour {NS} matches {len(colours)}.. run scoreboard players set #colour {NS} -1",
        f"execute if score #colour {NS} matches 0.. run scoreboard players add #colour {NS} 1",
        f"execute if score #colour {NS} matches 1.. run schedule function {NS}:colour_step 12t",
    ]
    files["colour_step.mcfunction"] = "\n".join(step) + "\n"
    return files


def windmill_pad(ox, oy, oz):
    """A glass block beside the bearing, behind the plane of sails, to stand on."""
    return origin(5, ox) - 3, oy + WINDMILL_HUB - 1, oz


def windmill_functions(ox, oy, oz):
    """The windmill bearing only starts when a player right-clicks it, so the set offers a
    small glass platform beside it to do that once. Once started it keeps turning, also
    after the world is closed and opened again."""
    px, py, pz = windmill_pad(ox, oy, oz)
    return {
        "windmill_start.mcfunction":
            f"function {NS}:stop\n"
            "gamemode creative @a\n"
            f"time set {NOON}\n"
            + setblock(px, py, pz, "minecraft:glass") + "\n"
            # aims at the lower corner of the bearing's side: the middle of the side is the
            # "Rotation Direction" setting, where a right-click changes that instead
            f"tp @a {px + 0.5} {py + 1} {pz + 0.5} -98 29\n"
            'tellraw @a {"text":"Right-click the bearing in front of you with an empty hand. '
            'Then click [FILM] or [6] Windmill in the menu.","color":"aqua"}\n',
    }


def bearing_pos(ox, oy, oz):
    return origin(5, ox), oy + WINDMILL_HUB, oz


def film_functions(ox, oy, oz):
    """The whole film: black screen, every shot in turn, black screen."""
    bx, by, bz = black_box(ox, oy, oz)
    wx, wy, wz = bearing_pos(ox, oy, oz)
    timeline, total = film_timeline()
    start = ticks(BLACK_START)
    steps = []                 # (tick, commands) relative to the film function
    for shot, t in timeline:
        steps.append((start + ticks(t), [f"function {NS}:setup_{shot.number}"]))
        if shot.action:
            steps.append((start + ticks(t + shot.lead), [f"function {NS}:{shot.action}"]))
    steps.append((start + ticks(total), [f"function {NS}:stop_actions"]
                  + camera_commands(bx + 0.5, by + 0.5, bz + 0.5, 0, 0)))
    steps.append((start + ticks(total + BLACK), [f"function {NS}:film_done"]))

    files = {}
    film = [
        "# Plays the whole film. Start recording first.",
        f"execute unless data block {wx} {wy} {wz} {{Running:1b}} run tellraw @a "
        '{"text":"The windmill is not turning yet: click [Start the windmill] first.","color":"red"}',
        f"execute unless data block {wx} {wy} {wz} {{Running:1b}} run return 0",
        f"function {NS}:stop",
        "gamemode spectator @a",
        f"time set {NOON}",
    ] + camera_commands(bx + 0.5, by + 0.5, bz + 0.5, 0, 0)
    for n, (tick, commands) in enumerate(steps):
        files[f"film_{n}.mcfunction"] = "\n".join(commands) + "\n"
        film.append(f"schedule function {NS}:film_{n} {tick}t append")
    files["film.mcfunction"] = "\n".join(film) + "\n"
    files["film_done.mcfunction"] = (
        "execute as @a run spectate\n"
        f'tellraw @a {{"text":"Film done ({round(total + BLACK_START + BLACK)} s). Stop the recording.","color":"gold"}}\n'
        f"function {NS}:menu\n")
    return files


def menu_function():
    def click(text, color, command):
        return {"text": text, "color": color, "clickEvent": {"action": "run_command", "value": command}}

    _, total = film_timeline()
    parts = [{"text": "\n=== Create Photomovement: Short film set ===\n", "color": "gold", "bold": True},
             {"text": "1. Once per world: ", "color": "gray"},
             click("[Start the windmill]\n", "green", f"/function {NS}:windmill_start"),
             {"text": "2. Start recording, then ", "color": "gray"},
             click(f"[FILM]", "green", f"/function {NS}:film"),
             {"text": f" plays every shot ({round(total + BLACK_START + BLACK)} s). Click into the game, "
                      "press F1, hands off.\n", "color": "gray"},
             {"text": "Single shots: ", "color": "gray"}]
    for shot in SHOTS:
        parts.append(click(f"[{shot.number}] ", "aqua", f"/function {NS}:shot_{shot.number}"))
    parts.append({"text": "\nTools: ", "color": "gray"})
    parts.append(click("[Stop] ", "yellow", f"/function {NS}:stop"))
    parts.append(click("[Creative] ", "yellow", f"/function {NS}:creative"))
    parts.append(click("[Dyes]", "yellow", f"/function {NS}:give_dyes"))
    parts.append({"text": "\nMenu again: /function cpm_short:menu\n", "color": "gray"})
    return "tellraw @a " + json.dumps(parts, ensure_ascii=False) + "\n"


def tour_functions(ox, oy, oz):
    """Plays the film with checks; prints the results to the chat. The windmill is not
    started (that needs a player), so the tour plays the shots one by one instead."""
    files = {}
    first = ["# Self-test: plays every shot, then shows the menu.",
             'tellraw @a {"text":"TOUR start","color":"gold"}',
             f"function {NS}:stop", "gamemode spectator @a"]
    first += camera_commands(*[c + 0.5 for c in black_box(ox, oy, oz)], 0, 0)
    # every step gets its own function, like the film's: a shot's setup clears the
    # actions scheduled by name, which would also clear the next shot's action
    t = ticks(BLACK)
    check_at = {5: 5.0}                        # the colour cycle is checked while it runs
    steps = []
    for shot in SHOTS:
        length = min(shot.length, 14)          # the long time-lapse is checked part way
        steps.append((t, f"function {NS}:setup_{shot.number}"))
        if shot.action:
            steps.append((t + ticks(shot.lead), f"function {NS}:{shot.action}"))
        steps.append((t + ticks(check_at.get(shot.number, length - 0.5)), f"function {NS}:check_{shot.number}"))
        t += ticks(length)
    steps.append((t, f"function {NS}:tour_done"))
    for n, (tick, command) in enumerate(steps):
        files[f"tour_{n}.mcfunction"] = command + "\n"
        first.append(f"schedule function {NS}:tour_{n} {tick}t append")
    files["tour.mcfunction"] = "\n".join(first) + "\n"
    files["tour_done.mcfunction"] = (f"function {NS}:stop\n"
                                     'tellraw @a {"text":"TOUR done","color":"gold"}\n'
                                     f"function {NS}:menu\n")

    def check(label, condition):
        ok = json.dumps({"text": f"CHECK ok   {label}", "color": "green"})
        bad = json.dumps({"text": f"CHECK FAIL {label}", "color": "red"})
        return (f"execute {condition} run tellraw @a {ok}\n"
                f"execute {condition.replace('if ', 'unless ', 1)} run tellraw @a {bad}\n")

    def block_at(scene, x, y, z, block):
        return f"if block {origin(scene, ox) + x} {oy + y} {oz + z} {block}"

    watching = check("camera: player watches the camera entity",
                     f"if entity @a[gamemode=spectator] if entity @e[type=minecraft:item_display,tag={NS}_cam]")
    files["check_1.mcfunction"] = (
        check("field: generators in place", block_at(0, 0, 0, 0, f"{MOD}:red_solar_generator"))
        + check("field: time-lapse reached the morning", f"if score #now {NS} matches 1500..22000")
        + watching)
    files["check_2.mcfunction"] = check("basic: generator placed", block_at(1, 0, 1, -2, f"{MOD}:solar_generator"))
    files["check_3.mcfunction"] = (
        check("advanced: brass generator in front", block_at(2, 0, 1, -2, f"{MOD}:adv_solar_generator"))
        + check("advanced: basic generator behind", block_at(2, 1, 1, -6, f"{MOD}:solar_generator")))
    ex, ey, ez = EAST_GEN
    wx, wy, wz = WEST_GEN
    files["check_4.mcfunction"] = (
        check("east/west: east panel in place",
              block_at(3, ex, ey, ez, f"{MOD}:horizontal_solar_generator[facing=east]"))
        + check("east/west: west panel in place",
                block_at(3, wx, wy, wz, f"{MOD}:horizontal_solar_generator[facing=west]"))
        + check("east/west: time-lapse running", f"if score #timelapse {NS} matches 1"))
    files["check_5.mcfunction"] = check("colours: cycling", f"if score #colour {NS} matches 1..")
    files["check_6.mcfunction"] = (
        check("windmill: bearing in place", block_at(5, 0, WINDMILL_HUB, 0, f"{MOD}:solar_windmill_bearing"))
        + check("windmill: camera glides", f"if score #glide {NS} matches 6"))
    files["check_7.mcfunction"] = check("end: camera glides", f"if score #glide {NS} matches 7") + watching
    return files


def write_datapack(world, ox, oy, oz, tour):
    pack = os.path.join(world, "datapacks", NS)
    if os.path.isdir(pack):
        shutil.rmtree(pack)
    fn = os.path.join(pack, "data", NS, "function")
    tags = os.path.join(pack, "data", "minecraft", "tags", "function")
    os.makedirs(fn)
    os.makedirs(tags)
    with open(os.path.join(pack, "pack.mcmeta"), "w", newline="\n") as f:
        json.dump({"pack": {"pack_format": 48, "description": "Builds the Create Photomovement Short film set"}},
                  f, indent=2)
    with open(os.path.join(tags, "load.json"), "w", newline="\n") as f:
        json.dump({"values": [f"{NS}:load"]}, f, indent=2)

    first = all_scenes()[0]
    x1, z1, x2, z2 = scene_area(first, ox, oz)
    dyes = "".join(f"give @s minecraft:{c}_dye 16\n" for c in COLORS)
    files = {
        "load.mcfunction":
            f"scoreboard objectives add {NS} dummy\n"
            # daytime runs from 0 to 23999; the time-lapse stops once it passes its target,
            # but not while it is still before midnight on the way to it (see timelapse_tick)
            f"scoreboard players set #wrap {NS} 22000\n"
            f"execute unless score #built {NS} matches 1 run function {NS}:start\n",
        "start.mcfunction":
            f"forceload add {x1} {z1} {x2} {z2}\n"
            f"forceload add {ox - 40} {oz - 30} {origin(len(all_scenes()) - 1, ox) + 30} {oz + 30}\n"
            f"schedule function {NS}:build 100t\n",
        "build.mcfunction": build_function(ox, oy, oz, tour),
        "menu.mcfunction": menu_function(),
        "glide.mcfunction": glide_function(ox),
        "give_dyes.mcfunction": f"function {NS}:creative\n" + dyes,
        "creative.mcfunction": f"function {NS}:stop\ngamemode creative @s\n",
    }
    for shot in SHOTS:
        files[f"setup_{shot.number}.mcfunction"] = shot_setup(shot, ox, oy, oz)
        files[f"shot_{shot.number}.mcfunction"] = shot_function(shot)
    files.update(reset_functions(ox, oy, oz))
    files.update(action_functions(ox, oy, oz))
    files.update(windmill_functions(ox, oy, oz))
    files.update(film_functions(ox, oy, oz))
    if tour:
        files.update(tour_functions(ox, oy, oz))
    for name, text in files.items():
        with open(os.path.join(fn, name), "w", newline="\n", encoding="utf-8") as f:
            f.write(text)
    return len(files)


def main():
    here = os.path.dirname(os.path.abspath(__file__))
    repo = os.path.abspath(os.path.join(here, "..", ".."))
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--template", default="New World")
    parser.add_argument("--out", default=os.path.join(repo, "neoforge", "1211", "run", "saves"))
    parser.add_argument("--origin", nargs=3, type=int, default=[0, -60, 0], metavar=("X", "Y", "Z"))
    parser.add_argument("--tour", action="store_true", help="play every shot after building and print checks")
    args = parser.parse_args()

    ox, oy, oz = args.origin
    template = os.path.join(args.out, args.template)
    if not os.path.isfile(os.path.join(template, "level.dat")):
        sys.exit(f"No level.dat in {template}. Create any superflat 1.21.1 world once, or pass --template.")

    world = os.path.join(args.out, WORLD_NAME)
    if os.path.exists(world):
        shutil.rmtree(world)
    spawn = (ox + 6, oy, oz + 10)
    base.write_level_dat(template, world, spawn, world_name=WORLD_NAME, day_time=NOON)
    count = write_datapack(world, ox, oy, oz, args.tour)

    scenes = all_scenes()
    _, total = film_timeline()
    print(f"Wrote {world}")
    print(f"  {len(scenes)} scenes, {sum(len(s.blocks) for s in scenes)} blocks, {len(SHOTS)} shots, "
          f"film {total + BLACK_START + BLACK:.0f} s, {count} functions" + (", with self-test tour" if args.tour else ""))


if __name__ == "__main__":
    main()
