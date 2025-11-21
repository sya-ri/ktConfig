plugins {
    `maven-publish`
    signing
}

dependencies {
    implementation(libs.ksp.api)
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)
}

val sourceJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

applyPublishingConfig(
    "ktConfig-ksp",
    publication = {
        from(components["kotlin"])
        artifact(sourceJar.get())
    },
)
