# Changelog

## v2.1.0 (SNAPSHOT)

### Added
- Added support for serialization of sealed classes and interfaces.
    - Added `discriminator` property to the `@KtConfig` annotation for handling sealed hierarchies.
- Added new methods to `KtConfigLoader` for easier file handling:
    - `loadAndSave`: Loads a file and immediately saves it back.
    - `loadAndSaveIfNotExists`: Loads a file and saves default values if the file doesn't exist.
    - `saveIfNotExists`: Saves the configuration only if the file does not already exist.
- Added `FormattedColorSerializer#isSupportedAlpha` property to detect Minecraft version support for an alpha channel in colors.

### Changed
- Improved the KSP code generator to use explicit imports instead of fully qualified names in generated loader classes.
  - This results in cleaner and more readable generated code.
  - <details>
    <summary>Example</summary>
    
    ```kotlin
      // Target
      @KtConfig
      data class ExampleConfig(
          val string: String,
          val list: List<String>,
      )
      
      // Before
      private val ListOfString: Serializer<List<String>> =
          dev.s7a.ktconfig.serializer.ListSerializer(dev.s7a.ktconfig.serializer.StringSerializer)
    
      override fun load(configuration: YamlConfiguration, parentPath: String): ExampleConfig = ExampleConfig(
        dev.s7a.ktconfig.serializer.StringSerializer.getOrThrow(configuration, "${parentPath}string"),
        ListOfString.getOrThrow(configuration, "${parentPath}list"),
      )
    
      // After
      private val ListOfString: Serializer<List<String>> = ListSerializer(StringSerializer)
    
      override fun load(configuration: YamlConfiguration, parentPath: String): ExampleConfig = ExampleConfig(
        StringSerializer.getOrThrow(configuration, "${parentPath}string"),
        ListOfString.getOrThrow(configuration, "${parentPath}list"),
      )
      ```
    </details>
- Deprecated `@PathName` and replaced it with `@SerialName` for better consistency.
    - `@PathName` is scheduled to be removed in v2.2.0.
- Fixed `FormattedColorSerializer` to ignore alpha values of 255 (fully opaque) when encoding colors, treating them as if no alpha channel is specified.

### Fixed
- Fixed `FormattedColorSerializer` to ignore an alpha channel when encoding colors on Minecraft versions that don't support alpha transparency.

## v2.0.0

Initial release.
