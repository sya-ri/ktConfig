package dev.s7a.ktconfig.serializer

import org.bukkit.configuration.file.YamlConfiguration

object NumberSerializer : Serializer<Number> {
    override fun get(
        configuration: YamlConfiguration,
        path: String,
    ) = configuration.get(path) as? Number?

    override fun save(
        configuration: YamlConfiguration,
        path: String,
        value: Number?,
    ) {
        configuration.set(path, value)
    }
}
