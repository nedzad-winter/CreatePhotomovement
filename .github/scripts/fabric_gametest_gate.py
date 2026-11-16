#!/usr/bin/env python3
"""Decides whether the Fabric game test run counts as passed.

On Fabric the game test server runs every mod's tests, not only ours: Create ships about
60 of its own, and a few of them fail in a bare test server. The Gradle task then exits
with an error even though every test of this mod passed. NeoForge does not have this
problem, because there the run is limited to our namespace.

This script reads the JUnit report the Fabric run writes and looks only at the tests that
are not Create's (class name not starting with "create:"):

    python3 .github/scripts/fabric_gametest_gate.py fabric/1201/build/gametest-report.xml

It fails if the report is missing (the server never got as far as running tests), if none
of our tests ran, or if one of ours failed. Create's failures are listed, not counted.
"""
import sys
import xml.etree.ElementTree as ET

FOREIGN_PREFIX = "create:"


def main(path):
    try:
        root = ET.parse(path).getroot()
    except (OSError, ET.ParseError) as error:
        print(f"::error::No usable game test report at {path}: {error}")
        return 1

    ours, ours_failed, foreign_failed = 0, [], []
    for case in root.iter("testcase"):
        name = case.get("name", "?")
        failed = case.find("failure") is not None or case.find("error") is not None
        if case.get("classname", "").startswith(FOREIGN_PREFIX):
            if failed:
                foreign_failed.append(name)
        else:
            ours += 1
            if failed:
                ours_failed.append(name)

    for name in foreign_failed:
        print(f"::notice::Create's own test failed (not counted): {name}")
    print(f"Our tests: {ours} ran, {len(ours_failed)} failed. "
          f"Create's tests that failed: {len(foreign_failed)}.")

    if ours == 0:
        print("::error::None of this mod's game tests ran.")
        return 1
    for name in ours_failed:
        print(f"::error::Game test failed: {name}")
    return 1 if ours_failed else 0


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print(__doc__)
        sys.exit(2)
    sys.exit(main(sys.argv[1]))
