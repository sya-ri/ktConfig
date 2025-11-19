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
    val nullable: String?,
) {
    object ExpectedLoader : KtConfigLoader<StringConfig>() {
        override fun load(
            configuration: YamlConfiguration,
            parentPath: String,
        ) = StringConfig(
            StringSerializer.getOrThrow(configuration, "value"),
            StringSerializer.get(configuration, "value"),
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

        override fun transform(value: Map<String, Any?>): StringConfig =
            StringConfig(
                value["value"]?.let(StringSerializer::deserialize) ?: throw IllegalArgumentException("value is null"),
                value["nullable"]?.let(StringSerializer::deserialize),
            )

        override fun transformBack(value: StringConfig): Map<String, Any?> =
            mapOf(
                "value" to StringSerializer.serialize(value.value),
                "nullable" to value.nullable?.let(StringSerializer::serialize),
            )
    }
}
