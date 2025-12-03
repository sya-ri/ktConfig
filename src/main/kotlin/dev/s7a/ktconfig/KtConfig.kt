package dev.s7a.ktconfig

/**
 * Marks a class as a configuration class.
 * Classes annotated with this will be processed as configuration containers.
 * This annotation enables automatic serialization and deserialization of configuration data.
 *
 * @property hasDefault Indicates that the configuration class has default values.
 * @since 2.0.0
 */
@Target(AnnotationTarget.CLASS)
annotation class KtConfig(
    val hasDefault: Boolean = false,
)
