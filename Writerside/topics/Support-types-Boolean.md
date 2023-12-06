# Boolean

[Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/) has a value of true or false.
If set to any other type, it will be considered null.

```Kotlin
data class Config(
    val boolean: Boolean
)
```

```yaml
# -> true
boolean: true

# -> false
boolean: false

# -> null (error)
boolean: other
```

Boolean can be used as a key type in Map.

```Kotlin
data class Config(
    val map: Map<Boolean, String>
)
```

```yaml
# -> {true="value1", false="value2"}
map:
  true: value1
  false: value2
```
