# Deprecation List

This document lists features and APIs that have been deprecated in `ktConfig`.

## Annotations

### @PathName

- **Deprecated in**: v2.1.0
- **Scheduled for removal**: v2.4.0
- **Replacement**: `dev.s7a.ktconfig.SerialName`

`@PathName` was used to specify a custom YAML path name for a property. It is being replaced by `@SerialName` to provide a unified annotation for both property paths and sealed class discriminators.

#### Example Migration

```kotlin
// ❌ Deprecated (Compilation Error in v2.1.0+)
@KtConfig
data class Config(
    @PathName("server-port")
    val port: Int
)

// ✅ Recommended
@KtConfig
data class Config(
    @SerialName("server-port")
    val port: Int
)
```

