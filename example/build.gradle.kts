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

dependencies {
    library(kotlin("stdlib"))
    compileOnly(libs.spigot8)
    implementation(project(":"))
    shadow(project(":"))
    ksp(project(":ksp"))
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
    "21" to "1.21.10",
).forEach { (minor, version) ->
    tasks.register<LaunchMinecraftServerTask>("testPlugin$minor") {
        dependsOn("build")

        doFirst {
            copy {
                from(layout.buildDirectory.file("libs/${project.name}-all.jar"))
                into(layout.buildDirectory.file("MinecraftServer$minor/plugins"))
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
