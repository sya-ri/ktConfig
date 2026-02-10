import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kover)
    alias(libs.plugins.dokka)
    alias(libs.plugins.dokka.javadoc)
    alias(libs.plugins.jmh)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.pluginYml.bukkit) apply false
    alias(libs.plugins.minecraftServer) apply false
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.mavenPublish)
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
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
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
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.framework.datatest)
    testImplementation(libs.mockBukkit)
}

tasks.test {
    useJUnitPlatform()
}

jmh {
    iterations.set(3)
    warmupIterations.set(3)
    fork.set(1)
    resultFormat.set("json")
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates("dev.s7a", "ktConfig", version.toString())
    configure(
        KotlinJvm(
            javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationJavadoc"),
            sourcesJar = true,
        ),
    )

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
