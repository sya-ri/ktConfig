package dev.s7a.ktconfig.serializer

import org.bukkit.configuration.serialization.ConfigurationSerializable

class ConfigurationSerializableSerializer<T : ConfigurationSerializable> : Serializer<T> {
    override fun serialize(value: T) = value

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(value: Any) = value as T
}
