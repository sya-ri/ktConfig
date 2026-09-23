# Configuration guide

Start with the [installation instructions](../README.md#-installation) and [quick example](../README.md#-quick-example).

## Generated loaders

The loader class provides the following methods:

- `load(File): T` - Loads configuration from the file.
- `loadAndSave(File): T` - Loads configuration from the file and immediately saves it back.
- `loadAndSaveIfNotExists(File): T` - Loads configuration from the file, or creates it with default values if it doesn't exist.
- `loadFromString(String): T` - Loads configuration from a YAML string.
- `save(File, T)` - Saves the configuration to the file.
- `saveIfNotExists(File, T)` - Saves the configuration only if the file doesn't already exist.
- `saveToString(T): String` - Serializes the configuration to a YAML string.

## 🚀 Usage

ktConfig provides various annotations to customize configuration behavior:

- `@KtConfig`: Marks a class as a configuration class. Required for code generation.
- `@Comment`: Adds comments to configuration headers or properties.
- `@SerialName`: Customizes the YAML path name for a property.
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

You can customize the YAML path name using the `@SerialName` annotation.

For existing `@PathName` users, see the [migration guide](../DEPRECATION.md#pathname).

```kotlin
@KtConfig
data class ServerConfig(
    @SerialName("server-name")
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

All properties must have defaults when `hasDefault = true`.
Use stable defaults: the generated loader reuses its default configuration rather than recalculating timestamps or random values on each load.
Loading does not write missing defaults back to the file; use `loadAndSave(file)` or save the loaded configuration explicitly when that is required.

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

For direct control over YAML values, implement [`Serializer<T>`](../src/main/kotlin/dev/s7a/ktconfig/serializer/Serializer.kt).
Implement `Serializer.Keyable<T>` when the type must also work as a map key.

#### Applying the Serializer

Use the `@UseSerializer` annotation on the property to apply your custom serializer.

```kotlin
@KtConfig
data class CustomConfig(
    val data: @UseSerializer(WrapperSerializer::class) Wrapper
)
```

You can also use type aliases with custom serializers for cleaner code reuse:

```kotlin
typealias SerializableWrapper =
        @UseSerializer(WrapperSerializer::class) Wrapper

@KtConfig
data class CustomConfig(
    val data: SerializableWrapper
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

For `[ksp] Unsupported type: java.util.Date` or another unsupported type, use a [custom serializer](#custom-serializers) with `@UseSerializer`.
Alternatively, store a supported type such as `Instant` and convert it in application code.

### Unresolve reference properties

Declare constructor properties with `val` or `var` so generated loaders can access them.
Using a data class enforces this for primary constructor properties.

### Unresolved reference using custom serializers

Declare serializers as Kotlin `object`s, not classes.
Incorrect declarations can produce unresolved `getOrThrow`, `set`, or `serialize` references in generated code.

### Mismatched dependency versions

If Gradle reports a missing `KotlinJvmCompilerOptions.getJvmDefault()` method, check the Kotlin/KSP combination against the [installation example](../README.md#-installation).
