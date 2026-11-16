# tools

Helper scripts for building, releasing and showing off the mod. None of them are part of
the mod itself.

## Building and releasing

| Script | What it does | Run with |
| --- | --- | --- |
| `build_mod.ps1` | Runs the headless tests, builds all three targets, copies the jars to `tools/builds/` and into the dev game profiles if they exist. Reads the version from the root `gradle.properties`. **This is the maintained build script.** | `.\build_mod.ps1` |
| `build_mod.sh` | The same for Linux/macOS, but simpler: no tests, and the version must be passed in. Not used in everyday work, so check it against `build_mod.ps1` before relying on it. | `./build_mod.sh 0.4.0` |
| `bump_version.ps1` | Changes `mod_version` in the root `gradle.properties` (the only place it is defined). Does not touch `CHANGELOG.md`. | `.\bump_version.ps1` |

Two more files live here on the author's machine only and are ignored by git:
`publish_mod.ps1` (uploads a release; contains API tokens) and `upload_history.json`.
`tools/builds/` is ignored as well.

## Game tests

| Script | What it does |
| --- | --- |
| `make_gametest_structure.ps1` | Writes the empty test arena (`solar_platform.nbt`) into the targets' resources. The game tests are built inside this arena. Only needed again if the arena's size changes. See [docs/gametests.md](../docs/gametests.md). |

## Showcase worlds (`showcase/`)

Python scripts that generate Minecraft 1.21.1 worlds for screenshots and videos. Each
writes a world into `neoforge/1211/run/saves/`; open it with the dev client.

| Script | World |
| --- | --- |
| `make_showcase_world.py` | Every block of the mod in one compact display, for screenshots. The other two scripts build on this one. |
| `make_guide_world.py` | One station per block with a board and live demos. |
| `make_short_world.py` | A film set that plays a YouTube Short by itself. |
| `cut_short.py` | Cuts a recording of that film into the finished Short. Needs ffmpeg. |

## Elsewhere

`neoforge/1211/update_models.ps1` is a one-off script that rewrote the solar sail models
(they now live in `common/`). It has to be run from inside `neoforge/1211`.
