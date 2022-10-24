import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.s7a.gradle.minecraft.server.tasks.LaunchMinecraftServerTask
import dev.s7a.gradle.minecraft.server.tasks.LaunchMinecraftServerTask.JarUrl
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("dev.s7a.gradle.minecraft.server") version "2.0.0" apply false
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

subprojects {
    apply(plugin = "net.minecrell.plugin-yml.bukkit")
    apply(plugin = "dev.s7a.gradle.minecraft.server")
    apply(plugin = "com.github.johnrengelman.shadow")

    dependencies {
        compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
        implementation(project(":"))
    }

    configure<BukkitPluginDescription> {
        main = "dev.s7a.ktconfig.${project.name.replace("-", "")}.Main"
        author = "sya_ri"
        version = rootProject.version.toString()
        apiVersion = "1.13"
    }

    tasks.getting(ShadowJar::class) {
        configurations = listOf(project.configurations.getByName("implementation"))
    }

    listOf(
        "8" to "1.8.8",
        "9" to "1.9.4",
        "10" to "1.10.2",
        "11" to "1.11.2",
        "12" to "1.12.2",
        "13" to "1.13.2",
        "14" to "1.14.4",
        "15" to "1.15.2",
        "16" to "1.16.5",
        "17" to "1.17.1",
        "18" to "1.18.2",
        "19" to "1.19.2"
    ).forEach { (name, version) ->
        task<LaunchMinecraftServerTask>("testPlugin$name") {
            dependsOn("build")

            doFirst {
                copy {
                    from(buildDir.resolve("libs/${project.name}-all.jar"))
                    into(buildDir.resolve("MinecraftServer$name/plugins"))
                }
            }

            serverDirectory.set(buildDir.resolve("MinecraftServer$name"))
            jarUrl.set(JarUrl.Paper(version))
            agreeEula.set(true)
        }
    }

    tasks.build {
        dependsOn("shadowJar")
    }
}
