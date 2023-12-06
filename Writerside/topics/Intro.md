# Intro

You can read and write configuration files just by defining a data class.

```Kotlin
class Main : JavaPlugin() { 
    @Comment("This is header comments")
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
# This is header comments

message: You can use default values
```
