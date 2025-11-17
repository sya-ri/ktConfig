package dev.s7a.example.config

import dev.s7a.ktconfig.Comment
import dev.s7a.ktconfig.ForKtConfig
import dev.s7a.ktconfig.KtConfigLoader
import dev.s7a.ktconfig.serializer.StringSerializer
import org.bukkit.configuration.file.YamlConfiguration

@ForKtConfig
@Comment("Header comment")
data class StringConfig(
    @Comment("Property comment", "Second line")
    val value: String,
) {
    object ExpectedLoader : KtConfigLoader<StringConfig>() {
        override fun load(configuration: YamlConfiguration) =
            StringConfig(
                StringSerializer.getOrThrow(configuration, "value"),
            )

        override fun save(
            configuration: YamlConfiguration,
            value: StringConfig,
        ) {
            setHeaderComment(configuration, listOf("Header comment"))
            setComment(configuration, "value", listOf("Property comment", "Second line"))
            StringSerializer.set(configuration, "value", value.value)
        }
    }
}
