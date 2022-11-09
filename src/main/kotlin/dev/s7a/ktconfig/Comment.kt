package dev.s7a.ktconfig

/**
 * Configuration comment
 *
 * @since 1.0.0
 */
@Target(AnnotationTarget.CLASS)
annotation class Comment(vararg val lines: String)
