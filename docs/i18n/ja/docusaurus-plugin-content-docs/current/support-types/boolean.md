---
sidebar_position: 2.02
---

# Boolean

[Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/) は `true` もしくは `false` の値を持ちます。
もしも、それ以外の文字列を設定すると、null になります。

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

また、[Map](map.md) のキーとして利用できます。

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
