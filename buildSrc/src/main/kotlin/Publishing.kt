import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.SigningExtension
import kotlin.toString

fun Project.applyPublishingConfig(
    publishName: String,
    publication: MavenPublication.() -> Unit = {},
    pom: MavenPom.() -> Unit = {},
) {
    version = rootProject.version

    extensions.configure(PublishingExtension::class.java) {
        repositories {
            maven {
                url =
                    uri(
                        if (version.toString().endsWith("SNAPSHOT")) {
                            "https://central.sonatype.com/repository/maven-snapshots/"
                        } else {
                            "https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/"
                        },
                    )
                credentials {
                    username = project.findProperty("sonatypeUsername")?.toString()
                    password = project.findProperty("sonatypePassword")?.toString()
                }
            }
        }
        publications {
            register("maven", MavenPublication::class.java) {
                groupId = "dev.s7a"
                artifactId = publishName
                publication()

                pom {
                    name.set(publishName)
                    description.set(
                        "Spigot configuration library for Kotlin using class annotations.",
                    )
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
                    pom()
                }
            }
        }
    }
    extensions.configure(SigningExtension::class.java) {
        val key = project.findProperty("signingKey")?.toString()?.replace("\\n", "\n")
        val password = project.findProperty("signingPassword")?.toString()
        useInMemoryPgpKeys(key, password)
        sign(extensions.getByType(PublishingExtension::class.java).publications.getAt("maven"))
    }
}
