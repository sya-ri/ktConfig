---
sidebar_position: 2.03
---

# Byte

[Byte](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte/) is an integer from -128 to 127.
Values outside that range underflow or overflow.

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

Also, it can be used as a key type for [Map](map.md).

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
