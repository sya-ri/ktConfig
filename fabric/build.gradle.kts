import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm

plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.dokka)
    alias(libs.plugins.dokka.javadoc)
    alias(libs.plugins.mavenPublish)
}

version = rootProject.version

kotlin {
    sourceSets {
        main {
            kotlin.srcDir(rootProject.file("src/main/kotlin"))
        }
    }
}

dependencies {
    api(libs.configurate.yaml)
    compileOnly(libs.fabric.loader)
    testImplementation(kotlin("test"))
    kspTest(project(":ksp"))
}

ksp {
    arg("ktconfig.platform", "fabric")
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates("dev.s7a", "ktConfig-fabric", version.toString())
    configure(
        KotlinJvm(
            javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationJavadoc"),
            sourcesJar = true,
        ),
    )
    pom {
        name.set("ktConfig Fabric")
        description.set("Fabric-compatible YAML configuration library for Kotlin using class annotations.")
        inceptionYear.set("2025")
        url.set("https://github.com/sya-ri/ktConfig")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://github.com/sya-ri/ktConfig/blob/master/LICENSE")
            }
        }
    }
}
