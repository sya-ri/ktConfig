---
sidebar_position: 2.02
---

# Boolean

[Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/) has a value of `true` or `false`.
If set to any other string, it will be null.

```kotlin title="Config.kt"
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

Also, it can be used as a key type for [Map](map.md).

```kotlin title="Config.kt"
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
