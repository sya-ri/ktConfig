# ktConfig

> **Warning**
> This library is under development, so you can't use it.

Spigot config library for Kotlin handled using class constructor.

```kotlin
class Main : JavaPlugin() { 
    @Comment("config.yml", "This is header comments")
    data class SimpleConfig(
        val message: String = "You can use default values"
    )

    override fun onEnable() {
        val config = this.ktConfigFile("config.yml", SimpleConfig())
        logger.info(config.message)
    }
}
```

```yaml
# config.yml
# This is header comments

message: You can use default values
```

## 🧭 Features

- Support types
  - Common types
    - [x] [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/) 🔑
    - [x] [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/) 🔑
    - [x] [Byte](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte/) 🔑
    - [x] [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/) 🔑
    - [x] [Short](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-short/) 🔑
    - [x] [Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/) 🔑
    - [x] [Float](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/) 🔑
    - [x] [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/) 🔑
    - [x] [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/) 🔑
  - Unsigned types
    - [x] [UByte](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-u-byte/) 🔑
    - [x] [UInt](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-u-int/) 🔑
    - [x] [UShort](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-u-short/) 🔑
    - [x] [ULong](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-u-long/) 🔑
  - Collection types
    - [x] [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/)
    - [x] [Collection](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/)
    - [x] [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/)
    - [x] [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/)
    - [x] [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/) (🔑 types are available as key)
    - [ ] [~~Array~~](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/)
  - Other types
    - [x] [UUID](https://docs.oracle.com/javase/8/docs/api/java/util/UUID.html) 🔑
    - [x] [ConfigurationSerializable](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/configuration/serialization/ConfigurationSerializable.html)
      - [ItemStack](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/inventory/ItemStack.html)
      - [Location](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Location.html)
      - ... all support!!
- Support Kotlin features
  - [x] [Nullable](https://kotlinlang.org/docs/null-safety.html)
  - [x] [Class](https://kotlinlang.org/docs/classes.html)
  - [x] [Data class](https://kotlinlang.org/docs/data-classes.html)
  - [x] [Enum class](https://kotlinlang.org/docs/enum-classes.html)
  - [x] [Inline class](https://kotlinlang.org/docs/inline-classes.html)
  - [x] [Generics](https://kotlinlang.org/docs/generics.html)
  - [ ] [~~Sealed class~~](https://kotlinlang.org/docs/sealed-classes.html)
- Bukkit & User friendly
  - [x] Use [YamlConfiguration](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/configuration/file/YamlConfiguration.html) internally
  - [x] Support comments using @Comment annotation
  - [x] Don't need to put the default yaml file in resources

## 🔗 Links

- [Examples](examples)
- [API Document](https://gh.s7a.dev/ktConfig)
- [License](LICENSE)
