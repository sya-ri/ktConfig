package dev.s7a.ktconfig.serializer

import org.bukkit.configuration.file.YamlConfiguration

interface ValueSerializer<T> : Serializer<T> {
    override fun get(
        configuration: YamlConfiguration,
        path: String,
    ): T? = configuration.get(path)?.let(::from)

    override fun save(
        configuration: YamlConfiguration,
        path: String,
        value: T?,
    ) = configuration.set(path, value?.let(::to))

    fun from(value: Any): T

    fun to(value: T): Any?
}
