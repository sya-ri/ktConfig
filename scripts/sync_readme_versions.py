#!/usr/bin/env python3
"""Synchronize README dependency versions with the Gradle version catalog."""

from __future__ import annotations

import argparse
import difflib
import os
import re
import stat
import sys
import tempfile
import tomllib
from dataclasses import dataclass
from pathlib import Path
from typing import Sequence


ROOT_DIRECTORY = Path(__file__).resolve().parents[1]
DEFAULT_CATALOG = ROOT_DIRECTORY / "gradle" / "libs.versions.toml"
DEFAULT_README = ROOT_DIRECTORY / "README.md"


class SynchronizationError(Exception):
    """Raised when a catalog key or README target cannot be synchronized safely."""


@dataclass(frozen=True)
class Rule:
    """Describes one fail-closed replacement inside a Markdown section."""

    name: str
    section: str
    pattern: re.Pattern[str]
    version_key: str
    expected_matches: int


SECTION_PATTERNS = {
    "installation": (
        re.compile(r"^## 📦 Installation[ \t]*\r?$", re.MULTILINE),
        re.compile(r"^### Fabric[ \t]*\r?$", re.MULTILINE),
    ),
    "fabric": (
        re.compile(r"^### Fabric[ \t]*\r?$", re.MULTILINE),
        re.compile(r"^##(?!#)\s", re.MULTILINE),
    ),
}


RULES = (
    Rule(
        name="Kotlin plugin",
        section="installation",
        pattern=re.compile(r'kotlin\("jvm"\)\s+version\s+"(?P<version>[^"\r\n]+)"'),
        version_key="kotlin",
        expected_matches=1,
    ),
    Rule(
        name="KSP plugin",
        section="installation",
        pattern=re.compile(r'id\("com\.google\.devtools\.ksp"\)\s+version\s+"(?P<version>[^"\r\n]+)"'),
        version_key="ksp",
        expected_matches=1,
    ),
    Rule(
        name="Bukkit ktConfig runtime",
        section="installation",
        pattern=re.compile(r'dev\.s7a:ktConfig:(?P<version>[^"\r\n]+)'),
        version_key="ktConfigBukkit",
        expected_matches=1,
    ),
    Rule(
        name="Bukkit ktConfig KSP",
        section="installation",
        pattern=re.compile(r'dev\.s7a:ktConfig-ksp:(?P<version>[^"\r\n]+)'),
        version_key="ktConfigBukkit",
        expected_matches=1,
    ),
    Rule(
        name="Fabric ktConfig KSP",
        section="fabric",
        pattern=re.compile(r'dev\.s7a:ktConfig-ksp:(?P<version>[^"\r\n]+)'),
        version_key="ktConfigFabric",
        expected_matches=1,
    ),
    Rule(
        name="Fabric ktConfig runtime",
        section="fabric",
        pattern=re.compile(r'dev\.s7a:ktConfig-fabric:(?P<version>[^"\r\n]+)'),
        version_key="ktConfigFabric",
        expected_matches=1,
    ),
    Rule(
        name="Fabric Minecraft adapter",
        section="fabric",
        pattern=re.compile(
            r'dev\.s7a:ktConfig-fabric-minecraft-[^:"\r\n]+:(?P<version>[^"\r\n]+)'
        ),
        version_key="ktConfigFabric",
        expected_matches=1,
    ),
    Rule(
        name="Configurate YAML artifact",
        section="fabric",
        pattern=re.compile(r'org\.spongepowered:configurate-yaml:(?P<version>[^"\r\n]+)'),
        version_key="configurate",
        expected_matches=1,
    ),
    Rule(
        name="Configurate core artifact",
        section="fabric",
        pattern=re.compile(r'org\.spongepowered:configurate-core:(?P<version>[^"\r\n]+)'),
        version_key="configurate",
        expected_matches=1,
    ),
    Rule(
        name="geantyref artifact",
        section="fabric",
        pattern=re.compile(r'io\.leangen\.geantyref:geantyref:(?P<version>[^"\r\n]+)'),
        version_key="geantyref",
        expected_matches=1,
    ),
    Rule(
        name="Option artifact",
        section="fabric",
        pattern=re.compile(r'net\.kyori:option:(?P<version>[^"\r\n]+)'),
        version_key="option",
        expected_matches=1,
    ),
)


def read_utf8(path: Path) -> str:
    """Read UTF-8 without normalizing line endings."""

    return path.read_bytes().decode("utf-8")


def load_versions(path: Path) -> dict[str, str]:
    """Load and validate the version strings required by the README rules."""

    document = tomllib.loads(read_utf8(path))
    versions = document.get("versions")
    if not isinstance(versions, dict):
        raise SynchronizationError(f"{path} does not contain a [versions] table")

    required_keys = {rule.version_key for rule in RULES} | {"ktConfig"}
    result: dict[str, str] = {}
    for key in sorted(required_keys):
        value = versions.get(key)
        if not isinstance(value, str) or not value:
            raise SynchronizationError(f"[versions].{key} must be a non-empty string in {path}")
        result[key] = value

    for key in ("ktConfigBukkit", "ktConfigFabric"):
        if result[key].endswith("-SNAPSHOT"):
            raise SynchronizationError(f"[versions].{key} must be a consumer-facing, non-SNAPSHOT version")

    project_version = result["ktConfig"]
    if not project_version.endswith("-SNAPSHOT"):
        mismatched_keys = [
            key
            for key in ("ktConfigBukkit", "ktConfigFabric")
            if result[key] != project_version
        ]
        if mismatched_keys:
            keys = ", ".join(f"[versions].{key}" for key in mismatched_keys)
            raise SynchronizationError(
                f"release project version {project_version} must match {keys} before publishing"
            )
    return result


