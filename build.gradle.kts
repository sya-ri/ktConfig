import org.jetbrains.dokka.gradle.DokkaTask
import java.io.FileFilter

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kover)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.pluginYml.bukkit) apply false
    alias(libs.plugins.minecraftServer) apply false
    alias(libs.plugins.shadow) apply false
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
        compileOnly(libs.spigot8)
    } else {
        compileOnly(libs.spigotLatest)
    }
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
    testImplementation(libs.mockBukkit)
}

tasks.withType<DokkaTask>().configureEach {
    val dokkaDir = projectDir.resolve("dokka")
    val version = version.toString()

    dependencies {
        dokkaPlugin(libs.dokka.plugin.versioning)
    }
    outputDirectory.set(file(dokkaDir.resolve(version)))
    pluginsMapConfiguration.set(
        mapOf(
            "org.jetbrains.dokka.versioning.VersioningPlugin" to """
                {
                    "version": "$version",
                    "olderVersionsDir": "$dokkaDir"
                }
            """.trimIndent(),
        ),
    )
}

tasks.named("dokkaHtml") {
    val dokkaDir = projectDir.resolve("dokka")

    doFirst {
        dokkaDir.listFiles()?.forEach { file ->
            if (file != null && file.isDirectory && file.name.endsWith("-SNAPSHOT")) {
                file.deleteRecursively()
            }
        }
    }
    doLast {
        if (version.toString().endsWith("-SNAPSHOT").not() || dokkaDir.listFiles(FileFilter { it.isDirectory }).singleOrNull()?.name == version.toString()) {
            dokkaDir.resolve("index.html").writeText(
                """
                    <!DOCTYPE html>
                    <meta charset="utf-8">
                    <meta http-equiv="refresh" content="0; URL=./$version/">
                    <link rel="canonical" href="./$version/">
                """.trimIndent(),
            )
        }
    }
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
