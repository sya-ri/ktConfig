# ktConfig Reference

Use this file when writing or reviewing Kotlin code that depends on `dev.s7a:ktConfig`.

## Overview

- ktConfig is a Kotlin configuration library for Bukkit/Spigot/Paper.
- It uses KSP to generate type-safe YAML loader classes at build time.
- Annotating `ServerConfig` with `@KtConfig` generates `ServerConfigLoader` in the same package.
- Runtime reflection is not the main pattern; generated code is.

## Installation

Use versions that match the current library docs:

```kotlin
plugins {
    kotlin("jvm") version "2.2.21"
    id("com.google.devtools.ksp") version "2.3.2"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.s7a:ktConfig:2.1.2")
    ksp("dev.s7a:ktConfig-ksp:2.1.2")
}
```

Trigger generation with `./gradlew kspKotlin` or `./gradlew build`.

## Quick Start

```kotlin
import dev.s7a.ktconfig.KtConfig

@KtConfig
data class ServerConfig(
    val serverName: String,
    val maxPlayers: Int,
)
```

```kotlin
class MyPlugin : JavaPlugin() {
    override fun onEnable() {
        val file = dataFolder.resolve("config.yml")
        val config = ServerConfigLoader.load(file)
        logger.info("Server: ${config.serverName}, max: ${config.maxPlayers}")
    }
}
```

YAML:

```yaml
serverName: "My Server"
maxPlayers: 100
```

## Loader API

Generated loaders extend `KtConfigLoader<T>` and expose these entry points:

- `load(file)`
- `loadAndSave(file)`
- `loadAndSaveIfNotExists(file)`
- `loadFromString(content)`
- `save(file, value)`
- `saveIfNotExists(file, value)`
- `saveToString(value)`

Practical choice:

- use `load` when the file should be read only
- use `loadAndSave` when comments and normalized YAML should be written back
- use `loadAndSaveIfNotExists` when the file should be created from defaults on first run

## Core Annotations

### `@KtConfig`

Required on every config class that should generate a loader.

```kotlin
@KtConfig(
    hasDefault = false,
    discriminator = "$",
    loaderName = "{CLASS_NAME}Loader",
)
```

- `hasDefault = true`: missing YAML keys fall back to Kotlin defaults
- `discriminator`: key used for sealed type decoding
- `loaderName`: rename the generated loader object

### `@Comment`

Adds header or property comments to YAML.

```kotlin
@KtConfig
@Comment("Global settings")
data class AppConfig(
    @Comment("Enable verbose logging")
    val debug: Boolean,
)
```

### `@SerialName`

Renames a YAML key or sealed subtype identifier.

```kotlin
@KtConfig
data class Config(
    @SerialName("server-name")
    val serverName: String,
)
```

Use this instead of deprecated `@PathName`.

### `@UseSerializer`

Applies a custom serializer to a property type or typealias.

```kotlin
@KtConfig
data class Config(
    val point: @UseSerializer(PointSerializer::class) Point,
)
```

## Default Values

Use `@KtConfig(hasDefault = true)` when missing YAML keys should use Kotlin defaults.

```kotlin
@KtConfig(hasDefault = true)
data class AppConfig(
    val message: String = "Hello",
    val count: Int = 10,
)
```

Rules:

- every property must have a default value
- defaults must be static expressions, not `UUID.randomUUID()` or `System.currentTimeMillis()`
- loading with defaults does not auto-save missing keys; call `save()` if the file should be updated

## Nested Configs

Nested config types can also be annotated with `@KtConfig`.

```kotlin
@KtConfig
data class RootConfig(
    val database: DatabaseConfig,
) {
    @KtConfig(hasDefault = true)
    data class DatabaseConfig(
        val host: String = "localhost",
        val port: Int = 3306,
    )
}
```

## Nullable Values

- nullable properties such as `String?` are supported
- nullable elements inside collections are supported, for example `List<String?>`
- if a property is non-nullable and YAML provides null, loading fails

## Supported Types

### Primitives and standard Kotlin types

