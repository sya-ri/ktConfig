package dev.s7a.example.config

import dev.s7a.ktconfig.Comment
import dev.s7a.ktconfig.KtConfig
import dev.s7a.ktconfig.KtConfigLoader
import dev.s7a.ktconfig.serializer.StringSerializer
import org.bukkit.configuration.file.YamlConfiguration

@KtConfig
@Comment("Header comment")
data class StringConfig(
    @Comment("Property comment", "Second line")
    val value: String,
) {
    object ExpectedLoader : KtConfigLoader<StringConfig>() {
        override fun load(
            configuration: YamlConfiguration,
            parentPath: String,
        ) = StringConfig(
            StringSerializer.getOrThrow(configuration, "value"),
        )

        override fun save(
            configuration: YamlConfiguration,
            value: StringConfig,
            parentPath: String,
        ) {
            setHeaderComment(configuration, listOf("Header comment"))
            setComment(configuration, "${parentPath}value", listOf("Property comment", "Second line"))
            StringSerializer.set(configuration, "value", value.value)
        }
    }
}
