package dev.s7a.ktconfig.serializer

/**
 * Serializer for Char type.
 * Handles conversion between Char values and YAML configuration.
 *
 * @since 2.0.0
 */
object CharSerializer : ExtendSerializer<Char, String>(StringSerializer) {
    override fun convertFrom(value: String) = value.single()

    override fun convertTo(value: Char) = value.toString()
}
