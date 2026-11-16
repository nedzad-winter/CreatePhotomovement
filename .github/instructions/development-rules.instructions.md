---
applyTo: '**'
---
Keep "docs/Current-Dev-Status.md" updated with the current development state (append to the already present stuff).

Keep the knowlagebase inside docs/Current-Dev-Status.md with as much info as possible. It should act as your brain and memory

When fixing a bug document the bug in "docs/bugs.md" (append to it). This is the only bug list; do not create .bugs.md files next to the code.

How the project is structured and why is described in "docs/architecture/arc42.md". When a change alters the structure (a new target, code moved to or from common/, a new folder), update that file, and add a short decision record under "docs/architecture/adr/" if a real choice was made.

Released changes go into "CHANGELOG.md" in the repository root. This is the only changelog.

When answering with code changes beeing made, explain me what has changed so that i as a non programmer can learn and understand it.

Dont use emojies

