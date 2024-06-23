# Define data class

Define a data class for reading and writing configurations.

## Nullable Types

By appending `?` to a type, you make it nullable.

```kotlin
data class Config(val message: String, val optionalMessage: String?)
```

```yaml
message: "Message"
# -> Config("Message", null)
```

When you define a data class like this and load YAML that does not include `optionalMessage`, you will get a configuration object where `message` holds a value and `optionalMessage` is null.

## Default Values

You can set default values for properties, which will be used if the corresponding configuration is missing.

```kotlin
data class Config(val message: String, val defaultMessage: String = "Default")
```

```yaml
message: "Message"
# -> Config("Message", "Default")
```

When you define a data class like this and load YAML that does not include `defaultMessage`, you will get a configuration object where `message` holds a value and `defaultMessage` holds the default value of `"Default"`.

## Nesting

...

## Generics

...

## Comment

...