- `Boolean`, `Byte`, `Short`, `Int`, `Long`
- `Float`, `Double`
- `Char`, `String`
- `UByte`, `UShort`, `UInt`, `ULong`
- `BigInteger`, `BigDecimal`

### Collections

- `List<T>`
- `Set<T>`
- `Map<K, V>` where `K` uses a keyable serializer
- `ArrayDeque<T>`
- `Array<T>`
- primitive arrays and unsigned primitive arrays

### Java standard library

- `UUID`
- `Instant`
- `LocalTime`
- `LocalDate`
- `LocalDateTime`
- `Year`
- `YearMonth`
- `OffsetTime`
- `OffsetDateTime`
- `ZonedDateTime`
- `Duration`
- `Period`

### Bukkit / Spigot

- `ConfigurationSerializable` values such as `ItemStack` and `Location`
- enum classes
- inline value classes
- sealed classes and interfaces

### Built-in formatted Bukkit aliases

- `FormattedVector`
- `FormattedLocation`
- `FormattedBlock`
- `FormattedBlockVector`
- `FormattedColor`
- `FormattedWorld`

These wrap Bukkit types in human-readable string formats instead of raw Bukkit serialization.

## Sealed Classes And Interfaces

Use a discriminator when serializing sealed hierarchies.

```kotlin
@KtConfig(discriminator = "type")
sealed interface AppConfig {
    @KtConfig
    @SerialName("message")
    data class Message(
        val content: String,
    ) : AppConfig

    @KtConfig
    @SerialName("broadcast")
    data class Broadcast(
        val content: String,
        val delay: Int,
    ) : AppConfig
}
```

YAML:

```yaml
type: message
content: "Hello World"
```

Rules:

- annotate the sealed parent with `@KtConfig`
- annotate every subclass with `@KtConfig`
- use `@SerialName` on subclasses to keep discriminator values short and stable
- the default discriminator is `"$"`
- `discriminator = ""` also resolves to `"$"`

## Custom Serializers

Preferred pattern: extend `TransformSerializer<T, B>` and convert through a supported base type.

```kotlin
object PointSerializer : TransformSerializer<Point, String>(StringSerializer) {
    override fun decode(value: String): Point {
        val (x, y) = value.split(",").map { it.trim().toInt() }
        return Point(x, y)
    }

    override fun encode(value: Point): String = "${value.x},${value.y}"
}
```

For full control, implement `Serializer<T>` directly.

If the type must be used as a `Map` key, implement a keyable serializer.

Important constraints:

- serializers must be declared as Kotlin `object`
- attach them with `@UseSerializer` on the property, typealias, or class

## Inline Value Classes

`@JvmInline value class` values are serialized using their wrapped type.

```kotlin
@JvmInline
value class PlayerId(val value: UUID)

@KtConfig
data class PlayerConfig(
    val id: PlayerId,
)
```

## Enum Classes

Enums are serialized as their constant names.

```kotlin
enum class Difficulty { EASY, NORMAL, HARD }
```

## Error Handling

Key exception types:

- `NotFoundValueException`
- `NullValueException`
- `InvalidFormatException`
- `InvalidDiscriminatorException`
- `UnsupportedConvertException`
- `KtConfigException`

Typical plugin-side handling:

```kotlin
try {
    val config = MyConfigLoader.load(file)
} catch (e: KtConfigException) {
    logger.severe("Config error: ${e.message}")
}
```

## Troubleshooting

### Unsupported type in KSP output

The type is not built in. Either:

- replace it with a supported type
- add a custom serializer with `@UseSerializer`

### Unresolved reference in generated loader

Check these first:

- constructor parameters intended for serialization are `val` or `var`
- custom serializers are `object`, not `class`
- KSP generation has been rerun with `./gradlew kspKotlin`

### Kotlin and KSP version mismatch

If build errors mention Kotlin compiler APIs, align Kotlin and KSP versions with the documented pair:

- Kotlin `2.2.21`
- KSP `2.3.2`

### `@PathName` deprecation

Replace `@PathName("my-key")` with `@SerialName("my-key")`.
