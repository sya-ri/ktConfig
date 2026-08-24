package dev.s7a.ktconfig

/**
 * Allows a config object to register validation rules that run after it has been loaded.
 *
 * Generated loaders automatically execute this validation after constructing a config value.
 *
 * Example:
 * ```kotlin
 * @KtConfig
 * data class ServerConfig(
 *     val host: String,
 *     val port: Int,
 * ) : KtConfigValidatable<ServerConfig> {
 *     override fun KtConfigValidatorBuilder<ServerConfig>.validate() {
 *         requireNotBlank(ServerConfig::host)
 *         requireIn(ServerConfig::port, 1..65535)
 *     }
 * }
 * ```
 *
 * @param T The config object type to validate.
 * @since 2.2.0
 */
interface KtConfigValidatable<T> {
    /**
     * Registers validation rules for this config type.
     *
     * @since 2.2.0
     */
    fun KtConfigValidatorBuilder<T>.validate()
}
