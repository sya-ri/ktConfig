package dev.s7a.ktconfig.serializer

import java.util.UUID

/**
 * Serializer implementation for [UUID] values.
 * Handles serialization and deserialization of [UUID] types by converting them to and from String representation.
 *
 * @since 2.0.0
 */
object UUIDSerializer : TransformSerializer.Keyable<UUID, String>(StringSerializer) {
    override fun decode(value: String): UUID = UUID.fromString(value)

    override fun encode(value: UUID): String = value.toString()
}
