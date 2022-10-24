import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2" apply false
    id("org.jetbrains.dokka") version "1.7.20"
    id("org.jmailen.kotlinter") version "3.12.0"
}

group = "dev.s7a"
version = "1.0.0-SNAPSHOT"

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "org.jmailen.kotlinter")

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

dependencies {
    if (project.hasProperty("USE_SPIGOT_8")) {
        compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    } else {
        compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
    }
    api("com.charleskorn.kaml:kaml:0.49.0")
}
