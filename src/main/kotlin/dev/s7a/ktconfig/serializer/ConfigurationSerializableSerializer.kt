package dev.s7a.ktconfig.serializer

import org.bukkit.configuration.serialization.ConfigurationSerializable

/**
 * Serializer for [ConfigurationSerializable] types.
 * Handles serialization and deserialization of Bukkit's [ConfigurationSerializable] objects.
 *
 * @param T The [ConfigurationSerializable] type to be serialized
 * @since 2.0.0
 */
class ConfigurationSerializableSerializer<T : ConfigurationSerializable> : Serializer<T> {
    override fun serialize(value: T) = value

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(value: Any) = value as T
}
