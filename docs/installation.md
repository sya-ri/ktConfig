# Installation

Add the following dependencies to your `build.gradle.kts`

```kotlin
plugins {
    kotlin("jvm") version "2.4.10"
    id("com.google.devtools.ksp") version "2.3.11"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.s7a:ktConfig:2.3.0")
    ksp("dev.s7a:ktConfig-ksp:2.3.0")
}
```

## Fabric

Fabric has no standard user configuration format. ktConfig keeps YAML so the same configuration can be moved between Bukkit and Fabric.
Use `ktConfig-fabric` instead of the Bukkit `ktConfig` runtime; the two runtime artifacts provide the same shared API and must not be installed together.

```kotlin
dependencies {
    ksp("dev.s7a:ktConfig-ksp:2.3.0")

    // Loom's include configuration is not transitive, so include the runtime pieces explicitly.
    include(implementation("dev.s7a:ktConfig-fabric:2.3.0")!!)
    include(implementation("org.spongepowered:configurate-yaml:4.2.0")!!)
    include(implementation("org.spongepowered:configurate-core:4.2.0")!!)
    include(implementation("io.leangen.geantyref:geantyref:1.3.16")!!)
    include(implementation("net.kyori:option:1.1.0")!!)
}

ksp {
    arg("ktconfig.platform", "fabric")
}
```

Resolve a file below Fabric Loader's `config` directory with:

```kotlin
val file = FabricConfigFiles.resolve("example.yml").toFile()
val config = ServerConfigLoader.loadAndSaveIfNotExists(file)
```

Minecraft serializers are published separately because Minecraft 1.21.x jars must be remapped while 26.x jars are unobfuscated:

```kotlin
dependencies {
    implementation("dev.s7a:ktConfig-fabric-minecraft-1.21.11:2.3.0")
}
```

The adapters provide `BlockPosSerializer`, `Vec3Serializer`, `DimensionKeySerializer`, and either `ResourceLocationSerializer` (1.21.10 and older) or `IdentifierSerializer` (1.21.11 and newer). Use Mojang's official mappings and select the artifact matching the exact Minecraft version.

Supported Fabric targets are every release from Minecraft 1.21 through 1.21.11, plus 26.1 and 26.2. Minecraft 1.21.x requires Java 21; Minecraft 26.x requires Java 25.

- Auto generate configuration loaders on build: `./gradlew build`.
- Manually generate loaders: `./gradlew kspKotlin` (maybe required)


Return to the [quick example](../README.md#-quick-example) after configuring your platform.
