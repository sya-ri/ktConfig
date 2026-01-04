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
    compileOnly(libs.spigot8)

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
        jarUrl.set(LaunchMinecraftServerTask.JarUrl.Paper(version))
        agreeEula.set(true)
    }
}
