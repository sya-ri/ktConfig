# ktConfig v2

Spigot configuration library for Kotlin using class annotations. The library generates configuration loaders at
build-time, ensuring zero runtime overhead (except for YamlConfiguration operations).

## ⚡ Features

- **Zero Runtime Overhead**: All configuration loaders are generated at build-time (KSP).
- **Type-Safe**: Fully typed configuration using Kotlin data classes.
- **Wide Type Support**: Supports primitives, collections, Bukkit types, and more out of the box.
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
    implementation("dev.s7a:ktConfig:2.0.0-SNAPSHOT")
    ksp("dev.s7a:ktConfig-ksp:2.0.0-SNAPSHOT")
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

### Default Values

You can support Kotlin's default values by adding the `@UseDefault` annotation to your class.

```kotlin
@KtConfig
@UseDefault
data class AppConfig(
    val message: String = "Hello",
    val count: Int = 10
)
```

> [!WARNING]
> **Rules for @UseDefault**
>
> 1. **All properties MUST have default values.**
>
> You cannot mix properties with and without default values in a `@UseDefault` annotated class.
>　
> ```kotlin
> // 👌 This is valid
> @KtConfig
> @UseDefault
> data class AppConfig(
>     val message: String = "Hello"
> )
> 
> // ❌ This is invalid
> @KtConfig
> @UseDefault
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
> @KtConfig
> @UseDefault
> data class AppConfig(
>     val message: String = "Hello",
>     val count: Int = 10,
>     val list: List<String> = listOf("a", "b")
> )
>
> // ❌ This is invalid
> @KtConfig
> @UseDefault
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

```kotlin
// Example: Serialize a custom Wrapper class as a String
object WrapperSerializer : TransformSerializer<Wrapper, String>(StringSerializer) {
    override fun transform(value: String): Wrapper {
        return Wrapper(value) // Convert String -> Wrapper
    }

    override fun transformBack(value: Wrapper): String {
        return value.data // Convert Wrapper -> String
    }
}
```

#### 2. Implementing `Serializer` Interface

For full control, implement the `Serializer<T>` interface directly.

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
    @UseSerializer(WrapperSerializer::class)
    val data: Wrapper
)
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
- `java.util.Date`
- `java.util.Calendar`
- [Enum classes](https://kotlinlang.org/docs/enum-classes.html)
- [Inline value classes](https://kotlinlang.org/docs/inline-classes.html)

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

