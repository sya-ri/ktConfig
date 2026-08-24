package dev.s7a.ktconfig.serializer

import dev.s7a.ktconfig.KtConfigLoader
import dev.s7a.ktconfig.platform.PlatformYamlConfiguration
import dev.s7a.ktconfig.platform.PlatformYamlConfigurationAdapter

/**
 * A serializer implementation for handling nested configuration objects.
 * This serializer delegates the actual loading and saving operations to a provided [KtConfigLoader].
 *
 * @param T The type of configuration object to serialize/deserialize
 * @property loader The configuration loader responsible for handling the nested object
 * @since 2.0.0
 */
class NestedSerializer<T>(
    val loader: KtConfigLoader<T>,
) : Serializer<T> {
    override fun get(
        configuration: PlatformYamlConfiguration,
        path: String,
    ): T? =
        if (PlatformYamlConfigurationAdapter.contains(configuration, path)) {
            loader.load(configuration, "${path}${KtConfigLoader.PATH_SEPARATOR}")
        } else {
            null
        }

    override fun set(
        configuration: PlatformYamlConfiguration,
        path: String,
        value: T?,
    ) {
        if (value == null) {
            PlatformYamlConfigurationAdapter.set(configuration, path, null)
        } else {
            loader.save(configuration, value, "${path}${KtConfigLoader.PATH_SEPARATOR}")
        }
    }

    override fun deserialize(value: Any) = loader.deserialize(value)

    override fun serialize(value: T) = loader.serialize(value)
}
