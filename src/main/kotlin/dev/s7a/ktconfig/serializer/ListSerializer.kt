package dev.s7a.ktconfig.serializer

import org.bukkit.configuration.file.YamlConfiguration

class ListSerializer<E>(
    val valueSerializer: ValueSerializer<E>,
) : Serializer<List<E>> {
    override fun get(
        configuration: YamlConfiguration,
        path: String,
    ): List<E>? =
        configuration.getList(path)?.map {
            valueSerializer.deserialize(it!!)
        }

    override fun save(
        configuration: YamlConfiguration,
        path: String,
        value: List<E>?,
    ) {
        configuration.set(path, value?.map(valueSerializer::serialize))
    }
}
