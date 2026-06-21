import dev.s7a.ktconfig.Comment
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

@KtConfig
@Comment(
    """
    Header line
    Header default
    """,
)
data class RawStringCommentConfig(
    @Comment(
        """
        The amount of time to pass until a new update check occurs.
        Default: 1h
        """,
    )
    val updaterDelay: String,
    @Comment("Line one\nLine two")
    val message: String,
)