def section_bounds(text: str, section: str) -> tuple[int, int]:
    """Locate a README section and fail if its boundary is ambiguous."""

    start_pattern, end_pattern = SECTION_PATTERNS[section]
    starts = list(start_pattern.finditer(text))
    if len(starts) != 1:
        raise SynchronizationError(
            f"README section {section!r} must have exactly one start heading; found {len(starts)}"
        )

    start = starts[0].start()
    end_match = end_pattern.search(text, starts[0].end())
    if end_match is None:
        raise SynchronizationError(f"README section {section!r} has no end heading")
    return start, end_match.start()


def replace_rule(text: str, versions: dict[str, str], rule: Rule) -> str:
    """Apply one rule after validating its exact occurrence count."""

    start, end = section_bounds(text, rule.section)
    section = text[start:end]
    matches = list(rule.pattern.finditer(section))
    if len(matches) != rule.expected_matches:
        raise SynchronizationError(
            f"{rule.name} must match {rule.expected_matches} time(s) in the "
            f"{rule.section} section; found {len(matches)}"
        )

    replacement_version = versions[rule.version_key]
    if not replacement_version:
        raise SynchronizationError(f"{rule.name} resolved to an empty version")

    def replace(match: re.Match[str]) -> str:
        relative_start = match.start("version") - match.start()
        relative_end = match.end("version") - match.start()
        return match.group(0)[:relative_start] + replacement_version + match.group(0)[relative_end:]

    updated_section = rule.pattern.sub(replace, section)
    return text[:start] + updated_section + text[end:]


def synchronize_text(readme: str, versions: dict[str, str]) -> str:
    """Return README content synchronized by every declared rule."""

    updated = readme
    for rule in RULES:
        updated = replace_rule(updated, versions, rule)
    return updated


def atomic_write(path: Path, content: str) -> None:
    """Replace a file atomically while preserving its permission bits."""

    mode = stat.S_IMODE(path.stat().st_mode)
    descriptor, temporary_name = tempfile.mkstemp(prefix=f".{path.name}.", dir=path.parent)
    temporary_path = Path(temporary_name)
    try:
        with os.fdopen(descriptor, "wb") as temporary_file:
            temporary_file.write(content.encode("utf-8"))
        os.chmod(temporary_path, mode)
        os.replace(temporary_path, path)
    finally:
        temporary_path.unlink(missing_ok=True)


def unified_diff(path: Path, original: str, updated: str) -> str:
    """Create a stable, readable diff independent of source line endings."""

    lines = difflib.unified_diff(
        original.splitlines(),
        updated.splitlines(),
        fromfile=str(path),
        tofile=str(path),
        lineterm="",
    )
    return "\n".join(lines)


def run(*, check: bool, catalog_path: Path, readme_path: Path) -> int:
    """Check or write the synchronized README and return a process exit code."""

    versions = load_versions(catalog_path)
    original = read_utf8(readme_path)
    updated = synchronize_text(original, versions)

    if original == updated:
        print(f"{readme_path} is synchronized with {catalog_path}.")
        return 0

    if check:
        print(f"{readme_path} is not synchronized with {catalog_path}.", file=sys.stderr)
        print("Run: python scripts/sync_readme_versions.py --write", file=sys.stderr)
        print(unified_diff(readme_path, original, updated), file=sys.stderr)
        return 1

    atomic_write(readme_path, updated)
    print(f"Updated {readme_path} from {catalog_path}.")
    return 0


def create_parser() -> argparse.ArgumentParser:
    """Create the command-line parser."""

    parser = argparse.ArgumentParser(description=__doc__)
    mode = parser.add_mutually_exclusive_group(required=True)
    mode.add_argument("--check", action="store_true", help="fail if README.md is out of date")
    mode.add_argument("--write", action="store_true", help="update README.md in place")
    parser.add_argument("--catalog", type=Path, default=DEFAULT_CATALOG)
    parser.add_argument("--readme", type=Path, default=DEFAULT_README)
    return parser


def main(arguments: Sequence[str] | None = None) -> int:
    """Run the command-line interface."""

    options = create_parser().parse_args(arguments)
    try:
        return run(
            check=options.check,
            catalog_path=options.catalog.resolve(),
            readme_path=options.readme.resolve(),
        )
    except (OSError, UnicodeError, tomllib.TOMLDecodeError, SynchronizationError) as error:
        print(f"error: {error}", file=sys.stderr)
        return 2


if __name__ == "__main__":
    raise SystemExit(main())
