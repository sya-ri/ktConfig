package dev.s7a.ktconfig.simpleconfig

import dev.s7a.ktconfig.ktConfig
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class Main : JavaPlugin() {
    override fun onEnable() {
        val config = ktConfig("config.yml", SimpleConfig())
        println(config)
    }
}
