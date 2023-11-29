plugins {
    kotlin("jvm") version "1.9.21"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0" apply false
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
    id("org.jetbrains.dokka") version "1.9.10"
    id("org.jmailen.kotlinter") version "4.1.0"
    id("dev.s7a.gradle.minecraft.server") version "3.0.0" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    `maven-publish`
    signing
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

    tasks.compileJava {
        targetCompatibility = "1.8"
    }

    tasks.compileKotlin {
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
        compileOnly("org.spigotmc:spigot-api:1.20.2-experimental-SNAPSHOT")
    }
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:3.49.0")
}

tasks.test {
    useJUnitPlatform()
}

val sourceJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

publishing {
    repositories {
        maven {
            url = uri(
                if (version.toString().endsWith("SNAPSHOT")) {
                    "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                } else {
                    "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                },
            )
            credentials {
                username = properties["sonatypeUsername"].toString()
                password = properties["sonatypePassword"].toString()
            }
        }
    }
    publications {
        register<MavenPublication>("maven") {
            groupId = "dev.s7a"
            artifactId = "ktConfig"
            from(components["kotlin"])
            artifact(sourceJar.get())
            pom {
                name.set("ktConfig")
                description.set("Spigot config library for Kotlin handled using class constructor")
                url.set("https://github.com/sya-ri/ktConfig")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/sya-ri/ktConfig/blob/master/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("sya-ri")
                        name.set("sya-ri")
                        email.set("contact@s7a.dev")
                    }
                }
                scm {
                    url.set("https://github.com/sya-ri/ktConfig")
                }
            }
        }
    }
}

signing {
    val key = properties["signingKey"]?.toString()?.replace("\\n", "\n")
    val password = properties["signingPassword"]?.toString()

    useInMemoryPgpKeys(key, password)
    sign(publishing.publications["maven"])
}
