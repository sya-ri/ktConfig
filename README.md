# ktConfig v2

> [!WARNING]
> This library is under development.

Spigot configuration library for Kotlin using class annotations. The library generates configuration loaders at
build-time, ensuring zero runtime overhead (except for YamlConfiguration operations).

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

- load(File): T
- loadFromString(String): T
- save(T, File)
- saveToString(T): String

## ⚡ Features

- **Zero Runtime Overhead**: All configuration loaders are generated at build-time (KSP).
- **Type-Safe**: Fully typed configuration using Kotlin data classes.
- **Wide Type Support**: Supports primitives, collections, Bukkit types, and more out of the box.
- **Rich Features**: Built-in support for comments and custom serializers.

> [!IMPORTANT]
> **Default Values are NOT Supported**
>
> Kotlin default values (e.g., `val count: Int = 0`) are currently **ignored**.
> All properties must be present in the configuration file. If a value is optional, you **must** use a nullable type (e.g., `val count: Int?`).

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

