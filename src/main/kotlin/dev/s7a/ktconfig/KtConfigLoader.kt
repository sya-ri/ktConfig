package dev.s7a.ktconfig

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

abstract class KtConfigLoader<T> {
    companion object {
        /**
         * Change the path separator to be able to use Double or Float as a key
         */
        private const val PATH_SEPARATOR = 0x00.toChar()
    }

    private fun configuration() =
        YamlConfiguration().apply {
            options().pathSeparator(PATH_SEPARATOR)
        }

    fun load(file: File) =
        load(
            configuration().apply {
                load(file)
            },
        )

    fun loadFromString(content: String) =
        load(
            configuration().apply {
                loadFromString(content)
            },
        )

    protected abstract fun load(configuration: YamlConfiguration): T

    fun save(
        file: File,
        value: T,
    ) = configuration()
        .apply {
            save(this, value)
        }.save(file)

    fun saveToString(
        file: File,
        value: T,
    ) = configuration()
        .apply {
            save(this, value)
        }.saveToString()

    protected abstract fun save(
        configuration: YamlConfiguration,
        value: T,
    )
}
