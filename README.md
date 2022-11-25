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

## 🔗 Links

- [Website](https://ktConfig.s7a.dev)
- [API Document](https://gh.s7a.dev/ktConfig)
- [License](LICENSE)
