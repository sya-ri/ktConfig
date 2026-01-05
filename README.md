# ktConfig v2

Spigot configuration library for Kotlin using class annotations. The library generates configuration loaders at
build-time, ensuring zero runtime overhead (except for YamlConfiguration operations).

## ⚡ Features

- **Zero Runtime Overhead**: All configuration loaders are generated at build-time (KSP).
- **Type-Safe**: Fully typed configuration using Kotlin data classes.
- **Wide Type Support**: Supports primitives, collections, Bukkit types, and more.
- **Sealed Classes and Interfaces Support**: Support for sealed classes and interfaces.
- **Rich Features**: Built-in support for comments and custom serializers.
- **Default Values**: Support for default values using Kotlin default values (e.g., `val count: Int = 0`).

## 📦 Installation

Add the following dependencies to your `build.gradle.kts`

```kotlin
plugins {
    kotlin("jvm") version "2.2.21"
    id("com.google.devtools.ksp") version "2.3.2"
}

repositories {
    maven(url = "https://central.sonatype.com/repository/maven-snapshots/")
}

dependencies {
    implementation("dev.s7a:ktConfig:2.1.0-SNAPSHOT")
    ksp("dev.s7a:ktConfig-ksp:2.1.0-SNAPSHOT")
}
```

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

The loader class provides the following methods:

- `load(File): T`
- `loadFromString(String): T`
- `save(T, File)`
- `saveToString(T): String`

## 🚀 Usage

ktConfig provides various annotations to customize configuration behavior:

- `@KtConfig`: Marks a class as a configuration class. Required for code generation.
- `@Comment`: Adds comments to configuration headers or properties.
- `@PathName`: Customizes the YAML path name for a property.
- `@UseSerializer`: Specifies a custom serializer for a property.

### Adding Comments

You can add comments to the generated YAML file using the `@Comment` annotation.

```kotlin
@KtConfig
@Comment("Global settings")
data class AppConfig(
    @Comment("Enable debug mode")
    val debug: Boolean
)
```

### Change the YAML Path Name

You can customize the YAML path name using the `@PathName` annotation.

```kotlin
@KtConfig
data class ServerConfig(
    @PathName("server-name")
    val serverName: String
)
```

The YAML file will look like this:

```yaml
server-name: "My Server"
```

### Default Values

You can support Kotlin's default values by adding `hasDefault = true` property to your `@KtConfig` annotation.

```kotlin
@KtConfig(hasDefault = true)
data class AppConfig(
    val message: String = "Hello",
    val count: Int = 10
)
```

> [!WARNING]
>
> 1. **All properties MUST have default values.**
>
> You cannot mix properties with and without default values in a `@KtConfig(hasDefault = true)` annotated class.
>　
> ```kotlin
> // 👌 This is valid
> @KtConfig(hasDefault = true)
> data class AppConfig(
>     val message: String = "Hello"
> )
> 
> // ❌ This is invalid
> @KtConfig(hasDefault = true)
> data class AppConfig(
>     val message: String,
>     val count: Int = 10
> )
> ```
> 
> 2. **Default values must be static.**
>
> Default values are generated once during construction and reused, so they must be static values.
>
> ```kotlin
> // 👌 This is valid
> @KtConfig(hasDefault = true)
> data class AppConfig(
>     val message: String = "Hello",
>     val count: Int = 10,
>     val list: List<String> = listOf("a", "b")
> )
>
> // ❌ This is invalid
> @KtConfig(hasDefault = true)
> data class AppConfig(
>     val timestamp: Long = System.currentTimeMillis(), // Not static
>     val random: Int = Random().nextInt(), // Not static
>     val uuid: UUID = UUID.randomUUID()     // Not static
> )
> ```
> 
> 3. **No Auto-Save for defaults.**
>
> If a value is missing in the file and the default value is used during loading, it is **not** automatically written back to the file. You must manually save the configuration if you want to persist the default values.
> Example of saving manually:
>
> ```kotlin
> // Load configuration (uses default values if keys are missing)
> val config = AppConfigLoader.load(file)
>
> // Manually save to ensure default values are written to the file
> AppConfigLoader.save(file, config)
> ```

### Custom Serializers

You can define custom serialization logic for specific types.

#### 1. Using `TransformSerializer` (Recommended)

The easiest way is to transform your type into a supported type (like `String` or `Map`).
Extend `TransformSerializer<YOUR_TYPE, BASE_TYPE>` and pass a base serializer to the constructor.
Classes implementing `Serializer.Keyable<T>` can be used as Map keys.

```kotlin
// Example: Serialize a custom Wrapper class as a String
object WrapperSerializer : TransformSerializer<Wrapper, String>(StringSerializer) {
    override fun decode(value: String): Wrapper {
        return Wrapper(value) // Convert String -> Wrapper
    }

    override fun encode(value: Wrapper): String {
        return value.data // Convert Wrapper -> String
    }
}
```

#### 2. Implementing `Serializer` Interface

For full control, implement the `Serializer<T>` interface directly.
Classes implementing `Serializer.Keyable<T>` can be used as Map keys.

```kotlin
object MyTypeSerializer : Serializer<MyType> {
    override fun deserialize(value: Any): MyType {
        // Convert raw YAML value (Map, String, Int, etc.) to MyType
    }

    override fun serialize(value: MyType): Any? {
        // Convert MyType to a YAML-compatible format
    }
}
```

#### Applying the Serializer

Use the `@UseSerializer` annotation on the property to apply your custom serializer.

