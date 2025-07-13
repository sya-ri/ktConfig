package dev.s7a.ktconfig.serializer

import org.bukkit.configuration.file.YamlConfiguration

object StringSerializer : PrimitiveSerializer<String> {
    override fun get(
        configuration: YamlConfiguration,
        path: String,
    ): String? = configuration.getString(path)

    override fun save(
        configuration: YamlConfiguration,
        path: String,
        value: String?,
    ) {
        configuration.set(path, value)
    }
}
