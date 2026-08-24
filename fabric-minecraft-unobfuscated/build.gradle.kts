import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.dokka)
    alias(libs.plugins.dokka.javadoc)
    alias(libs.plugins.mavenPublish)
}

val minecraftVersion = providers.gradleProperty("unobfuscatedMinecraftVersion").getOrElse("26.2")
version = rootProject.version

kotlin.sourceSets.main {
    kotlin.srcDir(rootProject.file("fabric-minecraft/src/common/kotlin"))
    kotlin.srcDir(rootProject.file("fabric-minecraft/src/identifier/kotlin"))
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    api(project(":fabric"))
    compileOnly(libs.fabric.loader)
    testImplementation(kotlin("test"))
}

tasks.compileJava { targetCompatibility = "25" }
tasks.compileKotlin { compilerOptions.jvmTarget.set(JvmTarget.JVM_25) }

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates("dev.s7a", "ktConfig-fabric-minecraft-$minecraftVersion", version.toString())
    configure(KotlinJvm(javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationJavadoc"), sourcesJar = true))
    pom {
        name.set("ktConfig Fabric Minecraft $minecraftVersion")
        description.set("Minecraft type serializers for ktConfig Fabric targeting Minecraft $minecraftVersion.")
    }
}
