# Changelog

## v2.2.0

### Added
- Added KSP support for applying `@KtConfig` to type aliases, including generic type substitution for generated loaders.
- Added KSP support for generating sealed subtype loaders from the concrete parent type alias, so sealed children no longer need their own `@KtConfig` annotation when their types can be inferred.
  ```kotlin
  sealed interface Config<T>

  @SerialName("value")
  data class Value<T>(
      val value: T,
  ) : Config<T>

  @KtConfig(discriminator = "type")
  typealias StringConfig = Config<String>

  // Generated:
  // - StringConfigLoader for Config<String>
  // - Value<String> handling inside StringConfigLoader
  //
  // StringConfigLoader.load(...) dispatches "type: value" to the inlined
  // Value<String> handling even though Value is not annotated with @KtConfig.
  ```
  If a sealed child is explicitly annotated with `@KtConfig`, that child loader's settings are used instead of inheriting settings from the parent type alias.
  ```kotlin
  sealed interface ExplicitConfig

  @KtConfig(hasDefault = true)
  @SerialName("value")
  data class ExplicitValue(
      val value: String = "default",
  ) : ExplicitConfig

  @KtConfig(discriminator = "type", hasDefault = false)
  typealias ExplicitConfigAlias = ExplicitConfig

  // Generated:
  // - ExplicitConfigAliasLoader for ExplicitConfig
  // - ExplicitValue handling inside ExplicitConfigAliasLoader
  //
  // ExplicitConfigAliasLoader.load("type: value") uses the ExplicitValue @KtConfig
  // settings in its inlined subtype handling, so the missing "value" property uses
  // ExplicitValue's default even though the parent type alias has hasDefault = false.
  ```
- Added a user-defined validation DSL for config objects.
  ```kotlin
  val validator = validate<ServerConfig> {
      requireIn(ServerConfig::port, 1..65535)
      anyOf("host or socketPath must be configured") {
          requireNotBlank(ServerConfig::host)
          requireNotBlank(ServerConfig::socketPath)
      }
      require("min must be less than or equal to max") {
          it.min <= it.max
      }
  }

  val errors: List<KtConfigError> = validator.validate(config)
  ```
- Added `KtConfigResult` and `KtConfigLoadException` for aggregate loading errors.
  ```kotlin
  when (val result = ServerConfigLoader.loadResultFromString(content)) {
      is KtConfigResult.Success -> result.value
      is KtConfigResult.Failure -> result.errors
  }

  try {
      ServerConfigLoader.loadFromString(content)
  } catch (e: KtConfigLoadException) {
      val errors: List<KtConfigError> = e.errors
  }
  ```
- Added `KtConfigValidatable` for config objects that should validate themselves after loading.
  Generated loaders automatically call `validate()` on loaded values that implement this interface.
  ```kotlin
  @KtConfig
  data class ServerConfig(
      val host: String,
      val port: Int,
  ) : KtConfigValidatable<ServerConfig> {
      override fun KtConfigValidatorBuilder<ServerConfig>.validate() {
          requireNotBlank(ServerConfig::host)
          requireIn(ServerConfig::port, 1..65535)
      }
  }
  ```
- Added `@file:Suppress` to generated loaders to suppress warnings caused by generated implementation details.

### Fixed
- Fixed `FormattedBlockVectorSerializer` decoding to preserve decimal coordinates by parsing values as doubles.

## v2.1.2

### Fixed
- Fixed generated loaders for `@Comment` values written as Kotlin raw strings.

## v2.1.1

### Added
- Added a distributable `ktconfig` agent skill under `skills/ktconfig`.
  - Includes installation metadata for agent tools.
  - Includes a focused reference covering annotations, generated loaders, serializers, sealed classes, and KSP usage notes.

## v2.1.0

### Added
- Added support for serialization of sealed classes and interfaces.
    - Added `discriminator` property to the `@KtConfig` annotation for handling sealed hierarchies.
- Added `loaderName` property to the `@KtConfig` annotation to allow customizing generated loader class names.
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
    - `@PathName` is scheduled to be removed in v2.4.0.
- Fixed `FormattedColorSerializer` to ignore alpha values of 255 (fully opaque) when encoding colors, treating them as if no alpha channel is specified.

### Fixed
- Fixed KSP loader generation to respect nullability when the original type is a nullable typealias (previously, `Nullable` could be ignored and a non-null loader would be generated).
- Fixed `FormattedColorSerializer` to ignore an alpha channel when encoding colors on Minecraft versions that don't support alpha transparency.
- Fixed `BigInteger`, `BigDecimal` unsupported exception.

## v2.0.0

Initial release.
