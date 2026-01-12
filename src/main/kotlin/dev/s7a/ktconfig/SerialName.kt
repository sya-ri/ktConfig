package dev.s7a.ktconfig

/**
 * Specifies the name to use for serialization, deserialization, and configuration path mapping.
 *
 * This annotation allows mapping between Kotlin property/class names and their serialized representations,
 * as well as defining custom names for reading from and writing to configuration files.
 *
 * When applied to a class, it specifies the serialization name for that class.
 * When applied to a property, it defines the custom name to be used in the configuration file instead of the property's actual name.
 *
 * Example:
 * ```kotlin
 * @KtConfig
 * @SerialName("server-config")
 * data class ServerConfig(
 *     @SerialName("server-port")
 *     val port: Int,
 *
 *     @SerialName("server-name")
 *     val name: String,
 * )
 * ```
 *
 * @property name The custom name to use for serialization/deserialization and configuration path mapping
 * @since 2.1.0
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.VALUE_PARAMETER)
annotation class SerialName(
    val name: String,
)
