# Getting started

Learn the basic setup steps for managing configuration files.
This library makes loading and saving configurations easy.

## Define config data class

Define a data class to store your configuration data, corresponding to your configuration file parameters.

```kotlin
data class Config(val message: String)
```

Please read [Define data class](Define-data-class.md) for more details.

## Load / Save

Methods for loading and saving configuration files.

### Load config from plugin file

```kotlin
fun load(plugin: JavaPlugin) {
    val config = plugin.ktConfigFile<Config>("config.yml")
    // config: Config?
}
```

Load the configuration from a plugin file.
Returns `null` if the file is missing or its content is empty.

```kotlin
fun load(plugin: JavaPlugin) {
    val config = plugin.ktConfigFile("config.yml", Config("default message"))
    // config: Config
}
```

Load the configuration with a default value.
If the file is missing or its content is empty, the function saves the default value to the file and returns it.

### Load config from file

```kotlin
fun load(file: File) {
    val config = ktConfigFile<Config>(file)
    // config: Config?
}
```

Load the configuration from a general file.
Returns `null` if the file is missing or its content is empty.

```kotlin
fun load(file: File) {
    val config = ktConfigFile(file, Config("default message"))
    // config: Config
}
```

Load the configuration with a default value.
If the file is missing or its content is empty, the function saves the default value to the file and returns it.

### Load config from text

```kotlin
fun load(yaml: String) {
    val config: Config? = ktConfigString(yaml)
}
```

Load the configuration from a YAML string. Returns `null` if the string is empty.

### Save config to plugin file

```kotlin
fun save(plugin: JavaPlugin, config: Config) {
    plugin.saveKtConfigFile("config.yml", config)
}
```

Save the configuration to a plugin file.

### Save config to file

```kotlin
fun save(file: File, config: Config) {
    saveKtConfigFile(file, config)
}
```

Save the configuration to a specified file.

### Save config to text

```kotlin
fun save(config: Config) {
    val yaml = saveKtConfigString(config)
    // yaml: String
}
```

Convert the configuration to a YAML string.
This is useful for storing or transmitting the configuration as a string.
