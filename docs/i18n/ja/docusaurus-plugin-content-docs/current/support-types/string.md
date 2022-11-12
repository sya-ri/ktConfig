---
sidebar_position: 2.01
---

# String

[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/) は任意の長さの文字列です。
もしも、String 以外を設定した場合、`toString` によって文字列に変換されます。

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

また、[Map](map.md) のキーとして利用できます。

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
