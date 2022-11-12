---
sidebar_position: 2.03
---

# Byte

[Byte](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte/) は -128 から 127 までの整数です。
範囲外の値はオーバーフローもしくはアンダーフローします。

```kotlin title="Config.kt"
data class Config(
    val byte: Byte
)
```

```yaml
# -> 5
byte: 5

# -> 12
byte: '12'

# -> -9
byte: -9

# -> 78
byte: 0x4E

# -> -126
byte: 130
```

また、[Map](map.md) のキーとして利用できます。

```kotlin title="Config.kt"
data class Config(
    val map: Map<Byte, String>
)
```

```yaml
# -> {5="value1", 12="value2", -9="value3", 78="value4", -126="value5"}
map:
  5: value1
  '12': value2
  -9: value3
  0x4E: value4
  130: value5
```
