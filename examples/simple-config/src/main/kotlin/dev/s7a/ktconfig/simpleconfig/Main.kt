package dev.s7a.ktconfig.simpleconfig

import dev.s7a.ktconfig.Comment
import dev.s7a.ktconfig.ktConfigFile
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class Main : JavaPlugin() {
    @Comment("config.yml", "This is header comments")
    data class SimpleConfig(
        val message: String = "You can use default values",
    )

    override fun onEnable() {
        val config = this.ktConfigFile("config.yml", SimpleConfig())
        logger.info(config.message)
    }
}
