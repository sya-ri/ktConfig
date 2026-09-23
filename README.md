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

For Bukkit, add the following to your `build.gradle.kts`. For Fabric, use the [Fabric setup](docs/installation.md#fabric), including its runtime packaging requirements. Do not install both platform runtimes together.

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

- [Installation](docs/installation.md): Bukkit, Fabric, Minecraft adapters, and loader generation.
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
