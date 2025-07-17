package dev.s7a.ktconfig.serializer

/**
 * Serializer for Boolean type.
 * Handles conversion between Boolean values and YAML configuration.
 *
 * @since 2.0.0
 */
object BooleanSerializer : Serializer<Boolean> {
    override fun deserialize(value: Any) =
        when (value) {
            is Boolean -> value
            is String -> value.toBoolean()
            else -> throw IllegalArgumentException("Cannot convert to Boolean: ${value::class.simpleName}")
        }

    override fun serialize(value: Boolean) = value
}
