# Support types

## Basic

All types are supported as Map keys.

| Type               | Values                                                                      | 
|--------------------|-----------------------------------------------------------------------------|
| String             | Any strings                                                                 |
| Char               | Any characters                                                              |
| Boolean            | `true` or `false`                                                           |
| Byte               | `-128` ~ `127`                                                              |
| UByte              | `0` ~ `255`                                                                 |
| Short              | `-32768` ~ `32767`                                                          |
| UShort             | `0` ~ `65535`                                                               |
| Int                | `-2147483648` ~ `2147483647`                                                |
| UInt               | `0` ~ `4294967295`                                                          |
| Long               | `-9223372036854775808` ~ `9223372036854775807`                              |
| ULong              | `0` ~ `18446744073709551615`                                                |
| Float              | `1.175494351E-38` ~ `3.402823466E+38`                                       |
| Double             | `2.2250738585072014E-308` ~ `1.7976931348623158E+308`                       |
| BigInteger         | Any integers                                                                |
| BigDecimal         | Any real numbers (Bug: [#26](https://github.com/sya-ri/ktConfig/issues/26)) |
| java.util.Date     | Any dates                                                                   |
| java.util.Calendar | Any dates                                                                   |
| java.util.UUID     | Any UUID                                                                    |
| Enum               |                                                                             |

## Collection

- Iterable
- Collection
- List
- Set
- HashSet
- LinkedHashSet
- Map
- HashMap
- LinkedHashMap
