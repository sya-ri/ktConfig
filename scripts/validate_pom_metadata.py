#!/usr/bin/env python3
"""Validate Maven Central metadata in generated publication POM files."""

from __future__ import annotations

import argparse
import sys
import xml.etree.ElementTree as element_tree
from pathlib import Path
from typing import Sequence


class PomValidationError(Exception):
    """Raised when a generated POM is missing required publication metadata."""


REQUIRED_FIELDS = (
    ("groupId", "project group ID"),
    ("artifactId", "project artifact ID"),
    ("version", "project version"),
    ("name", "project name"),
    ("description", "project description"),
    ("url", "project URL"),
    ("inceptionYear", "inception year"),
    ("licenses/license/name", "license name"),
    ("licenses/license/url", "license URL"),
    ("licenses/license/distribution", "license distribution"),
    ("developers/developer/id", "developer ID"),
    ("developers/developer/name", "developer name"),
    ("developers/developer/email", "developer email"),
    ("developers/developer/url", "developer URL"),
    ("scm/connection", "SCM connection"),
    ("scm/developerConnection", "SCM developer connection"),
    ("scm/url", "SCM URL"),
)


def namespace_prefix(root: element_tree.Element) -> str:
    """Return the namespace prefix required by ElementTree lookup paths."""

    if root.tag.startswith("{"):
        namespace, _, _ = root.tag[1:].partition("}")
        if namespace:
            return f"{{{namespace}}}"
    return ""


def find_text(root: element_tree.Element, path: str) -> str | None:
    """Read and trim one namespaced element value."""

    prefix = namespace_prefix(root)
    namespaced_path = "/".join(f"{prefix}{part}" for part in path.split("/"))
    element = root.find(namespaced_path)
    if element is None or element.text is None:
        return None
    value = element.text.strip()
    return value or None


def validate_pom(
    path: Path,
    *,
    require_release: bool,
    expected_artifact_id: str | None = None,
    project_dependency: str | None = None,
) -> None:
    """Validate one generated POM and raise a single actionable error on failure."""

    try:
        root = element_tree.parse(path).getroot()
    except (OSError, element_tree.ParseError) as error:
        raise PomValidationError(f"cannot read {path}: {error}") from error

    missing = [label for field, label in REQUIRED_FIELDS if find_text(root, field) is None]
    if missing:
        raise PomValidationError(f"{path} is missing: {', '.join(missing)}")

    group_id = find_text(root, "groupId")
    if group_id != "dev.s7a":
        raise PomValidationError(f"{path} uses group ID {group_id!r}; expected 'dev.s7a'")

    artifact_id = find_text(root, "artifactId")
    if expected_artifact_id is not None and artifact_id != expected_artifact_id:
        raise PomValidationError(
            f"{path} uses artifact ID {artifact_id!r}; expected {expected_artifact_id!r}"
        )

    version = find_text(root, "version")
    if require_release and version is not None and version.endswith("-SNAPSHOT"):
        raise PomValidationError(f"{path} uses SNAPSHOT version {version}; publishing requires a release version")

    if project_dependency is not None:
        prefix = namespace_prefix(root)
        dependency_path = f"{prefix}dependencies/{prefix}dependency"
        matching_versions = [
            find_text(dependency, "version")
            for dependency in root.findall(dependency_path)
            if find_text(dependency, "groupId") == "dev.s7a"
            and find_text(dependency, "artifactId") == project_dependency
        ]
        if matching_versions != [version]:
            raise PomValidationError(
                f"{path} must depend on dev.s7a:{project_dependency}:{version}; "
                f"found versions {matching_versions}"
            )


def create_parser() -> argparse.ArgumentParser:
    """Create the command-line parser."""

    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--release",
        action="store_true",
        help="also reject POMs whose project version ends with -SNAPSHOT",
    )
    parser.add_argument("--artifact-id", help="expected artifact ID (requires exactly one POM)")
    parser.add_argument(
        "--project-dependency",
        help="required dev.s7a dependency artifact whose version must match the project version",
    )
    parser.add_argument("pom", nargs="+", type=Path, help="generated POM file to validate")
    return parser


def main(arguments: Sequence[str] | None = None) -> int:
    """Run the command-line interface."""

    options = create_parser().parse_args(arguments)
    if (options.artifact_id is not None or options.project_dependency is not None) and len(options.pom) != 1:
        print("error: --artifact-id and --project-dependency require exactly one POM", file=sys.stderr)
        return 2

    errors: list[str] = []
    for path in options.pom:
        try:
            validate_pom(
                path.resolve(),
                require_release=options.release,
                expected_artifact_id=options.artifact_id,
                project_dependency=options.project_dependency,
            )
            print(f"Validated {path}.")
        except PomValidationError as error:
            errors.append(str(error))

    for error in errors:
        print(f"error: {error}", file=sys.stderr)
    return 1 if errors else 0


if __name__ == "__main__":
    raise SystemExit(main())
