# String

[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/) is a sequence of characters of any length.
If it is of a type other than String, it will be converted using the `toString` method.

```Kotlin
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

String can be used as a key type in Map.

```Kotlin
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
