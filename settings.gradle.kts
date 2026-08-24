pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "ktConfig"

include(
    ":ksp",
    ":example",
    ":fabric",
    ":fabric-minecraft-remap",
    ":fabric-minecraft-unobfuscated",
)
