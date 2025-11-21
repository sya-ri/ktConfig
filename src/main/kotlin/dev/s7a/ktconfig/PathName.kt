package dev.s7a.ktconfig

/**
 * Annotation used to specify a custom path name for a property in configuration.
 *
 * When applied to a property, this annotation allows defining a custom name that will be used
 * when reading from or writing to the configuration file, instead of using the property's actual name.
 *
 * Example:
 * ```kotlin
 * @KtConfig
 * data class ServerConfig(
 *     @PathName("server-port")
 *     val port: Int,
 *
 *     @PathName("server-name")
 *     val name: String,
 * )
 * ```
 *
 * @property name The custom path name to be used for the annotated property
 * @since 2.0.0
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class PathName(
    val name: String,
)
