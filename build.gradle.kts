import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.22"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2" apply false
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
    id("org.jetbrains.dokka") version "1.7.20"
    id("org.jmailen.kotlinter") version "3.13.0"
    id("dev.s7a.gradle.minecraft.server") version "2.0.0" apply false
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
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

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    if (project.hasProperty("USE_SPIGOT_8")) {
        compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    } else {
        compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
    }
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
    testImplementation("com.github.seeseemelk:MockBukkit-v1.19:2.141.0")
}

tasks.test {
    useJUnitPlatform()
}
