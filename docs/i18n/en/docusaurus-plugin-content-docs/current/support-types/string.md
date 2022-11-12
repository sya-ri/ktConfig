---
sidebar_position: 2.01
---

# String

[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/) is a character strings of arbitrary length.
If set to something other than String, it will be converted using `toString`.

```kotlin title="Config.kt"
data class Config(
    val string: String
)
```

```yaml
# -> "Hello"
string: "Hello"

# -> "5"
string: 5

# -> "[list1, list2]"
string:
  - list1
  - list2
```

Also, it can be used as a key type for [Map](map.md).

```kotlin title="Config.kt"
data class Config(
    val map: Map<String, String>
)
```

```yaml
# -> {"key1"="value1", "key2"="value2"}
map:
  key1: value1
  key2: value2
```
