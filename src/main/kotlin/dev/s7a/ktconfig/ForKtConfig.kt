package dev.s7a.ktconfig

/**
 * Marks a class as a configuration class.
 * Classes annotated with this will be processed as configuration containers.
 * This annotation enables automatic serialization and deserialization of configuration data.
 *
 * @since 2.0.0
 */
@Target(AnnotationTarget.CLASS)
annotation class ForKtConfig
