import dev.s7a.ktconfig.KtConfig

@KtConfig
data class ServerConfig(
    val nested: Nested,
) {
    @KtConfig(loaderName = "Custom{CLASS_NAME}Loader")
    data class Nested(
        val serverName: String,
    )
}
