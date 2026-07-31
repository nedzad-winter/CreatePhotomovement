#!/usr/bin/env python3
"""Render the JUnit XML from the headless tests into a GitHub job summary.

Written inline rather than pulling in a marketplace action: the format is simple,
and a workflow that can read the repository is worth keeping free of third-party
code it does not need.
"""

import os
import sys
import glob
import xml.etree.ElementTree as ET

RESULTS_GLOB = "common/build/test-results/test/TEST-*.xml"


def main() -> int:
    files = sorted(glob.glob(RESULTS_GLOB))
    summary_path = os.environ.get("GITHUB_STEP_SUMMARY")

    lines = ["## Headless test results", ""]

    if not files:
        lines += [
            "No test results found.",
            "",
            f"Looked in `{RESULTS_GLOB}`. If the build failed before the tests ran, "
            "that is the failure to look at.",
        ]
        write(summary_path, lines)
        # Not a failure in itself; the build step already decides the job outcome.
        return 0

    total = failures = errors = skipped = 0
    time = 0.0
    rows = []

    for path in files:
        try:
            suite = ET.parse(path).getroot()
        except ET.ParseError as exc:
            rows.append(("(unparseable)", os.path.basename(path), "-", "-", str(exc)))
            continue

        name = suite.get("name", "?").split(".")[-1]
        t = int(suite.get("tests", 0))
        f = int(suite.get("failures", 0))
        e = int(suite.get("errors", 0))
        s = int(suite.get("skipped", 0))

        total += t
        failures += f
        errors += e
        skipped += s
        time += float(suite.get("time", 0.0))

        mark = "pass" if (f + e) == 0 else "FAIL"
        rows.append((mark, name, str(t), str(f + e), str(s)))

    bad = failures + errors
    headline = (
        f"**{total} tests, all passing** in {time:.1f}s"
        if bad == 0
        else f"**{bad} of {total} tests failing**"
    )
    lines += [headline, ""]

    lines += ["| | Suite | Tests | Failed | Skipped |", "|---|---|---:|---:|---:|"]
    for mark, name, t, f, s in rows:
        lines.append(f"| {mark} | {name} | {t} | {f} | {s} |")

    if bad:
        lines += ["", "### Failures", ""]
        for path in files:
            try:
                suite = ET.parse(path).getroot()
            except ET.ParseError:
                continue
            for case in suite.iter("testcase"):
                for problem in list(case.findall("failure")) + list(case.findall("error")):
                    where = f"{case.get('classname', '?').split('.')[-1]} > {case.get('name', '?')}"
                    detail = (problem.text or problem.get("message") or "").strip()
                    # The stack trace is in the uploaded artifact; keep the summary readable.
                    detail = "\n".join(detail.splitlines()[:12])
                    lines += [f"**{where}**", "", "```", detail, "```", ""]

    write(summary_path, lines)
    return 0


def write(summary_path, lines: list) -> None:
    text = "\n".join(lines) + "\n"
    if summary_path:
        with open(summary_path, "a", encoding="utf-8") as handle:
            handle.write(text)
    else:
        print(text)


if __name__ == "__main__":
    sys.exit(main())
