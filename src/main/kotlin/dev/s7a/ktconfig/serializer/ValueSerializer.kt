package dev.s7a.ktconfig.serializer

import org.bukkit.configuration.file.YamlConfiguration

interface ValueSerializer<T> : Serializer<T> {
    override fun get(
        configuration: YamlConfiguration,
        path: String,
    ): T? = configuration.get(path)?.let(::deserialize)

    override fun save(
        configuration: YamlConfiguration,
        path: String,
        value: T?,
    ) = configuration.set(path, value?.let(::serialize))

    fun deserialize(value: Any): T

    fun serialize(value: T): Any?
}