```kotlin
@KtConfig
data class CustomConfig(
    val data: @UseSerializer(WrapperSerializer::class) Wrapper
)
```

### Sealed classes and interfaces

- Use the `discriminator` property in `@KtConfig` to specify the YAML key name (default is `$`).
- Use `@SerialName` on subclasses to define their identifier in YAML (default is the class full name).

```kotlin
@KtConfig(discriminator = "type")
sealed interface AppConfig {
    @KtConfig
    @SerialName("message")
    data class Message(
        val content: String
    ) : AppConfig

    @KtConfig
    @SerialName("broadcast")
    data class Broadcast(
        val content: String,
        val delay: Int
    ) : AppConfig
}
```

#### YAML Representation

Depending on the class being saved, the YAML will look like this:

```yaml
# For AppConfig.Message
type: message
content: "Hello World"
```

```yaml
# For AppConfig.Broadcast
type: broadcast
content: "Attention!"
delay: 20
```

## 📦 Supported Types

ktConfig supports the following types:

### Primitives

- `Boolean`
- `Byte`
- `Short`
- `Int`
- `Long`
- `Float`
- `Double`
- `Char`
- `String`
- `UByte`
- `UShort`
- `UInt`
- `ULong`
- `BigInteger`
- `BigDecimal`

### Collections

- `List`
- `Set`
- `Map`
- `ArrayDeque`
- `Array`
- `BooleanArray`
- `ByteArray`
- `CharArray`
- `ShortArray`
- `IntArray`
- `LongArray`
- `FloatArray`
- `DoubleArray`
- `UByteArray`
- `UShortArray`
- `UIntArray`
- `ULongArray`

### Others

- `org.bukkit.configuration.serialization.ConfigurationSerializable` : ItemStack, Location, ...
- `java.util.UUID`
- `java.time.Instant`
- `java.time.LocalTime`
- `java.time.LocalDate`
- `java.time.LocalDateTime`
- `java.time.Year`
- `java.time.YearMonth`
- `java.time.OffsetTime`
- `java.time.OffsetDateTime`
- `java.time.ZonedDateTime`
- `java.time.Duration`
- `java.time.Period`
- [Enum classes](https://kotlinlang.org/docs/enum-classes.html)
- [Inline value classes](https://kotlinlang.org/docs/inline-classes.html)
- [Sealed classes and interfaces](https://kotlinlang.org/docs/sealed-classes.html)

### Formatted Types

ktConfig provides several formatted types for easier string-based serialization:

- `FormattedBlock`: Represents [org.bukkit.Block](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/Block.html) (`World, X, Y, Z`)
- `FormattedBlockVector`: Represents [org.bukkit.util.BlockVector](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/util/BlockVector.html) (`X, Y, Z`)
- `FormattedColor`: Represents [org.bukkit.Color](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Color.html) (`#AARRGGBB`, `#RRGGBB`, `AARRGGBB`, `RRGGBB`)
- `FormattedLocation`: Represents [org.bukkit.Location](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Location.html) (`World, X, Y, Z`, `World, X, Y, Z, Yaw, Pitch`)
- `FormattedVector`: Represents [org.bukkit.Vector](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/util/Vector.html) (`X, Y, Z`)
- `FormattedWorld`: Represents [org.bukkit.World](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/World.html) by its name (`WorldName`)

These types are automatically serialized to and from their string representations.

## 🔧 Troubleshooting

### Unsupported type

Log example:

```text
[ksp] Unsupported type: java.util.Date
```

If you encounter an error when using a type that is not supported by ktConfig.

```kotlin
@KtConfig
class InvalidConfig(
    val date: java.util.Date, // Unsupported type
)
```

Define a custom serializer and specify it using `@UseSerializer` annotation to handle this type.

```kotlin
object DateSerializer : Serializer<java.util.Date> {
    // ...
}

@KtConfig
data class Config(
    val date: @UseSerializer(DateSerializer::class) java.util.Date,
)
```

Alternatively, handle YAML using supported types and convert them externally.

```kotlin
@KtConfig
data class Config(
    val instant: java.time.Instant, // Supported type
) {
    val date
        get() = Date.from(instant)
}
```

### Unresolve reference properties

Log example:

```
Unresolved reference 'text'.
```

If you encounter unresolved reference errors when using ktConfig, make sure your properties are properly declared.
Properties in Kotlin must be declared using `val` or `var` to be accessible:

```kotlin
@KtConfig
class InvalidConfig(
    text: String,
)
```

Using data classes is recommended as they enforce `val`/`var` declarations for all primary constructor properties
automatically.

```kotlin
@KtConfig
data class Config(
    val text: String,
)
```

### Unresolved reference using custom serializers

Log example:

```
Unresolved reference 'getOrThrow'
Unresolved reference 'set'
Inapplicable candidate(s): fun deserialize(value: Any): Date
Unresolved reference 'serialize'
```

If you encounter unresolved reference errors when using custom serializers, make sure you use objects instead of classes.

```kotlin
class DateSerializer : Serializer<java.util.Date> {
    // ...
}
```

Should be:

```kotlin
object DateSerializer : Serializer<java.util.Date> {
    // ...
}
```

### Mismatched dependency versions

Log example:

```
'org.gradle.api.provider.Property org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions.getJvmDefault()'
```

This error might occur due to version incompatibility between Kotlin and KSP. Please check and ensure that your Kotlin
and KSP versions are compatible.

## 🔑 License

```
MIT License

Copyright (c) 2025 sya-ri

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

