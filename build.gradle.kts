import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import kotlin.jvm.java

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kover)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.pluginYml.bukkit) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.dokka.javadoc)
    alias(libs.plugins.mavenPublish)
}

group = "dev.s7a"
version = "2.0.0-SNAPSHOT"

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

dependencies {
    compileOnly(libs.spigot)
    testImplementation(kotlin("test"))
    testImplementation(libs.spigot)
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
        description.set("Spigot configuration library for Kotlin using class annotations.")
        inceptionYear.set("2025")
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
