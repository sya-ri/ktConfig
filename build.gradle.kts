import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kover)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.pluginYml.bukkit) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.dokka.javadoc)
    alias(libs.plugins.mavenPublish)
}

group = "dev.s7a"
version = libs.versions.ktConfig.get()

val testJarDir = providers.systemProperty("ktconfig.testJarDir").orNull

kotlin {
    sourceSets.main {
        kotlin.srcDir("src/bukkit/kotlin")
    }
}

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "org.jmailen.kotlinter")

    if (testJarDir != null) {
        val projectJarDir = path.removePrefix(":").replace(':', '/').ifEmpty { "root" }
        tasks.named<Jar>("jar") {
            destinationDirectory.set(rootProject.file("$testJarDir/$projectJarDir"))
        }
    }

    repositories {
        mavenCentral()
        maven("https://maven.fabricmc.net/")
        maven("https://hub.spigotmc.org/nexus/content/groups/public/")
        maven("https://repo.papermc.io/repository/maven-public/")
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
    testImplementation(gradleTestKit())
    testImplementation(libs.mockbukkit)
    testImplementation(libs.adventure.api)
    testImplementation(libs.paper.api)
    kspTest(project(":ksp"))
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
        description.set("Bukkit configuration library for Kotlin using class annotations.")
    }
}
