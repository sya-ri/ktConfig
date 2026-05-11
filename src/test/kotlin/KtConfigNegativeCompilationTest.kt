import org.gradle.testkit.runner.GradleRunner
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertContains

class KtConfigNegativeCompilationTest {
    @Test
    fun testFailsWhenTypeAliasTargetsSealedClass() {
        val result =
            compileKtConfigSourceAndFail(
                """
                package negative

                import dev.s7a.ktconfig.KtConfig

                sealed interface SealedConfig {
                    @KtConfig
                    data class Child(
                        val value: String,
                    ) : SealedConfig
                }

                @KtConfig
                typealias SealedAliasConfig = SealedConfig
                """.trimIndent(),
            )

        assertContains(result.output, "@KtConfig type aliases for sealed classes are not supported")
    }

    @Test
    fun testFailsWhenPropertyTypeIsUnsupported() {
        val result =
            compileKtConfigSourceAndFail(
                """
                package negative

                import dev.s7a.ktconfig.KtConfig

                class UnsupportedType

                @KtConfig
                data class UnsupportedTypeConfig(
                    val value: UnsupportedType,
                )
                """.trimIndent(),
            )

        assertContains(result.output, "Unsupported type: negative.UnsupportedType")
    }

    @Test
    fun testFailsWhenConfigClassHasNoPrimaryConstructor() {
        val result =
            compileKtConfigSourceAndFail(
                """
                package negative

                import dev.s7a.ktconfig.KtConfig

                @KtConfig
                class NoPrimaryConstructorConfig {
                    constructor(value: String)
                }
                """.trimIndent(),
            )

        assertContains(result.output, "Classes annotated with @KtConfig must have a primary constructor")
    }

    private fun compileKtConfigSourceAndFail(source: String) =
        createKtConfigCompilationProject(source).let { projectDir ->
            GradleRunner
                .create()
                .withProjectDir(projectDir.toFile())
                .withArguments(
                    "--project-cache-dir",
                    projectDir.resolve("cache").toString(),
                    "compileKotlin",
                ).buildAndFail()
        }

    private fun createKtConfigCompilationProject(source: String): Path {
        val projectDir = Files.createTempDirectory("ktconfig-negative-test")
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

            rootProject.name = "ktconfig-negative-test"

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
            .resolve("src/main/kotlin/negative/NegativeConfig.kt")
            .apply { parent.createDirectories() }
            .writeText(source)
        return projectDir
    }

    private fun Path.invariantSeparatorsPath() = toString().replace("\\", "/")
}
