from __future__ import annotations

import contextlib
import io
import tempfile
import unittest
from pathlib import Path

from scripts.sync_readme_versions import (
    SynchronizationError,
    load_versions,
    main,
    synchronize_text,
)


CATALOG = """\
[versions]
ktConfig = "2.3.0-SNAPSHOT"
ktConfigBukkit = "2.2.0"
ktConfigFabric = "2.3.0"
kotlin = "2.4.10"
ksp = "2.3.11"
configurate = "4.2.0"
geantyref = "1.3.16"
option = "1.1.0"
"""

README = """\
# Project\r
\r
## 📦 Installation\r
\r
```kotlin\r
kotlin("jvm") version "0.0.1"\r
id("com.google.devtools.ksp") version "0.0.2"\r
implementation("dev.s7a:ktConfig:0.0.3")\r
ksp("dev.s7a:ktConfig-ksp:0.0.3")\r
```\r
\r
### Fabric\r
\r
```kotlin\n
ksp("dev.s7a:ktConfig-ksp:0.0.4")\n
include(implementation("dev.s7a:ktConfig-fabric:0.0.4")!!)\n
include(implementation("org.spongepowered:configurate-yaml:0.0.5")!!)\n
include(implementation("org.spongepowered:configurate-core:0.0.5")!!)\n
include(implementation("io.leangen.geantyref:geantyref:0.0.6")!!)\n
include(implementation("net.kyori:option:0.0.7")!!)\n
implementation("dev.s7a:ktConfig-fabric-minecraft-1.21.11:0.0.4")\n
```\n
\n
## Next\n
"""


class SynchronizeReadmeVersionsTest(unittest.TestCase):
    def setUp(self) -> None:
        self.temporary_directory = tempfile.TemporaryDirectory()
        self.addCleanup(self.temporary_directory.cleanup)
        self.directory = Path(self.temporary_directory.name)
        self.catalog_path = self.directory / "libs.versions.toml"
        self.readme_path = self.directory / "README.md"
        self.catalog_path.write_bytes(CATALOG.encode())
        self.readme_path.write_bytes(README.encode())

    def versions(self) -> dict[str, str]:
        return load_versions(self.catalog_path)

    def test_synchronizes_all_declared_versions_and_preserves_line_endings(self) -> None:
        updated = synchronize_text(README, self.versions())

        self.assertIn('kotlin("jvm") version "2.4.10"', updated)
        self.assertIn('id("com.google.devtools.ksp") version "2.3.11"', updated)
        self.assertEqual(updated.count("dev.s7a:ktConfig:2.2.0"), 1)
        self.assertEqual(updated.count("dev.s7a:ktConfig-ksp:2.2.0"), 1)
        self.assertEqual(updated.count("dev.s7a:ktConfig-ksp:2.3.0"), 1)
        self.assertEqual(updated.count("dev.s7a:ktConfig-fabric:2.3.0"), 1)
        self.assertEqual(updated.count("dev.s7a:ktConfig-fabric-minecraft-1.21.11:2.3.0"), 1)
        self.assertEqual(updated.count("org.spongepowered:configurate-yaml:4.2.0"), 1)
        self.assertEqual(updated.count("org.spongepowered:configurate-core:4.2.0"), 1)
        self.assertIn("io.leangen.geantyref:geantyref:1.3.16", updated)
        self.assertIn("net.kyori:option:1.1.0", updated)
        self.assertEqual(updated.count("\r\n"), README.count("\r\n"))
        self.assertEqual(updated.count("\n"), README.count("\n"))

    def test_synchronization_is_idempotent(self) -> None:
        synchronized = synchronize_text(README, self.versions())
        self.assertEqual(synchronize_text(synchronized, self.versions()), synchronized)

    def test_development_version_does_not_leak_into_dependency_examples(self) -> None:
        self.catalog_path.write_bytes(CATALOG.replace("2.3.0-SNAPSHOT", "2.4.0-SNAPSHOT").encode())
        updated = synchronize_text(README, self.versions())

        self.assertNotIn("2.4.0", updated)
        self.assertEqual(updated.count("dev.s7a:ktConfig-fabric:2.3.0"), 1)

    def test_fails_closed_when_a_readme_target_is_missing(self) -> None:
        incomplete = README.replace(
            'include(implementation("net.kyori:option:0.0.7")!!)\n',
            "",
        )
        with self.assertRaisesRegex(SynchronizationError, "Option artifact must match 1 time"):
            synchronize_text(incomplete, self.versions())

    def test_fails_closed_when_a_readme_target_is_duplicated(self) -> None:
        duplicated = README.replace(
            "## Next\n",
            'include(implementation("net.kyori:option:9.9.9")!!)\n## Next\n',
        )
        with self.assertRaisesRegex(SynchronizationError, "Option artifact must match 1 time"):
            synchronize_text(duplicated, self.versions())

    def test_fails_closed_when_one_artifact_replaces_another(self) -> None:
        swapped = README.replace("dev.s7a:ktConfig-ksp:0.0.3", "dev.s7a:ktConfig:0.0.3")
        with self.assertRaisesRegex(SynchronizationError, "Bukkit ktConfig runtime must match 1 time"):
            synchronize_text(swapped, self.versions())

    def test_requires_every_catalog_key(self) -> None:
        self.catalog_path.write_bytes(CATALOG.replace('option = "1.1.0"\n', "").encode())
        with self.assertRaisesRegex(SynchronizationError, r"\[versions\]\.option"):
            load_versions(self.catalog_path)

    def test_release_project_version_requires_matching_documentation_versions(self) -> None:
        self.catalog_path.write_bytes(CATALOG.replace("2.3.0-SNAPSHOT", "2.3.0").encode())
        with self.assertRaisesRegex(SynchronizationError, "release project version 2.3.0"):
            load_versions(self.catalog_path)

    def test_release_project_version_accepts_matching_documentation_versions(self) -> None:
        release_catalog = CATALOG.replace("2.3.0-SNAPSHOT", "2.3.0").replace(
            'ktConfigBukkit = "2.2.0"',
            'ktConfigBukkit = "2.3.0"',
        )
        self.catalog_path.write_bytes(release_catalog.encode())
        self.assertEqual(load_versions(self.catalog_path)["ktConfig"], "2.3.0")

    def test_check_mode_reports_diff_without_writing(self) -> None:
        original = self.readme_path.read_bytes()
        standard_error = io.StringIO()
        with contextlib.redirect_stderr(standard_error):
            exit_code = main(
                [
                    "--check",
                    "--catalog",
                    str(self.catalog_path),
                    "--readme",
                    str(self.readme_path),
                ]
            )

        self.assertEqual(exit_code, 1)
        self.assertEqual(self.readme_path.read_bytes(), original)
        self.assertIn("Run: python scripts/sync_readme_versions.py --write", standard_error.getvalue())

    def test_write_mode_is_byte_idempotent(self) -> None:
        first_exit_code = main(
            [
                "--write",
                "--catalog",
                str(self.catalog_path),
                "--readme",
                str(self.readme_path),
            ]
        )
        first_write = self.readme_path.read_bytes()
        second_exit_code = main(
            [
                "--write",
                "--catalog",
                str(self.catalog_path),
                "--readme",
                str(self.readme_path),
            ]
        )

        self.assertEqual(first_exit_code, 0)
        self.assertEqual(second_exit_code, 0)
        self.assertEqual(self.readme_path.read_bytes(), first_write)


if __name__ == "__main__":
    unittest.main()
