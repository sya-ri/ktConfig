import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.s7a.gradle.minecraft.server.tasks.LaunchMinecraftServerTask
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import org.gradle.kotlin.dsl.register

plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.pluginYml.bukkit)
    alias(libs.plugins.minecraftServer)
    alias(libs.plugins.shadow)
}

val useLocalBuild = project.properties.getOrDefault("useLocalBuild", "false") == "true"

repositories {
    if (useLocalBuild) {
        mavenLocal()
    }
}

dependencies {
    library(kotlin("stdlib"))
    compileOnly(libs.spigot)

    project.logger.lifecycle("Use Local Build: $useLocalBuild")
    if (useLocalBuild) {
        implementation("dev.s7a:ktConfig:${rootProject.version}")
        shadow("dev.s7a:ktConfig:${rootProject.version}")
        ksp("dev.s7a:ktConfig-ksp:${rootProject.version}")
    } else {
        implementation(project(":"))
        shadow(project(":"))
        ksp(project(":ksp"))
    }
}

configure<BukkitPluginDescription> {
    main = "dev.s7a.example.ExamplePlugin"
    author = "sya_ri"
    version = rootProject.version.toString()
    apiVersion = "1.13"
}

tasks.getting(ShadowJar::class) {
    dependencies {
        exclude(dependency("org.jetbrains.kotlin:.*"))
        exclude(dependency("org.jetbrains:annotations:.*"))
    }
}

tasks.named("build") {
    dependsOn("shadowJar")
}

listOf(
    "8" to "1.8.8",
    "21" to "1.21.11",
).forEach { (minor, version) ->
    tasks.register<LaunchMinecraftServerTask>("testPlugin$minor") {
        dependsOn("build")

        doFirst {
            copy {
                from(layout.buildDirectory.file("libs/${project.name}-all.jar"))
                into(layout.buildDirectory.file("MinecraftServer$minor/plugins"))
            }
        }

        doLast {
            logger.lifecycle("Test finished for Minecraft $version")
            val outputFile = layout.buildDirectory.file("MinecraftServer$minor/plugins/example/output.txt")
            val errorFile = layout.buildDirectory.file("MinecraftServer$minor/plugins/example/error.txt")
            if (outputFile.get().asFile.exists()) {
                val message = outputFile.get().asFile.readText()
                if (message.isNotEmpty()) {
                    logger.lifecycle(message)
                }
            }
            if (errorFile.get().asFile.exists()) {
                val message = errorFile.get().asFile.readText()
                if (message.isNotEmpty()) {
                    logger.error(message)

                    throw GradleException("Test failed for Minecraft $version")
                }
            }
        }

        serverDirectory.set(
            layout.buildDirectory
                .dir("MinecraftServer$minor")
                .get()
                .asFile.absolutePath,
        )
        // FIXME For some reason this doesn't work: 'kotlinx.serialization.KSerializer[] kotlinx.serialization.internal.GeneratedSerializer.typeParametersSerializers()'
        // jarUrl.set(LaunchMinecraftServerTask.JarUrl.Paper(version))
        jarUrl.set(
            when (version) {
                "1.21.11" -> {
                    LaunchMinecraftServerTask.JarUrl {
                        "https://fill-data.papermc.io/v1/objects/5be84d9fc43181a72d5fdee7e3167824d9667bfc97b1bf9721713f9a971481ca/paper-1.21.11-88.jar"
                    }
                }

                "1.8.8" -> {
                    LaunchMinecraftServerTask.JarUrl {
                        "https://fill-data.papermc.io/v1/objects/7ff6d2cec671ef0d95b3723b5c92890118fb882d73b7f8fa0a2cd31d97c55f86/paper-1.8.8-445.jar"
                    }
                }

                else -> {
                    error("Unknown Minecraft version: $version")
                }
            },
        )
        agreeEula.set(true)
    }
}
