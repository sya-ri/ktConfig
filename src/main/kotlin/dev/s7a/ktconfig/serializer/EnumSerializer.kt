package dev.s7a.ktconfig.serializer

import java.lang.Enum.valueOf

/**
 * Serializer for Enum types.
 * Handles conversion between Enum values and YAML configuration strings.
 *
 * @param E The enum type to be serialized
 * @property clazz The Class object representing the enum type
 * @since 2.0.0
 */
class EnumSerializer<E : Enum<E>>(
    val clazz: Class<E>,
) : TransformSerializer.Keyable<E, String>(StringSerializer) {
    override fun transform(value: String): E = valueOf(clazz, value)

    override fun transformBack(value: E) = value.name
}
