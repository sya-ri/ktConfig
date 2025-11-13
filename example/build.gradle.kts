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

tasks.register<LaunchMinecraftServerTask>("testPlugin") {
    dependsOn("build")

    doFirst {
        copy {
            from(layout.buildDirectory.file("libs/${project.name}-all.jar"))
            into(layout.buildDirectory.file("MinecraftServer/plugins"))
        }
    }

    jarUrl.set(LaunchMinecraftServerTask.JarUrl.Paper("1.21.7"))
    agreeEula.set(true)
}
