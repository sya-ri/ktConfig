import org.gradle.testkit.runner.GradleRunner
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

class KtConfigWarningCompilationTest {
    @Test
    fun testWarnsWhenTypeAliasSpecifiesHasDefault() {
        val result =
            compileKtConfigSource(
                """
                package warning

                import dev.s7a.ktconfig.KtConfig

                data class Config(
                    val value: String = "default",
                )

                @KtConfig(hasDefault = true)
                typealias ConfigAlias = Config
                """.trimIndent(),
            )

        assertContains(
            result.output,
            "@KtConfig(hasDefault = ...) on type aliases is ignored. Put @KtConfig(hasDefault = ...) on the aliased class instead.",
        )
    }

    @Test
    fun testDoesNotWarnWhenTypeAliasHasDefaultMatchesAliasedClass() {
        val result =
            compileKtConfigSource(
                """
                package warning

                import dev.s7a.ktconfig.KtConfig

                @KtConfig(hasDefault = true)
                data class Config(
                    val value: String = "default",
                )

                @KtConfig(hasDefault = true)
                typealias ConfigAlias = Config
                """.trimIndent(),
            )

        assertFalse(
            result.output.contains(
                "@KtConfig(hasDefault = ...) on type aliases is ignored. Put @KtConfig(hasDefault = ...) on the aliased class instead.",
            ),
        )
    }

    private fun compileKtConfigSource(source: String) =
        createKtConfigCompilationProject(source).let { projectDir ->
            GradleRunner
                .create()
                .withProjectDir(projectDir.toFile())
                .withArguments(
                    "--project-cache-dir",
                    projectDir.resolve("cache").toString(),
                    "compileKotlin",
                ).build()
        }

    private fun createKtConfigCompilationProject(source: String): Path {
        val projectDir = Files.createTempDirectory("ktconfig-warning-test")
        val rootProjectDir = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize()
        projectDir.resolve("settings.gradle.kts").writeText(
            """
            pluginManagement {
                repositories {
                    gradlePluginPortal()
                    mavenCentral()
                }
            }

            dependencyResolutionManagement {
                repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
                repositories {
                    mavenCentral()
                    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
                }
            }

            rootProject.name = "ktconfig-warning-test"

            includeBuild("${rootProjectDir.invariantSeparatorsPath()}") {
                dependencySubstitution {
                    substitute(module("dev.s7a:ktConfig")).using(project(":"))
                    substitute(module("dev.s7a:ktConfig-ksp")).using(project(":ksp"))
                }
            }
            """.trimIndent(),
        )
        projectDir.resolve("build.gradle.kts").writeText(
            """
            plugins {
                kotlin("jvm") version "2.3.21"
                id("com.google.devtools.ksp") version "2.3.7"
            }

            kotlin {
                jvmToolchain(21)
            }

            dependencies {
                compileOnly("org.spigotmc:spigot-api:1.21.11-R0.2-SNAPSHOT")
                implementation("dev.s7a:ktConfig")
                ksp("dev.s7a:ktConfig-ksp")
            }
            """.trimIndent(),
        )
        projectDir
            .resolve("src/main/kotlin/warning/WarningConfig.kt")
            .apply { parent.createDirectories() }
            .writeText(source)
        return projectDir
    }

    private fun Path.invariantSeparatorsPath() = toString().replace("\\", "/")
}
