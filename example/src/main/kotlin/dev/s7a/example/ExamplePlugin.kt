package dev.s7a.example

import dev.s7a.example.config.loadSimpleConfig
import dev.s7a.example.config.saveSimpleConfig
import org.bukkit.plugin.java.JavaPlugin

class ExamplePlugin : JavaPlugin() {
    override fun onEnable() {
        loadSimpleConfig()
        saveSimpleConfig()
    }
}
