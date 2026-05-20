#!/usr/bin/env python3
"""Validate relative links and heading anchors in Markdown files."""

from __future__ import annotations

import argparse
import os
import re
import sys
import urllib.parse
from collections import Counter
from typing import Iterable

# [label](target)
LINK_RE = re.compile(r"\[[^\]]+\]\(([^)]+)\)")
# ATX headings: ## My Heading
HEADING_RE = re.compile(r"^\s{0,3}#{1,6}\s+(.+?)\s*$")
SCHEME_RE = re.compile(r"^[a-zA-Z][a-zA-Z0-9+.-]*:")

DEFAULT_EXCLUDE_DIRS = {
    ".git",
    ".idea",
    ".mvn",
    "target",
}


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Check markdown links (local files + anchors)."
    )
    parser.add_argument(
        "--root",
        default=os.getcwd(),
        help="Root directory to scan. Defaults to current directory.",
    )
    parser.add_argument(
        "--include",
        default=".md",
        help="Comma-separated list of markdown file suffixes (default: .md).",
    )
    parser.add_argument(
        "--exclude-dir",
        action="append",
        default=[],
        help="Directory name to exclude. Can be provided multiple times.",
    )
    return parser.parse_args()


def iter_markdown_files(root: str, suffixes: tuple[str, ...], excluded: set[str]) -> Iterable[str]:
    for current_dir, dirs, files in os.walk(root):
        dirs[:] = [name for name in dirs if name not in excluded]
        for name in files:
            if name.lower().endswith(suffixes):
                yield os.path.join(current_dir, name)


def github_slug(text: str) -> str:
    # Approximate GitHub markdown anchor generation for ATX headings.
    text = text.strip()
    text = re.sub(r"\s+#+\s*$", "", text)
    text = text.lower()
    text = re.sub(r"[^\w\- ]", "", text, flags=re.UNICODE)
    text = re.sub(r"\s+", "-", text)
    text = re.sub(r"-{2,}", "-", text).strip("-")
    return text


def collect_anchors(path: str) -> set[str]:
    anchors: set[str] = set()
    counts: Counter[str] = Counter()

    with open(path, encoding="utf-8") as handle:
        for line in handle:
            match = HEADING_RE.match(line)
            if not match:
                continue
            base = github_slug(match.group(1))
            if not base:
                continue
            occurrence = counts[base]
            anchor = base if occurrence == 0 else f"{base}-{occurrence}"
            counts[base] += 1
            anchors.add(anchor)

    return anchors


def is_external(link: str) -> bool:
    return bool(SCHEME_RE.match(link))


def main() -> int:
    args = parse_args()
    root = os.path.abspath(args.root)
    suffixes = tuple(part.strip().lower() for part in args.include.split(",") if part.strip())
    excluded = set(DEFAULT_EXCLUDE_DIRS)
    excluded.update(args.exclude_dir)

    anchors_cache: dict[str, set[str]] = {}
    broken_count = 0

    markdown_files = sorted(iter_markdown_files(root, suffixes, excluded))
    if not markdown_files:
        print("No markdown files found.")
        return 0

    for path in markdown_files:
        with open(path, encoding="utf-8") as handle:
            content = handle.read()

        rel_path = os.path.relpath(path, root)
        for raw_link in LINK_RE.findall(content):
            link = raw_link.strip()
            if not link or is_external(link):
                continue

            target_part, fragment = (link.split("#", 1) + [""])[:2]
            target_part = target_part.strip()
            fragment = urllib.parse.unquote(fragment.strip()).lower()

            target_file = path if target_part == "" else os.path.normpath(
                os.path.join(os.path.dirname(path), target_part)
            )

            if not os.path.exists(target_file):
                print(
                    f"BROKEN FILE: {rel_path} -> {link} "
                    f"(missing: {os.path.relpath(target_file, root)})"
                )
                broken_count += 1
                continue

            if fragment:
                if target_file not in anchors_cache:
                    try:
                        anchors_cache[target_file] = collect_anchors(target_file)
                    except OSError:
                        anchors_cache[target_file] = set()
                if fragment not in anchors_cache[target_file]:
                    print(
                        f"BROKEN ANCHOR: {rel_path} -> {link} "
                        f"(missing #{fragment})"
                    )
                    broken_count += 1

    if broken_count > 0:
        print(f"\nFound {broken_count} broken markdown link(s).")
        return 1

    print(f"Checked {len(markdown_files)} markdown file(s): no broken links found.")
    return 0


if __name__ == "__main__":
    sys.exit(main())

