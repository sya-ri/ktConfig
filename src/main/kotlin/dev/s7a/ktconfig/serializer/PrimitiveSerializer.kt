package dev.s7a.ktconfig.serializer

import org.bukkit.configuration.file.YamlConfiguration

interface PrimitiveSerializer<T> {
    fun getOrThrow(
        configuration: YamlConfiguration,
        path: String,
    ): T = get(configuration, path) ?: throw IllegalArgumentException("Not found value: $path")

    fun get(
        configuration: YamlConfiguration,
        path: String,
    ): T?

    fun save(
        configuration: YamlConfiguration,
        path: String,
        value: T?,
    )
}
