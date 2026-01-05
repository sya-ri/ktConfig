package dev.s7a.ktconfig

/**
 * Specifies the name to use for serialization and deserialization of a property or class.
 * This annotation allows mapping between Kotlin property names and their serialized representations.
 *
 * @property name The name to use when serializing/deserializing this element.
 * @since 2.1.0
 */
@Target(AnnotationTarget.CLASS)
annotation class SerialName(
    val name: String,
)
