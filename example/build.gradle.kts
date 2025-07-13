import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.pluginYml.bukkit)
    alias(libs.plugins.minecraftServer)
    alias(libs.plugins.shadow)
}

dependencies {
    compileOnly(libs.spigotLatest)
    implementation(project(":"))
    ksp(project(":ksp"))
}

configure<BukkitPluginDescription> {
    main = "dev.s7a.example.ExamplePlugin"
    author = "sya_ri"
    version = rootProject.version.toString()
    apiVersion = "1.13"
}

tasks.getting(ShadowJar::class) {
    configurations = listOf(project.configurations.getByName("implementation"))
}
