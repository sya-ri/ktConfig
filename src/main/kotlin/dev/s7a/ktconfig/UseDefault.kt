package dev.s7a.ktconfig

/**
 * Indicates that a configuration class should use its default values.
 *
 * When this annotation is applied to a configuration class, the system will use
 * the default values defined in the class properties instead of attempting to
 * load values from external sources.
 *
 * Example usage:
 * ```
 * @KtConfig
 * @UseDefault
 * data class Config(
 *     val parameter: String = "default"
 * )
 * ```
 * @since 2.0.0
 */

@Target(AnnotationTarget.CLASS)
annotation class UseDefault
