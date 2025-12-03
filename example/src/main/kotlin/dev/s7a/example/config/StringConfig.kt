package dev.s7a.example.config

import dev.s7a.ktconfig.Comment
import dev.s7a.ktconfig.KtConfig
import dev.s7a.ktconfig.KtConfigLoader
import dev.s7a.ktconfig.exception.NotFoundValueException
import dev.s7a.ktconfig.serializer.StringSerializer
import org.bukkit.configuration.file.YamlConfiguration

@KtConfig
@Comment("Header comment")
data class StringConfig(
    @Comment("Property comment", "Second line")
    val value: String,
    val nullable: String?,
    val default: Default,
) {
    @KtConfig(hasDefault = true)
    data class Default(
        val value: String = "default",
    )

    object ExpectedLoader : KtConfigLoader<StringConfig>() {
        object DefaultLoader : KtConfigLoader<Default>() {
            private val defaultValue = Default()

            override fun load(
                configuration: YamlConfiguration,
                parentPath: String,
            ) = Default(
                StringSerializer.get(configuration, "${parentPath}value") ?: defaultValue.value,
            )

            override fun save(
                configuration: YamlConfiguration,
                value: Default,
                parentPath: String,
            ) {
                StringSerializer.set(configuration, "${parentPath}value", value.value)
            }

            override fun decode(value: Map<String, Any?>): Default =
                Default(
                    value["value"]?.let(StringSerializer::deserialize) ?: defaultValue.value,
                )

            override fun encode(value: Default): Map<String, Any?> =
                mapOf(
                    "value" to StringSerializer.serialize(value.value),
                )
        }

        override fun load(
            configuration: YamlConfiguration,
            parentPath: String,
        ) = StringConfig(
            StringSerializer.getOrThrow(configuration, "value"),
            StringSerializer.get(configuration, "value"),
            DefaultLoader.load(configuration, "default"),
        )

        override fun save(
            configuration: YamlConfiguration,
            value: StringConfig,
            parentPath: String,
        ) {
            setHeaderComment(configuration, listOf("Header comment"))
            setComment(configuration, "value", listOf("Property comment", "Second line"))
            StringSerializer.set(configuration, "value", value.value)
        }

        override fun decode(value: Map<String, Any?>): StringConfig =
            StringConfig(
                value["value"]?.let(StringSerializer::deserialize) ?: throw NotFoundValueException("value"),
                value["nullable"]?.let(StringSerializer::deserialize),
                value["default"]?.let(DefaultLoader::deserialize) ?: throw NotFoundValueException("default"),
            )

        override fun encode(value: StringConfig): Map<String, Any?> =
            mapOf(
                "value" to StringSerializer.serialize(value.value),
                "nullable" to value.nullable?.let(StringSerializer::serialize),
                "default" to DefaultLoader.serialize(value.default),
            )
    }
}
