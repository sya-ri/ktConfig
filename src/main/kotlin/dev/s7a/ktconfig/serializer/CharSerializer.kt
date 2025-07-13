package dev.s7a.ktconfig.serializer

import org.bukkit.configuration.file.YamlConfiguration

/**
 * Serializer for Char type.
 * Handles conversion between Char values and YAML configuration.
 *
 * @since 2.0.0
 */
object CharSerializer : Serializer<Char> {
    override fun get(
        configuration: YamlConfiguration,
        path: String,
    ): Char? = configuration.getString(path)?.firstOrNull()

    override fun save(
        configuration: YamlConfiguration,
        path: String,
        value: Char?,
    ) {
        configuration.set(path, value?.toString())
    }
}
