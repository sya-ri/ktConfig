package dev.s7a.ktconfig

/**
 * Adds comments to configuration properties.
 * Comments will be preserved when saving the configuration file.
 *
 * @property content Array of strings representing comment lines to be added.
 * @since 2.0.0
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.VALUE_PARAMETER)
annotation class Comment(
    vararg val content: String,
)
