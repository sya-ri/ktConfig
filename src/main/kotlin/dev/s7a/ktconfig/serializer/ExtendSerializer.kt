package dev.s7a.ktconfig.serializer

import org.bukkit.configuration.file.YamlConfiguration

abstract class ExtendSerializer<T, B>(
    val base: Serializer<B>,
) : Serializer<T> {
    override fun get(
        configuration: YamlConfiguration,
        path: String,
    ): T? = base.get(configuration, path)?.let(::from)

    override fun save(
        configuration: YamlConfiguration,
        path: String,
        value: T?,
    ) {
        base.save(configuration, path, value?.let(::to))
    }

    abstract fun from(base: B): T

    abstract fun to(value: T): B
}
