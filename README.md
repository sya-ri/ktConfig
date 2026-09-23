# ktConfig v2

Bukkit and Fabric YAML configuration library for Kotlin using class annotations.
The library generates type-safe configuration loaders at build-time using KSP.

## ⚡ Features

- **Generated Loaders**: KSP generates type-safe loading and saving code at build time.
- **Type-Safe**: Fully typed configuration using Kotlin data classes.
- **Wide Type Support**: Supports primitives, collections, Bukkit types, and more.
- **Bukkit and Fabric**: Shares the same annotations and YAML representation across both platforms.
- **Sealed Classes and Interfaces Support**: Support for sealed classes and interfaces.
- **Rich Features**: Built-in support for comments and custom serializers.
- **Default Values**: Support for default values using Kotlin default values (e.g., `val count: Int = 0`).

## 📦 Installation

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

### Fabric

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

## 📝 Quick Example

Add the `@KtConfig` annotation to your data class

```kotlin
@KtConfig
data class ServerConfig(
    val serverName: String,
    val maxPlayers: Int
)
```

The loader class will be defined, allowing you to perform loading and saving operations.

```kotlin
override fun onEnable() {
    val file = plugin.dataFolder.resolve("config.yml")
    val config = ServerConfigLoader.load(file)
    // config.serverName : "My Server"
    // config.maxPlayers : 100
}
```

```yaml
serverName: "My Server"
maxPlayers: 100
```

## Documentation

- [Configuration guide](docs/usage.md): loader methods, annotations, defaults, serializers, supported types, and troubleshooting.
- [Migration and deprecations](DEPRECATION.md): required changes for existing users.
- [Changelog](CHANGELOG.md): release history.

## 🤖 Agent Skill

This repository also includes a distributable agent skill for ktConfig at `skills/ktconfig`.
It summarizes how to use ktConfig in Kotlin Bukkit/Spigot/Paper projects, including generated loaders, annotations, default values, sealed classes, custom serializers, and common KSP pitfalls.

### Install with `gh skill`

`gh skill` can discover skills from repositories that follow the `skills/*/SKILL.md` layout, which this repository does.

```bash
gh skill install sya-ri/ktConfig skills/ktconfig
```

### Install with `npx skills`

`npx skills` can install the repository directly and lets you select just the `ktconfig` skill.

```bash
npx skills add sya-ri/ktConfig --skill ktconfig
```

After installing a skill, restart your agent tool so it reloads available skills.

## 🔑 License

ktConfig is available under the [MIT License](LICENSE).
