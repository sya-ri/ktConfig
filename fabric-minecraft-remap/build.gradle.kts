import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.fabric.loom.remap)
    alias(libs.plugins.dokka)
    alias(libs.plugins.dokka.javadoc)
    alias(libs.plugins.mavenPublish)
}

val minecraftVersion = providers.gradleProperty("remapMinecraftVersion").getOrElse("1.21.11")
version = rootProject.version

kotlin.sourceSets.main {
    kotlin.srcDir(rootProject.file("fabric-minecraft/src/common/kotlin"))
    kotlin.srcDir(
        rootProject.file(
            if (minecraftVersion == "1.21.11") {
                "fabric-minecraft/src/identifier/kotlin"
            } else {
                "fabric-minecraft/src/resource-location/kotlin"
            },
        ),
    )
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())
    api(project(":fabric"))
    modCompileOnly(libs.fabric.loader)
    testImplementation(kotlin("test"))
}

tasks.compileJava { targetCompatibility = "21" }
tasks.compileKotlin { compilerOptions.jvmTarget.set(JvmTarget.JVM_21) }

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
