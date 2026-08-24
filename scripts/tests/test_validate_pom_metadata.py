from __future__ import annotations

import contextlib
import io
import tempfile
import unittest
from pathlib import Path

from scripts.validate_pom_metadata import PomValidationError, main, validate_pom


VALID_POM = """\
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
  <modelVersion>4.0.0</modelVersion>
  <groupId>dev.s7a</groupId>
  <artifactId>ktConfig</artifactId>
  <version>2.3.0</version>
  <name>ktConfig</name>
  <description>Configuration library.</description>
  <url>https://github.com/sya-ri/ktConfig</url>
  <inceptionYear>2025</inceptionYear>
  <licenses>
    <license>
      <name>MIT License</name>
      <url>https://github.com/sya-ri/ktConfig/blob/master/LICENSE</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <developers>
    <developer>
      <id>sya-ri</id>
      <name>sya-ri</name>
      <email>contact@s7a.dev</email>
      <url>https://github.com/sya-ri</url>
    </developer>
  </developers>
  <scm>
    <connection>scm:git:https://github.com/sya-ri/ktConfig.git</connection>
    <developerConnection>scm:git:ssh://git@github.com/sya-ri/ktConfig.git</developerConnection>
    <url>https://github.com/sya-ri/ktConfig</url>
  </scm>
  <dependencies>
    <dependency>
      <groupId>dev.s7a</groupId>
      <artifactId>ktConfig-fabric</artifactId>
      <version>2.3.0</version>
    </dependency>
  </dependencies>
</project>
"""


class ValidatePomMetadataTest(unittest.TestCase):
    def setUp(self) -> None:
        self.temporary_directory = tempfile.TemporaryDirectory()
        self.addCleanup(self.temporary_directory.cleanup)
        self.pom_path = Path(self.temporary_directory.name) / "pom.xml"
        self.pom_path.write_text(VALID_POM, encoding="utf-8")

    def test_accepts_complete_release_pom(self) -> None:
        validate_pom(
            self.pom_path,
            require_release=True,
            expected_artifact_id="ktConfig",
            project_dependency="ktConfig-fabric",
        )

    def test_accepts_snapshot_pom_without_release_guard(self) -> None:
        self.pom_path.write_text(VALID_POM.replace("2.3.0", "2.4.0-SNAPSHOT"), encoding="utf-8")
        validate_pom(self.pom_path, require_release=False)

    def test_rejects_snapshot_pom_with_release_guard(self) -> None:
        self.pom_path.write_text(VALID_POM.replace("2.3.0", "2.4.0-SNAPSHOT"), encoding="utf-8")
        with self.assertRaisesRegex(PomValidationError, "publishing requires a release version"):
            validate_pom(self.pom_path, require_release=True)

    def test_rejects_missing_metadata(self) -> None:
        self.pom_path.write_text(
            VALID_POM.replace("<name>MIT License</name>", "<name> </name>").replace(
                "<developerConnection>scm:git:ssh://git@github.com/sya-ri/ktConfig.git</developerConnection>",
                "",
            ),
            encoding="utf-8",
        )
        with self.assertRaisesRegex(PomValidationError, "license name, SCM developer connection"):
            validate_pom(self.pom_path, require_release=False)

    def test_rejects_wrong_artifact_id(self) -> None:
        with self.assertRaisesRegex(PomValidationError, "expected 'ktConfig-fabric'"):
            validate_pom(
                self.pom_path,
                require_release=False,
                expected_artifact_id="ktConfig-fabric",
            )

    def test_rejects_mismatched_project_dependency_version(self) -> None:
        self.pom_path.write_text(
            VALID_POM.replace(
                "<artifactId>ktConfig-fabric</artifactId>\n      <version>2.3.0</version>",
                "<artifactId>ktConfig-fabric</artifactId>\n      <version>2.2.0</version>",
            ),
            encoding="utf-8",
        )
        with self.assertRaisesRegex(PomValidationError, "dev.s7a:ktConfig-fabric:2.3.0"):
            validate_pom(
                self.pom_path,
                require_release=False,
                project_dependency="ktConfig-fabric",
            )

    def test_cli_reports_every_invalid_pom(self) -> None:
        missing_path = self.pom_path.with_name("missing.xml")
        snapshot_path = self.pom_path.with_name("snapshot.xml")
        snapshot_path.write_text(VALID_POM.replace("2.3.0", "2.4.0-SNAPSHOT"), encoding="utf-8")
        standard_error = io.StringIO()
        with contextlib.redirect_stderr(standard_error):
            exit_code = main(["--release", str(missing_path), str(snapshot_path)])

        self.assertEqual(exit_code, 1)
        self.assertIn(str(missing_path), standard_error.getvalue())
        self.assertIn(str(snapshot_path), standard_error.getvalue())


if __name__ == "__main__":
    unittest.main()
