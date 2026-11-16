#!/usr/bin/env python3
"""Cuts the YouTube Short from one recording of the film set's [FILM] run.

    python tools/showcase/cut_short.py [recording] [--out FILE]

Without a recording it takes the newest .mkv or .mp4 in the Videos folder. The recording
must be portrait (9:16, e.g. 1080x1920 from OBS) and contain the whole film, from the
black screen at the start.

The film always plays the same way (see SHOTS in make_short_world.py), so once the end of
the first black screen is found, every shot's start is known. CUTS below says which part
of each shot goes into the Short, how much faster it plays and what text it shows. The
result is 1080x1920, 60 fps, without sound: add music while uploading, from YouTube's own
library, so there is no copyright trouble.

Needs ffmpeg (winget install Gyan.FFmpeg).
"""
import argparse
import glob
import os
import re
import shutil
import subprocess
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import make_short_world as film  # noqa: E402  (the shots and their timing)

FONT = "C\\:/Windows/Fonts/ariblk.ttf"

# shot, from, to (seconds into the shot), speed, text lines (title first)
CUTS = [
    (1, 0.3, 11.0, 2.0, ["Solar power for Create", "Create Photomovement"]),
    (2, 1.0, 6.8, 1.0, ["Solar Generator", "16 RPM – just sunlight"]),
    (3, 0.3, 6.0, 1.0, ["Advanced Generator", "brass: 32 RPM · basic: 16 RPM"]),
    (4, 1.0, 93.0, 8.0, ["Follows the sun", "one day in 12 seconds"]),
    (5, 0.8, 11.6, 2.0, ["17 colours", "dye it any way you like"]),
    (6, 0.3, 9.8, 1.5, ["Solar Windmill", "2× power in sunshine"]),
    (7, 0.3, 7.8, 1.25, ["Create Photomovement", "NeoForge · Fabric · 1.20.1 & 1.21.1",
                         "on CurseForge & Modrinth"]),
]


def find_tool(name):
    found = shutil.which(name)
    if found:
        return found
    pattern = os.path.join(os.environ.get("LOCALAPPDATA", ""), "Microsoft", "WinGet", "Packages",
                           "Gyan.FFmpeg*", "*", "bin", name + ".exe")
    hits = glob.glob(pattern)
    if not hits:
        sys.exit(f"{name} not found. Install it with: winget install Gyan.FFmpeg")
    return hits[0]


def newest_recording():
    videos = os.path.join(os.path.expanduser("~"), "Videos")
    files = [f for f in glob.glob(os.path.join(videos, "*")) if f.lower().endswith((".mkv", ".mp4"))
             and "Short" not in os.path.basename(f)]
    if not files:
        sys.exit(f"No recording in {videos}; pass one.")
    return max(files, key=os.path.getmtime)


def film_start(ffmpeg, recording, film_length):
    """When the first shot starts in the recording, found by the black screens.

    The film is black before the first shot and black again after the last one, exactly
    film_length seconds later. The black screens can come out shorter than planned (the
    game may need a moment to draw the black box after the jump there), so short ones
    count too, and the pair that is film_length apart is the one that is meant.
    """
    result = subprocess.run([ffmpeg, "-hide_banner", "-i", recording, "-an", "-vf",
                             "scale=270:480,blackdetect=d=0.3:pix_th=0.12", "-f", "null", "-"],
                            capture_output=True, text=True, encoding="utf-8", errors="replace")
    blacks = [(float(m.group(1)), float(m.group(2)))
              for m in re.finditer(r"black_start:([\d.]+) black_end:([\d.]+)", result.stderr)]
    print("black screens at: " + ", ".join(f"{a:.2f}-{b:.2f} s" for a, b in blacks))
    for _, end in blacks:                     # a black at the start, another one at the end
        if any(abs(start - (end + film_length)) < 2.0 for start, _ in blacks):
            return end
    if blacks and blacks[-1][0] >= film_length:   # only the black at the end was found
        return blacks[-1][0] - film_length
    sys.exit("No matching black screens found: is this a recording of the whole [FILM] run? "
             "Pass --start with the second where the first shot begins.")


def text_file(folder, name, text):
    path = os.path.join(folder, name)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    return path.replace("\\", "/").replace(":", "\\:")


def drawtext(path, size, y, color, delay):
    alpha = f"if(lt(t\\,{delay})\\,0\\,min(1\\,(t-{delay})/0.3))"
    return (f"drawtext=fontfile='{FONT}':textfile='{path}':fontsize={size}:fontcolor={color}"
            f":borderw={max(4, size // 14)}:bordercolor=black:x=(w-text_w)/2:y={y}:alpha='{alpha}'")


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("recording", nargs="?")
    parser.add_argument("--out", default=os.path.join(os.path.expanduser("~"), "Videos",
                                                      "Create Photomovement Short.mp4"))
    parser.add_argument("--start", type=float, help="end of the black screen, if it isn't found")
    args = parser.parse_args()

    ffmpeg = find_tool("ffmpeg")
    recording = args.recording or newest_recording()
    timeline, film_length = film.film_timeline()
    t0 = args.start if args.start is not None else film_start(ffmpeg, recording, film_length)
    starts = {shot.number: t for shot, t in timeline}
    print(f"recording: {recording}\nfilm starts at {t0:.2f} s")

    work = os.path.join(os.path.dirname(os.path.abspath(args.out)), ".cut_short_text")
    os.makedirs(work, exist_ok=True)
    parts, labels, length = [], [], 0.0
    for n, (shot, a, b, speed, lines) in enumerate(CUTS):
        start, end = t0 + starts[shot] + a, t0 + starts[shot] + b
        chain = [f"trim=start={start:.3f}:end={end:.3f}", f"setpts=(PTS-STARTPTS)/{speed}",
                 "scale=1080:1920:flags=lanczos", "fps=60", "setsar=1"]
        y = 300
        for i, line in enumerate(lines):
            if i == 0:
                size, color = (96 if len(line) <= 16 else 76), "white"
            else:
                size, color = (58 if len(line) <= 26 else 44), "0xFFD54A"
            chain.append(drawtext(text_file(work, f"{n}_{i}.txt", line), size, y, color, 0.15 + 0.3 * i))
            y += size + 30
        seconds = (b - a) / speed
        if n == len(CUTS) - 1:
            chain.append(f"fade=t=out:st={seconds - 0.6:.2f}:d=0.6")
        parts.append(f"[0:v]{','.join(chain)}[v{n}]")
        labels.append(f"[v{n}]")
        length += seconds
    graph = ";".join(parts) + ";" + "".join(labels) + f"concat=n={len(CUTS)}:v=1:a=0,format=yuv420p[out]"
    subprocess.run([ffmpeg, "-hide_banner", "-v", "error", "-y", "-i", recording, "-filter_complex", graph,
                    "-map", "[out]", "-c:v", "libx264", "-preset", "slow", "-crf", "18",
                    "-movflags", "+faststart", args.out], check=True)
    shutil.rmtree(work, ignore_errors=True)
    print(f"wrote {args.out} ({length:.1f} s)")


if __name__ == "__main__":
    main()
